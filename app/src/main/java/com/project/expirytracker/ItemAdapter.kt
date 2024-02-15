package com.project.expirytracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.expirytracker.db.AppDatabase
import com.project.expirytracker.db.DatabaseModel

class ItemAdapter(private val itemList:List<ItemModel>, private val context: Context):RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val item = inflate.inflate(R.layout.card_item,parent,false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.name_item.text = item.name
        holder.price.text = item.price.toString()
        val expDate = "${item.date} - ${item.month} - ${item.year}"
        holder.expDate.text = expDate

        holder.itemView.setOnClickListener{


//            CoroutineScope(Dispatchers.IO).launch {
//                val getData = fetchDatabase()
//                getData.binarySearch{ item.id }
//            }
        }
    }

    private fun openDetails() {

    }


    class ViewHolder(private val item: View):RecyclerView.ViewHolder(item) {
        val name_item = item.findViewById<TextView>(R.id.item_name)
        val price = item.findViewById<TextView>(R.id.price)
        val expDate = item.findViewById<TextView>(R.id.exp_date)
    }

    private suspend fun fetchDatabase():List<DatabaseModel> {
        val database = AppDatabase.getDatabase(context)
        return database.databaseDao().itemData()
    }
}