package com.magda.mamasbiz.main.userInterface.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPageBinding
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.data.entity.CattleBought
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Metadata
import com.magda.mamasbiz.main.utils.CattleTableLayout
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Status
import kotlinx.android.synthetic.main.cattle_layout.view.*


class CreditDebtPageFragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPageBinding
    private lateinit var name: String
    private lateinit var status: String
    private lateinit var phoneNumber: String
    private lateinit var credit: String
    private var cattleList = arrayListOf<CattleBought>()
    private var toUpdateCattleList : ArrayList<CattleBought>? = null
    private var creditDebt: CreditDebt? = null
    private lateinit var navController: NavController
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private lateinit var metadata: Metadata
    private val TAG = "CreditDebtPageFragment"

    companion object {
        //This new instance allows the fragment activity to share the extras from the Details activity
        fun newInstance(creditDebt: CreditDebt) = CreditDebtPageFragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.CREDIT_DEBT, creditDebt)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditDebtPageBinding.inflate(inflater, container, false)
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)

        getTheArguments()
        addView()
        getArgExtras()



        addCreditDebtLiveData()
        fetchCattleBoughtLiveData()
        updateCattleBoughtLiveData()
        deleteCattleBoughtLiveData()
        fetchMetadataLiveData()
        deleteMetadataLiveData()
        addMetadataLiveData()

        binding.nextLayout.tvNext.setOnClickListener { checkRadioButton() }
        binding.nextLayout.tvBack.setOnClickListener { previousPage() }
        binding.fabAdd.setOnClickListener { addView() }
        return binding.root
    }

    private fun deleteMetadataLiveData() {
        creditDebtViewModel._deleteMetadataLiveData.observe(viewLifecycleOwner){
            when(it.status) {
                Status.LOADING -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    //Method sub

                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addMetadataLiveData() {
        creditDebtViewModel._addMetadataLiveData.observe(viewLifecycleOwner){
            when(it.status) {
                Status.LOADING -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    //Method sub

                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun fetchMetadataLiveData() {
        creditDebtViewModel._fetchMetadataLiveData.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
                    //Method sub
                }
                Status.SUCCESS -> {
                    metadata = it.data!!

                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchMetadata() {
        creditDebtViewModel.fetchMetadata(creditDebt!!.userId!!)
    }

    private fun fetchCattleBoughtLiveData() {
        creditDebtViewModel._fetchCattleBoughtLiveData.observe(viewLifecycleOwner){
            when(it.status){
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    //method sub
                }
                Status.SUCCESS -> {
                    toUpdateCattleList = it.data
                    fetchMetadata()

                }
            }
        }

    }

    private fun deleteCattleBoughtLiveData() {
        creditDebtViewModel._deleteCattleBoughtLiveData.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    deleteFromMetadata()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteCattleBought() {
        for(cattleBought: CattleBought in toUpdateCattleList!!){
            Log.d(TAG, "deleteCattleBought: $cattleBought")
            creditDebtViewModel.deleteCattleBought(creditDebt!!.creditDebtId!!,cattleBought)


        }
    }

    private fun deleteFromMetadata() {
        val metadata = Metadata(
            metadata.totalMoneySentPaid.minus(creditDebt!!.cattleBoughtPaid!!.toInt()),
            metadata.totalMoneySentAmt.minus(creditDebt!!.cattleBoughtAmount!!.toInt()),
            metadata.totalMoneySentBalance.minus(creditDebt!!.cattleBoughtBalance!!.toInt()),
            metadata.totalMoneyReceivedPaid,
            metadata.totalMoneyReceivedAmt,
            metadata.totalMoneyReceivedBalance
        )
        creditDebtViewModel.deleteMetadata(metadata, creditDebt!!.userId!!)
    }

    private fun updateCattleBoughtLiveData() {
        creditDebtViewModel._addCattleBoughtLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                    binding.btUpdate.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "Successfully updated the cattle bought",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().finish()

                }
                Status.LOADING -> {
                    binding.btUpdate.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addMetadata(
        cattleBoughtAmtPaid: String,
        totalCattleBoughtBal: String,
        totalCattleBoughtAmt: String
    ) {
        val metadata = Metadata(
            metadata.totalMoneySentPaid.plus(cattleBoughtAmtPaid.toInt()),
            metadata.totalMoneySentAmt.plus(totalCattleBoughtAmt.toInt()),
            metadata.totalMoneySentBalance.plus(totalCattleBoughtBal.toInt()),
            metadata.totalMoneyReceivedPaid,
            metadata.totalMoneyReceivedAmt,
            metadata.totalMoneyReceivedBalance
        )
        creditDebtViewModel.addMetadata(metadata, creditDebt!!.userId!!)

    }

    private fun addCreditDebtLiveData() {
        creditDebtViewModel._loadCDLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.btUpdate.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    //To be known
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                    binding.btUpdate.visibility = View.VISIBLE

                }
            }
        }
    }

    private fun getArgExtras() {
        creditDebt = requireArguments().getParcelable(Constants.CREDIT_DEBT)
        if (creditDebt != null) {
            creditDebtViewModel.fetchCattleBought(creditDebt!!.creditDebtId!!)
            binding.apply {
                nextLayout.tvNext.visibility = View.GONE
                nextLayout.tvBack.visibility = View.GONE
                btUpdate.visibility = View.VISIBLE
                radioButton.visibility = View.GONE
                btUpdate.setOnClickListener {
                    getTexts()
                updateCattleBought()}
            }
        }
    }


    private fun checkRadioButton() {
        if (binding.radioButton.isChecked) {
            if (binding.tvTotalExactAmt.text.toString() != "0") {
                getTexts()
            } else {
                navController = Navigation.findNavController(binding.root)
                val arg = setArguments()
                navController.navigate(
                    R.id.action_creditDebtPageFragment_to_creditPage2Fragment,
                    arg
                )
            }


        } else {
            if (binding.tvTotalExactAmt.text.toString().isNotEmpty()) {
                getTexts()

            } else Toast.makeText(requireContext(), "Fill in the cattle bought", Toast.LENGTH_SHORT)
                .show()


        }
    }

    private fun setArguments(): Bundle {
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
        val cattleBought = CattleBought(
            "",
            "",
            cattleBoughtType,
            cattleBoughtPrice,
            cattleBoughtQty,
            cattleBoughtAmt
        )
        Log.d(TAG, "addView: $cattleList")
        Log.d(TAG, "addView: ${cattleList.size}")
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

    private fun updateTitles() {
        Log.d(TAG, "updateTitles: ${binding.cattleLinearLayout.childCount}")
        if (binding.cattleLinearLayout.childCount < 1) {
            return
        }
        for (i in 0..binding.cattleLinearLayout.childCount) {
            Log.d(TAG, "updateTitles: $i")
            val table = binding.cattleLinearLayout.getChildAt(i) as? CattleTableLayout
            table?.setTableTitle(i + 1)

        }
    }


    private fun calculateTotals() {
        var total = 0
        for (i in 0..binding.cattleLinearLayout.childCount) {
            val table = binding.cattleLinearLayout.getChildAt(i) as? CattleTableLayout
            total += table?.getAmount()?.toInt() ?: 0
        }
        binding.tvTotalExactAmt.text = total.toString()

    }

    private fun calculateQty(): Int {
        var totalQty = 0
        for (i in 0..binding.cattleLinearLayout.childCount) {
            val table = binding.cattleLinearLayout.getChildAt(i) as? CattleTableLayout
            totalQty += table?.getQty()?.toInt() ?: 0
        }

        return totalQty
    }

    private fun getTexts() {
        Log.d(TAG, "getTexts Counts: ${binding.cattleLinearLayout.childCount}")
        if (binding.cattleLinearLayout.childCount > 0) {
            for (i in 0 until binding.cattleLinearLayout.childCount) {
                val cattleTableLayout =
                    binding.cattleLinearLayout.getChildAt(i) as? CattleTableLayout
                val cattleBoughtType = cattleTableLayout?.tvCattleType?.text.toString()
                val cattleBoughtPrice = cattleTableLayout?.etPrice?.text.toString()
                val cattleBoughtQty = cattleTableLayout?.etQty?.text.toString()
                val cattleBoughtAmt = cattleTableLayout?.tvCattleAmount?.text.toString()

                Log.d(TAG, "getTexts: $cattleTableLayout")
                validateInfo(cattleBoughtType, cattleBoughtPrice, cattleBoughtQty, cattleBoughtAmt)

            }
        }

    }

    private fun validateInfo(
        cattleBoughtType: String,
        cattleBoughtPrice: String,
        cattleBoughtQty: String,
        cattleBoughtAmt: String
    ) {
        val cattleBoughtAmtPaid = binding.etAmountPaid.text.toString()

        if (cattleBoughtPrice == "0" || cattleBoughtQty == "0" || cattleBoughtAmt == "0") {
            Toast.makeText(requireContext(), "Fill in the cattle bought", Toast.LENGTH_SHORT).show()
        } else {
            if (cattleBoughtAmtPaid.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Fill in the Amount paid for the cattle",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Log.d(TAG, "validateInfo: and $cattleBoughtQty")
            val totalCattleBoughtQty = calculateQty().toString()

            val totalCattleBoughtAmt = binding.tvTotalExactAmt.text.toString()
            Log.d(
                TAG,
                "validateInfo: $cattleBoughtAmtPaid $totalCattleBoughtAmt $totalCattleBoughtQty"
            )
                       val cattleBought = CattleBought(
                "",
                cattleBoughtId = null,
                cattleBoughtType,
                cattleBoughtPrice,
                cattleBoughtQty,
                cattleBoughtAmt
            )
            Log.d(TAG, "validateInfo: $cattleBought")
            cattleList.add(cattleBought)
            Log.d(TAG, "validateInfo: ${cattleList.size}")
            if(toUpdateCattleList!=null){
                deleteCattleBought()
            }
            if (creditDebt != null) {
                updateCreditDebt(
                totalCattleBoughtAmt,
                totalCattleBoughtQty,
                cattleBoughtAmtPaid
            )} else  toTheNextPage(totalCattleBoughtAmt, totalCattleBoughtQty, cattleBoughtAmtPaid)


        }
    }

    private fun toTheNextPage(
        totalCattleBoughtAmt: String,
        totalCattleBoughtQty: String,
        cattleBoughtAmtPaid: String
    ) {
        val arg = setArguments()
        arg.putParcelableArrayList(Constants.CATTLE_BOUGHT_LIST, cattleList)
        Log.d(TAG, "toTheNextPage: ${cattleList.size}")
        arg.putString(Constants.TOTAL_CATTLE_BOUGHT_AMOUNT, totalCattleBoughtAmt)
        arg.putString(Constants.TOTAL_CATTLE_BOUGHT_PAID, cattleBoughtAmtPaid)
        arg.putString(Constants.TOTAL_CATTLE_BOUGHT_QTY, totalCattleBoughtQty)
        navController = Navigation.findNavController(binding.root)
        if (navController.currentDestination?.id == R.id.creditDebtPageFragment) {
            if (binding.radioButton.isChecked) {
                navController.navigate(
                    R.id.action_creditDebtPageFragment_to_creditPage2Fragment,
                    arg
                )

            } else {

                navController.navigate(
                    R.id.action_creditDebtPageFragment_to_creditPage3Fragment,
                    arg
                )
            }
        }
    }

    private fun updateCreditDebt(
        totalCattleBoughtAmt: String,
        totalCattleBoughtQty: String,
        cattleBoughtAmtPaid: String
    ) {
        val totalCattleBoughtBal =
            totalCattleBoughtAmt.toInt().minus(cattleBoughtAmtPaid.toInt()).toString()
        val updatedTotalAmount =
            creditDebt?.totalAllAmount?.toInt()!!.minus(creditDebt?.cattleBoughtAmount?.toInt()!!).plus(totalCattleBoughtAmt.toInt())
        val updatedTotalPaid =
            creditDebt?.totalAllPaid?.toInt()!!.minus(creditDebt?.cattleBoughtPaid?.toInt()!!).plus(cattleBoughtAmtPaid.toInt())
        val updatedTotalBalance =
            creditDebt?.totalAllBalance?.toInt()!!.minus(creditDebt?.cattleBoughtBalance?.toInt()!!).plus(totalCattleBoughtBal.toInt())
        val creditDebt = CreditDebt(
            creditDebt?.creditDebtId,
            creditDebt?.userId,
            creditDebt?.type,
            creditDebt?.name,
            creditDebt?.phoneNumber,
            creditDebt?.status,
            creditDebt?.paymentDate,
            creditDebt?.dateCreated,
            updatedTotalAmount.toString(),
            updatedTotalPaid.toString(),
            updatedTotalBalance.toString(),
            creditDebt?.productId,
            creditDebt?.cattleBoughtId,
            creditDebt?.productPaid,
            creditDebt?.productBalance,
            creditDebt?.productAmount,
            cattleBoughtAmtPaid,
            totalCattleBoughtBal,
            totalCattleBoughtAmt,
            totalCattleBoughtQty,
            creditDebt?.updatedId
        )
        creditDebtViewModel.addCreditDebt(creditDebt)
        addMetadata(cattleBoughtAmtPaid,totalCattleBoughtBal,totalCattleBoughtAmt)

    }

    private fun updateCattleBought() {
        Log.d(TAG, "updateCattleBought: list is ${cattleList.size}")
        if (cattleList.size > 0) {
            for (i in 0 until cattleList.size ) {
                val updatedCattleBought = cattleList[i]
                Log.d(TAG, "updateCattleBought: $updatedCattleBought")
                creditDebtViewModel.addCattleBought(
                    creditDebt!!.creditDebtId!!,
                    updatedCattleBought
                )
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