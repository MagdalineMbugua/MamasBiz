package com.magda.mamasbiz.main.userInterface.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPageBinding
import com.magda.mamasbiz.main.data.entity.CattleBought
import com.magda.mamasbiz.main.utils.CattleTableLayout
import com.magda.mamasbiz.main.utils.Constants
import kotlinx.android.synthetic.main.cattle_layout.view.*


class CreditDebtPageFragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPageBinding
    private lateinit var name: String
    private lateinit var status: String
    private lateinit var phoneNumber: String
    private lateinit var credit: String
    private var cattleList = arrayListOf<CattleBought>()
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
        binding.nextLayout.tvBack.setOnClickListener { previousPage() }
        binding.fabAdd.setOnClickListener { addView() }
        return binding.root
    }



    private fun checkRadioButton() {
       if(binding.radioButton.isChecked) {
           if(binding.tvTotalExactAmt.text.toString()!= "0"){
               getTexts()
           }else{
               navController = Navigation.findNavController(binding.root)
               val arg = setArguments()
               navController.navigate(R.id.action_creditDebtPageFragment_to_creditPage2Fragment, arg)
           }


       }else{
           if(binding.tvTotalExactAmt.text.toString() .isNotEmpty()){
               getTexts()

           } else Toast.makeText(requireContext(), "Fill in the cattle bought", Toast.LENGTH_SHORT).show()



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
        val cattleBoughtPrice = cattleTableLayout.etPrice.text.toString()
        val cattleBoughtType = cattleTableLayout.tvCattleType.text.toString()
        val cattleBoughtQty = cattleTableLayout.etQty.text.toString()
        val cattleBoughtAmt = cattleTableLayout.tvCattleAmount.text.toString()
        val cattleBought = CattleBought("","",cattleBoughtType,cattleBoughtPrice,cattleBoughtQty,cattleBoughtAmt)
        cattleList.add(cattleBought)
        cattleTableLayout.onAmountChangeListener {
            calculateTotals()
        }
        cattleTableLayout.onRemoveAmountListener {
            layout.removeView(cattleTableLayout)
            updateTitles()
            calculateTotals()
            cattleList.remove(cattleBought)
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

    private fun getTexts(){
        for(i in 0..binding.cattleLinearLayout.childCount){
            val cattleTableLayout = binding.cattleLinearLayout.getChildAt(i)as? CattleTableLayout
            val cattleBoughtPrice = cattleTableLayout?.etPrice?.text.toString()
            val cattleBoughtQty = cattleTableLayout?.etQty?.text.toString()
            val cattleBoughtAmt = cattleTableLayout?.tvCattleAmount?.text.toString()
            val cattleBoughtAmtPaid = binding.etAmountPaid.text.toString()
            validateInfo(cattleBoughtPrice,cattleBoughtQty,cattleBoughtAmt,cattleBoughtAmtPaid)

        }
    }

    private fun validateInfo(
        cattleBoughtPrice: String,
        cattleBoughtQty: String,
        cattleBoughtAmt: String,
        cattleBoughtAmtPaid: String
    ) {
        var totalUnitsBought = 0
        if(cattleBoughtPrice=="0"||cattleBoughtQty=="0"|| cattleBoughtAmt =="0"){
            Toast.makeText(requireContext(), "Fill in the cattle bought", Toast.LENGTH_SHORT).show()
        }else {
            if(cattleBoughtAmtPaid.isEmpty()){
                Toast.makeText(requireContext(), "Fill in the Amount paid for the cattle", Toast.LENGTH_SHORT).show()
            }
            totalUnitsBought += cattleBoughtQty.toInt()
            val totalCattleBoughtQty = totalUnitsBought.toString()
            val totalCattleBoughtAmt = binding.tvTotalExactAmt.text.toString()
            Log.d(
                TAG,
                "validateInfo: $cattleBoughtAmtPaid $totalCattleBoughtAmt $totalCattleBoughtQty"
            )
            val arg = setArguments()
            arg.putParcelableArrayList(Constants.CATTLE_BOUGHT_LIST, cattleList)
            arg.putString(Constants.TOTAL_CATTLE_BOUGHT_AMOUNT, totalCattleBoughtAmt)
            arg.putString(Constants.TOTAL_CATTLE_BOUGHT_PAID,cattleBoughtAmtPaid)
            arg.putString(Constants.TOTAL_CATTLE_BOUGHT_QTY, totalCattleBoughtQty)
            navController = Navigation.findNavController(binding.root)
            if (navController.currentDestination?.id == R.id.creditDebtPageFragment){
                if(binding.radioButton.isChecked){
                    navController.navigate(R.id.action_creditDebtPageFragment_to_creditPage2Fragment, arg)

                } else {

                    navController.navigate(R.id.action_creditDebtPageFragment_to_creditPage3Fragment, arg)
                }
            }



        }
    }
    private fun previousPage() {
        val navController: NavController = Navigation.findNavController(binding.root)
        navController.navigate(R.id.action_creditDebtPageFragment_to_creditPage1Fragment)

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