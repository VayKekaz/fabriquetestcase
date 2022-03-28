package studio.fabrique.backend.testcase.model.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import studio.fabrique.backend.testcase.model.Survey

@Repository
interface SurveyRepository : CrudRepository<Survey, Int> {
    fun findAll(pageable: Pageable): Page<Survey>
}
