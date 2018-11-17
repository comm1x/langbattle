import com.fasterxml.jackson.databind.ObjectMapper
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant
import java.time.format.DateTimeFormatter
import io.undertow.server.HttpServerExchange
import io.undertow.Undertow

fun main(args: Array<String>) = Undertow.builder()
    .addHttpListener(8080, "0.0.0.0")
    .setHandler {
        it.requestReceiver.receiveFullString { exchange: HttpServerExchange, json: String ->
            val jackson = ObjectMapper()
            val tree = jackson.readTree(json)
            val fn = tree["first_name"].asText()
            val ln = tree["last_name"].asText()
            val responseObj = hashMapOf(
                "id" to tree["id"].asText(),
                "first_name" to "$fn ${fn.md5()}",
                "last_name" to "$ln ${ln.md5()}",
                "current_time" to DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                "say" to "Kotlin!"
            )
            val responseBody = jackson.writeValueAsString(responseObj)
            exchange.responseSender.send(responseBody)
        }
    }
    .build()
    .start()

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}