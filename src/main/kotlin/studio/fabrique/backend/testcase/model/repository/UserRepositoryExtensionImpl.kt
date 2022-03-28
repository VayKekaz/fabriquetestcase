package studio.fabrique.backend.testcase.model.repository

import org.springframework.stereotype.Repository
import studio.fabrique.backend.testcase.createQuery
import studio.fabrique.backend.testcase.model.user.User
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class UserRepositoryExtensionImpl(
    @PersistenceContext private val entityManager: EntityManager,
) : UserRepositoryExtension {

    override fun loadUserByUsername(username: String): User = entityManager.createQuery<User>(
        "SELECT user FROM User user WHERE user.username = :username"
    ).setParameter("username", username)
        .singleResult
}