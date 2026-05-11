package com.example.vehicleapiapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vehicleapiapp.model.Vehicle
import com.example.vehicleapiapp.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddVehicleActivity : AppCompatActivity() {

    private lateinit var etModel: TextInputEditText
    private lateinit var etType: TextInputEditText
    private lateinit var etManufacturer: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        // Inisialisasi view
        etModel = findViewById(R.id.etModel)
        etType = findViewById(R.id.etType)
        etManufacturer = findViewById(R.id.etManufacturer)
        btnSave = findViewById(R.id.btnSave)
        progressBar = findViewById(R.id.progressBar)

        // Setup tombol simpan
        btnSave.setOnClickListener {
            saveVehicle()
        }
    }

    private fun saveVehicle() {
        // Ambil data dari input
        val model = etModel.text.toString().trim()
        val type = etType.text.toString().trim()
        val manufacturer = etManufacturer.text.toString().trim()

        // Validasi input
        if (model.isEmpty()) {
            etModel.error = "Model tidak boleh kosong"
            etModel.requestFocus()
            return
        }

        if (type.isEmpty()) {
            etType.error = "Tipe tidak boleh kosong"
            etType.requestFocus()
            return
        }

        if (manufacturer.isEmpty()) {
            etManufacturer.error = "Produsen tidak boleh kosong"
            etManufacturer.requestFocus()
            return
        }

        // Buat object Vehicle
        val vehicle = Vehicle(
            model = model,
            type = type,
            manufacturer = manufacturer
        )

        // Kirim ke API
        createVehicle(vehicle)
    }

    private fun createVehicle(vehicle: Vehicle) {
        lifecycleScope.launch {
            showLoading(true)

            try {
                val response = RetrofitClient.apiService.createVehicle(vehicle)

                if (response.isSuccessful) {
                    showMessage("Data berhasil ditambahkan")
                    // Kembali ke MainActivity
                    finish()
                } else {
                    showMessage("Gagal menambah data: ${response.code()}")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSave.isEnabled = !isLoading
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}