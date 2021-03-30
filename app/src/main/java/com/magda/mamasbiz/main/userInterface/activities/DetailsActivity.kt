package com.magda.mamasbiz.main.userInterface.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.ActivityDetailsBinding
import com.magda.mamasbiz.main.businessLogic.viewModels.ProductViewModel
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Status

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailsBinding
    private val _binding get() = binding!!
    private lateinit var creditDebt : CreditDebt
    private lateinit var productViewModel: ProductViewModel
    private lateinit var products: Products
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        //Get extras from the previous activity

        creditDebt = intent.getParcelableExtra(Constants.CREDIT_DEBT)!!
        val debt = intent.getStringExtra(Constants.DEBT)
        val credit = intent.getStringExtra(Constants.CREDIT)
         if (credit!=null){
             val creditName = resources.getString(R.string.creditor_name)
             val creditNumber = resources.getString(R.string.creditor_number)
             binding.tvName.text = creditName
             binding.tvNumber.text = creditNumber
        }

        //initiate view model
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        //Get products
        productViewModel.getProducts(creditDebt.productId!!)

        //observe the product data fetched
        productViewModel._liveDataFetchProduct.observe(this){
            when(it.status){
                Status.ERROR -> {
                  Toast.makeText(this, "Product data was not available", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS ->{
                   products = it.data!!
                    toFillTable()
                }
                Status.LOADING ->{
                    // Loaded
                }
            }
        }

        //set text on views
        initViews()



    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.apply {
            tvDebtorName.text = creditDebt.name
            tvDebtorNumber.text = creditDebt.phoneNumber
            tvDebtorStatus.text = creditDebt.status
            tvDebtorPaymentDate.text = creditDebt.paymentDate
            tvDebtorTotalDebt.text = "KES: ${creditDebt.totalPaid}"
            tvDebtorTotalBalance.text = "KES: ${creditDebt.totalBalance}"
            tvTotalAmt.text = "KES: ${creditDebt.totalAmount}"

        }
    }

    private fun toFillTable() {

        binding.apply {
            if (products.meatAmt != "0") {
                tvMeatPrice.text = products.meatPrice
                tvMeatQty.text = products.meatQty
                tvMeatAmt.text = products.meatAmt
            } else meatRow.visibility = View.GONE
            if (products.intestineAmt != "0") {
                tvIntestinesPrice.text = products.intestinePrice
                tvIntestinesQty.text = products.intestineQty
                tvIntestinesAmt.text = products.intestineAmt
            } else intestinesRow.visibility = View.GONE
            if (products.africanSausageAmt != "0") {
                tvAfricanSausageQty.text = products.africanSausageQty
                tvAfricanSausagePrice.text = products.africanSausagePrice
                tvAfricanSausageAmt.text = products.africanSausageAmt
            } else africanSausageRow.visibility = View.GONE
            if (products.headAndLegsAmt != "0") {
                tvHeadAndLegsPrice.text = products.headAndLegsPrice
                tvHeadAndLegsQty.text = products.headAndLegsQty
                tvHeadAndLegsAmt.text = products.headAndLegsAmt
            } else headAndToeRow.visibility = View.GONE
            if (products.liverAmt != "0") {
                tvLiverQty.text = products.liverQty
                tvLiverPrice.text = products.liverPrice
                tvLiverAmt.text = products.liverAmt
            } else liverRow.visibility = View.GONE
            if (products.skinAmt != "0") {
                tvSkinQty.text = products.skinQty
                tvSkinPrice.text = products.skinPrice
                tvSkinAmt.text = products.skinAmt
            } else skinRow.visibility = View.GONE
            if (products.filletAmt != "0") {
                tvFilletQty.text = products.filletQty
                tvFilletPrice.text = products.filletPrice
                tvFilletAmt.text = products.filletAmt
            } else filletRow.visibility = View.GONE
        }

    }
}