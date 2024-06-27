package com.rpl.sicfo.ui.notifikasi

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.text.style.TextAppearanceSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.rpl.sicfo.R
import com.rpl.sicfo.adapter.NotifikasiAdapter
import com.rpl.sicfo.data.Notifikasi
import com.rpl.sicfo.databinding.ActivityNotifikasiBinding

import java.text.SimpleDateFormat
import java.util.*

class NotifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotifikasiBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var notifikasiAdapter: NotifikasiAdapter
    private val notifikasiList = mutableListOf<Notifikasi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupToolbar()
        setupRecyclerView()
        loadUserNpmAndNotifications()
    }

    private fun setupToolbar() {
        val toolbar = binding.appBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val title = "Notifikasi"
        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(
            TextAppearanceSpan(this, R.style.textColorTitleWelcome),
            0,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        supportActionBar?.title = spannableTitle
        val backIcon = ContextCompat.getDrawable(this, R.drawable.back)
        backIcon?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)
            spannableTitle.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun setupRecyclerView() {
        notifikasiAdapter = NotifikasiAdapter(notifikasiList)
        binding.rvNotifikasi.apply {
            layoutManager = LinearLayoutManager(this@NotifikasiActivity)
            adapter = notifikasiAdapter
        }
    }

    private fun loadUserNpmAndNotifications() {
        val user = auth.currentUser
        if (user != null) {
            val userRef = database.child("Users").child(user.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val npm = snapshot.child("npm").value?.toString()
                    if (!npm.isNullOrEmpty()) {
                        loadNotifications(npm)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@NotifikasiActivity, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadNotifications(npm: String) {
        val notificationsRef = database.child("Notifikasi").child(npm)
        notificationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notifikasiList.clear()
                for (notifSnapshot in snapshot.children) {
                    val notifikasi = notifSnapshot.getValue(Notifikasi::class.java)
                    notifikasi?.let {
                        notifikasiList.add(it)
                    }
                }
                notifikasiAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@NotifikasiActivity, "Failed to load notifications", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
