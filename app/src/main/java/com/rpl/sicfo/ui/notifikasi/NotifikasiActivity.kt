package com.rpl.sicfo.ui.notifikasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.text.style.TextAppearanceSpan
import androidx.core.content.ContextCompat
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityNotifikasiBinding

class NotifikasiActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNotifikasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar = binding.appBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val title = "Notifikasi"
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

}