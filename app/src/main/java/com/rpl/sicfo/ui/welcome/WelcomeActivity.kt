package com.rpl.sicfo.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.rpl.sicfo.MainActivity
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var adapter: WelcomeAdapter
    private lateinit var dots: Array<TextView?>
    private lateinit var layouts: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener { onClickNext() }
        binding.btnSkip.setOnClickListener { onClickSkip() }

        setupLayout()
        setupViewPager()

    }

    private fun setupLayout() {
        layouts = intArrayOf(
            R.layout.slide_1,
            R.layout.slide_2,
            R.layout.slide_3
        )
    }

    private fun onClickSkip() {
        navigateToMainActivity()
    }

    private fun onClickNext() {
        val currentPage: Int = binding.viewPager.currentItem + 1

        if (currentPage < layouts.size) {
            binding.viewPager.currentItem = currentPage
        } else {
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupViewPager() {
        adapter = WelcomeAdapter(layouts, this)
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == layouts.size - 1) {
                    binding.btnNext.text = getString(R.string.start)
                    binding.btnSkip.visibility = View.GONE
                } else {
                    binding.btnNext.text = getString(R.string.selanjutnya)
                    binding.btnSkip.visibility = View.VISIBLE
                }
                setDots(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        setDots(0)
    }

    @Suppress("DEPRECATION")
    private fun setDots(page: Int) {
        binding.indicator.removeAllViews()
        dots = arrayOfNulls(layouts.size)

        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(getColor(R.color.light_gray_2))
            binding.indicator.addView(dots[i])
        }

        if (dots.isNotEmpty()) {
            dots[page]?.setTextColor(getColor(R.color.primary))
        }
    }

    companion object {
        const val TAG = "WelcomeActivity"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PASSWORD = "extra_password"
    }
}
