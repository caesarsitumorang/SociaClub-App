package com.rpl.sicfo.ui.splashscreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.rpl.sicfo.MainActivity
import com.rpl.sicfo.R
import com.rpl.sicfo.ui.login.LoginActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE)

        Handler(Looper.getMainLooper()).postDelayed({
            // Cek apakah pengguna sudah login sebelumnya
            val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)
            if (isLoggedIn && auth.currentUser != null) {
                // Pindah ke MainActivity jika pengguna sudah login
                navigateToMainActivity()
            } else {
                // Pindah ke LoginActivity jika pengguna belum login
                navigateToLoginActivity()
            }
        }, 3000) // Delay selama 3 detik
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
