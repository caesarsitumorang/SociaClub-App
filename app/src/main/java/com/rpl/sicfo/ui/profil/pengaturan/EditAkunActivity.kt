package com.rpl.sicfo.ui.profil.pengaturan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rpl.sicfo.databinding.ActivityEditAkunBinding

class EditAkunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAkunBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Users").child(auth.currentUser!!.uid)

        binding.ibBack.setOnClickListener {
            backToPengaturan()
        }

        binding.btSimpan.setOnClickListener {
            saveUserData()
        }

        // Retrieve user data and populate EditText fields
        retrieveUserData()
    }

    private fun backToPengaturan() {
        finish()
    }

    private fun retrieveUserData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val namaLengkap = snapshot.child("namaLengkap").getValue(String::class.java)
                    val npm = snapshot.child("npm").getValue(String::class.java)
                    val semester = snapshot.child("semester").getValue(String::class.java)
                    val prodi = snapshot.child("prodi").getValue(String::class.java)
                    val alamat = snapshot.child("alamat").getValue(String::class.java)

                    // Set retrieved data to EditText fields
                    binding.edEditNama.setText(namaLengkap)
                    binding.edEditNpm.setText(npm)
                    binding.edEditSemester.setText(semester)
                    binding.edEditProdi.setText(prodi)
                    binding.edEditAlamat.setText(alamat)
                } else {
                    Toast.makeText(this@EditAkunActivity, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditAkunActivity, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserData() {
        val namaLengkap = binding.edEditNama.text.toString().trim()
        val npm = binding.edEditNpm.text.toString().trim()
        val semester = binding.edEditSemester.text.toString().trim()
        val prodi = binding.edEditProdi.text.toString().trim()
        val alamat = binding.edEditAlamat.text.toString().trim()

        if (namaLengkap.isEmpty() || npm.isEmpty() || semester.isEmpty() || prodi.isEmpty() || alamat.isEmpty()) {
            Toast.makeText(this, "Harap isi semua bidang", Toast.LENGTH_SHORT).show()
            return
        }

        val userUpdates = hashMapOf<String, Any>(
            "namaLengkap" to namaLengkap,
            "npm" to npm,
            "semester" to semester,
            "prodi" to prodi,
            "alamat" to alamat
        )

        database.updateChildren(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                backToPengaturan()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memperbarui data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
