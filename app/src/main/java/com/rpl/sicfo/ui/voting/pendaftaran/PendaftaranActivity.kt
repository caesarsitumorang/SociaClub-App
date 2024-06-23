package com.rpl.sicfo.ui.voting.pendaftaran

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityPendaftaranBinding
import java.util.Calendar

class PendaftaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendaftaranBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendaftaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupToolbar()
        setupYearSpinner()
        loadUserData()
        registerMember()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            val title = "Pendaftaran"
            val spannableTitle = SpannableString(title)
            spannableTitle.setSpan(
                TextAppearanceSpan(this@PendaftaranActivity, R.style.textColorTitleWelcome),
                0,
                title.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            supportActionBar?.title = spannableTitle
        }
    }

    private fun setupYearSpinner() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 50..currentYear).map { it.toString() }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTahunMasuk.adapter = adapter
        binding.spinnerTahunMasuk.setSelection(years.indexOf(currentYear.toString()))
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            // Get user email and NPM
            val email = user.email
            val npm = email?.substringBefore("@")
            binding.etNpm.setText(npm)

            // Retrieve full name from Firebase user profile or another source
            val userRef = database.child("Users").child(user.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fullName = snapshot.child("namaLengkap").value?.toString()
                    if (!fullName.isNullOrEmpty()) {
                        binding.etNama.setText(fullName)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PendaftaranActivity, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun registerMember() {
        binding.btnSubmit.setOnClickListener {

            // Retrieve data from input fields
            val namaLengkap = binding.etNama.text.toString().trim()
            val npm = binding.etNpm.text.toString().trim()
            val jurusan = binding.spinnerJurusan.selectedItem.toString().trim()
            val tahunMasuk = binding.spinnerTahunMasuk.selectedItem.toString().trim()
            val organisasiTitle = intent.getStringExtra("organisasi_title")

            if (namaLengkap.isNotEmpty() && npm.isNotEmpty() && jurusan.isNotEmpty() && tahunMasuk.isNotEmpty() && organisasiTitle != null) {
                val newPendaftarRef = database.child("DaftarPendaftar").child(organisasiTitle).child(npm)

                // Data to be stored
                val pendaftarData = hashMapOf(
                    "namaLengkap" to namaLengkap,
                    "npm" to npm,
                    "jurusan" to jurusan,
                    "tahunMasuk" to tahunMasuk
                )

                newPendaftarRef.setValue(pendaftarData)
                    .addOnSuccessListener {
                        // Handle success
                        Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
                        finish() // Close activity after successful registration
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Toast.makeText(this, "Pendaftaran gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
