package org.dci.woltjuniorsoftwareengineer.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dci.woltjuniorsoftwareengineer.exception.ExternalApiException
import org.dci.woltjuniorsoftwareengineer.model.DistanceRange
import org.dci.woltjuniorsoftwareengineer.model.VenueDynamicData
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import java.io.InputStream

@Service
class VenueDynamicService(
    @Value("\${api.base-url}") private val baseUrl: String,
    @Value("\${api.endpoints.dynamic}") private val dynamicEndpoint: String,
    private val restTemplate: RestTemplate = RestTemplate(),
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {

    companion object {
        private val logger = LoggerFactory.getLogger(VenueDynamicService::class.java)
    }

    /**
     * Retrieves dynamic data for a venue by making an API call to the specified endpoint.
     */
    fun getDynamicData(slug: String): VenueDynamicData? {
        val url = "$baseUrl/$slug$dynamicEndpoint"
        return callApi(url)
    }

    /**
     * Calls the external API to retrieve dynamic data and parses the response into VenueDynamicData.
     * Throws an ExternalApiException if the API response is unsuccessful or malformed.
     */
    private fun callApi(url: String): VenueDynamicData? {
        val extractor = ResponseExtractor<VenueDynamicData> { response: ClientHttpResponse ->
            //validateResponse(response)
            ApiResponseValidator.validateResponse(response)
            parseVenueDynamicData(response.body)
        }
        return restTemplate.execute(url, HttpMethod.GET, null, extractor)
    }

    /**
     * Parses the API response body (JSON) to extract venue dynamic data.
     * Returns a VenueDynamicData object or throws an ExternalApiException if parsing fails.
     */
    private fun parseVenueDynamicData(body: InputStream): VenueDynamicData {
        try {
            val root: JsonNode = objectMapper.readTree(body)
            val orderMinimumNoSurcharge = root.path("venue_raw").path("delivery_specs")
                .path("order_minimum_no_surcharge").asDouble()
            val basePrice = root.path("venue_raw").path("delivery_specs")
                .path("delivery_pricing").path("base_price").asDouble()
            val distanceRangesNode = root.path("venue_raw").path("delivery_specs")
                .path("delivery_pricing").path("distance_ranges")

            return VenueDynamicData(
                orderMinimumNoSurcharge = orderMinimumNoSurcharge,
                basePrice = basePrice,
                distanceRanges = extractDistanceRanges(distanceRangesNode)
            )
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

    /**
     * Extracts a list of DistanceRange objects from the provided JSON node.
     */
    private fun extractDistanceRanges(distanceRangesNode: JsonNode): List<DistanceRange> {
        return try {
            objectMapper.readValue(distanceRangesNode.toString())
        } catch (ex: JsonProcessingException) {
            val errorMessage = "Error parsing distance ranges from JSON"
            logger.error("$errorMessage: ${ex.message}", ex)
            throw ExternalApiException(errorMessage, ex)
        }
    }
}
