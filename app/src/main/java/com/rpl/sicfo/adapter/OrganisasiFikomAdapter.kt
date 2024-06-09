package com.rpl.sicfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rpl.sicfo.R
import com.rpl.sicfo.data.Organisasi
import com.squareup.picasso.Picasso

class OrganisasiFikomAdapter (
    private val organisasi: List<Organisasi>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<OrganisasiFikomAdapter.OrganisasiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganisasiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_organisasi_fikom, parent, false)
        return OrganisasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrganisasiViewHolder, position: Int) {
        val organisasi = organisasi[position]
        holder.bind(organisasi)
    }

    override fun getItemCount(): Int {
        return organisasi.size
    }

    inner class OrganisasiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.img_item_logo_organisasi)
        private val textViewTitle: TextView = itemView.findViewById(R.id.tv_title_organisasi)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(organisasi[position])
                }
            }
        }

        fun bind(organisasi: Organisasi) {
            textViewTitle.text = organisasi.title
            Picasso.get().load(organisasi.logo).into(imageView)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(organisasi: Organisasi)
    }
}