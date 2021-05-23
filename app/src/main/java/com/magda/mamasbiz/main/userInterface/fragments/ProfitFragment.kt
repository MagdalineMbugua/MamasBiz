package com.magda.mamasbiz.main.userInterface.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magda.mamasbiz.databinding.FragmentProfitBinding
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.data.entity.Metadata
import com.magda.mamasbiz.main.utils.SessionManager
import com.magda.mamasbiz.main.utils.Status
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.math.RoundingMode


class ProfitFragment : Fragment() {
    private lateinit var binding: FragmentProfitBinding
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private lateinit var metadata: Metadata
    private val TAG = "ProfitFragment"




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfitBinding.inflate(inflater, container, false)
        creditDebtViewModel =
            ViewModelProvider(requireActivity()).get(CreditDebtViewModel::class.java)
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        creditDebtViewModel.fetchMetadata(userId!!)
        initFetchMetadataObserver()



        return binding.root
    }

    private fun initViews() {
        val totalBought = "Total bought: Kes ${metadata.totalMoneySentAmt}"
        val totalSold = "Total bought: Kes ${metadata.totalMoneyReceivedAmt}"
        val receivedPaidPercentage: String
        val receivedBalancePercentage : String
        if (metadata.totalMoneyReceivedAmt==0 || metadata.totalMoneyReceivedPaid==0 ||metadata.totalMoneyReceivedBalance==0){
            receivedPaidPercentage = "0"
            receivedBalancePercentage ="0"
        }else {
            receivedPaidPercentage = "${
                metadata.totalMoneyReceivedPaid.times(100).toFloat().div(metadata.totalMoneyReceivedAmt)
                    .toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
            }% Paid"
            receivedBalancePercentage = "${
                (metadata.totalMoneyReceivedBalance.times(100).toFloat()
                    .div(metadata.totalMoneyReceivedAmt)).toBigDecimal()
                    .setScale(2, RoundingMode.HALF_EVEN)
            }% Bal"
        }
        val sentBalancePercentage: String
        val sentPaidPercentage : String
            if (metadata.totalMoneySentAmt==0 || metadata.totalMoneySentPaid==0 ||metadata.totalMoneySentBalance==0){
            sentPaidPercentage= "0"
            sentBalancePercentage ="0"
        }else {sentBalancePercentage = "${
                    (metadata.totalMoneySentBalance.times(100).toFloat()
                        .div(metadata.totalMoneySentAmt)).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                }% Bal"
                sentPaidPercentage = "${
                    (metadata.totalMoneySentPaid.times(100).toFloat()
                        .div(metadata.totalMoneySentAmt)).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                }% Paid"
        }
        val receivedPaid = "Kes ${metadata.totalMoneyReceivedPaid} Paid"
        val receivedBal = "Kes ${metadata.totalMoneyReceivedBalance} Bal"
        val sentPaid = "Kes ${metadata.totalMoneySentPaid} Paid"
        val sentBal = "Kes ${metadata.totalMoneySentBalance} Bal"
        val profit = " Kes ${metadata.totalMoneyReceivedAmt.minus(metadata.totalMoneySentAmt)}"
        binding.apply {
            tvTotalBought.text = totalBought
            tvTotalSold.text = totalSold
            tvTotalMoneyReceivedBalancePercentage.text = receivedBalancePercentage
            tvTotalMoneyReceivedPaidPercentage.text = receivedPaidPercentage
            tvTotalMoneySentBalancePercentage.text = sentBalancePercentage
            tvTotalMoneySentPaidPercentage.text = sentPaidPercentage
            tvTotalMoneySentPaid.text = sentPaid
            tvTotalMoneySentBalance.text = sentBal
            tvTotalMoneyReceivedPaid.text = receivedPaid
            tvTotalMoneyReceivedBalance.text = receivedBal
            tvTotalGrossProfit.text = profit
        }
    }

    private fun initFetchMetadataObserver() {
        creditDebtViewModel._fetchMetadataLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    //To figure it out
                }
                Status.SUCCESS -> {
                    metadata = it.data!!
                    initTotalReceivedPaidView()
                    initTotalReceivedBalView()
                    initTotalSentPaidView()
                    initTotalSentBalanceView()
                    initViews()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initTotalSentBalanceView() {
        binding.totalMoneySentBalance.apply {
            if (metadata.totalMoneySentAmt!=0 || metadata.totalMoneySentBalance!=0){
                setProgressWithAnimation(metadata.totalMoneySentBalance.toFloat(), 1000)
                progressMax = metadata.totalMoneySentAmt.toFloat()
            }



        }
    }

    private fun initTotalSentPaidView() {
        binding.totalMoneySentPaid.apply {
            if (metadata.totalMoneySentAmt!=0 || metadata.totalMoneySentPaid!=0){
                setProgressWithAnimation(metadata.totalMoneySentPaid.toFloat(), 1000)
                progressMax = metadata.totalMoneySentAmt.toFloat()
            }



        }
    }

    private fun initTotalReceivedBalView() {
        binding.totalMoneyReceivedPaid.apply {
            if(metadata.totalMoneyReceivedBalance!=0 || metadata.totalMoneySentAmt!=0){
                setProgressWithAnimation(metadata.totalMoneyReceivedBalance.toFloat(), 1000)
                progressMax = metadata.totalMoneyReceivedAmt.toFloat()
            }




        }
    }

    private fun initTotalReceivedPaidView() {
        binding.totalMoneyReceivedBalance.apply {
            if(metadata.totalMoneyReceivedPaid!=0 || metadata.totalMoneySentAmt!=0){
                setProgressWithAnimation(metadata.totalMoneyReceivedPaid.toFloat(), 1000)
                progressMax = metadata.totalMoneyReceivedAmt.toFloat()
            }



        }
    }


}