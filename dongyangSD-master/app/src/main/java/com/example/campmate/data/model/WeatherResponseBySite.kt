package com.example.campmate.data.model

data class WeatherResponseBySite(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    // ... (imageUrl, address 등)
)
