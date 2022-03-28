package studio.fabrique.backend.testcase

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
    exclude = [
        JacksonAutoConfiguration::class,
    ],
)
class FabriquetestcaseApplication

fun main(args: Array<String>) {
    runApplication<FabriquetestcaseApplication>(*args)
}
