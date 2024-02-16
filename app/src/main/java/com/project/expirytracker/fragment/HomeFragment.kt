package com.project.expirytracker.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.expirytracker.db.AppDatabase
import com.project.expirytracker.db.DatabaseModel
import com.project.expirytracker.ItemAdapter
import com.project.expirytracker.MyItemClickListener
import com.project.expirytracker.R
import com.project.expirytracker.databinding.FragmentHomeBinding
import com.project.expirytracker.fragment.HomeFragmentDirections.Companion.actionHomeFragmentToDetailsFragment2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                if (query != null) {
                    x =query
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
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
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