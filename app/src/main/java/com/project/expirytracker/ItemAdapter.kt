package com.project.expirytracker

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.project.expirytracker.databinding.CardItemBinding
import com.project.expirytracker.db.DatabaseModel
import com.project.expirytracker.fragment.soldTrack
import java.time.LocalDate

class ItemAdapter(private val itemList:List<DatabaseModel>, private val listener: MyItemClickListener):RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = itemList.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(itemList[position], listener)
    }

    class ViewHolder(private val binding: CardItemBinding):RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: DatabaseModel, listener: MyItemClickListener) {
            binding.itemName.text = item.name
            binding.price.text = "₹ ${ item.itemPrice }"
            val expDate = "${item.expDate} - ${item.expMonth} - ${item.expYear}"
            binding.expDate.text = expDate
            val fromDate = LocalDate.of(item.expYear.toInt(),item.expMonth.toInt(),item.expDate.toInt())
            val temp = soldTrack(item.quantity,fromDate,item.arrayData).toInt()

            if(temp>0){
                binding.pWaste.setTextColor(Color.parseColor("#B83039"))
                binding.pWaste.text = "Probable Wastage: ${temp+1}"
            }else{
                val x = -temp+1
                binding.pWaste.setTextColor(Color.parseColor("#3F68A5"))
                binding.pWaste.text = "Probable Shortage: $x"
            }

            binding.root.setOnClickListener {
                listener.onItemClicked(item)
            }
            binding.root.setOnLongClickListener{
                listener.onLongPress(item.id)
                return@setOnLongClickListener true
            }
        }
    }
}


interface MyItemClickListener {
    fun onItemClicked(item: DatabaseModel)
    fun onLongPress(item: Int)
}

