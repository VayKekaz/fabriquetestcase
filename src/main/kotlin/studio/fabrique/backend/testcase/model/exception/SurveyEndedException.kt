package studio.fabrique.backend.testcase.model.exception

import studio.fabrique.backend.testcase.configuration.jackson.toJson
import studio.fabrique.backend.testcase.model.Survey
import java.lang.RuntimeException

class SurveyEndedException(survey: Survey) : RuntimeException(
    "Survey(id=${survey.id}) is ended on ${survey.closesOn.toJson()}"
)