import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Timer
import java.util.concurrent.Semaphore
import kotlin.concurrent.scheduleAtFixedRate

private const val MAXIMUM_PROVIDERS_ALLOWED = 10
private const val MAXIMUM_PARALLELISM_ALLOWED = 5
private const val HEART_BEAT_CHECK_INTERVAL = 60000L

abstract class LoadBalancer(
    val availableProviders: MutableSet<Provider>,
    private val timer: Timer = Timer(),
    private val semaphore: Semaphore = Semaphore(MAXIMUM_PARALLELISM_ALLOWED * availableProviders.size),
    private val mutex: Mutex = Mutex()
) : ProviderIdRetriever, ProviderRegistration {

    private val providerStatus: MutableMap<ProviderStatus, Set<Provider>> =
        mutableMapOf(ProviderStatus.ACTIVE to availableProviders)

    init {
        require(availableProviders.isNotEmpty()) { "Failed precondition: the load balancer must have at least one provider to distribute requests" }
        require(availableProviders.size <= MAXIMUM_PROVIDERS_ALLOWED) { "Maximum provider number reached" }
    }

    abstract fun getProviderId(): String

    override fun get(): String {
        semaphore.acquire()
        val providerId = getProviderId()
        semaphore.release()
        return providerId
    }

    override fun include(provider: Provider) {
        synchronized(availableProviders) {
            availableProviders.add(provider)
        }
    }

    override fun exclude(provider: Provider) {
        synchronized(availableProviders) {
            availableProviders.remove(provider)
        }
    }

    fun checkProviderStatus() {
        runBlocking {
            mutex.withLock {
                timer.scheduleAtFixedRate(
                    0L, HEART_BEAT_CHECK_INTERVAL
                ) {
                    updateProviderStatus()
                    updateAvailableProviders()
                }
            }
        }
    }

    private fun updateProviderStatus() {
        val updatedProviderStatus = providerStatus.entries.asSequence().flatMap { status ->
            status.value.groupBy { provider -> providerStatus(status.key, provider.check()) }.asSequence()
        }.groupBy { it.key }.mapValues { entry -> entry.value.flatMap { it.value }.toSet() }

        updatedProviderStatus.forEach { (status, providers) -> providerStatus[status] = providers }
    }

    private fun providerStatus(providerStatus: ProviderStatus, heartBeatStatus: HeartBeatStatus): ProviderStatus =
        when (heartBeatStatus) {
            HeartBeatStatus.ACTIVE -> when (providerStatus) {
                ProviderStatus.ACTIVE -> ProviderStatus.ACTIVE
                ProviderStatus.RECOVERABLE -> ProviderStatus.ACTIVE
                ProviderStatus.DISABLED -> ProviderStatus.RECOVERABLE
            }

            HeartBeatStatus.DISABLED -> ProviderStatus.DISABLED
        }

    private fun updateAvailableProviders() {
        val updatedActiveProviders = providerStatus.getOrDefault(ProviderStatus.ACTIVE, mutableSetOf())
        availableProviders.apply {
            retainAll(updatedActiveProviders)
            addAll(updatedActiveProviders)
        }
    }

}