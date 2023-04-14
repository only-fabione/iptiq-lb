abstract class LoadBalancer(val availableProviders: List<Provider>) : ProviderIdRetriever {

    init {
        require(availableProviders.isNotEmpty()) { "Failed precondition: the load balancer must have at least one provider to distribute requests" }
        require(availableProviders.size <= MAXIMUM_PROVIDERS_ALLOWED) { "Maximum provider number reached" }
    }

    companion object {
        private const val MAXIMUM_PROVIDERS_ALLOWED = 10
    }
}