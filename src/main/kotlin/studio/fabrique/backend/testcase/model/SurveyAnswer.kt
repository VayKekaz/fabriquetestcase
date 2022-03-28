package studio.fabrique.backend.testcase.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType.SINGLE_TABLE

@Entity
@Inheritance(strategy = SINGLE_TABLE)
abstract class SurveyAnswer(
    @Id @GeneratedValue
    var id: Int = -1,
)
