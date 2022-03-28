package studio.fabrique.backend.testcase

import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import studio.fabrique.backend.testcase.configuration.jackson.toObject
import studio.fabrique.backend.testcase.configuration.jackson.toPrettyJson
import studio.fabrique.backend.testcase.model.Survey
import studio.fabrique.backend.testcase.model.repository.SurveyRepository
import studio.fabrique.backend.testcase.model.repository.UserRepository
import studio.fabrique.backend.testcase.model.user.Role
import studio.fabrique.backend.testcase.model.user.User

@Component
@Profile("mockDb")
class MockDb(
    private val userRepository: UserRepository,
    private val surveyRepository: SurveyRepository,
) {

    companion object {
        const val ADMIN_USERNAME = "adminUser11"
        const val ADMIN_PASSWORD = "adminPass"
        const val BASIC_USER_USERNAME = "basicUser11"
        const val BASIC_USER_PASSWORD = "userPass"
    }

    private val logger = logger()

    @EventListener(ContextRefreshedEvent::class) fun loadUsers() {
        var adminUser = User(
            username = ADMIN_USERNAME,
            password = ADMIN_PASSWORD,
            roles = mutableSetOf(Role.ADMIN)
        )
        adminUser = userRepository.save(adminUser)
        logger.info("Created ${adminUser.toPrettyJson()}.")
    }

    @EventListener(ContextRefreshedEvent::class) fun loadSurveys() {
        val surveyJson = """{
  "title" : "do you hate niggers",
  "startsOn":"2022-01-01T00:00:00Z",
  "questions" : [ {
    "type" : "CHOOSE_ONE",
    "theQuestion" : "do you love black color?",
    "options" : [ "yes", "no" ]
  }, {
    "type" : "CHOOSE_ONE",
    "theQuestion" : "do you hate black people?",
    "options" : [ "yes", "no" ]
  }, {
    "type" : "WRITE_ANSWER",
    "theQuestion" : "describe how you hate niggers"
  }, {
    "type" : "CHOOSE_MULTIPLE",
    "theQuestion" : "choose reasons to hate niggers",
    "options" : [ "they're black", "they smell like pigs", "they smell like coal", "they are stupid" ]
  } ]
}"""
        var survey = surveyJson.toObject<Survey>()
        survey = surveyRepository.save(survey)
        logger.info("Created ${survey.toPrettyJson()}.")
    }

}