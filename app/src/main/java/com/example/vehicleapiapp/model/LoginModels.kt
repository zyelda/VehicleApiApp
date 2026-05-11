package com.example.vehicleapiapp.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val token: String,
    val user: UserData
)

data class LogoutResponse(
    val success: Boolean,
    val message: String
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val is_active: Boolean
)