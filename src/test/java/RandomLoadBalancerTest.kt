import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class RandomLoadBalancerTest {

    internal class RegisterProviderTest {

        @Test
        fun checkSuccessfullyRegisterNewProvider() {
            val randomLoadBalancer = RandomLoadBalancer()

            randomLoadBalancer.register(listOf(Provider()))

            assertEquals(randomLoadBalancer.availableProviders.size, 1)
        }

        @Test
        fun checkLoadBalancerRefuseMoreThanAllowedConnections() {
            val randomLoadBalancer = RandomLoadBalancer()

            assertThrows<MaximumProviderNumberReached> {
                randomLoadBalancer.register(MutableList(11) { Provider() })
            }
        }
    }

    internal class RetrieveProviderTest {

        @Test
        fun checkSelectRandomProvider() {
            val randomLoadBalancer = RandomLoadBalancer()
            val mockedProvider: Provider = mockk()

            randomLoadBalancer.register(listOf(mockedProvider))

            val id = UUID.randomUUID().toString()
            every { mockedProvider.get() } returns id

            val providerId = randomLoadBalancer.get()

            assertEquals(providerId, id)
            verify { mockedProvider.get() }
        }
    }
}