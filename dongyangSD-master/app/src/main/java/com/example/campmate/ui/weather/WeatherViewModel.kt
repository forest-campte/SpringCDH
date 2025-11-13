package com.example.campmate.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.BookedCampsiteRepository // ❗️ 이 리포지토리가 List<WeatherResponseBySite>를 반환해야 합니다.
import com.example.campmate.data.LocationRepository
import com.example.campmate.data.WeatherRepository
import com.example.campmate.data.model.WeatherResponse // ❗️ Spring Boot에서 오는 3시간 예보 DTO
import com.example.campmate.data.model.WeatherResponseBySite // ❗️ 예약된 캠핑장 DTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

// --- UI에 표시할 모든 데이터를 담는 상태 클래스 ---

/**
 * 날씨 예보의 '하루치' 요약 데이터 모델 (UI용)
 */
data class DailyForecastSummary(
    val date: LocalDate,        // 날짜 (예: 2025-10-30)
    val dayOfWeek: String,      // 요일 (예: "목")
    val icon: String,           // 대표 아이콘 (예: "01d")
    val description: String,    // 대표 날씨 (예: "맑음")
    val tempMin: Double,        // 그날의 최저 기온
    val tempMax: Double,        // 그날의 최고 기온
    val hourlyData: List<WeatherResponse> // 이 날짜에 해당하는 3시간 간격 원본 데이터
)

/**
 * 날씨를 조회할 위치 (현재위치 또는 캠핑장)
 */
data class SelectableLocation(
    val id: String, // "current" 또는 "campsite_1"
    val name: String, // "현재 위치" 또는 "A 캠핑장"
    val lat: Double,
    val lon: Double
)

/**
 * 날씨 화면의 전체 UI 상태
 */
data class ForecastUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val dailySummaries: List<DailyForecastSummary> = emptyList(),
    val availableLocations: List<SelectableLocation> = emptyList(),
    val selectedLocationId: String = "current", // "current" 또는 "campsite_1"
    val locationDisplayName: String = "날씨 로딩 중..."
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val bookedCampsiteRepository: BookedCampsiteRepository // ⬅️ (가상) 예약 리포지토리
) : ViewModel() {

    private val _forecastState = MutableStateFlow(ForecastUiState())
    val forecastState: StateFlow<ForecastUiState> = _forecastState

    // 날짜/시간 포맷터
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val dayOfWeekFormatter = { date: LocalDate ->
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN) // Locale.KOR -> Locale.KOREAN
    }

    init {
        // 앱 시작 시 날씨 로드
        loadInitialWeather()
    }

    private fun loadInitialWeather() {
        viewModelScope.launch {
            _forecastState.value = ForecastUiState(isLoading = true) // 로딩 시작

            try {
                // 1. 예약된 캠핑장 목록 가져오기 (List<WeatherResponseBySite>)
                val bookedCampsites = bookedCampsiteRepository.getBookedCampsites() // (가상 함수)

                // 2. 현재 위치 정보 가져오기 (권한은 View에서 처리 가정)
                val currentLocation = locationRepository.getCurrentLocation()

                // 3. 선택 가능한 위치 목록 생성
                val locations = mutableListOf<SelectableLocation>()

                // 3-1. 현재 위치 추가
                currentLocation?.let {
                    locations.add(
                        SelectableLocation(
                            id = "current",
                            name = "현재 위치",
                            lat = it.latitude,
                            lon = it.longitude
                        )
                    )
                }

                // 3-2. 예약된 캠핑장 추가 (수정된 부분)
                bookedCampsites.forEach { site -> // 'WeatherResponseBySite' 대신 'site' 변수 사용
                    locations.add(
                        SelectableLocation(
                            id = "campsite_${site.id}",
                            name = site.name,
                            lat = site.latitude,
                            lon = site.longitude
                        )
                    )
                }

                // 4. 기본으로 보여줄 날씨 결정
                // 예약한 캠핑장이 있으면 첫 번째 캠핑장, 없으면 "현재 위치"
                val initialLocation = if (bookedCampsites.isNotEmpty()) {
                    locations.first { it.id.startsWith("campsite_") } // 캠핑장 우선
                } else {
                    locations.firstOrNull { it.id == "current" } // 없으면 현재 위치
                }

                if (initialLocation == null) {
                    // 위치 정보를 아예 못 가져온 경우 (권한 거부 등)
                    _forecastState.value = ForecastUiState(
                        isLoading = false,
                        errorMessage = "위치 정보를 가져올 수 없습니다. 권한을 확인해주세요."
                    )
                    return@launch
                }

                // 5. 결정된 위치의 날씨 정보 로드
                fetchWeatherForLocation(initialLocation, locations)

            } catch (e: Exception) {
                _forecastState.value = ForecastUiState(isLoading = false, errorMessage = e.message)
            }
        }
    }

    /**
     * 사용자가 다른 위치(칩)를 탭했을 때 호출
     */
    fun onLocationSelected(locationId: String) {
        viewModelScope.launch {
            val selectedLocation = _forecastState.value.availableLocations.find { it.id == locationId }

            selectedLocation?.let {
                // 로딩 상태 표시 (데이터는 유지)
                _forecastState.value = _forecastState.value.copy(
                    isLoading = true,
                    selectedLocationId = locationId // 선택된 ID 즉시 반영
                )
                // 새 위치의 날씨 로드
                fetchWeatherForLocation(it, _forecastState.value.availableLocations)
            }
        }
    }

    /**
     * 특정 위치의 날씨를 가져오고 UI 상태를 업데이트하는 공통 함수
     */
    private suspend fun fetchWeatherForLocation(
        location: SelectableLocation,
        allLocations: List<SelectableLocation>
    ) {
        // ❗️ weatherRepository.fetchForecast가 lat, lon을 받도록 수정되어 있어야 합니다.
        weatherRepository.fetchForecast(location.lat, location.lon)
            .onSuccess { fullForecastList ->
                val dailySummaries = processForecast(fullForecastList)
                _forecastState.value = ForecastUiState(
                    isLoading = false,
                    dailySummaries = dailySummaries,
                    availableLocations = allLocations,
                    selectedLocationId = location.id,
                    locationDisplayName = location.name
                )
            }
            .onFailure { error ->
                _forecastState.value = _forecastState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "날씨 정보 로드 실패"
                )
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