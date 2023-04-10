class LoadBalancer(val availableProviders: MutableList<Provider> = mutableListOf()) {
    companion object {
        private const val MAXIMUM_PROVIDERS_ALLOWED = 10
    }

    fun get() {}

    fun register(providers: List<Provider>) {
        if(availableProviders.size + providers.size > MAXIMUM_PROVIDERS_ALLOWED)
            throw MaximumProviderNumberReached()

        availableProviders.addAll(providers)
    }
}