package studio.fabrique.backend.testcase.controller

import org.springframework.http.HttpStatus.*
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import studio.fabrique.backend.testcase.model.exception.SurveyEndedException
import javax.persistence.EntityNotFoundException

@RestControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(SurveyEndedException::class)
    @ResponseStatus(BAD_REQUEST)
    fun surveyAlreadyClosed(exception: SurveyEndedException) = exception

//    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(UNAUTHORIZED)
    fun authException(exception: AuthenticationException) = exception

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(NOT_FOUND)
    fun notFound(exception: EntityNotFoundException) = exception

}