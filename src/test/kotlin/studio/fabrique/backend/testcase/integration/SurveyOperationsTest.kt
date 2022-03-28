package studio.fabrique.backend.testcase.integration

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import studio.fabrique.backend.testcase.MockDb.Companion.ADMIN_PASSWORD
import studio.fabrique.backend.testcase.MockDb.Companion.ADMIN_USERNAME
import studio.fabrique.backend.testcase.api.ServerApi
import studio.fabrique.backend.testcase.api.andReturnOfType
import studio.fabrique.backend.testcase.configuration.jackson.equalsJson
import studio.fabrique.backend.testcase.configuration.jackson.toJson
import studio.fabrique.backend.testcase.configuration.jackson.toObject
import studio.fabrique.backend.testcase.configuration.jackson.toPrettyJson
import studio.fabrique.backend.testcase.logger
import studio.fabrique.backend.testcase.model.CompletedSurvey
import studio.fabrique.backend.testcase.model.Survey
import studio.fabrique.backend.testcase.randomAnswers
import studio.fabrique.backend.testcase.zipLines

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mockDb")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SurveyOperationsTest
constructor(@Autowired mockMvc: MockMvc) {

    private val logger = logger()

    private val api = ServerApi(mockMvc)

    private val userId = 1

    fun loginAsAdmin() {
        api.jwt = api.users.login(ADMIN_USERNAME, ADMIN_PASSWORD)
    }

    @Order(1)
    @Test fun `get surveys, should return one`() {
        api.surveys.get().andExpect {
            jsonPath("$.content") {
                isArray()
                isNotEmpty()
            }
        }
    }

    @Order(2)
    @Test fun `get completions, should be empty`() {
        api.surveys.getCompletions(userId).andExpect {
            jsonPath("$.content") {
                isArray()
                isEmpty()
            }
        }
    }

    @Order(3)
    @Test fun `post survey completion, should appear in db`() {
        val response = api.surveys.get().andReturnOfType<Page<Survey>>()
        val serverSurvey = response.content.first()
        val answersToPost = serverSurvey.randomAnswers()
        api.surveys.postCompletion(serverSurvey.id, userId, answersToPost)
        val completions = api.surveys.getCompletions(userId).andExpect {
            jsonPath("$.content") { isNotEmpty() }
            jsonPath("$.content[0].survey.id") { value(serverSurvey.id) }
            jsonPath("$.content[0].answers") { isArray(); isNotEmpty() }
        }.andReturnOfType<Page<CompletedSurvey>>()
        val completion = completions.content.first()
        assert(completion.answers equalsJson answersToPost) {
            completion.answers.toJson() zipLines answersToPost.toJson()
        }
    }

    @Order(4)
    @Test fun `create new survey, should appear in db`() {
        loginAsAdmin()
        val newSurvey = """{
  "title" : "new test survey",
  "startsOn":"2022-03-28T03:13:45Z",
  "questions" : [ {
    "type" : "CHOOSE_ONE",
    "theQuestion" : "choose one test",
    "options" : [ "yes", "no" ]
  } ]
}""".toObject<Survey>()
        val serverCreatedSurvey = api.surveys.post(newSurvey).andExpect {
            status { isCreated() }
            jsonPath("$.title") {
                value(newSurvey.title)
            }
        }.andReturnOfType<Survey>()
        newSurvey.id = serverCreatedSurvey.id
        val serverFetchSurvey = api.surveys.get(serverCreatedSurvey.id).andReturnOfType<Survey>()
        assert(newSurvey equalsJson serverFetchSurvey) {
            //"${newSurvey.toJson()}\n!=\n${serverFetchSurvey.toJson()}"
            newSurvey.toPrettyJson() zipLines serverFetchSurvey.toPrettyJson()
        }
    }

    @Order(5)
    @Test fun `add question, should change in db`() {
        loginAsAdmin()
        val title = "new test survey"
        val idToPut = api.surveys.get().andReturnOfType<Page<Survey>>().content.first { it.title == title }.id
        val newSurvey = """{
  "title" : "$title",
  "startsOn":"2022-03-28T03:13:45Z",
  "questions" : [ {
    "type" : "CHOOSE_ONE",
    "theQuestion" : "choose one test",
    "options" : [ "yes", "no" ]
  }, {
    "type" : "CHOOSE_MULTIPLE",
    "theQuestion" : "choose multiple test",
    "options" : [ "option one", "option two", "third option" ]
  } ]
}""".toObject<Survey>()
        api.surveys.put(idToPut, newSurvey).andExpect {
            status { isOk() }
        }.andReturnOfType<Survey>()
        newSurvey.id = idToPut
        val serverFetchSurvey = api.surveys.get(idToPut).andExpect {
            status { isOk() }
        }.andReturnOfType<Survey>()
        assert(newSurvey equalsJson serverFetchSurvey) {
            newSurvey.toPrettyJson() zipLines serverFetchSurvey.toPrettyJson()
        }
    }

}