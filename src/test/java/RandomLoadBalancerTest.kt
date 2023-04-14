import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertNotNull

internal class RandomLoadBalancerTest {
    @Test
    fun checkSelectRandomProvider() {
        val firstMockedProvider: Provider = mockk()
        val secondMockedProvider: Provider = mockk()
        val randomLoadBalancer = RandomLoadBalancer(mutableListOf(firstMockedProvider, secondMockedProvider))

        every { firstMockedProvider.get() } returns UUID.randomUUID().toString()
        every { secondMockedProvider.get() } returns UUID.randomUUID().toString()

        val providerId = randomLoadBalancer.get()

        assertNotNull(providerId)
    }
}