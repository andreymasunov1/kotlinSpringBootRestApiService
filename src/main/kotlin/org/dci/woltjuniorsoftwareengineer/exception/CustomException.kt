package org.dci.woltjuniorsoftwareengineer.exception

/**
 * CustomException is used to represent application-specific errors.
 * This exception extends RuntimeException, allowing it to be used for unchecked exceptions
 * that can carry a custom message and optional cause.
 *
 * @param message a detailed message describing the reason for the exception
 * @param cause an optional Throwable that caused this exception to be thrown
 */
class CustomException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
