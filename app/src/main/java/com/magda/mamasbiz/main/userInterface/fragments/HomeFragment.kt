package com.magda.mamasbiz.main.userInterface.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magda.mamasbiz.databinding.FragmentHomeBinding
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.data.entity.Metadata
import com.magda.mamasbiz.main.userInterface.activities.CreditDebtActivity
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.SessionManager
import com.magda.mamasbiz.main.utils.Status
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow


class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private val _binding get() = binding!!
    private var metadata: Metadata? = null
    private lateinit var creditDebtViewModel: CreditDebtViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        creditDebtViewModel = ViewModelProvider(this@HomeFragment).get(CreditDebtViewModel::class.java)
        creditDebtViewModel.fetchMetadata(getUserId())
        fetchMetadataLiveData()




        _binding.btCreateCredit.setOnClickListener{
            val intent = Intent(requireActivity(), CreditDebtActivity::class.java)
            val credit = "Credit"
            intent.putExtra(Constants.CREDIT, credit)
            startActivity(intent)
        }
        _binding.btCreateDebt.setOnClickListener{
            val intent = Intent(requireActivity(), CreditDebtActivity::class.java)
            val debt = "Debt"
            intent.putExtra(Constants.DEBT, debt)
            startActivity(intent)
        }

        return _binding.root
    }

    private fun setViews() {
        _binding.apply {
            val totalAmount = metadata?.totalMoneyReceivedAmt!!.plus(metadata?.totalMoneySentAmt!!)
            val totalAmountString = "Total Payment Kes: ${convertNumbersToAcronyms(totalAmount)}"
            tvTotalPayment.text = totalAmountString
            tvTotalCustomerPayment.text =convertNumbersToAcronyms(metadata?.totalMoneyReceivedAmt!!)
            tvTotalSupplierPayment.text = convertNumbersToAcronyms(metadata?.totalMoneySentAmt!!)
        }
    }

    private fun fetchMetadataLiveData() {
        creditDebtViewModel._fetchMetadataLiveData.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    metadata = it.data
                    setViews()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    //Method sub
                }
            }
        }

    }

    private fun getUserId (): String{
        val sessionManager = SessionManager(requireContext())
        return sessionManager.getUserId()!!

    }
    private fun convertNumbersToAcronyms(metadata: Int):String{
        if (metadata < 1000) return " $metadata"
        val exp = (ln(metadata.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c", metadata/ 1000.0.pow(exp.toDouble()), "KMBTPE"[exp - 1])
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}