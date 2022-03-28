package studio.fabrique.backend.testcase.api

import org.springframework.test.web.servlet.MockMvc

class ServerApi(
    mvc: MockMvc,
    bearerToken: String? = null,
) {
    var jwt = bearerToken
        set(value) {
            field = value
            surveys.jwt = value
        }

    val surveys: SurveysApi by lazy { SurveysApi(mvc, bearerToken) }
    val users: UserApi by lazy { UserApi(mvc) }
}