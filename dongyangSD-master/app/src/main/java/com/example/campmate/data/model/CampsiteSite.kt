// CampsiteSite.kt

package com.example.campmate.data.model

// 캠핑장 내의 개별 사이트(존) 정보를 담는 클래스
data class CampsiteSite(
    //1030cdh
    val siteId: String,
    val name: String?,
    val price: Int?
)