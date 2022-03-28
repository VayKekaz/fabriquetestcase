package studio.fabrique.backend.testcase

import org.junit.jupiter.api.Test
import studio.fabrique.backend.testcase.configuration.jackson.toJson
import studio.fabrique.backend.testcase.configuration.jackson.toObject
import studio.fabrique.backend.testcase.model.Survey
import java.time.Instant

class JacksonTest {

    val logger = logger()

    @Test fun `instant test`() {
        val inputFormattedInstant = """
            "2022-03-28T03:13:45.0950419Z"
        """.trimIndent()
        val expectedOutput = """
            "2022-03-28T03:13:45Z"
        """.trimIndent()
        val instant = inputFormattedInstant.toObject<Instant>()
        assert(expectedOutput == instant.toJson()) {
            "$expectedOutput != ${instant.toJson()}"
        }
    }

    @Test fun `survey test`() {
        val surveyJson = """
            {"id":7,"title":"new test survey","startsOn":"2022-03-28T02:53:38Z","closesOn":null,"questions":[{"type":"CHOOSE_ONE","theQuestion":"choose one test","options":["yes","no"]}]}
        """.trimIndent()
        val survey = surveyJson.toObject<Survey>()
        assert(surveyJson == survey.toJson()) {
            "$surveyJson\n!=\n${survey.toJson()}"
        }
    }
}