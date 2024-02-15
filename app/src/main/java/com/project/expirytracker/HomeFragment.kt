package com.project.expirytracker

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.expirytracker.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private val itemList : ArrayList<ItemModel> = ArrayList()
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.faButton.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_addItemFragment)
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ItemAdapter(itemList,requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            val getData = fetchDatabase()

            getData.forEach{
                val listItem = listOf<ItemModel>(
                    ItemModel(it.id,it.name,it.itemPrice,it.expYear,it.expMonth,it.expDate)
                )
                itemList.addAll(listItem)
            }
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
                if (query != null) {
                    x =query
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = AppDatabase.getDatabase(requireContext())
                        val list = database.databaseDao().searchItem(x)
                        list.forEach{
                            val listItem = listOf<ItemModel>(
                                ItemModel(it.id,it.name,it.itemPrice,it.expYear,it.expMonth,it.expDate)
                            )
                            itemList.addAll(listItem)
                        }
                        withContext(Dispatchers.Main){
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private suspend fun fetchDatabase():List<DatabaseModel> {
        val database = AppDatabase.getDatabase(requireContext())
        return database.databaseDao().itemData()
    }
}