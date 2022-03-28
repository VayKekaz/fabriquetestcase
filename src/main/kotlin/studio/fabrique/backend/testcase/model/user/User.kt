package studio.fabrique.backend.testcase.model.user

import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*
import javax.persistence.FetchType.EAGER

@Entity
class User(
    @Id @GeneratedValue
    var id: Int = -1,
    private var username: String,
    private var password: String,

    @ElementCollection(fetch = EAGER)
    @Enumerated(EnumType.STRING)
    var roles: MutableSet<Role> = mutableSetOf(),
) : UserDetails {

    override fun getUsername() = username
    override fun getPassword() = password

    override fun getAuthorities(): Set<Authority> =
        roles.flatMap(Role::authorities).toSet()

    // unused
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}