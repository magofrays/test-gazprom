package by.magofrays.controller

import by.magofrays.exception.BusinessException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime


@RestControllerAdvice
class RestExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BusinessException::class)
    fun handle(e: BusinessException): ProblemDetail {
        log.warn("Business exception occurred: status={}, message={}", e.status, e.message)
        val problemDetail = ProblemDetail.forStatusAndDetail(
            e.status,
            e.message
        )
        problemDetail.title = "Business Error"
        problemDetail.setProperty("errorCode", e.status.value())
        problemDetail.setProperty("timestamp", LocalDateTime.now())
        return problemDetail
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidationException(ex: ConstraintViolationException): ProblemDetail {
        log.warn("Validation exception occurred: {}", ex.message)
        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        problemDetail.title = "Validation Error"
        problemDetail.detail = "Request validation failed"

        val errors = ex.constraintViolations.map { violation ->
            mapOf(
                "field" to violation.propertyPath.toString(),
                "message" to violation.message,
                "invalidValue" to violation.invalidValue?.toString(),
                "constraint" to violation.constraintDescriptor?.annotation?.annotationClass?.simpleName
            )
        }
        log.debug("Validation errors: {}", errors)

        problemDetail.setProperty("timestamp", LocalDateTime.now())
        problemDetail.setProperty("errors", errors)
        problemDetail.setProperty("errorCount", errors.size)

        return problemDetail
    }
}