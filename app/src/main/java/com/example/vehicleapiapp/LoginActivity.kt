package com.example.vehicleapiapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vehicleapiapp.model.LoginRequest
import com.example.vehicleapiapp.network.RetrofitClient
import com.example.vehicleapiapp.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // Cek apakah sudah login (token ada), langsung lempar ke MainActivity
        if (sessionManager.fetchAuthToken() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val pbLogin = findViewById<ProgressBar>(R.id.pbLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                pbLogin.visibility = View.VISIBLE
                btnLogin.isEnabled = false

                try {
                    val request = LoginRequest(email, password)
                    val response = RetrofitClient.apiService.login(request)

                    if (response.isSuccessful && response.body()?.success == true) {
                        val token = response.body()?.data?.token
                        if (token != null) {
                            // Simpan token
                            sessionManager.saveAuthToken(token)
                            Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()

                            // Pindah halaman
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        // Response 401 atau error lain dari API
                        Toast.makeText(this@LoginActivity, "Email atau password salah", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    pbLogin.visibility = View.GONE
                    btnLogin.isEnabled = true
                }
            }
        }
    }
}