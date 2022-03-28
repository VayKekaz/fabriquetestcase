package studio.fabrique.backend.testcase.configuration.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import studio.fabrique.backend.testcase.logger
import studio.fabrique.backend.testcase.model.user.User
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtIssuer {

    companion object {
        const val AUTHORIZATION_HEADER_PREFIX = "Bearer "
    }

    private val jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val jwtIssuer = "backend.fabrique.studio"

    private val logger = this.logger()

    fun getJwtFor(user: User): String {
        return Jwts.builder()
            .setClaims(mapOf(
                "userId" to user.id,
                "username" to user.username
            ))
            .setIssuer(jwtIssuer)
            .setIssuedAt(Date())
            .signWith(jwtSecret)
            .compact()
    }

    fun getUserIdOf(token: String): Int {
        val claims = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).body
        return claims["userId"] as Int
    }
}