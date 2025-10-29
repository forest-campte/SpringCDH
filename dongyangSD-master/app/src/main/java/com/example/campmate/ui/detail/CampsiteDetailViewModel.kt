package com.example.campmate.ui.detail

import android.util.Log.e
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.CampsiteSite
import com.example.campmate.data.model.ReservationRequest // ✅ ReservationRequest import
import com.example.campmate.data.model.Review
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CampsiteDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // ❌ 임시 저장소(ReservationRepository) 삭제
    private val apiService: ApiService
) : ViewModel() {

    private val _campsite = MutableStateFlow<Campsite?>(null)
    val campsite: StateFlow<Campsite?> = _campsite

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    /*

    init {
        // 1. Int로 받은 뒤 .toLong()으로 변환 (가장 안전한 방법) cdh1028
        val campsiteId: Long = (savedStateHandle.get<Int>("campsiteId") ?: 0).toLong()
        if (campsiteId != null) {
            fetchCampsiteDetails(campsiteId)
            fetchReviews(campsiteId)
        }
    }

    1030cdh 로딩 및 에러 상태 */
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        val campsiteId: Long = (savedStateHandle.get<Int>("campsiteId") ?: 0).toLong()
        if (campsiteId > 0) { // 0일 경우 로드하지 않음
            fetchAllDetails(campsiteId)
        } else {
            _error.value = "유효하지 않은 캠핑장 ID입니다."
            _isLoading.value = false
        }
    }


    /*
    // (fetchCampsiteDetails, fetchReviews 함수는 이전과 동일)

    private fun fetchCampsiteDetails(campsiteId: Long) { /* ... */ }
    private fun fetchReviews(campsiteId: Long) { /* ... */ }

    //
    1030cdh fetch 두개를 동시에 관리하는 함수 */
    private fun fetchAllDetails(campsiteId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 두 API 호출을 동시에 (또는 순차적으로) 실행
                fetchCampsiteDetails(campsiteId)
                fetchReviews(campsiteId)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "데이터 로딩 중 오류 발생: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    // fetchCampsiteDetails 구현
    private suspend fun fetchCampsiteDetails(campsiteId: Long) {

        val response = apiService.getCampsiteDetail(campsiteId) // 🚨 이 함수는 ApiService에 정의되어 있어야 함
        if (response.isSuccessful) {
            _campsite.value = response.body()
        } else {
            throw Exception("캠핑장 정보 로드 실패: ${response.code()}")
        }
    }

    // fetchReviews 구현
    private suspend fun fetchReviews(campsiteId: Long) {
        // (참고: ApiService에 getCampsiteReviews(id) 함수가 있다고 가정)
        val response = apiService.getCampsiteReviews(campsiteId) // 🚨 이 함수는 ApiService에 정의되어 있어야 함
        if (response.isSuccessful) {
            _reviews.value = response.body() ?: emptyList()
        } else {
            throw Exception("리뷰 정보 로드 실패: ${response.code()}")
        }
    }




    // 날짜 포맷 함수
    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    /**
     * ✅ [수정됨] 임시 저장소 대신 실제 API를 호출하여 예약합니다.
     */
    fun makeReservation(
        authToken: String, // ✅ (1) Activity/Fragment로부터 토큰 받기
        adults: Int,
        children: Int,
        startDate: Long,
        endDate: Long,
        site: CampsiteSite
    ) {
        _campsite.value?.let { currentCampsite ->
            viewModelScope.launch {
                try {
                    //val zoneIdAsLong : Long
                    //try{
                    //    zoneIdAsLong = site.siteId.toLong()
                    //}catch (e: NumberFormatException){
                    //    e.printStackTrace()
                    //    return@launch
                    //}
                    // 1. 서버로 보낼 '예약 요청' 데이터 상자 (수정됨)
                    val request = ReservationRequest(
                        adminsId = currentCampsite.id.toLong(),
                        campingZoneId = site.siteId,
                        checkIn = formatDate(startDate),
                        checkOut = formatDate(endDate),
                        adults = adults,     // ✅ (2) 인원 정보 추가
                        children = children  // ✅ (2) 인원 정보 추가
                    )

                    // 2. 실제 API 호출 (수정됨)
                    // (ApiService에 추가한 함수 형식에 맞게 'token' 전달)
                    val response = apiService.makeReservation(authToken, request) // ✅ (3) 토큰 전달

                    if (response.isSuccessful) {
                        // TODO: 예약 성공 UI 처리 (예: 토스트 메시지, 화면 이동)
                    } else {
                        // TODO: 예약 실패 UI 처리 (예: "이미 예약된 날짜입니다" 토스트)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    // TODO: 예약 실패 시 에러 토스트 (네트워크 오류 등)
                }
            }
        }
    }
}