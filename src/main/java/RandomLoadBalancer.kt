class RandomLoadBalancer(availableProviders: MutableSet<Provider>) : LoadBalancer(availableProviders) {

    override fun get() = availableProviders.random().get()
}