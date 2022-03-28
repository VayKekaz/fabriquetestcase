package studio.fabrique.backend.testcase.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS
import javax.persistence.*

@Entity
class Survey(
    @Id @GeneratedValue
    var id: Int,
    var title: String,
    @JsonProperty(required = false)
    @Column(updatable = false)
    var startsOn: Instant = Instant.now().truncatedTo(SECONDS),
    var closesOn: Instant?,
    @OneToMany(cascade = [CascadeType.ALL])
    var questions: MutableList<Question>,
) {

    @get:JsonIgnore
    @get:Transient
    val ended: Boolean
        get() = closesOn?.isBefore(Instant.now()) ?: false
}
