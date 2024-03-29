package com.project.expirytracker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.expirytracker.R
import com.project.expirytracker.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private var _binding : FragmentStartBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartBinding.inflate(inflater, container, false)

        binding.getStartBtn.setOnClickListener{
            findNavController().navigate(R.id.action_startFragment_to_homeFragment)
        }
        return binding.root
        }
}