package com.magda.mamasbiz.main.userInterface.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPage4Binding
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.businessLogic.viewModels.ProductViewModel
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.userInterface.activities.DashboardActivity
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.DateCreated
import com.magda.mamasbiz.main.utils.SessionManager
import com.magda.mamasbiz.main.utils.Status
import kotlinx.android.synthetic.main.fragment_credit_debt_page4.*


class CreditPage4Fragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPage4Binding
    private val _binding get() = binding!!
    private lateinit var paymentDate: String
    private lateinit var products: Products
    private lateinit var debtorName: String
    private lateinit var phoneNumber: String
    private lateinit var status: String
    private lateinit var totalAmount: String
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var type: String
    private var credit: String? = ""
    private var debt: String? = ""
    private lateinit var totalPaid: String
    private lateinit var totalBalance: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Getting the argument extras
        arguments?.getParcelable<Products>(Constants.PRODUCTS_BOUGHT)?.let { productsBought ->
            this.products = productsBought
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
        debt =arguments?.getString(Constants.DEBT)
        credit = arguments?.getString(Constants.CREDIT)

        arguments?.getString(Constants.TOTAL_PAID)?.let { totalPaid ->
            this.totalPaid = totalPaid
        }
        arguments?.getString(Constants.BALANCE)?.let { totalBalance ->
            this.totalBalance = totalBalance
        }


        //instantiate view model for creditdebt and product
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCreditDebtPage4Binding.inflate(inflater)

        //Setting text to the text views
        initViews()
        binding.apply {
            if (credit?.isNotEmpty() == true) {
                val creditName = resources.getString(R.string.creditor_name)
                val creditNumber = resources.getString(R.string.creditor_number)
                tvName.text = creditName
                tvNumber.text = creditNumber
                type = Constants.CREDIT

            }else type = Constants.DEBT

            tvBack.setOnClickListener { toPreviousPage() }
            tvNext.setOnClickListener { toUploadCreditDebtData() }
        }
        creditDebtViewModel._loadCDLiveData.observe(viewLifecycleOwner, {
            Log.d(TAG, "toSubmitData: Called viewmodel")
            when (it.status) {
                Status.LOADING -> {
                    Toast.makeText(requireActivity(), "Uploading...", Toast.LENGTH_SHORT).show()

                }
                Status.SUCCESS -> {
                    toUpdateProductData()
                    Toast.makeText(
                        requireActivity(),
                        "Uploading was successful",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(Intent(requireActivity(), DashboardActivity::class.java))




                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity(), it.error, Toast.LENGTH_SHORT).show()

                }
            }

        })
        productViewModel._liveDataAddProduct.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Toast.makeText(
                        requireActivity(),
                        "Uploading product successful",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                Status.ERROR -> {
                    Toast.makeText(
                        requireActivity(),
                        "Uploading product not successful",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                Status.LOADING -> {
                    Toast.makeText(
                        requireActivity(),
                        "Uploading products ...",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })


        return _binding.root
    }

    private fun toUpdateProductData() {
        productViewModel.addProducts(products)
    }


    // Upload data to firebase
    private fun toUploadCreditDebtData() {
        Log.d(TAG, "toSubmitData: data")
        val dateCreated = DateCreated.getDateCreated()
        val creditDebtId = creditDebtViewModel.getCreditDebtId()
        val userId = getUserId()
        val creditDebt =
            CreditDebt(
                creditDebtId,
                userId,
                type,
                debtorName,
                phoneNumber,
                status,
                paymentDate,
                dateCreated,
                totalAmount,
                products.productId,
                totalPaid,
                totalBalance
            )
        Log.d(TAG, "toSubmitData: $creditDebt")
        creditDebtViewModel.addCreditDebt(creditDebt)

        Log.d(TAG, "toSubmitData: after Viewmodel")


    }
    private fun getUserId():String{
        val sessionManager = SessionManager(requireContext())
        val userId =sessionManager.getUserId()
        return userId!!


    }

    //Navigating back on fragment
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
            tvDebtorTotalDebt.text = "KES: $totalPaid"
            tvDebtorTotalBalance.text = "KES: $totalBalance"
            tvTotalAmt.text = "KES: $totalAmount"
            toFillTable()
        }
    }

    //Setting the table to only show filled data
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