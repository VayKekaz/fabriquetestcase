package studio.fabrique.backend.testcase.model.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import studio.fabrique.backend.testcase.model.user.User
import java.util.*

@Repository
interface UserRepository : CrudRepository<User, Int>, UserRepositoryExtension {

    fun findUserByUsername(username: String): Optional<User>

    fun findByUsernameAndPassword(username: String, password: String): Optional<User>
}