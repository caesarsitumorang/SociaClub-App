package com.rpl.sicfo.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
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
                (binding.imageMessage.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.END
            } else {
                // If sender is other user, align message to the left
                (binding.tvUsername.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.START
                (binding.tvMessage.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.START
                (binding.imageMessage.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.START
            }

            if (message.imageUrl != null) {
                binding.imageMessage.visibility = View.VISIBLE
                binding.tvMessage.visibility = if (message.message.isEmpty()) View.GONE else View.VISIBLE
                Glide.with(binding.imageMessage.context)
                    .load(message.imageUrl)
                    .apply(RequestOptions().transform(RoundedCorners(16)))
                    .placeholder(R.drawable.bg_chat_user)
                    .into(binding.imageMessage)
            } else {
                binding.imageMessage.visibility = View.GONE
                binding.tvMessage.visibility = View.VISIBLE
            }
        }
    }
}
