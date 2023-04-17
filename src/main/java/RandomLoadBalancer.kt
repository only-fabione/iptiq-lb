class RandomLoadBalancer(availableProviders: MutableSet<Provider>) : LoadBalancer(availableProviders) {

    override fun getProviderId() = availableProviders.random().get()
}