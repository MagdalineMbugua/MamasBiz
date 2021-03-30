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
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.utils.Constants


class CreditPage3Fragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPage3Binding
    private  var paymentDate: String = ""
    private lateinit var products: Products
    private lateinit var name: String
    private lateinit var phoneNumber: String
    private lateinit var status: String
    private lateinit var totalAmount: String
    private lateinit var totalPaid: String
    private lateinit var totalBalance: String
    private  var credit: String?=""
    private  var debt: String?=""
    private val _binding get() = binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<Products>(Constants.PRODUCTS_BOUGHT)?.let { productsBought ->
          this.products = productsBought
        }
        arguments?.getString(Constants.DEBTOR_NAME)?.let{debtorName ->
            name=debtorName}
        arguments?.getString(Constants.DEBTOR_STATUS)?.let{status ->
            this.status= status}
        arguments?.getString(Constants.DEBTOR_NUMBER)?.let{phoneNumber ->
            this.phoneNumber= phoneNumber}
        arguments?.getString(Constants.TOTAL_AMOUNT)?.let{totalAmount ->
            this.totalAmount= totalAmount}
        arguments?.getString(Constants.DEBT)?.let{debt ->
            this.debt= debt}
        arguments?.getString(Constants.CREDIT)?.let{credit ->
            this.credit= credit}
        arguments?.getString(Constants.TOTAL_PAID)?.let{totalPaid ->
            this.totalPaid= totalPaid}
        arguments?.getString(Constants.BALANCE)?.let{totalBalance ->
            this.totalBalance= totalBalance}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditDebtPage3Binding.inflate(inflater, container, false)


        initViews()



        binding.apply {
            if(credit?.isNotEmpty() == true){
                val creditInfo = resources.getString(R.string.kindly_select_the_date_credit_to_be_paid)
                tvInfo.text=creditInfo
            } else if(debt?.isNotEmpty() == true){
                val debtInfo = resources.getString(R.string.kindly_select_the_date_to_be_paid)
                tvInfo.text= debtInfo
            }
            nextLayout.tvBack.setOnClickListener{toPreviousPage()}
            nextLayout.tvNext.setOnClickListener{toNextPage()}
        }


        return _binding.root
    }

    private fun toNextPage() {
        val args = Bundle()
        val navController = Navigation.findNavController(binding.root)
        if(paymentDate.isNotEmpty()){
            args.putParcelable(Constants.PRODUCTS_BOUGHT,products)
            args.putString(Constants.DEBTOR_NAME, name)
            args.putString(Constants.DEBTOR_NUMBER, phoneNumber)
            args.putString(Constants.DEBTOR_STATUS, status)
            args.putString(Constants.PAYMENT_DATE, paymentDate)
            args.putString(Constants.TOTAL_AMOUNT, totalAmount)
            args.putString(Constants.BALANCE, totalBalance)
            args.putString(Constants.TOTAL_PAID, totalPaid)
            if(credit!=null){
                args.putString(Constants.CREDIT, credit)
            } else if (debt!=null){
                args.putString(Constants.DEBT, debt)
            }
            navController.navigate(R.id.action_creditPage3Fragment_to_creditPage4Fragment, args)
        } else Toast.makeText(requireActivity(), "Select the payment date", Toast.LENGTH_SHORT).show()





    }

    private fun toPreviousPage() {
        val navController = Navigation.findNavController(binding.root)
        navController.navigate(R.id.action_creditPage3Fragment_to_creditPage2Fragment)

    }


    private fun initViews() {
        binding.apply {
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                paymentDate = StringBuilder(dayOfMonth).append(dayOfMonth).append("/").append(month+1).append("/")
                    .append(year).toString()
                tvDate.text = paymentDate

            }
        }

    }


}