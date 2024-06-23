package com.rpl.sicfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rpl.sicfo.data.KlubFikom
import com.rpl.sicfo.data.Organisasi
import com.rpl.sicfo.databinding.ItemPendaftaranBinding

class PendaftaranAdapter(
    private val dataList: MutableList<Any>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PendaftaranAdapter.PendaftaranViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: Any)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendaftaranViewHolder {
        val binding = ItemPendaftaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendaftaranViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendaftaranViewHolder, position: Int) {
        holder.bind(dataList[position], itemClickListener)
    }

    override fun getItemCount(): Int = dataList.size

    inner class PendaftaranViewHolder(private val binding: ItemPendaftaranBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any, clickListener: OnItemClickListener) {
            when (item) {
                is KlubFikom -> {
                    binding.tvTitleSearch.text = item.title
                    Glide.with(binding.root.context)
                        .load(item.image)
                        .into(binding.imgLogoPendaftaran)

                    binding.root.setOnClickListener {
                        clickListener.onItemClick(item)
                    }
                }
                is Organisasi -> {
                    binding.tvTitleSearch.text = item.title
                    Glide.with(binding.root.context)
                        .load(item.logo)
                        .into(binding.imgLogoPendaftaran)

                    binding.root.setOnClickListener {
                        clickListener.onItemClick(item)
                    }
                }
            }
        }
    }
}
