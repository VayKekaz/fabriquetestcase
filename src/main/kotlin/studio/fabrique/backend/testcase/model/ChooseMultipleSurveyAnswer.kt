package studio.fabrique.backend.testcase.model

import com.fasterxml.jackson.annotation.JsonValue
import javax.persistence.ElementCollection
import javax.persistence.Entity

@Entity
class ChooseMultipleSurveyAnswer(
    @ElementCollection
    @JsonValue var answer: List<Int>,
    id: Int = -1,
) : SurveyAnswer(
    id,
)