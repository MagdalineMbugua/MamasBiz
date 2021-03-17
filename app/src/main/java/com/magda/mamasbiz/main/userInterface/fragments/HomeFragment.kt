package com.magda.mamasbiz.main.userInterface.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.magda.mamasbiz.databinding.FragmentHomeBinding
import com.magda.mamasbiz.main.userInterface.activities.CreditDebtActivity
import com.magda.mamasbiz.main.utils.Constants


class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private val _binding get() = binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        _binding.btCreateCredit.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), CreditDebtActivity::class.java)
            val credit = "Credit"
            intent.putExtra(Constants.CREDIT, credit)
            startActivity(intent)
        })
        _binding.btCreateDebt.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), CreditDebtActivity::class.java)
            val debt = "Debt"
            intent.putExtra(Constants.DEBT, debt)
            startActivity(intent)
        })

        return _binding.root
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}