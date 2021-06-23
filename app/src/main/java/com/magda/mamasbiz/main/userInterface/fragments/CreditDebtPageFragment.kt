package com.magda.mamasbiz.main.userInterface.fragments

import android.app.Activity
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
    private var toUpdateCattleList: ArrayList<CattleBought>? = null
    private var creditDebt: CreditDebt? = null
    private lateinit var navController: NavController
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private lateinit var metadata: Metadata
    private var updatedTotalMoneySentPaid: Int = 0
    private var updatedTotalMoneySentBal: Int = 0
    private var updatedTotalMoneySentAmt: Int = 0
    private lateinit var cattleBoughtAmtPaid: String
    private lateinit var totalCattleBoughtAmt: String
    private lateinit var totalCattleBoughtBal: String
    private var isMetadataUpdated: Boolean = false

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
        creditDebtViewModel._addMetadataLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    isMetadataUpdated = true
                    //Method sub

                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addMetadataLiveData() {
        creditDebtViewModel._updateMetadataLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    Log.d(TAG, "addMetadataLiveData: added")


                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun fetchMetadataLiveData() {
        creditDebtViewModel._fetchMetadataLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
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
        creditDebtViewModel._fetchCattleBoughtLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
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
        creditDebtViewModel._deleteCattleBoughtLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
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

    private fun deleteCattleBought() {
        creditDebtViewModel.deleteCattleBought(creditDebt!!.creditDebtId!!)
        deleteFromMetadata()
    }

    private fun deleteFromMetadata() {
        updatedTotalMoneySentAmt =
            metadata.totalMoneySentAmt.minus(creditDebt!!.cattleBoughtAmount!!.toInt())
        updatedTotalMoneySentPaid =
            metadata.totalMoneySentPaid.minus(creditDebt!!.cattleBoughtPaid!!.toInt())
        updatedTotalMoneySentBal =
            metadata.totalMoneySentBalance.minus(creditDebt!!.cattleBoughtBalance!!.toInt())
        val metadata = Metadata(
            updatedTotalMoneySentPaid,
            updatedTotalMoneySentAmt,
            updatedTotalMoneySentBal,
            metadata.totalMoneyReceivedPaid,
            metadata.totalMoneyReceivedAmt,
            metadata.totalMoneyReceivedBalance
        )
        creditDebtViewModel.addMetadata(metadata, creditDebt!!.userId!!)
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
                    requireActivity().setResult(Activity.RESULT_OK)
                    requireActivity().finish()


                }
                Status.LOADING -> {
                    binding.btUpdate.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addMetadata() {
        Log.d(TAG, "addMetadata: $isMetadataUpdated")
        if (isMetadataUpdated) {
            val metadata = Metadata(
                updatedTotalMoneySentPaid.plus(cattleBoughtAmtPaid.toInt()),
                updatedTotalMoneySentAmt.plus(totalCattleBoughtAmt.toInt()),
                updatedTotalMoneySentBal.plus(totalCattleBoughtBal.toInt()),
                metadata.totalMoneyReceivedPaid,
                metadata.totalMoneyReceivedAmt,
                metadata.totalMoneyReceivedBalance
            )
            creditDebtViewModel.updateMetadata(
                creditDebt!!.type!!,
                creditDebt!!.userId!!,
                metadata.totalMoneySentPaid,
                metadata.totalMoneySentBalance,
                metadata.totalMoneySentAmt
            )
            Log.d(
                TAG,
                "addMetadata: ${updatedTotalMoneySentPaid.plus(cattleBoughtAmtPaid.toInt())}paid, ${
                    updatedTotalMoneySentAmt.plus(totalCattleBoughtAmt.toInt())
                }amt,${updatedTotalMoneySentBal.plus(totalCattleBoughtBal.toInt())} bal"
            )

        }

    }

    private fun addCreditDebtLiveData() {
        creditDebtViewModel._loadCDLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.btUpdate.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    addMetadata()
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
                }
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
        for (i in 0 until binding.cattleLinearLayout.childCount) {
            val table = binding.cattleLinearLayout.getChildAt(i) as? CattleTableLayout
            val getQty = if (table?.getQty() == "") {
                "0"
            } else {
                table?.getQty()
            }
            totalQty += getQty?.toInt() ?: 0
        }

        return totalQty
    }

    private fun getTexts() {
        Log.d(TAG, "getTexts Counts: ${binding.cattleLinearLayout.childCount}")
        if (binding.cattleLinearLayout.childCount > 0) {
            for (i in 0 until binding.cattleLinearLayout.childCount) {
                val cattleTableLayout =
                    binding.cattleLinearLayout.getChildAt(i) as? CattleTableLayout
                Log.d(TAG, "getTexts: $cattleTableLayout")
                checkAllLayoutIfFilled(cattleTableLayout)
                Log.d(TAG, "getTexts: checked layouts")
            }
            if (creditDebt != null) updateCattleBought()
        }

    }

    private fun validateInfo(
        cattleBoughtType: String,
        cattleBoughtPrice: String,
        cattleBoughtQty: String,
        cattleBoughtAmt: String
    ) {
        cattleBoughtAmtPaid = binding.etAmountPaid.text.toString()

        if (cattleBoughtPrice == "0" || cattleBoughtQty == "0" || cattleBoughtAmt == "0") {
            Toast.makeText(
                requireContext(),
                "You did not fill all the cattle details",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (cattleBoughtAmtPaid.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Fill in the Amount paid for the cattle",
                    Toast.LENGTH_SHORT
                ).show()
            } else amountPaidCheck(
                cattleBoughtType,
                cattleBoughtPrice,
                cattleBoughtQty,
                cattleBoughtAmt
            )
        }
    }

    private fun toCheckIfNewOrUpdate(
        cattleBoughtType: String,
        cattleBoughtPrice: String,
        cattleBoughtQty: String,
        cattleBoughtAmt: String
    ) {
        val totalCattleBoughtQty = calculateQty().toString()
        totalCattleBoughtAmt = binding.tvTotalExactAmt.text.toString()
        val cattleBought = CattleBought(
            "",
            cattleBoughtId = null,
            cattleBoughtType,
            cattleBoughtPrice,
            cattleBoughtQty,
            cattleBoughtAmt
        )
        cattleList.add(cattleBought)
        if (toUpdateCattleList != null) {
            deleteCattleBought()
        }
        if (creditDebt != null) {
            totalCattleBoughtBal =
                totalCattleBoughtAmt.toInt().minus(cattleBoughtAmtPaid.toInt()).toString()
            updateCreditDebt(totalCattleBoughtQty)

        } else toTheNextPage(totalCattleBoughtQty)

    }

    private fun toTheNextPage(
        totalCattleBoughtQty: String
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
        totalCattleBoughtQty: String
    ) {

        val updatedTotalAmount =
            creditDebt?.totalAllAmount?.toInt()!!.minus(creditDebt?.cattleBoughtAmount?.toInt()!!)
                .plus(totalCattleBoughtAmt.toInt())
        val updatedTotalPaid =
            creditDebt?.totalAllPaid?.toInt()!!.minus(creditDebt?.cattleBoughtPaid?.toInt()!!)
                .plus(cattleBoughtAmtPaid.toInt())
        val updatedTotalBalance =
            creditDebt?.totalAllBalance?.toInt()!!.minus(creditDebt?.cattleBoughtBalance?.toInt()!!)
                .plus(totalCattleBoughtBal.toInt())
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
            creditDebt?.productPaid,
            creditDebt?.productBalance,
            creditDebt?.productAmount,
            cattleBoughtAmtPaid,
            totalCattleBoughtBal,
            totalCattleBoughtAmt,
            totalCattleBoughtQty,

        )
        creditDebtViewModel.addCreditDebt(creditDebt)

    }

    private fun updateCattleBought() {
        Log.d(TAG, "updateCattleBought: list is ${cattleList.size}")
        if (cattleList.size > 0) {
            for (i in 0 until cattleList.size) {
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

    private fun checkAllLayoutIfFilled(cattleTableLayout: CattleTableLayout?) {
        Log.d(TAG, "checkAllLayoutIfFilled: starts the check $cattleTableLayout")
        val cattleDetails = arrayListOf<String>()
        val cattleBoughtType = cattleTableLayout?.tvCattleType?.text.toString()
        val cattleBoughtPrice = cattleTableLayout?.etPrice?.text.toString()
        val cattleBoughtQty = cattleTableLayout?.etQty?.text.toString()
        val cattleBoughtAmt = cattleTableLayout?.tvCattleAmount?.text.toString()
        Log.d(TAG, "checkAllLayoutIfFilled: $cattleBoughtPrice  and $cattleBoughtQty")
        cattleDetails.add(cattleBoughtPrice)
        cattleDetails.add(cattleBoughtQty)
        Log.d(TAG, "checkAllLayoutIfFilled: ${cattleDetails.size}")
        if (cattleDetails.size < 2) {
            Toast.makeText(
                requireContext(),
                "Fill in the price and quantity or delete the type",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            validateInfo(cattleBoughtType, cattleBoughtPrice, cattleBoughtQty, cattleBoughtAmt)


        }


    }

    private fun amountPaidCheck(
        cattleBoughtType: String,
        cattleBoughtPrice: String,
        cattleBoughtQty: String,
        cattleBoughtAmt: String
    ) {
        binding.apply {
            val amountPaid = etAmountPaid.text.toString()
            val amount = tvTotalExactAmt.text.toString()
            when {
                amountPaid.toInt() < 0 -> {
                    Toast.makeText(
                        requireContext(),
                        "Amount paid cannot be negative",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                amountPaid.toInt() > amount.toInt() -> {
                    Toast.makeText(
                        requireContext(),
                        "The amount paid is more than the amount of the products bought",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    statusCheck(
                        amount,
                        amountPaid,
                        cattleBoughtType,
                        cattleBoughtPrice,
                        cattleBoughtQty,
                        cattleBoughtAmt
                    )


                }
            }

        }

    }

    private fun statusCheck(
        amount: String,
        amountPaid: String,
        cattleBoughtType: String,
        cattleBoughtPrice: String,
        cattleBoughtQty: String,
        cattleBoughtAmt: String
    ) {
        if(creditDebt!=null) status = creditDebt?.status!!
        when (status) {
            "not fully paid" -> {
                if (amountPaid.toInt() == 0) {
                    Toast.makeText(
                        requireContext(),
                        "Enter the amount partially paid",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else toCheckIfNewOrUpdate(
                    cattleBoughtType,
                    cattleBoughtPrice,
                    cattleBoughtQty,
                    cattleBoughtAmt
                )

            }
            "paid" -> {

                if (amountPaid.toInt() != amount.toInt()) {
                    Toast.makeText(
                        requireContext(),
                        "The amount paid should be equal to the amount of the products",
                        Toast.LENGTH_SHORT
                    ).show()
                } else toCheckIfNewOrUpdate(
                    cattleBoughtType,
                    cattleBoughtPrice,
                    cattleBoughtQty,
                    cattleBoughtAmt
                )
            }
            else -> {
                toCheckIfNewOrUpdate(
                    cattleBoughtType,
                    cattleBoughtPrice,
                    cattleBoughtQty,
                    cattleBoughtAmt
                )

            }

        }

    }


}