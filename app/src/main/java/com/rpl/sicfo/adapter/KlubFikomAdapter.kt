package com.rpl.sicfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rpl.sicfo.R
import com.rpl.sicfo.data.KlubFikom
import com.squareup.picasso.Picasso

class KlubFikomAdapter(
    private val klubList: List<KlubFikom>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<KlubFikomAdapter.KlubViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KlubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_klub_fikom, parent, false)
        return KlubViewHolder(view)
    }

    override fun onBindViewHolder(holder: KlubViewHolder, position: Int) {
        val klub = klubList[position]
        holder.bind(klub)
    }

    override fun getItemCount(): Int {
        return klubList.size
    }

    inner class KlubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.img_klub_photo)
        private val textViewTitle: TextView = itemView.findViewById(R.id.tv_klub_name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(klubList[position])
                }
            }
        }

        fun bind(klub: KlubFikom) {
            textViewTitle.text = klub.title
            Picasso.get().load(klub.logo).into(imageView)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(klub: KlubFikom)
    }
}
