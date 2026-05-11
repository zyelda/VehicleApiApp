package com.example.vehicleapiapp

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vehicleapiapp.network.RetrofitClient
import com.example.vehicleapiapp.utils.SessionManager
import kotlinx.coroutines.launch

class DetailPasienActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pasien)

        sessionManager = SessionManager(this)

        // Mengambil ID yang dikirim dari MainActivity
        val pasienId = intent.getIntExtra("PASIEN_ID", -1)

        if (pasienId != -1) {
            loadDetailPasien(pasienId)
        } else {
            Toast.makeText(this, "ID Pasien tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadDetailPasien(id: Int) {
        val token = sessionManager.fetchAuthToken() ?: return
        val pbDetail = findViewById<ProgressBar>(R.id.pbDetail)
        val tvDetailNama = findViewById<TextView>(R.id.tvDetailNama)
        val tvDetailInfo = findViewById<TextView>(R.id.tvDetailInfo)
        val tvDaftarPenyakit = findViewById<TextView>(R.id.tvDaftarPenyakit)

        lifecycleScope.launch {
            pbDetail.visibility = View.VISIBLE
            try {
                val response = RetrofitClient.apiService.getDetailPasien("Bearer $token", id)

                if (response.isSuccessful && response.body()?.success == true) {
                    val detail = response.body()?.data

                    if (detail != null) {
                        tvDetailNama.text = detail.nama
                        tvDetailInfo.text = "Tgl Lahir: ${detail.tanggal_lahir}\n" +
                                "Gender: ${detail.jenis_kelamin}\n" +
                                "Telepon: ${detail.no_telepon}\n" +
                                "Alamat: ${detail.alamat}"

                        // Menyusun daftar penyakit menjadi satu teks panjang
                        val penyakitList = detail.penyakit
                        if (penyakitList.isNullOrEmpty()) {
                            tvDaftarPenyakit.text = "- Tidak ada riwayat penyakit -"
                        } else {
                            var textPenyakit = ""
                            penyakitList.forEachIndexed { index, p ->
                                textPenyakit += "${index + 1}. [${p.kode_icd}] ${p.nama}\n   Kategori: ${p.kategori}\n   Ket: ${p.deskripsi}\n\n"
                            }
                            tvDaftarPenyakit.text = textPenyakit
                        }
                    }
                } else {
                    Toast.makeText(this@DetailPasienActivity, "Gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailPasienActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                pbDetail.visibility = View.GONE
            }
        }
    }
}