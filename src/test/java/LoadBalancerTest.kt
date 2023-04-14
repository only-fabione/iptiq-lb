import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

internal class LoadBalancerTest {

    @Test
    fun checkSuccessfullyRegisterNewProvider() {
        val randomLoadBalancer = RandomLoadBalancer(listOf(Provider()))

        assertEquals(randomLoadBalancer.availableProviders.size, 1)
    }

    @Test
    fun checkLoadBalancerRefuseMoreThanAllowedConnections() {
        assertThrows<IllegalArgumentException>("Maximum provider number reached") {
            RandomLoadBalancer(List(11) { Provider() })
        }
    }

    @Test
    fun checkLoadBalancerRefuseNoConnection() {
        assertThrows<IllegalArgumentException>("Failed precondition: the load balancer must have at least one provider to distribute requests") {
            RandomLoadBalancer(emptyList())
        }
    }
}