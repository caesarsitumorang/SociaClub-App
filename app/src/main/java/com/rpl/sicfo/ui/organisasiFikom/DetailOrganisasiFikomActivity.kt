package com.rpl.sicfo.ui.organisasiFikom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityDetailOrganisasiFikomBinding

class DetailOrganisasiFikomActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailOrganisasiFikomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailOrganisasiFikomBinding.inflate(layoutInflater)
        setContentView(binding.root)

       getDataDetailOrganisasi()
        binding.ibBack.setOnClickListener {
            backFromDetail()
        }
    }

    private fun backFromDetail() {
        finish()
    }
    private fun getDataDetailOrganisasi() {
        val title = intent.getStringExtra("detailTitle")
        val profil = intent.getStringExtra("profil")
        val logo = intent.getStringExtra("logo")
        val fakultas = intent.getStringExtra("fakultas")
        val image1 = intent.getStringExtra("image1")
        val struktural = intent.getStringExtra("strukturalImage")
        val visiMisi = intent.getStringExtra("visiMisi")


        binding.tvTitleOrganisasi.text = title
        binding.tvFakultas.text = fakultas
        Glide.with(this)
            .load(logo)
            .into(binding.imgLogo)
        Glide.with(this)
            .load(image1)
            .into(binding.imgDetail1)
        Glide.with(this)
            .load(visiMisi)
            .into(binding.imgDetailMisi)
        Glide.with(this)
            .load(profil)
            .into(binding.tvProfilLengkap)
        Glide.with(this)
            .load(struktural)
            .into(binding.imgStrukturalBem)
    }

}