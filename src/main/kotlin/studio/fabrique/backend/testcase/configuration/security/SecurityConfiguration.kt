package studio.fabrique.backend.testcase.configuration.security

import org.springframework.context.annotation.AdviceMode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import studio.fabrique.backend.testcase.model.repository.UserRepository
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration(
    private val jwtAuthorizationFilter: JwtAuthorizationFilter,
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.sessionManagement().sessionCreationPolicy(STATELESS)
        // H2 console throws ERR_BLOCKED_BY_RESPONSE,
        http.headers().frameOptions().sameOrigin()
        http.csrf().disable()
        // Cors policy, it uses @Bean(name = "corsConfigurationSource") as config source.
        http.cors(Customizer.withDefaults())
        // Disable any endpoint security, controller methods are secured with @PreAuthorize
        http.authorizeRequests().anyRequest().permitAll()
        // disables default /login page
        http.formLogin().disable()
        // No redirect.
        http.exceptionHandling().authenticationEntryPoint(invalidAccessRequestHandler())
        // JWT Authentication & Authorization
        http.addFilterBefore(
            jwtAuthorizationFilter,
            UsernamePasswordAuthenticationFilter::class.java
        )
    }

    @Bean fun invalidAccessRequestHandler() = AuthenticationEntryPoint(fun(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth is AnonymousAuthenticationToken)
            response.sendError(401, authException.message)
        else
            response.sendError(403, authException.message)
    })

    // Name here is critical
    @Bean(name = ["corsConfigurationSource"]) fun corsConfig() = UrlBasedCorsConfigurationSource().apply {
        registerCorsConfiguration(
            "/**",
            CorsConfiguration().apply {
                allowedOrigins = listOf("*")
                allowCredentials = true
                addAllowedHeader(CorsConfiguration.ALL)
                addAllowedMethod("GET")
                addAllowedMethod("POST")
                addAllowedMethod("PUT")
                addAllowedMethod("DELETE")
                addAllowedMethod("OPTIONS")
            }
        )
    }

    @Bean fun passwordEncoder() = BCryptPasswordEncoder()
}