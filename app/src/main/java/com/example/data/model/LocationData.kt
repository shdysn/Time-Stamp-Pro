package com.example.data.model

data class LocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val speed: Float = 0f,
    val bearing: Float = 0f,
    val address: String = "Detecting location...",
    val locality: String = "",
    val adminArea: String = "",
    val country: String = "",
    val postalCode: String = "",
    val isMock: Boolean = false,
    val provider: String = "GPS"
)
