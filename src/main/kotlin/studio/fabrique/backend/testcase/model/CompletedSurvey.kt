package studio.fabrique.backend.testcase.model

import javax.persistence.*
import javax.persistence.CascadeType.ALL

@Entity
class CompletedSurvey(
    @Id @GeneratedValue
    var id: Int = -1,
    var userId: Int,
    @ManyToOne
    var survey: Survey,
    @OneToMany(cascade = [ALL])
    var answers: MutableList<SurveyAnswer>,
)
