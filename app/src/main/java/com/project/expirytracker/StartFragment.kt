package com.project.expirytracker

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.project.expirytracker.databinding.FragmentHomeBinding
import com.project.expirytracker.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private var _binding : FragmentStartBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartBinding.inflate(inflater, container, false)

        binding.getStartBtn.setOnClickListener{
            findNavController().navigate(R.id.action_startFragment_to_homeFragment)
        }

        return binding.root
        //return inflater.inflate(R.layout.fragment_start, container, false)
        }
}