package com.rpl.sicfo.ui.register

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityRegisterBinding
import com.rpl.sicfo.ui.welcome.WelcomeActivity
import com.rpl.sicfo.MainActivity
import com.rpl.sicfo.ui.costumView.ButtonLogin
import com.rpl.sicfo.ui.costumView.ButtonRegister
import com.rpl.sicfo.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var buttonRegister: ButtonRegister

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        sharedPreferences = getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE)

        binding.tvRegis.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btRegis.setOnClickListener {
            val namaLengkap = binding.ednamaRegister.text.toString()
            val npm = binding.ednpmRegister.text.toString()
            val email = binding.edemailRegister.text.toString()
            val password = binding.edpasswordRegister.text.toString()

            showLoading(true)
            registerFirebase(namaLengkap, npm, email, password)
        }

        binding.apply {
            buttonRegister = btRegis
            buttonRegister.isEnabled = false

            edemailRegister.addTextChangedListener(loginTextWatcher)
            ednamaRegister.addTextChangedListener(loginTextWatcher)
            ednpmRegister.addTextChangedListener(loginTextWatcher)
            edpasswordRegister.addTextChangedListener(loginTextWatcher)
            icPasswordToggle.setOnClickListener { togglePasswordVisibility() }
        }
    }

    private val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val emailInput = binding.edemailRegister.text.toString().trim()
            val namaLengkap = binding.ednamaRegister.text.toString().trim()
            val npmInput = binding.ednpmRegister.text.toString().trim()
            val passwordInput = binding.edpasswordRegister.text.toString().trim()

            buttonRegister.isEnabled = emailInput.isNotEmpty()  && namaLengkap.isNotEmpty() && npmInput.isNotEmpty() && passwordInput.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable?) {}
    }


    private fun registerFirebase(namaLengkap: String, npm: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword("$npm@domain.com", password)
            .addOnCompleteListener(this) { task ->

                binding.progressBarRegister.visibility = View.GONE

                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val uid = currentUser?.uid ?: ""

                    // Simpan informasi pengguna ke database Firebase Realtime Database
                    val userRef = database.getReference("Users").child(uid)
                    val userData = hashMapOf(
                        "namaLengkap" to namaLengkap,
                        "npm" to npm,
                        "email" to email
                    )
                    userRef.setValue(userData)
                        .addOnSuccessListener {
                            showLoading(false)
                            Log.d("RegisterActivity", "Registrasi dan penyimpanan data berhasil")
                            Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()

                            // Save login state
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("IS_LOGGED_IN", true)
                            editor.apply()

                            val intent = Intent(this, WelcomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            showLoading(false)
                            Log.e("RegisterActivity", "Gagal menyimpan informasi pengguna: ${exception.message}")
                            Toast.makeText(this, "Gagal menyimpan informasi pengguna: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    showLoading(false)
                    Log.e("RegisterActivity", "Register gagal: ${task.exception?.message}")
                    Toast.makeText(this, "Register gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun togglePasswordVisibility() {
        val passwordEditText = binding.edpasswordRegister
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarRegister.visibility = View.VISIBLE
        } else {
            binding.progressBarRegister.visibility = View.GONE
        }
    }
}
