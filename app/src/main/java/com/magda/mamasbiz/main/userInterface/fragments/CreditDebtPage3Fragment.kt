package com.magda.mamasbiz.main.userInterface.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPage3Binding
import com.magda.mamasbiz.main.data.entity.ProductsBought
import com.magda.mamasbiz.main.utils.Constants


class CreditPage3Fragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPage3Binding
    private  var paymentDate: String = ""
    private lateinit var productsBought: ProductsBought
    private lateinit var name: String
    private lateinit var phoneNumber: String
    private lateinit var status: String
    private lateinit var totalAmount: String
    private val _binding get() = binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<ProductsBought>(Constants.PRODUCTS_BOUGHT)?.let { productsBought ->
          this.productsBought = productsBought
        }
        arguments?.getString(Constants.DEBTOR_NAME)?.let{debtorName ->
            name=debtorName}
        arguments?.getString(Constants.DEBTOR_STATUS)?.let{status ->
            this.status= status}
        arguments?.getString(Constants.DEBTOR_NUMBER)?.let{phoneNumber ->
            this.phoneNumber= phoneNumber}
        arguments?.getString(Constants.TOTAL_AMOUNT)?.let{totalAmount ->
            this.totalAmount= totalAmount}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditDebtPage3Binding.inflate(inflater, container, false)


        initViews()

        binding.apply {
            nextLayout.tvBack.setOnClickListener{toPreviousPage()}
            nextLayout.tvNext.setOnClickListener{toNextPage()}
        }


        return _binding.root
    }

    private fun toNextPage() {
        val args = Bundle()
        val navController = Navigation.findNavController(binding.root)
        if(paymentDate.isNotEmpty()){
            args.putParcelable(Constants.PRODUCTS_BOUGHT,productsBought)
            args.putString(Constants.DEBTOR_NAME, name)
            args.putString(Constants.DEBTOR_NUMBER, phoneNumber)
            args.putString(Constants.DEBTOR_STATUS, status)
            args.putString(Constants.PAYMENT_DATE, paymentDate)
            args.putString(Constants.TOTAL_AMOUNT, totalAmount)
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