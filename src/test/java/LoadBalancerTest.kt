import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Thread.sleep

internal class LoadBalancerTest {

    internal class LoadBalancerConnectionTest {
        @Test
        fun checkSuccessfullyRegisterNewProvider() {
            val loadBalancer = RandomLoadBalancer(mutableSetOf(Provider()))

            assertEquals(loadBalancer.availableProviders.size, 1)
        }

        @Test
        fun checkLoadBalancerRefuseMoreThanAllowedConnections() {
            assertThrows<IllegalArgumentException>("Maximum provider number reached") {
                RandomLoadBalancer(List(11) { Provider() }.toMutableSet())
            }
        }

        @Test
        fun checkLoadBalancerRefuseNoConnection() {
            assertThrows<IllegalArgumentException>("Failed precondition: the load balancer must have at least one provider to distribute requests") {
                RandomLoadBalancer(mutableSetOf())
            }
        }
    }

    internal class LoadBalancerRegistrationTest {
        @Test
        fun checkSuccessfullyExcludeNewProvider() {
            val provider = Provider()
            val loadBalancer = RandomLoadBalancer(mutableSetOf(provider))

            loadBalancer.exclude(provider)

            assertTrue(loadBalancer.availableProviders.isEmpty())
        }

        @Test
        fun checkNotExcludeNotExistingProvider() {
            val loadBalancer = RandomLoadBalancer(mutableSetOf(Provider()))

            val anotherProvider = Provider()
            loadBalancer.exclude(anotherProvider)

            assertEquals(loadBalancer.availableProviders.size, 1)
        }

        @Test
        fun checkSuccessfullyIncludeNewProvider() {
            val provider = Provider()
            val loadBalancer = RandomLoadBalancer(mutableSetOf(provider))

            val anotherProvider = Provider()
            loadBalancer.include(anotherProvider)

            assertEquals(loadBalancer.availableProviders.size, 2)
        }
    }

    internal class CheckProviderStatusTest {
        @Test
        fun checkSuccessfullyDisableNotActiveProvider() {
            val activeProvider: Provider = mockk()
            val disabledProvider: Provider = mockk()
            val anotherDisabledProvider: Provider = mockk()

            val loadBalancer = RandomLoadBalancer(mutableSetOf(activeProvider, disabledProvider, anotherDisabledProvider))

            every { activeProvider.check() } returns HeartBeatStatus.ACTIVE
            every { disabledProvider.check() } returns HeartBeatStatus.DISABLED
            every { anotherDisabledProvider.check() } returns HeartBeatStatus.DISABLED

            loadBalancer.checkProviderStatus()
            sleep(3000L)

            assertEquals(loadBalancer.availableProviders.size, 1)
            verify { activeProvider.check() }
            verify { disabledProvider.check() }
            verify { anotherDisabledProvider.check() }
        }


        @Test
        fun checkSuccessfullyReintroduceActiveProvider() {
            val activeProvider: Provider = mockk()
            val disabledProvider: Provider = mockk()
            val anotherDisabledProvider: Provider = mockk()

            val loadBalancer = RandomLoadBalancer(mutableSetOf(activeProvider, disabledProvider, anotherDisabledProvider))

            every { activeProvider.check() } returns HeartBeatStatus.ACTIVE
            every { disabledProvider.check() } returns HeartBeatStatus.DISABLED
            every { anotherDisabledProvider.check() } returns HeartBeatStatus.DISABLED

            loadBalancer.checkProviderStatus()
            sleep(3000L)

            assertEquals(loadBalancer.availableProviders.size, 1)
            verify { activeProvider.check() }
            verify { disabledProvider.check() }
            verify { anotherDisabledProvider.check() }

            every { activeProvider.check() } returns HeartBeatStatus.ACTIVE
            every { disabledProvider.check() } returns HeartBeatStatus.ACTIVE
            every { anotherDisabledProvider.check() } returns HeartBeatStatus.DISABLED

            loadBalancer.checkProviderStatus()
            sleep(3000L)

            assertEquals(loadBalancer.availableProviders.size, 1)
            verify { activeProvider.check() }
            verify { disabledProvider.check() }
            verify { anotherDisabledProvider.check() }

            every { activeProvider.check() } returns HeartBeatStatus.ACTIVE
            every { disabledProvider.check() } returns HeartBeatStatus.ACTIVE
            every { anotherDisabledProvider.check() } returns HeartBeatStatus.DISABLED

            loadBalancer.checkProviderStatus()
            sleep(3000L)

            assertEquals(loadBalancer.availableProviders.size, 2)
            verify { activeProvider.check() }
            verify { disabledProvider.check() }
            verify { anotherDisabledProvider.check() }
        }
    }

}