import java.util.*

class Provider(private val id: String = UUID.randomUUID().toString(),
               private val status: HeartBeatStatus = HeartBeatStatus.ACTIVE): HeartBeatChecker {

    fun get() = id
    override fun check() = status
}