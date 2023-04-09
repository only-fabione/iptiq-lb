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
}