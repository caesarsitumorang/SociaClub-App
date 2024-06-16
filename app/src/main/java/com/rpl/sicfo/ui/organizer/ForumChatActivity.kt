package com.rpl.sicfo.ui.organizer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.rpl.sicfo.adapter.MessageAdapter
import com.rpl.sicfo.data.Message
import com.rpl.sicfo.databinding.ActivityForumChatBinding
import java.util.*

class ForumChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForumChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var messageAdapter: MessageAdapter
    private val messageList: MutableList<Message> = mutableListOf()
    private var organizationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        organizationId = intent.getStringExtra("organizationId")
        if (organizationId == null) {
            Toast.makeText(this, "Organization ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        database = FirebaseDatabase.getInstance().reference.child("Messages").child(organizationId!!)

        setupRecyclerView()

        binding.btnSendMessage.setOnClickListener {
            sendMessage()
        }

        fetchUsernameAndMessages()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(messageList)
        binding.rvMessages.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@ForumChatActivity)
        }
    }

    private fun sendMessage() {
        val currentUser = auth.currentUser
        val messageText = binding.etMessage.text.toString().trim()

        if (messageText.isNotEmpty() && currentUser != null) {
            fetchUsername(currentUser.uid) { username ->
                if (username.isNotEmpty()) {
                    val message = Message(currentUser.uid, username, messageText, Date().time)
                    val messageRef = database.push()
                    messageRef.setValue(message)
                        .addOnSuccessListener {
                            binding.etMessage.text.clear()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun fetchUsernameAndMessages() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messageList.add(it)
                    messageAdapter.notifyItemInserted(messageList.size - 1)
                    binding.rvMessages.smoothScrollToPosition(messageList.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle changed data (not needed for chat app)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle removed data (not needed for chat app)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle moved data (not needed for chat app)
            }

            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@ForumChatActivity, "Failed to load messages: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUsername(userId: String, callback: (String) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("namaLengkap").getValue(String::class.java) ?: ""
                callback(username)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForumChatActivity, "Failed to fetch username: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
