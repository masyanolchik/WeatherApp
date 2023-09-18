package com.weatherapp.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationZipApiResponse(
    val zip: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
)
