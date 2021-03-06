package com.magda.mamasbiz.main.userInterface.activities

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.ActivityDetailsBinding
import com.magda.mamasbiz.main.businessLogic.adapter.CattleBoughtAdapter
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.businessLogic.viewModels.ProductViewModel
import com.magda.mamasbiz.main.data.entity.*
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.DateCreated
import com.magda.mamasbiz.main.utils.Status
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var creditDebt: CreditDebt
    private lateinit var productViewModel: ProductViewModel
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private var products: Products? = null
    private var cattleBoughtList: ArrayList<CattleBought>? = null
    private var updatePayments: UpdatePayments? = null
    private var credit: String? = ""
    private var debt: String? = ""
    private lateinit var metadata: Metadata
    private lateinit var newTotalAmountPaid: String
    private lateinit var cattleBoughtAdapter: CattleBoughtAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get extras from the previous activity
        getExtraIntents()
        initCreditDebtView()


        //initiate view model
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)


        //Get products
        creditDebt.productId?.let {
            productViewModel.getProducts(it)
        }
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

        //Observe cattle bought fetched
        fetchCattleBoughtLiveData()


        fetchCreditDebtDocumentLiveData()


        deleteCattleBoughtLiveData()


        //Setting click listeners on the fabs
        settingFabClickListener()


        binding.tvViewUpdatePayment.setOnClickListener {
            val intent = Intent(this@DetailsActivity, PaymentActivity::class.java)
            intent.putExtra(Constants.CREDIT_DEBT, creditDebt)
            startActivity(intent)
        }


    }

    private fun deleteCattleBoughtLiveData() {
        creditDebtViewModel._deleteCattleBoughtLiveData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    //To observe
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    creditDebtViewModel.deleteCreditDebt(creditDebt)

                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_OK)
    }

    private fun fetchCreditDebtDocumentLiveData() {
        creditDebtViewModel._fetchCDDocumentLiveData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    //To observe
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    creditDebt = it.data!!
                    initCreditDebtView()
                    productViewModel.getProducts(creditDebt.productId!!)

                }

            }
        }
    }

    private fun fetchCattleBoughtLiveData() {
        creditDebtViewModel._fetchCattleBoughtLiveData.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    cattleBoughtList = it.data
                    if (cattleBoughtList?.size!! < 1) cattleBoughtList = null
                    initViews()
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    // Method sub
                }
            }
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
                    setResult(Activity.RESULT_OK)
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
                    initViews()
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
            val supplierName = resources.getString(R.string.supplier_name)
            val supplierNumber = resources.getString(R.string.supplier_number)
            val pageTitle = resources.getString(R.string.details_of_the_supplier)
            binding.apply {
                tvName.text = supplierName
                tvNumber.text = supplierNumber
                tvInfo.text = pageTitle
            }
            fabEditCattle.visibility = View.VISIBLE
        } else {
            val pageTitle = resources.getString(R.string.details_of_the_customer)
            binding.tvInfo.text = pageTitle
            fabEditCattle.visibility = View.GONE
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
            fabEditCattle.setOnClickListener { toEditCattle() }
            fabUpdate.setOnClickListener { toUpdatePayment() }
        }
    }

    private fun toEditCattle() {
        val intent = Intent(this@DetailsActivity, EditCattleActivity::class.java)
        intent.putExtra(Constants.CREDIT_DEBT, creditDebt)
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                cattleBoughtList?.clear()
                creditDebtViewModel.fetchMetadata(creditDebt.userId!!)
                creditDebtViewModel.fetchCattleBought(creditDebt.creditDebtId!!)
                creditDebtViewModel.getCreditDebtDocument(creditDebt.creditDebtId!!)
            }

        } else if (requestCode == 2000) {
            if (resultCode == Activity.RESULT_OK) {
                creditDebtViewModel.fetchMetadata(creditDebt.userId!!)
                creditDebtViewModel.getCreditDebtDocument(creditDebt.creditDebtId!!)
            }

        } else Toast.makeText(
            this,
            "Error occurred updating data to this page",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun toEdit() {
        val intent = Intent(this@DetailsActivity, EditProductActivity::class.java)
        intent.putExtra(Constants.CREDIT_DEBT, creditDebt)
        startActivityForResult(intent, 2000)
    }

    private fun toUpdatePayment() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.bottomDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.bottom_sheet_update_payment_dialog,
            findViewById(R.id.updateBottomSheetContainer)
        )
        val mTotalAmtPaid = bottomSheetView.findViewById<EditText>(R.id.etTotalAmtPaid)
        val mTotalBalance = bottomSheetView.findViewById<TextView>(R.id.tvTotalBal)
        val mTotalAmt = bottomSheetView.findViewById<TextView>(R.id.tvTotalAmt)
        val mUpdatePayment = bottomSheetView.findViewById<Button>(R.id.btUpdate)
        val totalBalance = "Total Balance: Kes ${creditDebt.totalAllBalance}"
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
                    val paidAmt = text.toIntOrNull()
                    if (paidAmt != null) {
                        if (paidAmt > 0) {
                            calculateBalance(paidAmt, mTotalBalance)
                        } else Toast.makeText(
                            this@DetailsActivity,
                            "Total amount paid cannot be negative",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else Toast.makeText(
                        this@DetailsActivity,
                        "Add only Integers",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }

        })
        mUpdatePayment.setOnClickListener {
            val updatedPaidAmt = mTotalAmtPaid.text.toString()
            val updatedBalance = mTotalBalance.text.toString()
            val updatedBalanceInteger = updatedBalance.filter { it.isDigit() }
            validateUpdatedAmount(updatedPaidAmt, updatedBalanceInteger)
            bottomSheetDialog.dismissWithAnimation = true
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun validateUpdatedAmount(updatedPaidAmt: String, updatedBalance: String) {
        newTotalAmountPaid = updatedPaidAmt
        val updatedTotalAmtPaid =
            updatedPaidAmt.toInt().plus(creditDebt.totalAllPaid!!.toInt())
        when (updatedTotalAmtPaid.plus(updatedBalance.toInt())) {
            creditDebt.totalAllAmount?.toInt() -> {
                val status = if (updatedBalance == "0") {
                    "paid"
                } else "not fully paid"
                updatePayments = UpdatePayments(
                    creditDebt.creditDebtId,
                    creditDebtViewModel.getUpdatePaymentId(creditDebt),
                    updatedPaidAmt,
                    DateCreated.getDateCreated(),
                    updatedBalance,
                    creditDebt.userId
                )
                checkIndividualBal(
                    updatedPaidAmt,
                    updatedTotalAmtPaid.toString(),
                    updatedBalance,
                    status
                )

            }
            else -> Toast.makeText(
                this@DetailsActivity,
                "The amount paid and balance does not total the amount. Try again.",
                Toast.LENGTH_SHORT
            )
                .show()
        }


    }

    private fun calculateBalance(paidAmt: Int, mTotalBalance: TextView) {
        val totalBal = creditDebt.totalAllBalance?.toInt()?.minus(paidAmt)
        if (totalBal != null) {
            if (totalBal >= 0) {
                val totalBalance = "Total Balance: Kes $totalBal"
                mTotalBalance.text = totalBalance

            } else Toast.makeText(
                this@DetailsActivity,
                "You cannot pay more than the sold or purchased amount",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    private fun checkIndividualBal(
        updatedIndividualTotalAmountPaid: String,
        newTotalAmountPaid: String,
        newTotalBalance: String,
        status: String
    ) {
        if (cattleBoughtList != null && products == null) {
            val cattleBoughtPaid =
                creditDebt.cattleBoughtPaid?.toInt()?.plus(updatedIndividualTotalAmountPaid.toInt())
            val cattleBoughtBal =
                creditDebt.cattleBoughtBalance?.toInt()?.minus(updatedIndividualTotalAmountPaid.toInt())
            creditDebtViewModel.updateTotalMoney(
                creditDebt.creditDebtId!!,
                newTotalAmountPaid,
                newTotalBalance,
                status,
                creditDebt.productPaid!!,
                creditDebt.productBalance!!,
                cattleBoughtPaid.toString(),
                cattleBoughtBal.toString()
            )

        } else if (cattleBoughtList == null && products != null) {
            val productPaid =
                creditDebt.productPaid?.toInt()?.plus(updatedIndividualTotalAmountPaid.toInt())
            val productBal =
                creditDebt.productBalance?.toInt()?.minus(updatedIndividualTotalAmountPaid.toInt())
            creditDebtViewModel.updateTotalMoney(
                creditDebt.creditDebtId!!,
                newTotalAmountPaid,
                newTotalBalance,
                status,
                productPaid.toString(),
                productBal.toString(),
                creditDebt.cattleBoughtPaid!!,
                creditDebt.cattleBoughtBalance!!
            )
        } else if (cattleBoughtList != null && products != null) {
            creditDebtViewModel.updateTotalMoney(
                creditDebt.creditDebtId!!,
                newTotalAmountPaid,
                newTotalBalance,
                status,
                creditDebt.productPaid!!,
                creditDebt.productBalance!!,
                creditDebt.cattleBoughtPaid!!,
                creditDebt.cattleBoughtBalance!!
            )
        }
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
        creditDebtViewModel.deleteCattleBought(creditDebt.creditDebtId!!)

    }

    private fun initCreditDebtView() {
        binding.apply {
            tvDebtorName.text = creditDebt.name
            tvCustomerNumber.text = creditDebt.phoneNumber
            val totalBalance = "Kes: ${creditDebt.totalAllBalance}"
            val totalPaid = "Kes: ${creditDebt.totalAllPaid}"
            val totalAmount = "Kes: ${creditDebt.totalAllAmount}"
            tvTotalExactBalance.text = totalBalance
            tvCustomerTotalPaid.text = totalPaid
            tvCustomerTotalAllAMount.text = totalAmount
            tvDebtorStatus.text = creditDebt.status
            tvDebtorPaymentDate.text = creditDebt.paymentDate

        }
    }


    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.apply {
            when {
                (cattleBoughtList != null && products == null) -> {
                    setRecyclerViewAndViews()
                }
                (cattleBoughtList == null && products != null) -> {
                    setProductViews()
                }
                (cattleBoughtList != null && products != null) -> {
                    setRecyclerViewAndViews()
                    setProductViews()
                }

            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setRecyclerViewAndViews() {
        binding.apply {
            cattleBoughtTitle.visibility = View.VISIBLE
            tvTotalCattleAmount.visibility = View.VISIBLE
            tvTotalCattleExactAmount.visibility = View.VISIBLE
            tvTotalCattleExactAmount.text = "Kes: ${creditDebt.cattleBoughtAmount}"
            tvTotalCattleQty.visibility = View.VISIBLE
            tvTotalExactCattleQty.visibility = View.VISIBLE
            tvTotalExactCattleQty.text = "${creditDebt.cattleBoughtQty}"
            cattleBoughtRecyclerView.visibility = View.VISIBLE
            setUpRecyclerView()

        }

    }

    private fun setUpRecyclerView() {
        cattleBoughtAdapter = CattleBoughtAdapter(this)
        cattleBoughtAdapter.addList(cattleBoughtList!!)
        val manager = LinearLayoutManager(this)
        manager.orientation = RecyclerView.VERTICAL
        binding.cattleBoughtRecyclerView.apply {
            layoutManager = manager
            adapter = cattleBoughtAdapter
            hasFixedSize()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setProductViews() {
        binding.apply {
            purchasedProductTable.visibility = View.VISIBLE
            tvProductAmount.visibility = View.VISIBLE
            tvTotalExactProductAmount.visibility = View.VISIBLE
            tvTotalExactProductAmount.text = "KES: ${creditDebt.productAmount}"
            toFillTable()

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