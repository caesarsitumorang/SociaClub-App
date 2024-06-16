package com.rpl.sicfo.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rpl.sicfo.MainActivity
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityLoginBinding
import com.rpl.sicfo.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        sharedPreferences = getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE)

        // Cek apakah pengguna sudah login sebelumnya
        val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)
        if (isLoggedIn) {
            // Pindah ke MainActivity jika pengguna sudah login
            navigateToMainActivity()
        }

        binding.tvRegis.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btLogin.setOnClickListener {
            val npm = binding.ednpmLogin.text.toString()
            val password = binding.edpasswordLogin.text.toString()

            showLoading(true)
            loginUser(npm, password)
        }
    }

    private fun loginUser(npm: String, password: String) {
        // Ubah npm menjadi format email yang valid
        val email = "$npm@domain.com"

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    // Jika login berhasil, simpan status login di SharedPreferences
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("IS_LOGGED_IN", true)
                    editor.apply()

                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                } else {
                    // Jika login gagal, tampilkan pesan kesalahan
                    Toast.makeText(
                        this,
                        "Gagal login. Periksa NPM dan password Anda",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarLogin.visibility = View.VISIBLE
        } else {
            binding.progressBarLogin.visibility = View.GONE
        }
    }
}
