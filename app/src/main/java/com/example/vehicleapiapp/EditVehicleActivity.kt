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

class EditVehicleActivity : AppCompatActivity() {

    private lateinit var etModel: TextInputEditText
    private lateinit var etType: TextInputEditText
    private lateinit var etManufacturer: TextInputEditText
    private lateinit var btnUpdate: Button
    private lateinit var progressBar: ProgressBar

    private var vehicleId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_vehicle)

        // Inisialisasi view
        etModel = findViewById(R.id.etModel)
        etType = findViewById(R.id.etType)
        etManufacturer = findViewById(R.id.etManufacturer)
        btnUpdate = findViewById(R.id.btnUpdate)
        progressBar = findViewById(R.id.progressBar)

        // Ambil data dari Intent extras
        vehicleId = intent.getIntExtra("VEHICLE_ID", 0)
        val model = intent.getStringExtra("VEHICLE_MODEL") ?: ""
        val type = intent.getStringExtra("VEHICLE_TYPE") ?: ""
        val manufacturer = intent.getStringExtra("VEHICLE_MANUFACTURER") ?: ""

        // Validasi: pastikan ID valid
        if (vehicleId == 0) {
            showMessage("Data vehicle tidak ditemukan")
            finish()
            return
        }

        // Isi form dengan data existing
        etModel.setText(model)
        etType.setText(type)
        etManufacturer.setText(manufacturer)

        // Setup tombol update
        btnUpdate.setOnClickListener {
            updateVehicle()
        }
    }

    private fun updateVehicle() {
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

        // Buat object Vehicle dengan data baru
        val vehicle = Vehicle(
            model = model,
            type = type,
            manufacturer = manufacturer
        )

        // Kirim update ke API
        sendUpdateToApi(vehicleId, vehicle)
    }

    private fun sendUpdateToApi(id: Int, vehicle: Vehicle) {
        lifecycleScope.launch {
            showLoading(true)

            try {
                val response = RetrofitClient.apiService.updateVehicle(id, vehicle)

                if (response.isSuccessful) {
                    showMessage("Data berhasil diupdate")
                    // Kembali ke MainActivity
                    finish()
                } else {
                    showMessage("Gagal update data: ${response.code()}")
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
        btnUpdate.isEnabled = !isLoading
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}