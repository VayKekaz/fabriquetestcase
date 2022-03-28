package studio.fabrique.backend.testcase.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import studio.fabrique.backend.testcase.logger
import studio.fabrique.backend.testcase.model.CompletedSurvey
import studio.fabrique.backend.testcase.model.Survey
import studio.fabrique.backend.testcase.model.SurveyAnswer
import studio.fabrique.backend.testcase.model.exception.SurveyEndedException
import studio.fabrique.backend.testcase.model.repository.CompletedSurveyRepository
import studio.fabrique.backend.testcase.model.repository.SurveyRepository
import studio.fabrique.backend.testcase.model.user.Authority.Companion.COMPLETE_SURVEYS_AUTHORITY
import studio.fabrique.backend.testcase.model.user.Authority.Companion.EDIT_SURVEYS_AUTHORITY
import studio.fabrique.backend.testcase.model.user.Authority.Companion.WATCH_COMPLETED_SURVEYS_AUTHORITY
import studio.fabrique.backend.testcase.model.user.Authority.Companion.WATCH_SURVEYS_AUTHORITY
import javax.persistence.EntityNotFoundException

@RestController
@RequestMapping("/surveys")
class SurveyController(
    private val surveyRepository: SurveyRepository,
    private val completedSurveyRepository: CompletedSurveyRepository,
) {

    private val logger = this.logger()

    @PreAuthorize("hasAuthority('$WATCH_SURVEYS_AUTHORITY')")
    @GetMapping fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
    ): Page<Survey> {
        return surveyRepository.findAll(PageRequest.of(pageNumber, pageSize))
    }

    @PreAuthorize("hasAuthority('$WATCH_SURVEYS_AUTHORITY')")
    @GetMapping("/{id}") fun getById(@PathVariable id: Int): ResponseEntity<Survey> {
        val survey = surveyRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Survey with id=$id does not exists.") }
        return ResponseEntity(survey, OK)
    }

    @PreAuthorize("hasAuthority('$EDIT_SURVEYS_AUTHORITY')")
    @PostMapping fun createNew(@RequestBody survey: Survey): ResponseEntity<Survey> {
        val createdSurvey = surveyRepository.save(survey)
        return ResponseEntity(createdSurvey, CREATED)
    }

    @PreAuthorize("hasAuthority('$WATCH_COMPLETED_SURVEYS_AUTHORITY')")
    @GetMapping("/completions") fun getCompletions(
        @RequestParam(required = false) surveyId: Int?,
        @RequestParam(required = false) userId: Int?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
    ): ResponseEntity<Page<CompletedSurvey>> {
        val pageRequest = PageRequest.of(pageNumber, pageSize)
        return ResponseEntity(
            if (userId != null && surveyId != null)
                completedSurveyRepository.findAllByUserIdAndSurveyId(userId, surveyId, pageRequest)
            else if (userId != null)
                completedSurveyRepository.findAllByUserId(userId, pageRequest)
            else if (surveyId != null)
                completedSurveyRepository.findAllBySurveyId(surveyId, pageRequest)
            else
                completedSurveyRepository.findAll(pageRequest),
            OK
        )
    }

    @PreAuthorize("hasAuthority('$COMPLETE_SURVEYS_AUTHORITY')")
    @PostMapping("/completions/{id}") fun complete(
        @PathVariable id: Int,
        @RequestParam(required = true) userId: Int,
        @RequestBody answers: MutableList<SurveyAnswer>,
    ): ResponseEntity<CompletedSurvey> {
        val survey = surveyRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Survey with id=$id does not exists.") }
        if (survey.ended)
            throw SurveyEndedException(survey)
        var completedSurvey = CompletedSurvey(
            userId = userId,
            survey = survey,
            answers = answers,
        )
        completedSurvey = completedSurveyRepository.save(completedSurvey)
        return ResponseEntity(completedSurvey, OK)
    }

    @PreAuthorize("hasAuthority('$EDIT_SURVEYS_AUTHORITY')")
    @PutMapping("/{id}") fun edit(
        @PathVariable id: Int,
        @RequestBody survey: Survey,
    ): ResponseEntity<Survey> {
        surveyRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Survey(id=$id) does not exist. Use POST method.") }
        val updatedSurvey = surveyRepository.save(survey.apply { this.id = id })
        return ResponseEntity(updatedSurvey, OK)
    }

}