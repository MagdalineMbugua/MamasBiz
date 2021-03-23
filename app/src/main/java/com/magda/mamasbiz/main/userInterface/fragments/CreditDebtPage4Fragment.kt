package com.magda.mamasbiz.main.userInterface.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPage3Binding
import com.magda.mamasbiz.databinding.FragmentCreditDebtPage4Binding
import com.magda.mamasbiz.main.businessLogic.CreditDebtViewModel
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.ProductsBought
import com.magda.mamasbiz.main.userInterface.activities.DashboardActivity
import com.magda.mamasbiz.main.utils.Constants
import kotlinx.android.synthetic.main.fragment_credit_debt_page4.*


class CreditPage4Fragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPage4Binding
    private val _binding get() = binding!!
    private lateinit var paymentDate: String
    private lateinit var productsBought: ProductsBought
    private lateinit var debtorName: String
    private lateinit var phoneNumber: String
    private lateinit var status: String
    private lateinit var totalAmount: String
    private lateinit var creditDebtViewModel: CreditDebtViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Getting the argument extras
        arguments?.getParcelable<ProductsBought>(Constants.PRODUCTS_BOUGHT)?.let { productsBought ->
            this.productsBought = productsBought
        }
        arguments?.getString(Constants.DEBTOR_NAME)?.let { debtorName ->
            this.debtorName = debtorName
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
        arguments?.getString(Constants.PAYMENT_DATE)?.let { paymentDate ->
            this.paymentDate = paymentDate
        }

        //instantiate view model
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditDebtPage4Binding.inflate(inflater)
        initViews()

        binding.tvBack.setOnClickListener {
            toPreviousPage()
        }
        binding.tvNext.setOnClickListener { toSubmitData() }
        return _binding.root
    }

    // Submit data to firebase
    private fun toSubmitData() {
        val creditDebt =
            CreditDebt(1, "", "", debtorName, phoneNumber, status, paymentDate, "", totalAmount, "")
        creditDebtViewModel.addCreditDebt(creditDebt)
        Toast.makeText(requireContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show()
        startActivity(Intent(requireActivity(), DashboardActivity::class.java))


    }

    private fun toPreviousPage() {
        val navController: NavController = Navigation.findNavController(binding.root)
        navController.navigate(R.id.action_creditPage4Fragment_to_creditPage3Fragment)
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.apply {
            tvDebtorName.text = debtorName
            tvDebtorNumber.text = phoneNumber
            tvDebtorStatus.text = status
            tvDebtorPaymentDate.text = paymentDate
            tvDebtorTotalDebt.text = "KES: $totalAmount "
            toFillTable()
        }
    }

    //Setting text on the text views
    private fun toFillTable() {
        binding.apply {
            if (productsBought.meatAmt != "0") {
                tvMeatPrice.text = productsBought.meatPrice
                tvMeatQty.text = productsBought.meatQty
                tvMeatAmt.text = productsBought.meatAmt
            } else meatRow.visibility = View.GONE
            if (productsBought.intestineAmt != "0") {
                tvIntestinesPrice.text = productsBought.intestinePrice
                tvIntestinesQty.text = productsBought.intestineQty
                tvIntestinesAmt.text = productsBought.intestineAmt
            } else intestinesRow.visibility = View.GONE
            if (productsBought.africanSausageAmt != "0") {
                tvAfricanSausageQty.text = productsBought.africanSausageQty
                tvAfricanSausagePrice.text = productsBought.africanSausagePrice
                tvAfricanSausageAmt.text = productsBought.africanSausageAmt
            } else africanSausageRow.visibility = View.GONE
            if (productsBought.headAndLegsAmt != "0") {
                tvHeadAndLegsPrice.text = productsBought.headAndLegsPrice
                tvHeadAndLegsQty.text = productsBought.headAndLegsQty
                tvHeadAndLegsAmt.text = productsBought.headAndLegsAmt
            } else headAndToeRow.visibility = View.GONE
            if (productsBought.liverAmt != "0") {
                tvLiverQty.text = productsBought.liverQty
                tvLiverPrice.text = productsBought.liverPrice
                tvLiverAmt.text = productsBought.liverAmt
            } else liverRow.visibility = View.GONE
            if (productsBought.skinAmt != "0") {
                tvSkinQty.text = productsBought.skinQty
                tvSkinPrice.text = productsBought.skinPrice
                tvSkinAmt.text = productsBought.skinAmt
            } else skinRow.visibility = View.GONE
            if (productsBought.filletAmt != "0") {
                tvFilletQty.text = productsBought.filletQty
                tvFilletPrice.text = productsBought.filletPrice
                tvFilletAmt.text = productsBought.filletAmt
            } else filletRow.visibility = View.GONE
        }
    }


}