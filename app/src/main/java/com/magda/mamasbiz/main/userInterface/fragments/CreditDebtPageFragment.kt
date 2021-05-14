package com.magda.mamasbiz.main.userInterface.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPageBinding
import com.magda.mamasbiz.main.data.entity.CattleBought
import com.magda.mamasbiz.main.utils.CattleTableLayout
import com.magda.mamasbiz.main.utils.Constants
import kotlinx.android.synthetic.main.cattle_layout.*
import kotlinx.android.synthetic.main.cattle_layout.view.*
import kotlinx.android.synthetic.main.fragment_credit_debt_page.*
import kotlin.math.log


class CreditDebtPageFragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPageBinding
    private lateinit var name: String
    private lateinit var status: String
    private lateinit var phoneNumber: String
    private lateinit var credit: String
    private var cattleList = mutableListOf<CattleBought>()
    private lateinit var navController: NavController
    private val TAG = "CreditDebtPageFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditDebtPageBinding.inflate(inflater, container, false)
        getTheArguments()
        addView()

        binding.nextLayout.tvNext.setOnClickListener { checkRadioButton() }
        binding.fabAdd.setOnClickListener { addView() }
        return binding.root
    }



    private fun checkRadioButton() {
       if(binding.radioButton.isChecked) {
           navController = Navigation.findNavController(binding.root)
           val arg = setArguments()
           navController.navigate(R.id.action_creditDebtPageFragment_to_creditPage2Fragment, arg)

       }else{



       }
    }
    private fun setArguments():Bundle{
        val arg = Bundle()
        arg.putString(Constants.DEBTOR_NAME, name)
        arg.putString(Constants.DEBTOR_NUMBER, phoneNumber)
        arg.putString(Constants.DEBTOR_STATUS, status)
        arg.putString(Constants.CREDIT, credit)
        return arg
    }





    private fun addView() {

        val cattleTableLayout = CattleTableLayout(requireContext())
        val layout = binding.cattleLinearLayout
        layout.addView(cattleTableLayout)
        updateTitles()
        cattleTableLayout.onAmountChangeListener {
            calculateTotals()
        }
        cattleTableLayout.onRemoveAmountListener {
            layout.removeView(cattleTableLayout)
            updateTitles()
            calculateTotals()
        }


    }

    private fun updateTitles(){
        Log.d(TAG, "updateTitles: ${binding.cattleLinearLayout.childCount}")
        if (binding.cattleLinearLayout.childCount<1){
            return
        }
        for(i in 0..binding.cattleLinearLayout.childCount){
            Log.d(TAG, "updateTitles: $i")
            val table = binding.cattleLinearLayout.getChildAt(i)as? CattleTableLayout
            table?.setTableTitle(i+1)

        }
    }


    private fun calculateTotals(){
        var total = 0
        for(i in 0..binding.cattleLinearLayout.childCount){
            val table = binding.cattleLinearLayout.getChildAt(i)as? CattleTableLayout
            total += table?.getAmount()?.toInt()?:0
            }
        binding.tvTotalExactAmt.text = total.toString()

    }
    private fun addViewViews(cattleTableLayout:CattleTableLayout){
        val cattleBoughtPrice = cattleTableLayout?.etPrice?.text.toString()
        val cattleBoughtQty = cattleTableLayout?.etQty?.text.toString()
        val cattleBoughtAmt = cattleTableLayout?.tvCattleAmount?.text.toString()
        val cattleBought = CattleBought("","",cattleBoughtPrice,cattleBoughtQty,cattleBoughtAmt)
        cattleList.add(cattleBought)


    }

    private fun getTheArguments() {
        requireArguments().getString(Constants.DEBTOR_NAME)?.let { debtorName ->
            name = debtorName
        }
        requireArguments().getString(Constants.DEBTOR_STATUS)?.let { status ->
            this.status = status
        }
        requireArguments().getString(Constants.DEBTOR_NUMBER)?.let { phoneNumber ->
            this.phoneNumber = phoneNumber
        }
        requireArguments().getString(Constants.CREDIT)?.let { credit ->
            this.credit = credit
        }

    }


}