package com.rpl.sicfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rpl.sicfo.R
import com.rpl.sicfo.data.Notifikasi

class NotifikasiAdapter(private val notifikasiList: List<Notifikasi>) : RecyclerView.Adapter<NotifikasiAdapter.NotifikasiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifikasiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notifikasi, parent, false)
        return NotifikasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifikasiViewHolder, position: Int) {
        val notifikasi = notifikasiList[position]
        holder.bind(notifikasi)
    }

    override fun getItemCount(): Int {
        return notifikasiList.size
    }

    inner class NotifikasiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNotificationMessage: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        private val tvNotificationTime: TextView = itemView.findViewById(R.id.tvNotificationTime)
        private val imgLogo: ImageView = itemView.findViewById(R.id.img_logo_notifikasi)

        fun bind(notifikasi: Notifikasi) {
            tvNotificationMessage.text = notifikasi.message
            tvNotificationTime.text = notifikasi.timestamp
            // Load logo using Glide or another image loading library
            Glide.with(itemView.context)
                .load(notifikasi.logo) // Assuming logo is stored as a drawable resource ID
                .placeholder(R.drawable.avatar) // Placeholder image
                .error(R.drawable.avatar) // Error image if loading fails
                .into(imgLogo)
        }
    }
}