package org.dci.woltjuniorsoftwareengineer.service

import org.dci.woltjuniorsoftwareengineer.exception.ExternalApiException
import org.slf4j.LoggerFactory
import org.springframework.http.client.ClientHttpResponse

object ApiResponseValidator {
    private val logger = LoggerFactory.getLogger(ApiResponseValidator::class.java)

    fun validateResponse(response: ClientHttpResponse) {
        if (!response.statusCode.is2xxSuccessful) {
            val errorMessage = "External API returned error: ${response.statusCode} - ${response.statusText}"
            logger.error(errorMessage)
            throw ExternalApiException(errorMessage)
        }
        if (response.body.available() == 0) {
            val errorMessage = "External API returned status 200 but the body is empty"
            logger.error(errorMessage)
            throw ExternalApiException(errorMessage)
        }
    }
}
