package com.rpl.sicfo.ui.profil.pengaturan

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.text.style.TextAppearanceSpan
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityPengaturanBinding
import com.rpl.sicfo.ui.login.LoginActivity
import com.rpl.sicfo.ui.tentang.TentangAppActivity


class PengaturanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaturanBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    private lateinit var tvNamaLengkap: TextView
    private lateinit var tvNpm: TextView
    private lateinit var tvSemester: TextView
    private lateinit var tvProdi: TextView
    private lateinit var tvAlamat: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaturanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().reference.child("Users").child(auth.currentUser!!.uid)

        tvNamaLengkap = binding.tvNama
        tvNpm = binding.tvNpm
        tvSemester = binding.tvSemester
        tvProdi = binding.tvProdi
        tvAlamat = binding.tvAlamat

        setupToolbar()
        onClickKeluar()
        fetchUserData()
        binding.viewTentang.setOnClickListener{
            onClickTentangApp()
        }

        binding.btnEdit.setOnClickListener {
            navigateToEditAkun()
        }
    }

    private fun setupToolbar() {
        val toolbar = binding.appBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val title = "Pengaturan"
        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(TextAppearanceSpan(this, R.style.textColorTitleWelcome), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = spannableTitle
        val backIcon = ContextCompat.getDrawable(this, R.drawable.back)
        backIcon?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)
            spannableTitle.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun onClickKeluar() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun onClickTentangApp(){
        val intent = Intent(this, TentangAppActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Keluar")
            .setMessage("Anda yakin ingin Keluar?")
            .setPositiveButton("Ya") { _, _ ->
                logout()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        auth.signOut()

        // Clear login status from SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("IS_LOGGED_IN", false)
        editor.apply()

        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun fetchUserData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val namaLengkap = snapshot.child("namaLengkap").getValue(String::class.java)
                    val npm = snapshot.child("npm").getValue(String::class.java)
                    val semester = snapshot.child("semester").getValue(String::class.java)
                    val prodi = snapshot.child("prodi").getValue(String::class.java)
                    val alamat = snapshot.child("alamat").getValue(String::class.java)

                    tvNamaLengkap.text = namaLengkap ?: "Nama Lengkap tidak tersedia"
                    tvNpm.text = npm ?: "NPM tidak tersedia"
                    tvSemester.text = semester ?: "Semester tidak tersedia"
                    tvProdi.text = prodi ?: "Program Studi tidak tersedia"
                    tvAlamat.text = alamat ?: "Alamat tidak tersedia"
                } else {
                    // Handle case where user data doesn't exist
                    tvNamaLengkap.text = "Data tidak ditemukan"
                    tvNpm.text = ""
                    tvSemester.text = ""
                    tvProdi.text = ""
                    tvAlamat.text = ""
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                tvNamaLengkap.text = "Gagal mengambil data"
                tvNpm.text = ""
                tvSemester.text = ""
                tvProdi.text = ""
                tvAlamat.text = ""
            }
        })
    }

    private fun navigateToEditAkun() {
        val intent = Intent(this, EditAkunActivity::class.java)
        startActivity(intent)
    }
}
