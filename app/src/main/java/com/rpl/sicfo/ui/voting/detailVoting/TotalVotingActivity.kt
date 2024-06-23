package com.rpl.sicfo.ui.voting.detailVoting

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityTotalVotingBinding

class TotalVotingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTotalVotingBinding
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTotalVotingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()

        setupToolbar()
        displayVoteCounts()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            val title = "Total Voting"
            val spannableTitle = SpannableString(title)
            spannableTitle.setSpan(
                TextAppearanceSpan(this@TotalVotingActivity, R.style.textColorTitleWelcome),
                0,
                title.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            supportActionBar?.title = spannableTitle
        }
    }

    private fun displayVoteCounts() {
        val votesRef = firebaseDatabase.reference.child("votes")
        votesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val voteCounts = mutableMapOf<Int, Int>()

                // Initialize counts
                for (i in 1..3) {
                    voteCounts[i] = 0
                }

                // Count votes for each item
                for (voteSnapshot in snapshot.children) {
                    val itemNumber = voteSnapshot.child("itemNumber").getValue(Int::class.java)
                    if (itemNumber != null) {
                        voteCounts[itemNumber] = voteCounts.getOrDefault(itemNumber, 0) + 1
                    }
                }

                // Display counts
                binding.item1VotesTextView.text = "${voteCounts[1]} Suara"
                binding.item2VotesTextView.text = "${voteCounts[2]} Suara"
                binding.item3VotesTextView.text = "${voteCounts[3]} Suara"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TotalVotingActivity, "Gagal Mendapatkan Jumlah Voting", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
