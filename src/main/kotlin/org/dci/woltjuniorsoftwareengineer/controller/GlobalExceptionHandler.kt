package org.dci.woltjuniorsoftwareengineer.controller

import jakarta.validation.ConstraintViolationException
import org.dci.woltjuniorsoftwareengineer.exception.CustomException
import org.dci.woltjuniorsoftwareengineer.exception.ExternalApiException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.validation.FieldError

/**
 * Global exception handler for handling common exceptions across the application.
 * Catches exceptions and returns appropriate HTTP responses and error messages.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handles validation exceptions for method arguments.
     * Returns a map of field names and error messages when validation fails.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors = ex.bindingResult.allErrors.associate { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            fieldName to errorMessage
        }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handles IllegalArgumentException when invalid arguments are passed.
     * Returns a message with the specific cause of the error.
     */
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val error = mapOf("error" to "Wrong argument: ${ex.localizedMessage}")
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handles MissingServletRequestParameterException when required parameters are missing.
     * Returns a message indicating which parameter is missing.
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingParams(ex: MissingServletRequestParameterException): ResponseEntity<Map<String, String>> {
        val error = mapOf("error" to "Parameter '${ex.parameterName}' is missing")
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handles MethodArgumentTypeMismatchException for type mismatches in method arguments.
     * Returns a message indicating the invalid value and the expected type.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Map<String, String>> {
        val error = mapOf("error" to "Invalid value '${ex.value}' for parameter '${ex.name}'")
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handles ConstraintViolationException for validation errors on request parameters.
     * Returns a map of property paths and violation messages.
     */
    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<Map<String, String>> {
        val errors = ex.constraintViolations.associate { violation ->
            violation.propertyPath.toString() to violation.message
        }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handles ExternalApiException for errors encountered when calling external APIs.
     * Returns an internal error message with a BAD_GATEWAY (502) status code.
     */
    @ExceptionHandler(ExternalApiException::class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    fun handleExternalApiException(ex: ExternalApiException): ResponseEntity<Map<String, String>> {
        val error = mapOf("error" to "Internal error: ${ex.localizedMessage}")
        return ResponseEntity(error, HttpStatus.BAD_GATEWAY)
    }

    /**
     * Handles CustomException for custom application-specific errors.
     * Returns a specific message to prompt users to check their request values.
     */
    @ExceptionHandler(CustomException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleCustomException(ex: CustomException): ResponseEntity<Map<String, String>> {
        val error = mapOf("error" to "Please check request values: ${ex.localizedMessage}")
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }
}
