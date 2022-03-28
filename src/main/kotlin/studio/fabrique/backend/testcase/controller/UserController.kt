package studio.fabrique.backend.testcase.controller

import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import studio.fabrique.backend.testcase.configuration.security.JwtIssuer
import studio.fabrique.backend.testcase.model.repository.UserRepository
import studio.fabrique.backend.testcase.model.user.TokenResponse
import studio.fabrique.backend.testcase.model.user.User

@RestController
class UserController(
    private val userRepository: UserRepository,
    private val jwtIssuer: JwtIssuer,
) {

    @PostMapping("/login")
    fun getToken(@RequestBody user: User): ResponseEntity<TokenResponse> {
        val authenticatedUser = userRepository.findByUsernameAndPassword(user.username, user.password)
            .orElseThrow { BadCredentialsException("Wrong username(${user.username}) or password.") }
        val token = jwtIssuer.getJwtFor(authenticatedUser)
        return ResponseEntity(TokenResponse(token), OK)
    }
}