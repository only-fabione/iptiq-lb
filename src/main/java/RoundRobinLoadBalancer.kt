class RoundRobinLoadBalancer(availableProviders: List<Provider>) : LoadBalancer(availableProviders) {
    private var availableProvidersIterator: ListIterator<Provider> = availableProviders.listIterator()

    override fun get(): String {
        if (!availableProvidersIterator.hasNext()) availableProvidersIterator = availableProviders.listIterator()
        return availableProvidersIterator.next().get()
    }
}