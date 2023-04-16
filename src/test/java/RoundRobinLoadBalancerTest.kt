import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RoundRobinLoadBalancerTest {

    @Test
    fun checkSelectProvidersSequentially() {
        val firstProvider = Provider()
        val secondProvider = Provider()

        val loadBalancer = RoundRobinLoadBalancer(mutableSetOf(firstProvider, secondProvider))

        assertEquals(loadBalancer.get(), firstProvider.get())
        assertEquals(loadBalancer.get(), secondProvider.get())
        assertEquals(loadBalancer.get(), firstProvider.get())
        assertEquals(loadBalancer.get(), secondProvider.get())
    }
}