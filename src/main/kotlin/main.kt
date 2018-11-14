import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            jackson {
                dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            }
        }

        routing {
            post("/") {
                val request = call.receive<HashMap<String, String>>()
                call.respond(hashMapOf(
                    "id" to request["id"]!!,
                    "first_name" to "${request["first_name"]} ${request["first_name"]!!.md5()}",
                    "last_name" to "${request["last_name"]} ${request["last_name"]!!.md5()}",
                    "current_time" to Date(),
                    "say" to "Kotlin!"
                ))
            }
        }
    }
    server.start(wait = true)
}

val md = MessageDigest.getInstance("MD5")!!
fun String.md5() = BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')