package com.rpl.sicfo.ui.notifikasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityNotifikasiBinding

class NotifikasiActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNotifikasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backToHome()
    }

    private fun backToHome() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}