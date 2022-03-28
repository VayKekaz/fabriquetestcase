package studio.fabrique.backend.testcase.api

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import studio.fabrique.backend.testcase.configuration.jackson.toJson
import studio.fabrique.backend.testcase.model.Survey
import studio.fabrique.backend.testcase.model.SurveyAnswer

class SurveysApi(mvc: MockMvc, bearerToken: String?) : CrudApi<Survey>(mvc, bearerToken) {
    override val url: String = "/surveys"

    fun getCompletions(id: Int? = null): ResultActionsDsl =
        mvc.get("$url/completions") {
            if (id != null)
                param("userId", id.toString())
        }.andDo { print() }

    fun postCompletion(surveyId: Int, userId: Int, answers: List<SurveyAnswer>) =
        mvc.post("$url/completions/$surveyId") {
            param("userId", userId.toString())
            this.contentType = MediaType.APPLICATION_JSON
            this.content = answers.toJson()
            jwt?.let { this.authorization = jwt }
        }.andDo { print() }
}