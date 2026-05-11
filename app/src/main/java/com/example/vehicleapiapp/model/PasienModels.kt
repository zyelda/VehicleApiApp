package com.example.vehicleapiapp.model

data class PasienResponse(
    val success: Boolean,
    val message: String,
    val data: List<Pasien>?
)

data class Pasien(
    val id: Int,
    val nama: String,
    val tanggal_lahir: String,
    val jenis_kelamin: String,
    val alamat: String,
    val no_telepon: String
)

data class PasienDetailResponse(
    val success: Boolean,
    val message: String,
    val data: PasienDetail?
)

data class PasienDetail(
    val id: Int,
    val nama: String,
    val tanggal_lahir: String,
    val jenis_kelamin: String,
    val alamat: String,
    val no_telepon: String,
    val penyakit: List<Penyakit>?
)

data class Penyakit(
    val id: Int,
    val kode_icd: String,
    val nama: String,
    val deskripsi: String,
    val kategori: String
)

data class PasienRequest(
    val nama: String,
    val tanggal_lahir: String,
    val jenis_kelamin: String,
    val alamat: String,
    val no_telepon: String
)

data class PasienSingleResponse(
    val success: Boolean,
    val message: String,
    val data: Pasien?
)

data class DeleteResponse(
    val success: Boolean,
    val message: String
)