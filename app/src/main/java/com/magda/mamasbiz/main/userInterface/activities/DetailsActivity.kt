package com.magda.mamasbiz.main.userInterface.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.ActivityDetailsBinding
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.businessLogic.viewModels.ProductViewModel
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.DateCreated
import com.magda.mamasbiz.main.utils.Status

class DetailsActivity : AppCompatActivity() {
    private val TAG = "Derails Activity"
    private lateinit var binding : ActivityDetailsBinding
    private val _binding get() = binding!!
    private lateinit var creditDebt : CreditDebt
    private lateinit var productViewModel: ProductViewModel
    private lateinit var creditDebtViewModel: CreditDebtViewModel
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
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)
        //Get products
        productViewModel.getProducts(creditDebt.productId!!)
         Log.d(TAG, "onCreate: ${creditDebt.productId}")

        //observe the product data fetched
        productViewModel._liveDataFetchProduct.observe(this){
            when(it.status){
                Status.ERROR -> {
                  Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    products = it.data!!
                    toFillTable()
                }
                Status.LOADING ->{
                    // Loaded
                }
            }
        }

        // observe the credit debt deleted
        creditDebtViewModel._deleteCDLiveData.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    Toast.makeText(this, "Successfully deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                Status.LOADING -> {
                    //to check on it later
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // observe the credit debt updated
        creditDebtViewModel._loadCDLiveData.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
                    finish()

                }
                Status.LOADING -> {
                    //to check on it later
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }

        }

        //set text on views
        initViews()

        //Setting click listeners on the fabs
         settingFabClickListener()

        



    }

    private fun settingFabClickListener(){
        binding.apply {
            fabDelete.setOnClickListener { toDelete() }
            fabEdit.setOnClickListener { toEdit() }
            fabUpdate.setOnClickListener { toUpdatePayment() }
        }
    }

    private fun toEdit() {
       val intent = Intent(this@DetailsActivity, EditProductActivity::class.java)
        intent.putExtra(Constants.CREDIT_DEBT, creditDebt)
        startActivity(intent)
    }

    private fun toUpdatePayment() {
      val bottomSheetDialog = BottomSheetDialog(this, R.style.bottomDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.bottom_sheet_update_payment_dialog,
            findViewById (R.id.updateBottomSheetContainer))
        val mTotalAmtPaid = bottomSheetView.findViewById<EditText>(R.id.etTotalAmtPaid)
        val mTotalBalance = bottomSheetView.findViewById<TextView>(R.id.tvExactTotalBal)
        val mTotalAmt = bottomSheetView.findViewById<TextView>(R.id.tvTotalAmt)
        val mUpdatePayment = bottomSheetView.findViewById<Button>(R.id.btUpdate)
        val totalBalance =creditDebt.totalBalance
        val totalAmount = "Total Amount: Kes ${creditDebt.totalAmount}"
        mTotalAmt.text =totalAmount
        mTotalBalance.text = totalBalance
        mTotalAmtPaid.setText(creditDebt.totalPaid)
        mTotalAmtPaid.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Method sub
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Method sub
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if(text.isNotEmpty()){
                    val totalBal = creditDebt.totalAmount?.toInt()?.minus(text.toInt())
                    mTotalBalance.text = totalBal.toString()

                }
            }

        })
        mUpdatePayment.setOnClickListener {
            val newTotalBalance = mTotalBalance.text.toString()
            val newTotalAmountPaid = mTotalAmtPaid.text.toString()
            val status = if(newTotalBalance == "0"){
                "paid"
            }else "Not Paid"
            val creditDebt = CreditDebt(creditDebt.creditDebtId, creditDebt.userId,creditDebt.type, creditDebt.name, creditDebt.phoneNumber,
            status, creditDebt.paymentDate,creditDebt.dateCreated,creditDebt.totalAmount, creditDebt.productId,
            newTotalAmountPaid,newTotalBalance, DateCreated.getDateCreated(),true)
            creditDebtViewModel.addCreditDebt(creditDebt)
            bottomSheetDialog.dismissWithAnimation = true
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun toDelete() {
       val deleteDialog = AlertDialog.Builder(this)
        deleteDialog.setIcon(R.drawable.ic_baseline_delete_outline_24)
            .setTitle("Delete details")
            .setMessage("Are you sure you want to delete this document?")
            .setCancelable(true)
            .setPositiveButton("Ok",DialogInterface.OnClickListener{ _, _ -> toDeleteData()  })
        deleteDialog.show()

    }

    private fun toDeleteData() {
        creditDebtViewModel.deleteCreditDebt(creditDebt)

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
            tvTotalAmt.text = "KES ${creditDebt.totalAmount}"

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