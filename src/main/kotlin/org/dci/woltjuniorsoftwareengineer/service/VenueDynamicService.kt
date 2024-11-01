package org.dci.woltjuniorsoftwareengineer.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dci.woltjuniorsoftwareengineer.exception.ExternalApiException
import org.dci.woltjuniorsoftwareengineer.model.DistanceRanges
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
    val restTemplate: RestTemplate = RestTemplate()
) {
    private val logger = LoggerFactory.getLogger(VenueDynamicService::class.java)

    fun getDynamicData(slug: String): VenueDynamicData? {
        val url = "$baseUrl/${slug}$dynamicEndpoint"
        return callApi(url)
    }

    fun callApi(url: String): VenueDynamicData? {

        val extractor = ResponseExtractor<VenueDynamicData> { response: ClientHttpResponse ->

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

                val orderMinimumNoSurcharge =
                    root.path("venue_raw").path("delivery_specs").path("order_minimum_no_surcharge")
                        .asDouble()
                val basePrice =
                    root.path("venue_raw").path("delivery_specs").path("delivery_pricing")
                        .path("base_price").asDouble()
                val distanceRangesNode =
                    root.path("venue_raw").path("delivery_specs").path("delivery_pricing")
                        .path("distance_ranges")

                VenueDynamicData(
                    orderMinimumNoSurcharge,
                    basePrice,
                    extractDistanceRanges(distanceRangesNode)
                )
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

    private fun extractDistanceRanges(jsonString: JsonNode): List<DistanceRanges> {
        val objectMapper = jacksonObjectMapper()
        return objectMapper.readValue<List<DistanceRanges>>(jsonString.toString())
    }
}