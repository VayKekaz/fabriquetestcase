package studio.fabrique.backend.testcase.configuration.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.addDeserializer
import com.fasterxml.jackson.module.kotlin.addSerializer
import io.jsonwebtoken.io.DeserializationException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import studio.fabrique.backend.testcase.configuration.jackson.Jackson.Companion.jacksonMapper
import studio.fabrique.backend.testcase.model.ChooseMultipleSurveyAnswer
import studio.fabrique.backend.testcase.model.ChooseOneSurveyAnswer
import studio.fabrique.backend.testcase.model.SurveyAnswer
import studio.fabrique.backend.testcase.model.TextSurveyAnswer
import java.time.Instant
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit.SECONDS


@Configuration class Jackson {
    companion object {
        @get:Primary @get:Bean
        val jacksonMapper = ObjectMapper()

        init {
            jacksonMapper.registerModules(
                JavaTimeModule(),
                SimpleModule().addSerializer(
                    Instant::class,
                    object : JsonSerializer<Instant>() {
                        override fun serialize(
                            obj: Instant,
                            jsonGenerator: JsonGenerator,
                            sp: SerializerProvider,
                        ) {
                            jsonGenerator.writeString(
                                DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(UTC)
                                    .format(obj.truncatedTo(SECONDS))
                            )
                        }
                    }
                ),
                SimpleModule().addDeserializer(
                    SurveyAnswer::class,
                    object : JsonDeserializer<SurveyAnswer>() {
                        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): SurveyAnswer =
                            when (val value = p.codec.readValue(p, Any::class.java)) {
                                is Int -> ChooseOneSurveyAnswer(value)
                                is String -> TextSurveyAnswer(value)
                                is List<*> ->
                                    if (value.isNotEmpty() && value.first() is Int)
                                        ChooseMultipleSurveyAnswer(value as List<Int>)
                                    else
                                        throw DeserializationException("$value is empty or contains elements other than Ints.")
                                else -> throw DeserializationException("${value::class} can't be converted to ${SurveyAnswer::class}")
                            }
                    }
                ),
                SpringDataDomainModule,
            )
        }
    }
}

fun Any?.toJson(): String =
    jacksonMapper.writeValueAsString(this)

fun Any?.toPrettyJson(): String =
    jacksonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

inline fun <reified ExpectedType> String.toObject(): ExpectedType =
    jacksonMapper.readValue(this)

inline fun <reified ExpectedType> ObjectMapper.readValue(json: String): ExpectedType =
    this.readValue(json, object : TypeReference<ExpectedType>() {})

infix fun Any?.equalsJson(other: Any?): Boolean =
    this.toJson() == other.toJson()
