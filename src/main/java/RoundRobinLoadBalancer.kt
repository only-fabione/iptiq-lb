class RoundRobinLoadBalancer(availableProviders: MutableSet<Provider>) : LoadBalancer(availableProviders) {
    private var availableProvidersIterator: Iterator<Provider> = availableProviders.iterator()

    override fun get(): String {
        if (!availableProvidersIterator.hasNext()) availableProvidersIterator = availableProviders.iterator()
        return availableProvidersIterator.next().get()
    }
}