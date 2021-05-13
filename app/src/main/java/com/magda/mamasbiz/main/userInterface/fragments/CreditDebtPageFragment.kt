package com.magda.mamasbiz.main.userInterface.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        val previousAmt = binding.tvTotalExactAmt.text.toString()
        val cattleTableLayout = CattleTableLayout(requireContext())
        val layout = binding.cattleLinearLayout
        layout.addView(cattleTableLayout)
        cattleTableLayout.onAmountChangeListener {
            val total = previousAmt.toInt().plus(it.toInt()).toString()
            binding.tvTotalExactAmt.text = total
        }
        cattleTableLayout.onRemoveAmountListener {
            Log.d(TAG, "addView: $it")
            Log.d(TAG, "addView: $previousAmt")
            val total = tvTotalExactAmt.text.toString().toInt().minus(it.toInt()).toString()
            binding.tvTotalExactAmt.text = total
            layout.removeView(cattleTableLayout)
        }


        Log.d(TAG, "addView: $previousAmt")






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