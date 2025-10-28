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

    init {
        val campsiteId = savedStateHandle.get<Long>("campsiteId")
        if (campsiteId != null) {
            fetchCampsiteDetails(campsiteId)
            fetchReviews(campsiteId)
        }
    }

    // (fetchCampsiteDetails, fetchReviews 함수는 이전과 동일)
    private fun fetchCampsiteDetails(campsiteId: Long) { /* ... */ }
    private fun fetchReviews(campsiteId: Long) { /* ... */ }

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