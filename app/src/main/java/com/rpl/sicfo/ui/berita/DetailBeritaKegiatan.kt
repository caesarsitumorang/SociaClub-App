package com.rpl.sicfo.ui.berita

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityDetailBeritaKegiatanBinding

class DetailBeritaKegiatan : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBeritaKegiatanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBeritaKegiatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val imageUrl = intent.getStringExtra("imageUrl")
        val waktu = intent.getStringExtra("waktu")
        val information = intent.getStringExtra("information")

        // Show ProgressBar
        binding.progressBar.visibility = View.VISIBLE

        // Load content
        binding.tvTitleDetail.text = title
        binding.tvWaktuDetail.text = waktu
        binding.tvInformationDetail.text = information
        Glide.with(this)
            .load(imageUrl)
            .into(binding.imgDetail)

        // Hide ProgressBar when content is loaded
        binding.progressBar.visibility = View.GONE
        backtoHome()
    }

    private fun backtoHome() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}