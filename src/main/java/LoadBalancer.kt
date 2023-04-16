import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

abstract class LoadBalancer(
    val availableProviders: MutableSet<Provider>,
    private val timer: Timer = Timer()
) : ProviderIdRetriever, ProviderRegistration {

    private val providerStatus: MutableMap<ProviderStatus, Set<Provider>> =
        mutableMapOf(ProviderStatus.ACTIVE to availableProviders)

    init {
        require(availableProviders.isNotEmpty()) { "Failed precondition: the load balancer must have at least one provider to distribute requests" }
        require(availableProviders.size <= MAXIMUM_PROVIDERS_ALLOWED) { "Maximum provider number reached" }
    }

    companion object {
        private const val MAXIMUM_PROVIDERS_ALLOWED = 10
    }

    override fun include(provider: Provider) {
        availableProviders.add(provider)
    }

    override fun exclude(provider: Provider) {
        availableProviders.remove(provider)
    }

    fun checkProviderStatus() {
        timer.scheduleAtFixedRate(
            0L, 60000L
        ) {
            updateProviderStatus()
            updateAvailableProviders()
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
        availableProviders.retainAll(providerStatus.getOrDefault(ProviderStatus.ACTIVE, mutableSetOf()))
        availableProviders.addAll(providerStatus.getOrDefault(ProviderStatus.ACTIVE, mutableSetOf()))
    }

}