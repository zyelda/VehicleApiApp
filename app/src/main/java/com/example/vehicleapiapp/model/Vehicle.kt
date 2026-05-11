package com.example.vehicleapiapp.model

data class Vehicle(
    val id: Int? = null,
    val model: String,
    val type: String,
    val manufacturer: String,
    val created_at: String? = null,
    val updated_at: String? = null
)