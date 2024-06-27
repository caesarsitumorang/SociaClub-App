package com.rpl.sicfo.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rpl.sicfo.MainActivity
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityLoginBinding
import com.rpl.sicfo.ui.costumView.ButtonLogin
import com.rpl.sicfo.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLogin: ButtonLogin

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

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, LupaPasswordActivity::class.java))
        }


        binding.apply {
            buttonLogin = btLogin
            buttonLogin.isEnabled = false

            ednpmLogin.addTextChangedListener(loginTextWatcher)
            edpasswordLogin.addTextChangedListener(loginTextWatcher)
            icPasswordToggle.setOnClickListener { togglePasswordVisibility() }
        }
    }

    private val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val npmInput = binding.ednpmLogin.text.toString().trim()
            val passwordInput = binding.edpasswordLogin.text.toString().trim()

            buttonLogin.isEnabled = npmInput.isNotEmpty() && passwordInput.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable?) {}
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

    private fun togglePasswordVisibility() {
        val passwordEditText = binding.edpasswordLogin
        val passwordToggle = binding.icPasswordToggle
        if (passwordEditText.inputType == 129) { // InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordEditText.inputType = 1 // InputType.TYPE_CLASS_TEXT
            passwordToggle.setImageResource(R.drawable.ic_password_visible)
        } else {
            passwordEditText.inputType = 129 // InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordToggle.setImageResource(R.drawable.ic_password)
        }
        passwordEditText.setSelection(passwordEditText.text!!.length)
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
