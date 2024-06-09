package com.rpl.sicfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rpl.sicfo.R
import com.rpl.sicfo.data.Berita
import com.squareup.picasso.Picasso

class BeritaOrganisasiAdapter(
    private val beritaList: List<Berita>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<BeritaOrganisasiAdapter.BeritaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_berita_kegiatan, parent, false)
        return BeritaViewHolder(view)
    }


    override fun onBindViewHolder(holder: BeritaViewHolder, position: Int) {
        val berita = beritaList[position]
        holder.bind(berita)
    }

    override fun getItemCount(): Int {
        return beritaList.size
    }

    inner class BeritaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.img_item_photo)
        private val textViewTitle: TextView = itemView.findViewById(R.id.tv_item_name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(beritaList[position])
                }
            }
        }
        fun bind(berita: Berita) {
            textViewTitle.text = berita.title
            Picasso.get().load(berita.imageUrl).into(imageView)
        }
    }
    interface OnItemClickListener {
        fun onItemClick(berita: Berita)
    }
}