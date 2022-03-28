package studio.fabrique.backend.testcase.model

import javax.persistence.DiscriminatorValue
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER

@Entity
@DiscriminatorValue("CHOOSE_MULTIPLE")
class QuestionWithMultipleChosenAnswers(
    id: Int,
    theQuestion: String,
    @ElementCollection
    var options: List<String>,
) : Question(
    id,
    theQuestion,
)
