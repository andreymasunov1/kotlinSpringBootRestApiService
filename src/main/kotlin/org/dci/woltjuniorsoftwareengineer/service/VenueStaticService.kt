package org.dci.woltjuniorsoftwareengineer.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.dci.woltjuniorsoftwareengineer.exception.ExternalApiException
import org.dci.woltjuniorsoftwareengineer.model.VenueStaticData
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import java.io.InputStream

@Service
class VenueStaticService(
    @Value("\${api.base-url}") private val baseUrl: String,
    @Value("\${api.endpoints.static}") private val staticEndpoint: String,
    private val restTemplate: RestTemplate = RestTemplate(),
    private val objectMapper: ObjectMapper = ObjectMapper()
) {

    companion object {
        private val logger = LoggerFactory.getLogger(VenueStaticService::class.java)
    }

    /**
     * Retrieves static data for a venue by making an API call to the specified endpoint.
     */
    fun getStaticData(slug: String): VenueStaticData? {
        val url = "$baseUrl/$slug$staticEndpoint"
        return callApi(url)
    }

    /**
     * Makes a GET request to the given URL to fetch venue static data, and parses it into a VenueStaticData object.
     * Throws an ExternalApiException if the response is invalid or if parsing fails.
     */
    private fun callApi(url: String): VenueStaticData? {
        val extractor = ResponseExtractor<VenueStaticData> { response: ClientHttpResponse ->
            ApiResponseValidator.validateResponse(response)
            parseVenueStaticData(response.body)
        }
        return restTemplate.execute(url, HttpMethod.GET, null, extractor)
    }

    /**
     * Parses the API response body (JSON) to extract venue static data, including coordinates.
     * Returns a VenueStaticData object or throws an ExternalApiException if parsing fails.
     */
    private fun parseVenueStaticData(body: InputStream): VenueStaticData {
        return try {
            val root: JsonNode = objectMapper.readTree(body)
            val coordinatesNode = root.path("venue_raw").path("location").path("coordinates")

            // Check if coordinates are present and correctly formatted
            if (coordinatesNode.size() < 2) {
                throw ExternalApiException("Coordinates data is incomplete or missing")
            }

            val latitude = coordinatesNode[0].asDouble()
            val longitude = coordinatesNode[1].asDouble()

            VenueStaticData(Pair(latitude, longitude))
        } catch (ex: JsonProcessingException) {
            val errorMessage = "Error parsing JSON response from external API"
            logger.error("$errorMessage: ${ex.message}", ex)
            throw ExternalApiException(errorMessage, ex)
        } catch (ex: Exception) {
            val errorMessage = "Unexpected error occurred while processing API response"
            logger.error("$errorMessage: ${ex.message}", ex)
            throw ExternalApiException(errorMessage, ex)
        }
    }
}
