package com.project.expirytracker.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.expirytracker.db.AppDatabase
import com.project.expirytracker.db.DatabaseModel
import com.project.expirytracker.ItemAdapter
import com.project.expirytracker.MyItemClickListener
import com.project.expirytracker.R
import com.project.expirytracker.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

public var itemList : ArrayList<DatabaseModel> = ArrayList()
public var adapter : ItemAdapter? = null

class HomeFragment : Fragment(), MyItemClickListener {

    val customMenu = com.project.expirytracker.FilterMenu()
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.faButton.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_addItemFragment)
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = ItemAdapter(itemList, this)
        CoroutineScope(Dispatchers.IO).launch {
            val getData = fetchDatabase(requireContext())
            itemList.clear()
            itemList.addAll(getData)
            withContext(Dispatchers.Main){
                adapter?.notifyDataSetChanged()
            }
        }

        val recyle = requireView().findViewById<RecyclerView>(R.id.item_view)
        recyle.layoutManager = LinearLayoutManager(requireContext())
        recyle.adapter = adapter

        var x = ""
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    x =newText
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = AppDatabase.getDatabase(requireContext())
                        val list = database.databaseDao().searchItem(x)
                        itemList.clear()
                        itemList.addAll(list)
                        withContext(Dispatchers.Main){
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }
                return true
            }
        })

        binding.filterBtn.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                val getData = fetchDatabase(requireContext())
                itemList.clear()
                itemList.addAll(getData)
                withContext(Dispatchers.Main){
                    adapter?.notifyDataSetChanged()
                }
            }
            customMenu.showMenu(requireContext(),it)
        }
    }



    override fun onItemClicked(item: DatabaseModel) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment2(item)
        findNavController().navigate(action)
    }

    override fun onLongPress(item: Int) {

        val inflater = layoutInflater
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Delete")
        builder.setPositiveButton("DELETE"){dialog,it->
            CoroutineScope(Dispatchers.IO).launch {
                deleteItem(requireContext(),item)
                val getData = fetchDatabase(requireContext())
                itemList.clear()
                itemList.addAll(getData)
                withContext(Dispatchers.Main){
                    adapter?.notifyDataSetChanged()
                }
            }
        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.show()

    }
}
private suspend fun deleteItem(context: Context,id:Int){
    val db = AppDatabase.getDatabase(context)
    db.databaseDao().delete(id)
}
public suspend fun fetchDatabase(context: Context):List<DatabaseModel> {
    val database = AppDatabase.getDatabase(context)
    return database.databaseDao().itemData()
}



@RequiresApi(Build.VERSION_CODES.O)
public fun sortByWaste(context: Context) {
    val sortedList: MutableList<DatabaseModel> = mutableListOf()
//            Toast.makeText(requireContext(), "filterBtn", Toast.LENGTH_SHORT).show()
    itemList.forEach{
        val fromDate = LocalDate.of(it.expYear.toInt(),it.expMonth.toInt(),it.expDate.toInt())
        val x = soldTrack(it.quantity,fromDate,it.arrayData).toInt()+1
//                Toast.makeText(requireContext(), "$x", Toast.LENGTH_SHORT).show()
        if(x>0){
            sortedList += it
        }
    }

    if(sortedList.isEmpty()){
//                Toast.makeText(requireContext(), "toast1", Toast.LENGTH_SHORT).show()
        itemList.clear()
        adapter?.notifyDataSetChanged()
    }else{
//                Toast.makeText(requireContext(), "toast2", Toast.LENGTH_SHORT).show()
        itemList.clear()
        itemList.addAll(sortedList)
        adapter?.notifyDataSetChanged()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
public fun sortByShortage(context: Context){
    val sortedList: MutableList<DatabaseModel> = mutableListOf()

    itemList.forEach{
        val fromDate = LocalDate.of(it.expYear.toInt(),it.expMonth.toInt(),it.expDate.toInt())
        val x = soldTrack(it.quantity,fromDate,it.arrayData).toInt()
        if(x<0){
            sortedList += it
        }
    }
    if(sortedList.isEmpty()){
//        Toast.makeText(context, "toast1", Toast.LENGTH_SHORT).show()
        itemList.clear()
        adapter?.notifyDataSetChanged()
    }else{
//                Toast.makeText(requireContext(), "toast2", Toast.LENGTH_SHORT).show()
        itemList.clear()
        itemList.addAll(sortedList)
        adapter?.notifyDataSetChanged()
    }
}