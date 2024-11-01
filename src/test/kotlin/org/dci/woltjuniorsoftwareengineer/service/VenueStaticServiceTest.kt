package org.dci.woltjuniorsoftwareengineer.service

import org.dci.woltjuniorsoftwareengineer.exception.ExternalApiException
import org.dci.woltjuniorsoftwareengineer.model.VenueStaticData
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
class VenueStaticServiceTest {

    @Autowired
    lateinit var venueStaticService: VenueStaticService

    @MockBean
    lateinit var restTemplate: RestTemplate

    @Test
    fun `test getStaticService returns valid VenueStaticData`() {
        // Mock the external API response
        val mockResponse = """
            {
                "venue_raw": {
                    "location": {
                        "coordinates": [40.7128, -74.0060]
                    }
                }
            }
        """.trimIndent()

        val mockClientHttpResponse = Mockito.mock(ClientHttpResponse::class.java)
        Mockito.`when`(mockClientHttpResponse.body).thenReturn(ByteArrayInputStream(mockResponse.toByteArray(StandardCharsets.UTF_8)))

        Mockito.`when`(mockClientHttpResponse.statusCode).thenReturn(HttpStatus.OK)

        // Mock the RestTemplate execute method
        Mockito.`when`(restTemplate.execute(any(String::class.java), any(HttpMethod::class.java), any(), any<ResponseExtractor<VenueStaticData>>()))
            .thenAnswer { invocation ->
                val extractor = invocation.arguments[3] as ResponseExtractor<VenueStaticData>
                extractor.extractData(mockClientHttpResponse)
            }

        // Call the service method
        val slug = "test-venue"
        val result = venueStaticService.getStaticData(slug)

        // Assert the results
        assertNotNull(result)
        assertEquals(40.7128, result?.coordinates!!.first)
        assertEquals(-74.0060, result.coordinates.second)
    }

    @Test
    fun `test getStaticData returns null on empty response`() {
        // Mock an empty response from the API
        val mockClientHttpResponse = Mockito.mock(ClientHttpResponse::class.java)
        Mockito.`when`(mockClientHttpResponse.body).thenReturn(ByteArrayInputStream(ByteArray(0)))

        Mockito.`when`(mockClientHttpResponse.statusCode).thenReturn(HttpStatus.OK)

        Mockito.`when`(restTemplate.execute(any(String::class.java), any(HttpMethod::class.java), any(), any<ResponseExtractor<VenueStaticData>>()))
            .thenAnswer { invocation ->
                val extractor = invocation.arguments[3] as ResponseExtractor<VenueStaticData>
                extractor.extractData(mockClientHttpResponse)
            }

        val slug = "test-venue"
        val exception = assertThrows(ExternalApiException::class.java) {
            venueStaticService.getStaticData(slug)
        }

        assertTrue(exception.message!!.contains("External API returned status 200 but the body is empty"))
    }

    @Test
    fun `test getStaticData handles missing fields`() {

        val incompleteResponse = """
            {
                "venue_raw": {
                    "location": {
                    }
                }
            }
    """

        val mockClientHttpResponse = Mockito.mock(ClientHttpResponse::class.java)
        Mockito.`when`(mockClientHttpResponse.body).thenReturn(ByteArrayInputStream(incompleteResponse.toByteArray(StandardCharsets.UTF_8)))

        Mockito.`when`(mockClientHttpResponse.statusCode).thenReturn(HttpStatus.OK)

        Mockito.`when`(restTemplate.execute(any(String::class.java), any(HttpMethod::class.java), any(), any<ResponseExtractor<VenueStaticData>>()))
            .thenAnswer { invocation ->
                val extractor = invocation.arguments[3] as ResponseExtractor<VenueStaticData>
                extractor.extractData(mockClientHttpResponse)
            }

        val slug = "test-venue"
        val exception = assertThrows(ExternalApiException::class.java) {
            venueStaticService.getStaticData(slug)
        }

        assertTrue(exception.message!!.contains("Unexpected error occurred"))
    }

    @Test
    fun `test getStaticData return not 2successful code`() {

        val incompleteResponse = """
            {
                "venue_raw": {
                    "location": {
                    }
                }
            }
    """

        val mockClientHttpResponse = Mockito.mock(ClientHttpResponse::class.java)
        Mockito.`when`(mockClientHttpResponse.body).thenReturn(ByteArrayInputStream(incompleteResponse.toByteArray(StandardCharsets.UTF_8)))

        Mockito.`when`(mockClientHttpResponse.statusCode).thenReturn(HttpStatus.BAD_GATEWAY)

        Mockito.`when`(restTemplate.execute(any(String::class.java), any(HttpMethod::class.java), any(), any<ResponseExtractor<VenueStaticData>>()))
            .thenAnswer { invocation ->
                val extractor = invocation.arguments[3] as ResponseExtractor<VenueStaticData>
                extractor.extractData(mockClientHttpResponse)
            }

        val slug = "test-venue"
        val exception = assertThrows(ExternalApiException::class.java) {
            venueStaticService.getStaticData(slug)
        }

        assertTrue(exception.message!!.contains("External API returned error"))
    }
}