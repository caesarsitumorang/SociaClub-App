package com.rpl.sicfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rpl.sicfo.data.KlubFikom
import com.rpl.sicfo.data.Organisasi
import com.rpl.sicfo.databinding.ItemSearchResultBinding

class SearchAdapter(
    private val dataList: MutableList<Any>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: Any)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(dataList[position], itemClickListener)
    }

    override fun getItemCount(): Int = dataList.size

    inner class SearchViewHolder(private val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any, clickListener: OnItemClickListener) {
            when (item) {
                is KlubFikom -> {
                    binding.tvTitleSearch.text = item.title
                    Glide.with(binding.root.context)
                        .load(item.image)
                        .into(binding.imgLogoSearch)

                    binding.root.setOnClickListener {
                        clickListener.onItemClick(item)
                    }
                }
                is Organisasi -> {
                    binding.tvTitleSearch.text = item.title
                    Glide.with(binding.root.context)
                        .load(item.logo)
                        .into(binding.imgLogoSearch)

                    binding.root.setOnClickListener {
                        clickListener.onItemClick(item)
                    }
                }
            }
        }
    }
}
