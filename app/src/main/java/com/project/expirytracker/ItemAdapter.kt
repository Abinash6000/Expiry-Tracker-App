package com.project.expirytracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.expirytracker.databinding.CardItemBinding
import com.project.expirytracker.db.AppDatabase
import com.project.expirytracker.db.DatabaseModel

class ItemAdapter(private val itemList:List<DatabaseModel>, private val listener: MyItemClickListener):RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], listener)
    }

    class ViewHolder(private val binding: CardItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DatabaseModel, listener: MyItemClickListener) {
            binding.itemName.text = item.name
            binding.price.text = item.itemPrice.toString()
            val expDate = "${item.expDate} - ${item.expMonth} - ${item.expYear}"
            binding.expDate.text = expDate

            binding.root.setOnClickListener {
                listener.onItemClicked(item)
            }
        }
    }
}

interface MyItemClickListener {
    fun onItemClicked(item: DatabaseModel)
}
