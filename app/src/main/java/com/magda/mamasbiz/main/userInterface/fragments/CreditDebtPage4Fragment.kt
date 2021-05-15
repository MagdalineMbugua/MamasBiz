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
import com.magda.mamasbiz.main.data.entity.CattleBought
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Metadata
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.userInterface.activities.DashboardActivity
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.DateCreated
import com.magda.mamasbiz.main.utils.SessionManager
import com.magda.mamasbiz.main.utils.Status
import kotlinx.android.synthetic.main.fragment_credit_debt_page4.*
import java.util.ArrayList


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
    private lateinit var cattleBoughtList: ArrayList<CattleBought>
    private lateinit var type: String
    private var credit: String? = ""
    private var debt: String? = ""
    private lateinit var totalPaid: String
    private lateinit var totalBalance: String
    private lateinit var metadata: Metadata
    private lateinit var creditDebt: CreditDebt
    private lateinit var creditDebtId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Getting the argument extras
        getExtraArgument()


        //instantiate view model for creditdebt and product
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
    }

    private fun getExtraArgument() {
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
        debt = arguments?.getString(Constants.DEBT)
        credit = arguments?.getString(Constants.CREDIT)

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCreditDebtPage4Binding.inflate(inflater)

        //Setting text to the text views
        initViews()

        creditDebtViewModel.fetchMetadata(getUserId())
        binding.apply {
            if (credit?.isNotEmpty() == true) {
                val creditName = resources.getString(R.string.creditor_name)
                val creditNumber = resources.getString(R.string.creditor_number)
                tvName.text = creditName
                tvNumber.text = creditNumber
                type = Constants.CREDIT

            } else type = Constants.DEBT

            tvBack.setOnClickListener { toPreviousPage() }
            tvNext.setOnClickListener { toUploadCreditDebtData() }
        }
        loadCdLiveData()
        liveDataAddProduct()
        addMetadataLiveData()
        fetchMetadataLiveData()
        addCattleBoughtLiveData()







        return _binding.root
    }

    private fun addCattleBoughtLiveData() {
        creditDebtViewModel._addCattleBoughtLiveData.observe(viewLifecycleOwner){
            when (it.status) {
                Status.LOADING -> {
                    // to check on
                }
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "data has been added", Toast.LENGTH_SHORT)
                        .show()
                }
                Status.ERROR -> {
                    Toast.makeText(
                        requireActivity(),
                        it.error,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

        }
    }

    private fun fetchMetadataLiveData() {
        creditDebtViewModel._fetchMetadataLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    //Tocheck on
                }
                Status.SUCCESS -> {
                    metadata = it.data!!
                    addMetadata()

                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addMetadataLiveData() {
        creditDebtViewModel._addMetadataLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    // to check on
                }
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "data has been added", Toast.LENGTH_SHORT)
                        .show()
                }
                Status.ERROR -> {
                    Toast.makeText(
                        requireActivity(),
                        it.error,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun liveDataAddProduct() {
        productViewModel._liveDataAddProduct.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Toast.makeText(
                        requireActivity(),
                        "Uploading product successful",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    fetchMetadata()
                }
                Status.ERROR -> {
                    Toast.makeText(
                        requireActivity(),
                        it.error,
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
    }

    private fun loadCdLiveData() {
        creditDebtViewModel._loadCDLiveData.observe(viewLifecycleOwner, {
            Log.d(TAG, "toSubmitData: Called viewmodel")
            when (it.status) {
                Status.LOADING -> {
                    Toast.makeText(requireActivity(), "Uploading...", Toast.LENGTH_SHORT).show()

                }
                Status.SUCCESS -> {
                    toUpdateProductData()
                    toAddCattleBought()
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

    }

    private fun toAddCattleBought() {
        if (cattleBoughtList.size > 0) {
            for (cattleBought: CattleBought in cattleBoughtList) {
                val updatedCattleBought = CattleBought(
                    creditDebtId,
                    creditDebtViewModel.getCattleBoughtId(creditDebtId),
                    cattleBought.cattleBoughtType,
                    cattleBought.cattlePrice,
                    cattleBought.cattleQty,
                    cattleBought.cattleAmt
                )
                creditDebtViewModel.addCattleBought(creditDebtId,updatedCattleBought)
            }



        }

    }

    private fun toUpdateProductData() {
        productViewModel.addProducts(products)
    }

    private fun fetchMetadata() {
        creditDebtViewModel.fetchMetadata(getUserId())
    }

    private fun addMetadata() {
        if (type == Constants.DEBT) {
            val totalMoneyReceivedPaid = totalPaid.toInt().plus(metadata.totalMoneyReceivedPaid)
            val totalMoneyReceivedAmt = totalAmount.toInt().plus(metadata.totalMoneyReceivedAmt)
            val totalMoneyReceivedBalance =
                totalBalance.toInt().plus(metadata.totalMoneyReceivedBalance)
            val totalMoneySentPaid = 0 + metadata.totalMoneySentPaid
            val totalMoneySentAmt = 0 + metadata.totalMoneySentAmt
            val totalMoneySentBalance = 0 + metadata.totalMoneySentBalance
            val metadata = Metadata(
                totalMoneySentPaid, totalMoneySentAmt, totalMoneySentBalance,
                totalMoneyReceivedPaid, totalMoneyReceivedAmt, totalMoneyReceivedBalance
            )
            creditDebtViewModel.addMetadata(metadata, getUserId())

        } else if (type == Constants.CREDIT) {

            val totalMoneyReceivedPaid = 0 + metadata.totalMoneyReceivedPaid
            val totalMoneyReceivedAmt = 0 + metadata.totalMoneyReceivedAmt
            val totalMoneyReceivedBalance = 0 + metadata.totalMoneyReceivedBalance
            val totalMoneySentPaid = totalPaid.toInt().plus(metadata.totalMoneySentPaid)
            val totalMoneySentAmt = totalAmount.toInt().plus(metadata.totalMoneySentAmt)
            val totalMoneySentBalance = totalBalance.toInt().plus(metadata.totalMoneySentBalance)
            val metadata = Metadata(
                totalMoneySentPaid, totalMoneySentAmt, totalMoneySentBalance,
                totalMoneyReceivedPaid, totalMoneyReceivedAmt, totalMoneyReceivedBalance
            )
            creditDebtViewModel.addMetadata(metadata, getUserId())
        }


    }


    // Upload data to firebase
    private fun toUploadCreditDebtData() {
        Log.d(TAG, "toSubmitData: data")
        val dateCreated = DateCreated.getDateCreated()
        creditDebtId = creditDebtViewModel.getCreditDebtId()
        val userId = getUserId()
        creditDebt =
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

    private fun getUserId(): String {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
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