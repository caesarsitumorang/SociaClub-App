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
        val profil1 = intent.getStringExtra("tvProfil1")
        val profil2 = intent.getStringExtra("tvProfil2")
        val profil3 = intent.getStringExtra("tvProfil3")
        val profil4 = intent.getStringExtra("tvProfil4")
        val profil5 = intent.getStringExtra("tvProfil5")
        val detailProfil = intent.getStringExtra("detailProfil")
        val logo = intent.getStringExtra("logo")
        val fakultas = intent.getStringExtra("fakultas")
        val image1 = intent.getStringExtra("image1")
        val struktural = intent.getStringExtra("strukturalImage")
        val detailVisi = intent.getStringExtra("tvDetailVisi")
        val misi1 = intent.getStringExtra("tvDetailMisi1")
        val misi2 = intent.getStringExtra("tvDetailMisi2")
        val misi3 = intent.getStringExtra("tvDetailMisi3")
        val misi4 = intent.getStringExtra("tvDetailMisi4")
        val misi5 = intent.getStringExtra("tvDetailMisi5")
        val ajakanProfil = intent.getStringExtra("tvAjakanProfil")

        binding.tvDetailProfil.text = detailProfil
        binding.tvProfil1.text = profil1
        binding.tvProfil2.text = profil2
        binding.tvProfil3.text = profil3
        binding.tvProfil4.text = profil4
        binding.tvProfil5.text = profil5
        binding.tvAjakanProfil.text =ajakanProfil
        binding.tvDetailVisi.text = detailVisi
        binding.tvTitleDetailOrganisasi.text = title
        binding.tvDetailMisi1.text = misi1
        binding.tvDetailMisi2.text = misi2
        binding.tvDetailMisi3.text = misi3
        binding.tvDetailMisi4.text = misi4
        binding.tvDetailMisi5.text = misi5
        binding.fak.text = fakultas
        Glide.with(this)
            .load(logo)
            .into(binding.logobem)
        Glide.with(this)
            .load(image1)
            .into(binding.imgWelcome)
        Glide.with(this)
            .load(struktural)
            .into(binding.imgStrukturalBem)
    }

}