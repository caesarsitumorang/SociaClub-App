package com.rpl.sicfo.ui.klubFikom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityDetailKlubFikomBinding

class DetailKlubFikomActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailKlubFikomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailKlubFikomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val image = intent.getStringExtra("image")
        val image1 = intent.getStringExtra("image1")


        binding.tvTitleKlub.text = title
        Glide.with(this)
            .load(image)
            .into(binding.imgLogo)
        Glide.with(this)
            .load(image1)
            .into(binding.imgDetail1)
    }
}