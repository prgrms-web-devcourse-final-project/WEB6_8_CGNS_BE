
import com.back.koreaTravelGuide.KoreaTravelGuideApplication
import com.back.koreaTravelGuide.config.TestConfig
import com.back.koreaTravelGuide.domain.ai.weather.dto.MidForecastDto
import com.back.koreaTravelGuide.domain.ai.weather.dto.TemperatureAndLandForecastDto
import com.back.koreaTravelGuide.domain.ai.weather.service.WeatherService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

/**
 * 실제 기상청 API 상태를 확인하기 위한 통합 테스트.
 */
@SpringBootTest(classes = [KoreaTravelGuideApplication::class])
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestConfig::class)
@Transactional
class WeatherApiClientTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var weatherService: WeatherService

    @DisplayName("fetchMidForecast - 실제 기상청 API 중기전망조회 (데이터 기대)")
    @Test
    fun fetchMidForecastTest() {
        val mockData =
            listOf(
                MidForecastDto(
                    regionCode = "11B00000",
                    baseTime = "2025101006",
                    precipitation = "맑음",
                    temperature = "평년과 비슷",
                    maritime = null,
                    variability = null,
                ),
            )
        `when`(weatherService.getWeatherForecast()).thenReturn(mockData)

        // when & then - API 호출 및 검증
        mockMvc.perform(
            get("/weather/test1")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].regionCode").value("11B00000"))
            .andExpect(jsonPath("$[0].precipitation").value("맑음"))
            .andDo(print()) // 결과 출력
    }

    @DisplayName("TemperatureAndLandForecast - 실제 기상청 API 중기기온조회 + 중기육상예보조회(데이터 기대)")
    @Test
    fun fetchTemperatureTest() {
        // given
        val mockData =
            listOf(
                TemperatureAndLandForecastDto(
                    regionCode = "11B10101",
                    baseTime = "2025101006",
                    minTemp = 10,
                    maxTemp = 20,
                    minTempRange = "8~12",
                    maxTempRange = "18~22",
                    amRainPercent = 30,
                    pmRainPercent = 20,
                    amWeather = "맑음",
                    pmWeather = "구름많음",
                ),
            )
        `when`(weatherService.getTemperatureAndLandForecast("11B10101")).thenReturn(mockData)

        // when & then
        mockMvc.perform(get("/weather/test2"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].regionCode").value("11B10101"))
            .andExpect(jsonPath("$[0].minTemp").value(10))
            .andDo(print())
    }
}
