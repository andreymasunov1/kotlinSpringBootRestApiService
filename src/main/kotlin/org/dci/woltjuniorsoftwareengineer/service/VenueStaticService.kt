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
class VenueStaticService  (
    @Value("\${api.base-url}") private val baseUrl: String,
    @Value("\${api.endpoints.static}") private val staticEndpoint: String,
    val restTemplate: RestTemplate = RestTemplate()
) {
    private val logger = LoggerFactory.getLogger(VenueStaticService::class.java)

    fun getStaticData(slug: String): VenueStaticData? {
        val url = "$baseUrl/${slug}$staticEndpoint"
        return callApi(url)
    }

    private fun callApi(url: String): VenueStaticData? {
        val extractor = ResponseExtractor<VenueStaticData> { response: ClientHttpResponse ->

            if (!response.statusCode.is2xxSuccessful) {
                val errorMessage = "External API returned error: ${response.statusCode} - ${response.statusText}"
                logger.error(errorMessage)
                throw ExternalApiException(errorMessage)
            }

            val body = response.body
            if (body.available() == 0) {
                val errorMessage = "External API returned status 200 but the body is empty"
                logger.error(errorMessage)
                throw ExternalApiException(errorMessage)
            }

            try {
                val mapper = ObjectMapper()
                val root: JsonNode = mapper.readTree(response.body as InputStream)
                val coordinatesNode = root.path("venue_raw").path("location").path("coordinates")
                val latitude = coordinatesNode[0].asDouble()
                val longitude = coordinatesNode[1].asDouble()
                VenueStaticData(Pair(latitude, longitude))
            } catch (ex: JsonProcessingException) {
                val errorMessage = "Error parsing JSON response"
                logger.error(errorMessage)
                throw ExternalApiException(errorMessage, ex)
            } catch (ex: Exception) {
                val errorMessage = "Unexpected error occurred"
                logger.error(errorMessage)
                throw ExternalApiException(errorMessage, ex)
            }
        }
        return restTemplate.execute(url, HttpMethod.GET, null, extractor)
    }
}