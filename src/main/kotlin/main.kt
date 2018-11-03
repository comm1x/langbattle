import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            jackson {
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            }
        }

        install(StatusPages) {
            exception<JsonProcessingException> { call.respond(HttpStatusCode.BadRequest) }
        }

        routing {
            post("/") {
                val request = call.receive<Request>()
                call.respond(Response(request))
            }
        }
    }
    server.start(wait = true)
}

data class Request(
    val id: String,
    val firstName: String,
    val lastName: String
)

data class Response(
    val id: String,
    val firstName: String,
    val lastName: String,
    val currentTime: Long,
    val say: String
) {
    constructor(request: Request): this(
        id = request.id,
        firstName = "${request.firstName} ${request.firstName.md5()}",
        lastName = "${request.lastName} ${request.lastName.md5()}",
        currentTime = Date().time,
        say = "Kotlin is the best!"
    )
}

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}