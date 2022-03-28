package studio.fabrique.backend.testcase.model

import com.fasterxml.jackson.annotation.JsonValue
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class ChooseOneSurveyAnswer(
    @Column(name = "answerNumber")
    @JsonValue var answer: Int,
    id: Int = -1,
) : SurveyAnswer(
    id,
)