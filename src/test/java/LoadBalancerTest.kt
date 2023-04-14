import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

internal class LoadBalancerTest {

    internal class LoadBalancerConnectionTest {
        @Test
        fun checkSuccessfullyRegisterNewProvider() {
            val randomLoadBalancer = RandomLoadBalancer(mutableListOf(Provider()))

            assertEquals(randomLoadBalancer.availableProviders.size, 1)
        }

        @Test
        fun checkLoadBalancerRefuseMoreThanAllowedConnections() {
            assertThrows<IllegalArgumentException>("Maximum provider number reached") {
                RandomLoadBalancer(MutableList(11) { Provider() })
            }
        }

        @Test
        fun checkLoadBalancerRefuseNoConnection() {
            assertThrows<IllegalArgumentException>("Failed precondition: the load balancer must have at least one provider to distribute requests") {
                RandomLoadBalancer(mutableListOf())
            }
        }
    }

    internal class LoadBalancerRegistrationTest {
        @Test
        fun checkSuccessfullyExcludeNewProvider() {
            val provider = Provider()
            val randomLoadBalancer = RandomLoadBalancer(mutableListOf(provider))

            randomLoadBalancer.exclude(provider)

            assertTrue(randomLoadBalancer.availableProviders.isEmpty())
        }

        @Test
        fun checkNotExcludeNotExistingProvider() {
            val randomLoadBalancer = RandomLoadBalancer(mutableListOf(Provider()))

            val anotherProvider = Provider()
            randomLoadBalancer.exclude(anotherProvider)

            assertEquals(randomLoadBalancer.availableProviders.size, 1)
        }

        @Test
        fun checkSuccessfullyIncludeNewProvider() {
            val provider = Provider()
            val randomLoadBalancer = RandomLoadBalancer(mutableListOf(provider))

            val anotherProvider = Provider()
            randomLoadBalancer.include(anotherProvider)

            assertEquals(randomLoadBalancer.availableProviders.size, 2)
        }
    }
}