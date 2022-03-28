package studio.fabrique.backend.testcase.model

import com.fasterxml.jackson.annotation.JsonValue
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class TextSurveyAnswer(
    @Column(name = "answerText")
    @JsonValue var answer: String,
    id: Int = -1,
) : SurveyAnswer(
    id,
)
