package com.rpl.sicfo.ui.organizer

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rpl.sicfo.adapter.MessageAdapter
import com.rpl.sicfo.data.Message
import com.rpl.sicfo.databinding.ActivityForumChatBinding
import com.rpl.sicfo.ui.profil.ButtonSheetPicture
import java.util.*

class ForumChatActivity : AppCompatActivity(), ButtonSheetPicture.OnImageSelectedListener {

    private lateinit var binding: ActivityForumChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var messageAdapter: MessageAdapter
    private val messageList: MutableList<Message> = mutableListOf()
    private var organizationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("imageChat")

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
        binding.backtoOrganizer.setOnClickListener {
            backToOrganizer()
        }
        binding.btnUploadImage.setOnClickListener {
            showImagePicker()
        }

        fetchUsernameAndMessages()
    }

    private fun backToOrganizer() {
        finish()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(messageList)
        binding.rvMessages.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@ForumChatActivity)
        }
    }

    private fun sendMessage(imageUrl: String? = null) {
        val currentUser = auth.currentUser
        val messageText = binding.etMessage.text.toString().trim()

        if ((messageText.isNotEmpty() || imageUrl != null) && currentUser != null) {
            fetchUsername(currentUser.uid) { username ->
                if (username.isNotEmpty()) {
                    val message = Message(currentUser.uid, username, messageText, imageUrl, Date().time)
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
                // Toast.makeText(this@ForumChatActivity, "Failed to load messages: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUsername(userId: String, callback: (String) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("namaLengkap").getValue(String::class.java) ?: ""
                val kataPertama = username.split(" ")[0]
                callback(kataPertama)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForumChatActivity, "Failed to fetch username: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showImagePicker() {
        val buttonSheetPicture = ButtonSheetPicture()
        buttonSheetPicture.setOnImageSelectedListener(this)
        buttonSheetPicture.show(supportFragmentManager, ButtonSheetPicture.TAG)
    }

    override fun onImageSelected(uri: Uri) {
        showConfirmationDialog(uri)
    }

    private fun showConfirmationDialog(uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Kirim Gambar")
            .setMessage("Kamu Yakin Ingin Mengirim Foto Ini?")
            .setPositiveButton("Kirim") { _, _ -> uploadImage(uri) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun uploadImage(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        val fileName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("$fileName.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    sendMessage(downloadUrl.toString())
                    binding.progressBar.visibility = View.GONE
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
    }
}
