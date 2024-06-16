package com.rpl.sicfo.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.rpl.sicfo.R
import com.rpl.sicfo.data.Message
import com.rpl.sicfo.databinding.ItemMessageBinding

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.tvUsername.text = message.username
            binding.tvMessage.text = message.message

            // Determine alignment of message based on sender
            if (message.userId == auth.currentUser?.uid) {
                // If sender is current user, align message to the right
                (binding.tvUsername.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.END
                (binding.tvMessage.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.END
//                binding.messageContainer.setBackgroundResource(R.drawable.bg_button)
            } else {
                // If sender is other user, align message to the left
                (binding.tvUsername.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.START
                (binding.tvMessage.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.START
//                binding.messageContainer.setBackgroundResource(R.drawable.bg_button_disable)
            }
        }
    }
}
