package studio.fabrique.backend.testcase.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.persistence.*
import javax.persistence.InheritanceType.SINGLE_TABLE

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(
    name = "Type",
    discriminatorType = DiscriminatorType.STRING,
)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    Type(value = QuestionWIthTextAnswer::class, name = "WRITE_ANSWER"),
    Type(value = QuestionWithOneChosenAnswer::class, name = "CHOOSE_ONE"),
    Type(value = QuestionWithMultipleChosenAnswers::class, name = "CHOOSE_MULTIPLE"),
)
abstract class Question(
    @JsonIgnore
    @Id @GeneratedValue
    var id: Int,
    var theQuestion: String,
)
