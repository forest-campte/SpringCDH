package com.example.campmate.data

import com.example.campmate.data.model.WeatherResponse
import com.example.campmate.data.remote.ApiService
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: ApiService,
    private val locationRepository: LocationRepository
) {
    // 5일 예보를 가져옵니다 (3시간 간격 데이터 40개)
    suspend fun fetchForecast(): Result<List<WeatherResponse>> {
        return try {
            val location = locationRepository.getCurrentLocation()
                ?: return Result.failure(Exception("현재 위치를 가져올 수 없습니다."))

            val forecastData = apiService.getForecast(
                lat = location.latitude,
                lon = location.longitude
            )
            Result.success(forecastData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}