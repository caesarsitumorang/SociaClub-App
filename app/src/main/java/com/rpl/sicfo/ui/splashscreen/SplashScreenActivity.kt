package com.rpl.sicfo.ui.splashscreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.rpl.sicfo.MainActivity
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivitySplashScreenBinding
import com.rpl.sicfo.ui.login.LoginActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Apply animations
        binding.logo.startAnimation(fadeIn)
        binding.titleApp.startAnimation(slideUp)
        binding.titleApp1.startAnimation(slideUp)

        Handler(Looper.getMainLooper()).postDelayed({
            val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)
            if (isLoggedIn && auth.currentUser != null) {
                navigateToMainActivity()
            } else {
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
