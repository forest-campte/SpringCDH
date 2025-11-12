package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName
import java.time.ZoneId

data class ReviewRequest(
    //11.10 수정 KM 리뷰
    // DB : camping_zone_id
    @SerializedName("camping_zone_id") val campingZoneId: Int,
    // DB: reservation_id (필수)
    @SerializedName("reservation_id") val reservationId: Long,
    // DB: customer_id (필수 - 실제로는 토큰으로 처리되겠지만 요청 본문에 포함하는 경우)
    @SerializedName("customer_id") val customerId: Long,
    // DB: rating
    @SerializedName("rating") val rating: Float,
    // DB: coment -> content로 통일
    @SerializedName("coment") val content: String
)