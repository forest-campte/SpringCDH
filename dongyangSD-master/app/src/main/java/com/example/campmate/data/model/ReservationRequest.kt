package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

/**
 * DB reservations 테이블에 데이터를 저장하기 위해
 * 안드로이드 앱이 백엔드로 보내는 요청(Request) 데이터 모델
 */
data class ReservationRequest(
    // DB의 admins_id 컬럼과 매칭
    @SerializedName("adminsId")
    val adminsId: Long,

    // DB의 camping_zone_id 컬럼과 매칭
    @SerializedName("campingZoneId")
    val campingZoneId: String,

    // DB의 check_in 컬럼과 매칭 (YYYY-MM-DD 형식)
    @SerializedName("checkIn")
    val checkIn: String,

    // DB의 check_out 컬럼과 매칭 (YYYY-MM-DD 형식)
    @SerializedName("checkOut")
    val checkOut: String,

    // ✅ [추가] ViewModel에서 수집하는 인원 정보
    // (DB 테이블에도 이 컬럼들이 필요하다고 백엔드 팀에 알려야 합니다)
    @SerializedName("adults")
    val adults: Int,

    @SerializedName("children")
    val children: Int
)