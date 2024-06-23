package com.rpl.sicfo.ui.voting.detailVoting

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rpl.sicfo.R
import com.rpl.sicfo.databinding.ActivityVotingBinding
import com.rpl.sicfo.ui.profil.pengaturan.PengaturanActivity

class VotingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVotingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVotingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        setupToolbar()
        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        binding.selectButton1.setOnClickListener {
            confirmVoteSelection(1)
        }

        binding.selectButton2.setOnClickListener {
            confirmVoteSelection(2)
        }

        binding.selectButton3.setOnClickListener {
            confirmVoteSelection(3)
        }
    }

    private fun confirmVoteSelection(itemNumber: Int) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Vote")
            .setMessage("Apakah kamu yakin untuk memilih calon ke $itemNumber?")
            .setPositiveButton("Yakin") { dialog, which ->
                handleVoteSelection(itemNumber)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun handleVoteSelection(itemNumber: Int) {
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid ?: return

        // Assuming NPM is stored in the user's profile in Firebase Database
        val userRef = firebaseDatabase.reference.child("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val npm = snapshot.child("npm").value.toString()
                if (npm.isNotEmpty()) {
                    saveVoteToDatabase(userId, npm, itemNumber)
                } else {
                    Toast.makeText(this@VotingActivity, "User NPM not found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VotingActivity, "Failed to retrieve user NPM!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveVoteToDatabase(userId: String, npm: String, Calon: Int) {
        val voteRef = firebaseDatabase.reference.child("votes").child(userId)
        val voteData = mapOf(
            "npm" to npm,
            "itemNumber" to Calon
        )
        voteRef.setValue(voteData)
            .addOnSuccessListener {
                Toast.makeText(this, "Vote Berhasil!", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Vote Gagal!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            val title = "Voting"
            val spannableTitle = SpannableString(title)
            spannableTitle.setSpan(
                TextAppearanceSpan(this@VotingActivity, R.style.textColorTitleWelcome),
                0,
                title.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            supportActionBar?.title = spannableTitle
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_total_voting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_total_voting -> {
                onClickTotalVoting()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onClickTotalVoting() {
        val intent = Intent(this, TotalVotingActivity::class.java)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
