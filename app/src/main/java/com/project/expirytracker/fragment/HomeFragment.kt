package com.project.expirytracker.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
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

class HomeFragment : Fragment(), MyItemClickListener {
    private var itemList : ArrayList<DatabaseModel> = ArrayList()
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

        5
        val adapter = ItemAdapter(itemList, this)
        CoroutineScope(Dispatchers.IO).launch {
            val getData = fetchDatabase()
            itemList.clear()
            itemList.addAll(getData)
            withContext(Dispatchers.Main){
                adapter.notifyDataSetChanged()
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
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                return true
            }
        })
        binding.filter.setOnClickListener{
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
                adapter.notifyDataSetChanged()
            }else{
//                Toast.makeText(requireContext(), "toast2", Toast.LENGTH_SHORT).show()
                itemList.clear()
                itemList.addAll(sortedList)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private suspend fun fetchDatabase():List<DatabaseModel> {
        val database = AppDatabase.getDatabase(requireContext())
        return database.databaseDao().itemData()
    }

    override fun onItemClicked(item: DatabaseModel) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment2(item)
        findNavController().navigate(action)
    }

}