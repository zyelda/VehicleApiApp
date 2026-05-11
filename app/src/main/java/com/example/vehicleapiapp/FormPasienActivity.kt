package com.example.vehicleapiapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vehicleapiapp.model.PasienRequest
import com.example.vehicleapiapp.network.RetrofitClient
import com.example.vehicleapiapp.utils.SessionManager
import kotlinx.coroutines.launch

class FormPasienActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private var pasienId: Int = -1 // -1 artinya mode Tambah (POST)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_pasien)

        sessionManager = SessionManager(this)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etTanggalLahir = findViewById<EditText>(R.id.etTanggalLahir)
        val rgJenisKelamin = findViewById<RadioGroup>(R.id.rgJenisKelamin)
        val rbLaki = findViewById<RadioButton>(R.id.rbLaki)
        val rbPerempuan = findViewById<RadioButton>(R.id.rbPerempuan)
        val etAlamat = findViewById<EditText>(R.id.etAlamat)
        val etNoTelepon = findViewById<EditText>(R.id.etNoTelepon)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val pbForm = findViewById<ProgressBar>(R.id.pbForm)

        // Cek apakah ini mode Edit (Menerima data dari MainActivity)
        pasienId = intent.getIntExtra("EDIT_ID", -1)
        if (pasienId != -1) {
            supportActionBar?.title = "Edit Pasien"
            etNama.setText(intent.getStringExtra("EDIT_NAMA"))
            etTanggalLahir.setText(intent.getStringExtra("EDIT_TGL"))
            etAlamat.setText(intent.getStringExtra("EDIT_ALAMAT"))
            etNoTelepon.setText(intent.getStringExtra("EDIT_TELP"))

            if (intent.getStringExtra("EDIT_JK") == "P") {
                rbPerempuan.isChecked = true
            } else {
                rbLaki.isChecked = true
            }
            btnSimpan.text = "Update Data"
        } else {
            supportActionBar?.title = "Tambah Pasien Baru"
        }

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val tglLahir = etTanggalLahir.text.toString().trim()
            val jk = if (rbLaki.isChecked) "L" else "P"
            val alamat = etAlamat.text.toString().trim()
            val noTelp = etNoTelepon.text.toString().trim()

            // Validasi Sederhana
            if (nama.isEmpty() || tglLahir.isEmpty() || alamat.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val requestData = PasienRequest(nama, tglLahir, jk, alamat, noTelp)
            saveData(requestData, pbForm, btnSimpan)
        }
    }

    private fun saveData(request: PasienRequest, pb: ProgressBar, btn: Button) {
        val token = sessionManager.fetchAuthToken() ?: return

        lifecycleScope.launch {
            pb.visibility = View.VISIBLE
            btn.isEnabled = false

            try {
                val response = if (pasienId == -1) {
                    RetrofitClient.apiService.createPasien("Bearer $token", request)
                } else {
                    RetrofitClient.apiService.updatePasien("Bearer $token", pasienId, request)
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@FormPasienActivity, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish() // Tutup form dan kembali ke MainActivity
                } else {
                    // Biasannya error 422 karena format tanggal salah
                    Toast.makeText(this@FormPasienActivity, "Gagal: ${response.message()} (Pastikan format tanggal YYYY-MM-DD)", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FormPasienActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                pb.visibility = View.GONE
                btn.isEnabled = true
            }
        }
    }
}