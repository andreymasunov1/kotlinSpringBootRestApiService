package org.dci.woltjuniorsoftwareengineer.exception

/**
 * ExternalApiException is thrown when there are issues interacting with an external API.
 * This exception extends RuntimeException, allowing it to be used as an unchecked exception
 * to propagate specific API-related errors.
 *
 * @param message a detailed message explaining the nature of the API error
 * @param cause an optional Throwable that caused this exception, helpful for tracing issues
 */
class ExternalApiException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
