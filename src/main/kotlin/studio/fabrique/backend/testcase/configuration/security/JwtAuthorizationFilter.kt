package studio.fabrique.backend.testcase.configuration.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.filter.OncePerRequestFilter
import studio.fabrique.backend.testcase.configuration.security.JwtIssuer.Companion.AUTHORIZATION_HEADER_PREFIX
import studio.fabrique.backend.testcase.model.repository.UserRepository
import studio.fabrique.backend.testcase.model.user.Role
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Transactional(readOnly = true)
@Component
class JwtAuthorizationFilter(
    private val jwtIssuer: JwtIssuer,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        SecurityContextHolder.getContext().authentication = try {
            request.getAuthentication()
        } catch (exception: AuthenticationException) {
            anonymousAuthentication
        }
        chain.doFilter(request, response)
    }

    private fun HttpServletRequest.getAuthentication(): Authentication {
        // Get authorization header and validate
        val token = (this.getHeader(HttpHeaders.AUTHORIZATION) ?: "").substringAfter(AUTHORIZATION_HEADER_PREFIX)
        if (token.isBlank())
            throw AuthenticationCredentialsNotFoundException("Token not found.")
        val user = userRepository.findById(jwtIssuer.getUserIdOf(token))
            .orElseThrow { BadCredentialsException("Token $token is invalid.") }
        return UsernamePasswordAuthenticationToken(
            user,
            token,
            user.authorities
        )
    }

    private val anonymousAuthentication: AnonymousAuthenticationToken
        get() = AnonymousAuthenticationToken("anonymous", "anonymous", Role.ANONYMOUS.authorities)
}