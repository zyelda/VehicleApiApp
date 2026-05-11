package com.example.vehicleapiapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleapiapp.adapter.PasienAdapter
import com.example.vehicleapiapp.model.Pasien
import com.example.vehicleapiapp.network.RetrofitClient
import com.example.vehicleapiapp.utils.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var rvPasien: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: PasienAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        // Proteksi: Jika tidak ada token, paksa balik ke Login
        if (sessionManager.fetchAuthToken() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 1. Inisialisasi SEMUA View (Sangat penting agar tidak crash)
        rvPasien = findViewById(R.id.rvPasien)
        progressBar = findViewById(R.id.progressBar)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        // 2. Setup Tombol
        btnLogout.setOnClickListener {
            performLogout()
        }

        fabAdd.setOnClickListener {
            val intent = Intent(this, FormPasienActivity::class.java)
            startActivity(intent)
        }

        // 3. Setup Adapter dengan 3 aksi klik
        adapter = PasienAdapter(
            onItemClick = { pasienId ->
                val intent = Intent(this, DetailPasienActivity::class.java)
                intent.putExtra("PASIEN_ID", pasienId)
                startActivity(intent)
            },
            onEditClick = { pasien ->
                val intent = Intent(this, FormPasienActivity::class.java)
                intent.putExtra("EDIT_ID", pasien.id)
                intent.putExtra("EDIT_NAMA", pasien.nama)
                intent.putExtra("EDIT_TGL", pasien.tanggal_lahir)
                intent.putExtra("EDIT_JK", pasien.jenis_kelamin)
                intent.putExtra("EDIT_ALAMAT", pasien.alamat)
                intent.putExtra("EDIT_TELP", pasien.no_telepon)
                startActivity(intent)
            },
            onDeleteClick = { pasien ->
                showDeleteDialog(pasien)
            }
        )

        rvPasien.layoutManager = LinearLayoutManager(this)
        rvPasien.adapter = adapter

        // 4. Load Data Pertama Kali
        loadPasien()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data tiap kali kembali ke halaman ini
        if (sessionManager.fetchAuthToken() != null) {
            loadPasien()
        }
    }

    private fun loadPasien() {
        val token = sessionManager.fetchAuthToken() ?: return

        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.apiService.getPasien("Bearer $token")

                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    adapter.setData(list)

                    if (list.isEmpty()) {
                        showMessage("Data pasien kosong")
                    }
                } else if (response.code() == 401) {
                    sessionManager.clearSession()
                    showMessage("Sesi berakhir, silakan login kembali")
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                } else {
                    showMessage("Gagal memuat data: ${response.message()}")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showDeleteDialog(pasien: Pasien) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Yakin ingin menghapus ${pasien.nama}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                performDelete(pasien.id)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performDelete(id: Int) {
        val token = sessionManager.fetchAuthToken() ?: return
        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.apiService.deletePasien("Bearer $token", id)
                if (response.isSuccessful) {
                    showMessage("Data berhasil dihapus")
                    loadPasien()
                } else {
                    showMessage("Gagal menghapus: ${response.code()}")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun performLogout() {
        val token = sessionManager.fetchAuthToken() ?: return

        lifecycleScope.launch {
            showLoading(true)
            try {
                RetrofitClient.apiService.logout("Bearer $token")
                sessionManager.clearSession()
                showMessage("Berhasil Logout")
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}