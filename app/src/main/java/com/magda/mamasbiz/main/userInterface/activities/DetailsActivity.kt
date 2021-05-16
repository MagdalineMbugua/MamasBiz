package com.magda.mamasbiz.main.userInterface.activities

import android.annotation.SuppressLint
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
import com.magda.mamasbiz.main.data.entity.*
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.DateCreated
import com.magda.mamasbiz.main.utils.Status

class DetailsActivity : AppCompatActivity() {
    private val TAG = "Details Activity"
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var creditDebt: CreditDebt
    private lateinit var productViewModel: ProductViewModel
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private var products: Products? = null
    private var cattleBoughtList : ArrayList<CattleBought>? = null
    private var updatePayments: UpdatePayments? = null
    private var credit: String? = ""
    private var debt: String? = ""
    private lateinit var metadata: Metadata
    private lateinit var newTotalAmountPaid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get extras from the previous activity
        getExtraIntents()


        //initiate view model
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)


        //Get products
        productViewModel.getProducts(creditDebt.productId!!)
        Log.d(TAG, "onCreate: ${creditDebt.productId}")
        // fetch metadata
        creditDebtViewModel.fetchMetadata(creditDebt.userId!!)

        //fetchCattleBought
        creditDebtViewModel.fetchCattleBought(creditDebt.creditDebtId!!)

        //observe the product data fetched
        fetchProductsLiveData()


        // observe the credit debt deleted
        deleteCreditDebtLiveData()


        // observe the credit debt updated
        updateCreditDebtLiveData()


        //observe the update Payments
        updatePaymentsLiveData()


        //observe metadata fetched
        fetchMetadataLiveData()


        //Observe metadata added
        addMetadataLiveData()


        //set text on views
        initViews()

        //Setting click listeners on the fabs
        settingFabClickListener()


        binding.tvViewUpdatePayment.setOnClickListener {
            val intent = Intent(this@DetailsActivity, PaymentActivity::class.java)
            Log.d(TAG, "onCreate: $creditDebt")
            intent.putExtra(Constants.CREDIT_DEBT, creditDebt)
            startActivity(intent)
        }


    }

    private fun addMetadataLiveData() {
        creditDebtViewModel._addMetadataLiveData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    //to figure out
                }
                Status.SUCCESS -> {
                    Toast.makeText(this, "Successfully updated.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun fetchMetadataLiveData() {
        creditDebtViewModel._fetchMetadataLiveData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    //method sub
                }
                Status.SUCCESS -> {
                    metadata = it.data!!
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePaymentsLiveData() {
        creditDebtViewModel._updatePaymentLiveData.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    toUpdateMetadata()

                }
                Status.LOADING -> {
                    //to check on it later
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onCreate: ${it.error}")
                }
            }

        }
    }

    private fun updateCreditDebtLiveData() {
        creditDebtViewModel._updateTotalAmountLiveData.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show()
                    creditDebtViewModel.addUpdatePayments(creditDebt, updatePayments!!)
                }
                Status.LOADING -> {
                    //to check on it later
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun deleteCreditDebtLiveData() {
        creditDebtViewModel._deleteCDLiveData.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    deleteMetadata()
                }
                Status.LOADING -> {
                    //to check on it later
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchProductsLiveData() {
        productViewModel._liveDataFetchProduct.observe(this) {
            when (it.status) {
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    products = it.data!!
                    toFillTable()
                }
                Status.LOADING -> {
                    // Loaded
                }
            }
        }
    }

    private fun getExtraIntents() {
        creditDebt = intent.getParcelableExtra(Constants.CREDIT_DEBT)!!
        debt = intent.getStringExtra(Constants.DEBT)
        credit = intent.getStringExtra(Constants.CREDIT)
        if (credit != null) {
            val creditName = resources.getString(R.string.creditor_name)
            val creditNumber = resources.getString(R.string.creditor_number)
            binding.tvName.text = creditName
            binding.tvNumber.text = creditNumber
        }
    }

    private fun deleteMetadata() {
        if (credit != null) {
            val metadata = Metadata(
                metadata.totalMoneySentPaid.minus(creditDebt.totalAllPaid!!.toInt()),
                metadata.totalMoneySentAmt.minus(creditDebt.totalAllAmount!!.toInt()),
                metadata.totalMoneySentBalance.minus(creditDebt.totalAllBalance!!.toInt()),
                metadata.totalMoneyReceivedPaid,
                metadata.totalMoneyReceivedAmt,
                metadata.totalMoneyReceivedBalance
            )
            creditDebtViewModel.addMetadata(metadata, creditDebt.userId!!)

        } else if (debt != null) {
            val metadata = Metadata(
                metadata.totalMoneySentPaid,
                metadata.totalMoneySentAmt,
                metadata.totalMoneySentBalance,
                metadata.totalMoneyReceivedPaid.minus(creditDebt.totalAllPaid!!.toInt()),
                metadata.totalMoneyReceivedAmt.minus(creditDebt.totalAllAmount!!.toInt()),
                metadata.totalMoneyReceivedBalance.minus(creditDebt.totalAllBalance!!.toInt())
            )
            creditDebtViewModel.addMetadata(metadata, creditDebt.userId!!)

        }
    }

    private fun settingFabClickListener() {
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
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.bottom_sheet_update_payment_dialog,
            findViewById(R.id.updateBottomSheetContainer)
        )
        val mTotalAmtPaid = bottomSheetView.findViewById<EditText>(R.id.etTotalAmtPaid)
        val mTotalBalance = bottomSheetView.findViewById<TextView>(R.id.tvExactTotalBal)
        val mTotalAmt = bottomSheetView.findViewById<TextView>(R.id.tvTotalAmt)
        val mUpdatePayment = bottomSheetView.findViewById<Button>(R.id.btUpdate)
        val totalBalance = creditDebt.totalAllBalance
        val totalAmount = "Total Amount: Kes ${creditDebt.totalAllAmount}"
        mTotalAmt.text = totalAmount
        mTotalBalance.text = totalBalance
        mTotalAmtPaid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Method sub
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Method sub
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty()) {
                    val totalBal = creditDebt.totalAllAmount?.toInt()?.minus(text.toInt())
                    mTotalBalance.text = totalBal.toString()

                }
            }

        })
        mUpdatePayment.setOnClickListener {
            val newTotalBalance = mTotalBalance.text.toString()
            newTotalAmountPaid = mTotalAmtPaid.text.toString()
            val updatedTotalAmtPaid =
                newTotalAmountPaid.toInt().plus(creditDebt.totalAllPaid!!.toInt())
            val status = if (newTotalBalance == "0") {
                "paid"
            } else "not paid"
            updatePayments = UpdatePayments(
                creditDebt.creditDebtId,
                creditDebtViewModel.getUpdatePaymentId(creditDebt),
                newTotalAmountPaid,
                DateCreated.getDateCreated(),
                newTotalBalance,
                creditDebt.userId
            )
            creditDebtViewModel.updateTotalMoney(
                creditDebt.creditDebtId!!,
                updatedTotalAmtPaid.toString(),
                newTotalBalance,
                status
            )


            bottomSheetDialog.dismissWithAnimation = true
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun toUpdateMetadata() {
        if (credit != null) {
            val metadata = Metadata(
                metadata.totalMoneySentPaid.plus(newTotalAmountPaid.toInt()),
                metadata.totalMoneySentAmt,
                metadata.totalMoneySentBalance.minus(newTotalAmountPaid.toInt()),
                metadata.totalMoneyReceivedPaid,
                metadata.totalMoneyReceivedAmt,
                metadata.totalMoneyReceivedBalance
            )
            creditDebtViewModel.addMetadata(metadata, creditDebt.userId!!)


        } else if (debt != null) {
            val metadata = Metadata(
                metadata.totalMoneySentPaid,
                metadata.totalMoneySentAmt,
                metadata.totalMoneySentBalance,
                metadata.totalMoneyReceivedPaid.plus(newTotalAmountPaid.toInt()),
                metadata.totalMoneyReceivedAmt,
                metadata.totalMoneyReceivedBalance.minus(newTotalAmountPaid.toInt())
            )
            creditDebtViewModel.addMetadata(metadata, creditDebt.userId!!)
        }
    }

    private fun toDelete() {
        val deleteDialog = AlertDialog.Builder(this)
        deleteDialog.setIcon(R.drawable.ic_baseline_delete_outline_24)
            .setTitle("Delete details")
            .setMessage("Are you sure you want to delete this document?")
            .setCancelable(true)
            .setPositiveButton("Ok") { _, _ -> toDeleteData() }
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
            tvDebtorTotalDebt.text = "KES: ${creditDebt.totalAllPaid}"
            tvDebtorTotalBalance.text = "KES: ${creditDebt.totalAllBalance}"
            tvTotalAmt.text = "KES ${creditDebt.totalAllAmount}"

        }
    }

    private fun toFillTable() {
        binding.apply {
            if (products?.meatAmt != "0") {
                tvMeatPrice.text = products?.meatPrice
                tvMeatQty.text = products?.meatQty
                tvMeatAmt.text = products?.meatAmt
                meatRow.visibility = View.VISIBLE
            } else meatRow.visibility = View.GONE
            if (products?.intestineAmt != "0") {
                tvIntestinesPrice.text = products?.intestinePrice
                tvIntestinesQty.text = products?.intestineQty
                tvIntestinesAmt.text = products?.intestineAmt
                intestinesRow.visibility = View.VISIBLE
            } else intestinesRow.visibility = View.GONE
            if (products?.africanSausageAmt != "0") {
                tvAfricanSausageQty.text = products?.africanSausageQty
                tvAfricanSausagePrice.text = products?.africanSausagePrice
                tvAfricanSausageAmt.text = products?.africanSausageAmt
                africanSausageRow.visibility = View.VISIBLE
            } else africanSausageRow.visibility = View.GONE
            if (products?.headAndLegsAmt != "0") {
                tvHeadAndLegsPrice.text = products?.headAndLegsPrice
                tvHeadAndLegsQty.text = products?.headAndLegsQty
                tvHeadAndLegsAmt.text = products?.headAndLegsAmt
                headAndToeRow.visibility = View.VISIBLE
            } else headAndToeRow.visibility = View.GONE
            toFillMoreTable()

        }

    }

    private fun toFillMoreTable() {
        binding.apply {
            if (products?.liverAmt != "0") {
                tvLiverQty.text = products?.liverQty
                tvLiverPrice.text = products?.liverPrice
                tvLiverAmt.text = products?.liverAmt
                liverRow.visibility = View.VISIBLE
            } else liverRow.visibility = View.GONE
            if (products?.skinAmt != "0") {
                tvSkinQty.text = products?.skinQty
                tvSkinPrice.text = products?.skinPrice
                tvSkinAmt.text = products?.skinAmt
                skinRow.visibility = View.VISIBLE
            } else skinRow.visibility = View.GONE
            if (products?.filletAmt != "0") {
                tvFilletQty.text = products?.filletQty
                tvFilletPrice.text = products?.filletPrice
                tvFilletAmt.text = products?.filletAmt
                filletRow.visibility = View.VISIBLE
            } else filletRow.visibility = View.GONE

        }

    }
}