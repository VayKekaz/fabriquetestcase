package studio.fabrique.backend.testcase.model.user

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenResponse(
    @JsonProperty("token") val token: String,
)
