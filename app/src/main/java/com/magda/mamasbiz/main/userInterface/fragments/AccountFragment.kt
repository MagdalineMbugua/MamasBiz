package com.magda.mamasbiz.main.userInterface.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.magda.mamasbiz.databinding.FragmentAccountBinding


class AccountFragment : Fragment(){
    private lateinit var binding: FragmentAccountBinding
    private val _binding get() = binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)







        return _binding.root
    }














}