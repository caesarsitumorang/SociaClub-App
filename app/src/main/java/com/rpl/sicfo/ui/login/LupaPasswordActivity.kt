package com.rpl.sicfo.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.rpl.sicfo.adapter.User
import com.rpl.sicfo.databinding.ActivityLupaPasswordBinding

class LupaPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLupaPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLupaPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("Users")

        binding.btResetPassword.isEnabled = false
        binding.edNPM.addTextChangedListener(textWatcher)
        binding.edNamaLengkap.addTextChangedListener(textWatcher)

        binding.btResetPassword.setOnClickListener {
            val namaLengkap = binding.edNamaLengkap.text.toString().trim()
            val npm = binding.edNPM.text.toString().trim()
            val newPassword = binding.edNewPassword.text.toString().trim()
            resetPassword(namaLengkap, npm, newPassword)
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val npmInput = binding.edNPM.text.toString().trim()
            val namaLengkapInput = binding.edNamaLengkap.text.toString().trim()
            val newPasswordInput = binding.edNewPassword.text.toString().trim()

            binding.btResetPassword.isEnabled = npmInput.isNotEmpty() &&
                    namaLengkapInput.isNotEmpty() &&
                    newPasswordInput.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable?) {}
    }


    private fun resetPassword(namaLengkap: String, npm: String, newPassword: String) {
        val userQuery = userRef.orderByChild("npm").equalTo(npm)
        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var isMatched = false
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null && user.namaLengkap == namaLengkap) {
                            isMatched = true
                            // Update password here
                            val email = "$npm@domain.com"
                            auth.signInWithEmailAndPassword(email, "TEMP_PASSWORD_FOR_AUTH")
                                .addOnCompleteListener { authTask ->
                                    if (authTask.isSuccessful) {
                                        auth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { updateTask ->
                                            if (updateTask.isSuccessful) {
                                                // Simpan newPassword ke Firebase Database
                                                userRef.child(userSnapshot.key!!).child("newPassword").setValue(newPassword)
                                                    .addOnCompleteListener { saveTask ->
                                                        if (saveTask.isSuccessful) {
                                                            Toast.makeText(this@LupaPasswordActivity, "Password reset successful", Toast.LENGTH_SHORT).show()
                                                            startActivity(Intent(this@LupaPasswordActivity, LoginActivity::class.java))
                                                            finish()
                                                        } else {
                                                            Toast.makeText(this@LupaPasswordActivity, "Failed to reset password", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                            } else {
                                                Toast.makeText(this@LupaPasswordActivity, "Failed to update password", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this@LupaPasswordActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            break
                        }
                    }
                    if (!isMatched) {
                        Toast.makeText(this@LupaPasswordActivity, "Nama Lengkap atau NPM tidak sesuai", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LupaPasswordActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LupaPasswordActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
