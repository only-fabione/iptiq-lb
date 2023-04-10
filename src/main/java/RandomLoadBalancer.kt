class RandomLoadBalancer(val availableProviders: MutableList<Provider> = mutableListOf()) : ProviderIdRetriever,
    ProviderRegistration {

    companion object {
        private const val MAXIMUM_PROVIDERS_ALLOWED = 10
    }

    override fun get() = availableProviders.random().get()

    override fun register(providers: List<Provider>) {
        if (availableProviders.size + providers.size > MAXIMUM_PROVIDERS_ALLOWED)
            throw MaximumProviderNumberReached()

        availableProviders.addAll(providers)
    }
}