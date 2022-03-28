package studio.fabrique.backend.testcase.api

import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import studio.fabrique.backend.testcase.configuration.jackson.toJson
import studio.fabrique.backend.testcase.model.user.TokenResponse
import studio.fabrique.backend.testcase.model.user.User

class UserApi(private val mvc: MockMvc) {

    fun login(username: String, password: String): String =
        mvc.post("/login") {
            contentType = APPLICATION_JSON
            content = mapOf(
                "username" to username,
                "password" to password,
            ).toJson()
        }.andDo { print() }
            .andReturnOfType<TokenResponse>().token
}