import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LoadBalancerTest {

    @Test
    fun checkSuccessfullyRegisterNewProvider() {
        val loadBalancer = LoadBalancer()

        loadBalancer.register(listOf(Provider()))

        assertEquals(loadBalancer.availableProviders.size, 1)
    }

    @Test
    fun checkLoadBalancerRefuseMoreThanAllowedConnections() {
        val loadBalancer = LoadBalancer()

        assertThrows<MaximumProviderNumberReached> {
            loadBalancer.register(MutableList(11) { Provider() })
        }
    }
}