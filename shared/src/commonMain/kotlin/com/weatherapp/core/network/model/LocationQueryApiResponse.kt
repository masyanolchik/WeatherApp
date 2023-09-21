package com.weatherapp.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationQueryApiResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String = "",
)