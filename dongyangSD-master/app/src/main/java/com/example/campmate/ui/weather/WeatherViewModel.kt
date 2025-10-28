package com.example.campmate.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.WeatherResponse
import com.example.campmate.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

// UI 상태
sealed class ForecastUiState {
    object Loading : ForecastUiState()
    data class Success(
        val dailySummaries: List<DailyForecastSummary>,
        val locationName: String // (위치 이름은 API 응답에서 파싱해야 함)
    ) : ForecastUiState()
    data class Error(val message: String) : ForecastUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _forecastState = MutableStateFlow<ForecastUiState>(ForecastUiState.Loading)
    val forecastState: StateFlow<ForecastUiState> = _forecastState

    // 날짜/시간 포맷터
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val dayOfWeekFormatter = { date: LocalDate ->
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
    }

    fun loadForecast() {
        viewModelScope.launch {
            _forecastState.value = ForecastUiState.Loading
            weatherRepository.fetchForecast()
                .onSuccess { fullForecastList ->
                    // 3시간 -> 1일 요약 데이터로 가공
                    val dailySummaries = processForecast(fullForecastList)
                    // TODO: API 응답에서 위치 이름을 파싱 (city.name)
                    _forecastState.value = ForecastUiState.Success(dailySummaries, "서울") // (위치 임시)
                }
                .onFailure { error ->
                    _forecastState.value = ForecastUiState.Error(error.message ?: "예보 로드 실패")
                }
        }
    }

    /**
     * 3시간 간격 예보(40개)를 일별 요약(5~6개)으로 가공하는 헬퍼 함수
     */
    private fun processForecast(fullList: List<WeatherResponse>): List<DailyForecastSummary> {
        // 1. 날짜(LocalDate)별로 그룹화
        val groupedByDate = fullList.groupBy {
            LocalDate.parse(it.dt_txt, dateTimeFormatter)
        }

        // 2. 그룹화된 데이터를 DailyForecastSummary로 변환
        return groupedByDate.map { (date, hourlyList) ->
            // 그날의 대표 날씨 (보통 12시~15시 사이의 데이터 사용)
            val representativeWeather = hourlyList.getOrNull(hourlyList.size / 2) // 정오쯤 데이터

            // 그날의 최저/최고 기온
            val minTemp = hourlyList.minOf { it.temperature }
            val maxTemp = hourlyList.maxOf { it.temperature }

            DailyForecastSummary(
                date = date,
                dayOfWeek = dayOfWeekFormatter(date),
                icon = representativeWeather?.icon ?: hourlyList.first().icon,
                description = representativeWeather?.description ?: hourlyList.first().description,
                tempMin = minTemp,
                tempMax = maxTemp,
                hourlyData = hourlyList // 3시간 상세 데이터 포함
            )
        }.sortedBy { it.date } // 날짜순 정렬
    }
}