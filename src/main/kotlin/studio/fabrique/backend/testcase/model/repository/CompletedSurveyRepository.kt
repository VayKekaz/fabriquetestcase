package studio.fabrique.backend.testcase.model.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import studio.fabrique.backend.testcase.model.CompletedSurvey

interface CompletedSurveyRepository : CrudRepository<CompletedSurvey, Int> {

    fun findAllByUserId(userId: Int, pageable: Pageable): Page<CompletedSurvey>

    fun findAll(pageable: Pageable): Page<CompletedSurvey>

    fun findAllBySurveyId(surveyId: Int, pageable: Pageable): Page<CompletedSurvey>

    fun findAllByUserIdAndSurveyId(userId: Int, surveyId: Int, pageable: Pageable): Page<CompletedSurvey>
}
