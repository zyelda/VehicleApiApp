package com.example.vehicleapiapp.network

import com.example.vehicleapiapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("vehicles")
    suspend fun getVehicles(): Response<ApiResponse<List<Vehicle>>>

    @POST("vehicles")
    suspend fun createVehicle(
        @Body vehicle: Vehicle
    ): Response<ApiResponse<Vehicle>>

    @PUT("vehicles/{id}")
    suspend fun updateVehicle(
        @Path("id") id: Int,
        @Body vehicle: Vehicle
    ): Response<ApiResponse<Vehicle>>

    @DELETE("vehicles/{id}")
    suspend fun deleteVehicle(
        @Path("id") id: Int
    ): Response<ApiResponse<Vehicle>>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<LogoutResponse>

    @GET("pasien")
    suspend fun getPasien(
        @Header("Authorization") token: String
    ): Response<PasienResponse>

    @GET("pasien/{id}")
    suspend fun getDetailPasien(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<PasienDetailResponse>
    @POST("pasien")
    suspend fun createPasien(
        @Header("Authorization") token: String,
        @Body request: PasienRequest
    ): Response<PasienSingleResponse>

    @PUT("pasien/{id}")
    suspend fun updatePasien(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: PasienRequest
    ): Response<PasienSingleResponse>

    @DELETE("pasien/{id}")
    suspend fun deletePasien(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DeleteResponse>
}