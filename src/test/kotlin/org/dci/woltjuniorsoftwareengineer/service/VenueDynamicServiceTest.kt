package org.dci.woltjuniorsoftwareengineer.integration

import org.dci.woltjuniorsoftwareengineer.exception.ExternalApiException
import org.dci.woltjuniorsoftwareengineer.model.VenueDynamicData
import org.dci.woltjuniorsoftwareengineer.service.VenueDynamicService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@SpringBootTest
class VenueDynamicServiceTest {

    @Autowired
    lateinit var venueDynamicService: VenueDynamicService

    @MockBean
    lateinit var restTemplate: RestTemplate

    @Test
    fun `test getDynamicData returns valid VenueDynamicData`() {
        // Mock the external API response
        val mockResponse = """
            {
              "venue_raw": {
                "delivery_specs": {
                  "order_minimum_no_surcharge": 150.0,
                  "delivery_pricing": {
                    "base_price": 3.0,
                    "distance_ranges": [
                      { "min": 0.0, "max": 5.0, "a": 2, "b": 1.0 },
                      { "min": 5.0, "max": 10.0, "a": 3, "b": 0.5 }
                    ]
                  }
                }
              }
            }
        """.trimIndent()

        val mockClientHttpResponse = Mockito.mock(ClientHttpResponse::class.java)
        Mockito.`when`(mockClientHttpResponse.body).thenReturn(ByteArrayInputStream(mockResponse.toByteArray(StandardCharsets.UTF_8)))

        Mockito.`when`(mockClientHttpResponse.statusCode).thenReturn(HttpStatus.OK)

        // Mock the RestTemplate execute method
        Mockito.`when`(restTemplate.execute(any(String::class.java), any(HttpMethod::class.java), any(), any<ResponseExtractor<VenueDynamicData>>()))
            .thenAnswer { invocation ->
                val extractor = invocation.arguments[3] as ResponseExtractor<VenueDynamicData>
                extractor.extractData(mockClientHttpResponse)
            }


        // Call the service method
        val slug = "test-venue"
        val result = venueDynamicService.getDynamicData(slug)

        // Assert the results
        assertNotNull(result)
        assertEquals(150.0, result?.orderMinimumNoSurcharge)
        assertEquals(3.0, result?.basePrice)

        val distanceRanges = result?.distanceRanges
        assertEquals(2, distanceRanges?.size)
        assertEquals(2, distanceRanges?.get(0)?.a)
        assertEquals(0.5, distanceRanges?.get(1)?.b)
    }

    @Test
    fun `test getDynamicData returns null on empty response`() {
        // Mock an empty response from the API
        val mockClientHttpResponse = Mockito.mock(ClientHttpResponse::class.java)
        Mockito.`when`(mockClientHttpResponse.body).thenReturn(ByteArrayInputStream(ByteArray(0)))

        Mockito.`when`(mockClientHttpResponse.statusCode).thenReturn(HttpStatus.OK)

        Mockito.`when`(restTemplate.execute(any(String::class.java), any(HttpMethod::class.java), any(), any<ResponseExtractor<VenueDynamicData>>()))
            .thenAnswer { invocation ->
                val extractor = invocation.arguments[3] as ResponseExtractor<VenueDynamicData>
                extractor.extractData(mockClientHttpResponse)
            }

        val slug = "test-venue"
        val exception = assertThrows(ExternalApiException::class.java) {
            venueDynamicService.getDynamicData(slug)
        }

        assertTrue(exception.message!!.contains("External API returned status 200 but the body is empty"))
    }

    @Test
    fun `test getDynamicData handles malformed json`() {
        // Mock a malformed JSON response from the API
        val malformedResponse = """
            {
                "venue_raw": {
                    "delivery_specs": {
                        "order_minimum_no_surcharge": "a",
                        "delivery_pricing": {
                            "base_price": 3.0,
                            "distance_ranges": "INVALID"
                        }
                    }
                }
            }
        """

        val mockClientHttpResponse = Mockito.mock(ClientHttpResponse::class.java)
        Mockito.`when`(mockClientHttpResponse.body).thenReturn(ByteArrayInputStream(malformedResponse.toByteArray(StandardCharsets.UTF_8)))

        Mockito.`when`(mockClientHttpResponse.statusCode).thenReturn(HttpStatus.OK)

        Mockito.`when`(restTemplate.execute(any(String::class.java), any(HttpMethod::class.java), any(), any<ResponseExtractor<VenueDynamicData>>()))
            .thenAnswer { invocation ->
                val extractor = invocation.arguments[3] as ResponseExtractor<VenueDynamicData>
                extractor.extractData(mockClientHttpResponse)
            }

        val slug = "test-venue"
        val exception = assertThrows(ExternalApiException::class.java) {
            venueDynamicService.getDynamicData(slug)
        }

        assertTrue(exception.message!!.contains("Error parsing JSON response"))
    }

    @Test
    fun `test getDynamicData handles missing fields`() {
        // Mock a response with missing 'venue_raw' field
        val incompleteResponse = """
        {
            "delivery_specs": {
                "order_minimum_no_surcharge": 10.5,
                "delivery_pricing": {
                    "base_price": 5.0,
                    "distance_ranges": []
                }
            }
        }
    """

        val mockClientHttpResponse = Mockito.mock(ClientHttpResponse::class.java)
        Mockito.`when`(mockClientHttpResponse.body).thenReturn(ByteArrayInputStream(incompleteResponse.toByteArray(StandardCharsets.UTF_8)))

        Mockito.`when`(mockClientHttpResponse.statusCode).thenReturn(HttpStatus.OK)

        Mockito.`when`(restTemplate.execute(any(String::class.java), any(HttpMethod::class.java), any(), any<ResponseExtractor<VenueDynamicData>>()))
            .thenAnswer { invocation ->
                val extractor = invocation.arguments[3] as ResponseExtractor<VenueDynamicData>
                extractor.extractData(mockClientHttpResponse)
            }

        val slug = "test-venue"
        val exception = assertThrows(ExternalApiException::class.java) {
            venueDynamicService.getDynamicData(slug)
        }

        assertTrue(exception.message!!.contains("Error parsing JSON response"))
    }

    @Test
    fun `test getDynamicData return not 2successfull code`() {
        // Mock a response with missing 'venue_raw' field
        val incompleteResponse = """
        {
            "delivery_specs": {
                "order_minimum_no_surcharge": 10.5,
                "delivery_pricing": {
                    "base_price": 5.0,
                    "distance_ranges": []
                }
            }
        }
    """

        val mockClientHttpResponse = Mockito.mock(ClientHttpResponse::class.java)
        Mockito.`when`(mockClientHttpResponse.body).thenReturn(ByteArrayInputStream(incompleteResponse.toByteArray(StandardCharsets.UTF_8)))

        Mockito.`when`(mockClientHttpResponse.statusCode).thenReturn(HttpStatus.BAD_GATEWAY)

        Mockito.`when`(restTemplate.execute(any(String::class.java), any(HttpMethod::class.java), any(), any<ResponseExtractor<VenueDynamicData>>()))
            .thenAnswer { invocation ->
                val extractor = invocation.arguments[3] as ResponseExtractor<VenueDynamicData>
                extractor.extractData(mockClientHttpResponse)
            }

        val slug = "test-venue"
        val exception = assertThrows(ExternalApiException::class.java) {
            venueDynamicService.getDynamicData(slug)
        }

        assertTrue(exception.message!!.contains("External API returned error"))
    }

}