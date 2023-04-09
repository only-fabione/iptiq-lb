import java.util.*

class Provider {
    private val ID = UUID.randomUUID().toString()

    fun get(): String = ID
}