package com.magda.mamasbiz.main.userInterface.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPage3Binding
import com.magda.mamasbiz.main.data.entity.CattleBought
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.utils.Constants
import java.util.*


class CreditPage3Fragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPage3Binding
    private var paymentDate: String = ""
    private var products: Products? = null
    private lateinit var name: String
    private lateinit var phoneNumber: String
    private lateinit var status: String
    private  var totalAmount: String?= ""
    private  var totalPaid: String? =""
    private var totalBalance: String? =""
    private var credit: String? = ""
    private var debt: String? = ""
    private val _binding get() = binding!!
    private var cattleBoughtList: ArrayList<CattleBought>? = null
    private lateinit var totalCattleBoughtAmount: String
    private lateinit var totalCattleBoughtQty: String
    private lateinit var totalCattleBoughtPaid: String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getExtraArguments()

    }

    private fun getExtraArguments() {
        arguments?.getParcelable<Products>(Constants.PRODUCTS_BOUGHT)?.let { productsBought ->
            this.products = productsBought
        }
        arguments?.getString(Constants.DEBTOR_NAME)?.let { debtorName ->
            name = debtorName
        }
        arguments?.getString(Constants.DEBTOR_STATUS)?.let { status ->
            this.status = status
        }
        arguments?.getString(Constants.DEBTOR_NUMBER)?.let { phoneNumber ->
            this.phoneNumber = phoneNumber
        }
        arguments?.getString(Constants.TOTAL_AMOUNT)?.let { totalAmount ->
            this.totalAmount = totalAmount
        }
        arguments?.getString(Constants.DEBT)?.let { debt ->
            this.debt = debt
        }
        arguments?.getString(Constants.CREDIT)?.let { credit ->
            this.credit = credit
        }
        arguments?.getString(Constants.TOTAL_PAID)?.let { totalPaid ->
            this.totalPaid = totalPaid
        }
        arguments?.getString(Constants.BALANCE)?.let { totalBalance ->
            this.totalBalance = totalBalance
        }
        requireArguments().getParcelableArrayList<CattleBought>(Constants.CATTLE_BOUGHT_LIST)
            ?.let { cattleBoughtList ->
                this.cattleBoughtList = cattleBoughtList
            }
        requireArguments().getString(Constants.TOTAL_CATTLE_BOUGHT_AMOUNT)?.let { totalCattleBoughtAmount ->
            this.totalCattleBoughtAmount = totalCattleBoughtAmount
        }

        requireArguments().getString(Constants.TOTAL_CATTLE_BOUGHT_PAID)?.let { totalCattleBoughtPaid ->
            this.totalCattleBoughtPaid = totalCattleBoughtPaid
        }

        requireArguments().getString(Constants.TOTAL_CATTLE_BOUGHT_QTY)?.let { totalCattleBoughtQty ->
            this.totalCattleBoughtQty = totalCattleBoughtQty
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditDebtPage3Binding.inflate(inflater, container, false)


        initViews()



        binding.apply {
            if (credit?.isNotEmpty() == true) {
                val creditInfo =
                    resources.getString(R.string.kindly_select_the_date_credit_to_be_paid)
                tvInfo.text = creditInfo
            } else if (debt?.isNotEmpty() == true) {
                val debtInfo = resources.getString(R.string.kindly_select_the_date_to_be_paid)
                tvInfo.text = debtInfo
            }
            nextLayout.tvBack.setOnClickListener { toPreviousPage() }
            nextLayout.tvNext.setOnClickListener { toNextPage() }
        }


        return _binding.root
    }

    private fun toNextPage() {
        val args = Bundle()
        val navController = Navigation.findNavController(binding.root)
        if (paymentDate.isNotEmpty()) {
            if (products != null) args.putParcelable(Constants.PRODUCTS_BOUGHT, products)
            args.putString(Constants.DEBTOR_NAME, name)
            args.putString(Constants.DEBTOR_NUMBER, phoneNumber)
            args.putString(Constants.DEBTOR_STATUS, status)
            args.putString(Constants.PAYMENT_DATE, paymentDate)
           if(totalAmount?.isNotEmpty()==true) args.putString(Constants.TOTAL_AMOUNT, totalAmount)
            if(totalBalance?.isNotEmpty()==true) args.putString(Constants.BALANCE, totalBalance)
            if(totalPaid?.isNotEmpty()== true) args.putString(Constants.TOTAL_PAID, totalPaid)
            if (credit != null) {
                args.putString(Constants.CREDIT, credit)
            } else if (debt != null) {
                args.putString(Constants.DEBT, debt)
            }
            if (cattleBoughtList!=null) {
                args.putParcelableArrayList(Constants.CATTLE_BOUGHT_LIST, cattleBoughtList)
                args.putString(Constants.TOTAL_CATTLE_BOUGHT_AMOUNT, totalCattleBoughtAmount)
                args.putString(Constants.TOTAL_CATTLE_BOUGHT_PAID,totalCattleBoughtPaid)
                args.putString(Constants.TOTAL_CATTLE_BOUGHT_QTY, totalCattleBoughtQty)
            }
            navController.navigate(R.id.action_creditPage3Fragment_to_creditPage4Fragment, args)
        } else Toast.makeText(requireActivity(), "Select the payment date", Toast.LENGTH_SHORT)
            .show()


    }

    private fun toPreviousPage() {
        val navController = Navigation.findNavController(binding.root)
        val arg = Bundle()
        arg.putString(Constants.DEBTOR_NAME, name)
        arg.putString(Constants.DEBTOR_NUMBER, phoneNumber)
        arg.putString(Constants.DEBTOR_STATUS, status)
        if(credit!=null){
            arg.putString(Constants.CREDIT, credit)
        } else if (debt!=null){
            arg.putString(Constants.DEBT, debt)
        }
        if (products!=null){
            navController.navigate(R.id.action_creditPage3Fragment_to_creditPage2Fragment, arg)
        } else navController.navigate(R.id.action_creditPage3Fragment_to_creditDebtPageFragment, arg)


    }


    private fun initViews() {
        binding.apply {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE ,-1)
            calendar.also { calendarView.minDate = it.timeInMillis }
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                paymentDate =
                    StringBuilder(dayOfMonth).append(dayOfMonth).append("/").append(month + 1)
                        .append("/")
                        .append(year).toString()
                tvDate.text = paymentDate

            }

        }

    }


}