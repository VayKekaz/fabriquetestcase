package studio.fabrique.backend.testcase.model

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("WRITE_ANSWER")
class QuestionWIthTextAnswer(
    id: Int,
    theQuestion: String,
) : Question(
    id,
    theQuestion,
)