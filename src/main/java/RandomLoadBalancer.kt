class RandomLoadBalancer(availableProviders: MutableList<Provider>) : LoadBalancer(availableProviders) {

    override fun get() = availableProviders.random().get()
}