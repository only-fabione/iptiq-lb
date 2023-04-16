import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

internal class ProviderTest {

    @Test
    fun checkRetrieveValidIdentifier() {
        val provider = Provider()
        val providerIdentifier = provider.get()

        assertNotNull(providerIdentifier)
    }

    @Test
    fun checkRetrieveSameIdentifierForSameInstance() {
        val provider = Provider()

        assertSame(provider, provider)
        assertEquals(provider.get(), provider.get())
    }

    @Test
    fun checkRetrieveDifferentIdentifiers() {
        val firstProvider = Provider()
        val secondProvider = Provider()

        assertNotEquals(firstProvider.get(), secondProvider.get())
    }

    @Test
    fun checkDefaultActiveProviderStatus() {
        val provider = Provider()

        assertEquals(provider.check(), HeartBeatStatus.ACTIVE)
    }

    @Test
    fun checkRetrieveProviderStatus() {
        val status = HeartBeatStatus.DISABLED
        val provider = Provider(status = status)

        assertEquals(provider.check(), status)
    }
}