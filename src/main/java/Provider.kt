import java.util.*

class Provider(private val id: String = UUID.randomUUID().toString()) {

    fun get() = id
}