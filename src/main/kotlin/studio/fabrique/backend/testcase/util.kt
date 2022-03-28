package studio.fabrique.backend.testcase

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import studio.fabrique.backend.testcase.model.*
import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import kotlin.random.Random
import kotlin.random.nextInt

fun Any.logger(): Logger = LoggerFactory.getLogger(this::class.java)

inline fun <reified Type> EntityManager.createQuery(query: String): TypedQuery<Type> =
    this.createQuery(query, Type::class.java)

fun Survey.randomAnswers(): List<SurveyAnswer> {
    return this.questions.map {
        when (it) {
            is QuestionWithOneChosenAnswer -> {
                ChooseOneSurveyAnswer(Random.nextInt(0 until it.options.size))
            }
            is QuestionWithMultipleChosenAnswers -> {
                val questionAnswers = mutableSetOf<Int>()
                for (i in 0..(it.options.size / 2))
                    questionAnswers += Random.nextInt(0 until it.options.size)
                ChooseMultipleSurveyAnswer(questionAnswers.toList().sorted())
            }
            is QuestionWIthTextAnswer -> {
                TextSurveyAnswer(randomString(10))
            }
            else -> throw ClassNotFoundException("${it::class} is not handled inside this function")
        }
    }
}

private val charPool = ('A'..'Z') + ('a'..'z') + ('0'..'9')
fun randomString(length: Int): String {
    return (1..length)
        .map { charPool.random() }
        .joinToString("")
}

inline fun <reified ExceptionType : Throwable> ignore(block: () -> Unit) {
    try {
        block()
    } catch (thrownException: Throwable) {
        if (thrownException !is ExceptionType)
            throw thrownException
    }
}

infix fun String.zipLines(other: String): String {
    val maxLength = this.lines().maxOf { it.length }
    return (this.lines() zip other.lines()).joinToString("\n") {
        if (it.first == it.second)
            "${" ".repeat(maxLength - it.first.length)}${it.first}    ${it.second}"
        else
            "${" ".repeat(maxLength - it.first.length)}${it.first} !! ${it.second}"
    }
}
