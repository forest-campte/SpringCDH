package com.example.campmate.data.remote

import com.example.campmate.data.model.*
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    // --- 사용자 인증 ---
    @POST("customer/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("customer/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Unit>

    // --- 캠핑장 ---
//    @GET("campsites")
//    suspend fun getAllCampsites(): Response<List<Campsite>>
    @GET("api/zones/home") // 수정: cdh1028
    suspend fun getAllCampsites(): Response<List<Campsite>>

    // ✅ [추가] 특정 ID의 캠핑장 상세 정보를 가져옵니다.
    //임시 제외: cdh1028
//    @GET("campsites/{id}")
//    suspend fun getCampsiteDetail(@Path("id") campsiteId: Long): Response<Campsite>

    // --- 리뷰 ---
    @POST("reviews/submit") // 수정: cdh1028
    suspend fun submitReview(@Body reviewRequest: ReviewRequest): Response<Unit>

    // cdh1028 :
    @GET("reviews")
    suspend fun getMyReviews(): Response<List<Review>>

    // ✅✅✅ [핵심] ViewModel이 찾고 있던 'getCampsiteReviews' 함수입니다. ✅✅✅
    // (백엔드에 GET /reviews/campsite/{id} 주소가 필요합니다)
    // cdh1028 :
    @GET("reviews/campsite/{id}")
    suspend fun getCampsiteReviews(@Path("id") campsiteId: Long): Response<List<Review>>

    // --- 체크리스트 ---
    @GET("/api/checklist/categories-with-items")
    suspend fun getChecklistPresets(): Response<List<ChecklistPresetItem>>

    @POST("/api/checklist/getChecklist/{customerId}")
    suspend fun getChecklist(@Path("customerId") customerId: Long): Response<List<ChecklistItemResponse>>

    @POST("/api/checklist/getAddItem/{customerId}")
    suspend fun addChecklistItem(
        @Path("customerId") customerId: Long,
        @Query("itemName") itemName: String
    ): Response<ChecklistItemResponse>

    @PUT("/api/checklist/{itemId}")
    suspend fun updateChecklistItem(
        @Path("itemId") itemId: Long,
        @Query("isChecked") isChecked: Boolean
    ): Response<ChecklistItemResponse>

    @DELETE("/api/checklist/{itemId}")
    suspend fun deleteChecklistItem(@Path("itemId") itemId: Long): Response<Unit>
    /* 중복 주석 처리 : cdh1028
    @GET("checklist/{customerId}")
    suspend fun getMyChecklist(@Path("customerId") customerId: Long): Response<List<ChecklistItem>>

    @GET("checklist/presets")
    suspend fun getChecklistPresets(): Response<Map<String, List<String>>>

    @POST("checklist/{customerId}")
    suspend fun addChecklistItem(@Path("customerId") customerId: Long, @Body body: Map<String, String>): Response<ChecklistItem>

    @PUT("checklist/{itemId}")
    suspend fun updateChecklistItem(@Path("itemId") itemId: Long, @Body body: Map<String, Boolean>): Response<ChecklistItem>

    @DELETE("checklist/{itemId}")
    suspend fun deleteChecklistItem(@Path("itemId") itemId: Long): Response<Unit>
    */


    /**
     * 새로운 예약을 생성합니다.
     * (customer/login 과 경로 패턴을 맞추기 위해 "customer/reservations"로 가정)
     *
     * @Header token: 로그인 시 발급받은 인증 토큰
     * @Body request: 예약에 필요한 정보 (ReservationRequest)
     * @return Response<Unit> (회원가입처럼 성공 여부만 받음)
     */
    @POST("api/reservations") // 👈 백엔드 팀과 실제 엔드포인트 확인! -> cdh1028 확인
    suspend fun makeReservation(
        @Header("Authorization") token: String,
        @Body request: ReservationRequest
    ): Response<Unit> // 백엔드가 예약 성공 시 데이터를 준다면 Unit 대신 DTO로 변경
    @GET("api/reservations/{custimerId}")
    suspend fun getMyReservations(
        @Path("customerId") customerId: Long
    ): Response<List<Reservation>>
}