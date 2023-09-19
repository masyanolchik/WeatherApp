package com.weatherapp.core.model

import comweatherapp.LocationEntity
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val name: String,
    val latitude: String,
    val longitude: String,
    val country: String,
    val state: String,
    val zip: String
)