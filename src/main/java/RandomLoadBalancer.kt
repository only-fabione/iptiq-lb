class RandomLoadBalancer(availableProviders: List<Provider>) : LoadBalancer(availableProviders) {

    override fun get() = availableProviders.random().get()
}