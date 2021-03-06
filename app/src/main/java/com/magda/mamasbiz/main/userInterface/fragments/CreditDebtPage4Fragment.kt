package com.magda.mamasbiz.main.userInterface.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPage4Binding
import com.magda.mamasbiz.main.businessLogic.adapter.CattleBoughtAdapter
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
import java.util.*


class CreditPage4Fragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPage4Binding
    private val _binding get() = binding!!
    private lateinit var paymentDate: String
    private var products: Products? = null
    private lateinit var cattleBoughtAdapter: CattleBoughtAdapter
    private lateinit var debtorName: String
    private lateinit var phoneNumber: String
    private lateinit var status: String
    private var totalAmount: String? = ""
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private lateinit var productViewModel: ProductViewModel
    private var cattleBoughtList: ArrayList<CattleBought>? = null
    private lateinit var type: String
    private var credit: String? = ""
    private var debt: String? = ""
    private var totalPaid: String? = ""
    private var totalBalance: String? = ""
    private lateinit var metadata: Metadata
    private lateinit var creditDebt: CreditDebt
    private lateinit var creditDebtId: String
    private var cattleBoughtId: String? = ""
    private var totalCattleBoughtAmount: String? = "0"
    private var totalCattleBoughtQty: String? = "0"
    private var totalCattleBoughtPaid: String? = "0"
    private var totalAllPaid: Int = 0
    private var totalAllAmount: Int = 0
    private var totalAllBalance: Int = 0


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
        requireArguments().getParcelableArrayList<CattleBought>(Constants.CATTLE_BOUGHT_LIST)
            ?.let { cattleBoughtList ->
                this.cattleBoughtList = cattleBoughtList
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


        requireArguments().getString(Constants.TOTAL_CATTLE_BOUGHT_AMOUNT)
            ?.let { totalCattleBoughtAmount ->
                this.totalCattleBoughtAmount = totalCattleBoughtAmount
            }

        requireArguments().getString(Constants.TOTAL_CATTLE_BOUGHT_PAID)
            ?.let { totalCattleBoughtPaid ->
                this.totalCattleBoughtPaid = totalCattleBoughtPaid
            }

        requireArguments().getString(Constants.TOTAL_CATTLE_BOUGHT_QTY)
            ?.let { totalCattleBoughtQty ->
                this.totalCattleBoughtQty = totalCattleBoughtQty
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCreditDebtPage4Binding.inflate(inflater)

        cattleBoughtAdapter = CattleBoughtAdapter(requireContext())


        //Setting text to the text views
        initViews()

        creditDebtViewModel.fetchMetadata(getUserId())
        binding.apply {
            if (credit?.isNotEmpty() == true) {
                val creditName = resources.getString(R.string.supplier_name)
                val creditNumber = resources.getString(R.string.supplier_number)
                tvName.text = creditName
                tvNumber.text = creditNumber
                type = Constants.CREDIT

            } else type = Constants.DEBT

            tvBack.setOnClickListener { toPreviousPage() }
            tvNext.setOnClickListener { addMetadata() }
        }
        loadCdLiveData()
        liveDataAddProduct()
        addMetadataLiveData()
        fetchMetadataLiveData()
        addCattleBoughtLiveData()







        return _binding.root
    }

    private fun addCattleBoughtLiveData() {
        creditDebtViewModel._addCattleBoughtLiveData.observe(viewLifecycleOwner) {
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
                    toUploadCreditDebtData()
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
        if (cattleBoughtList != null) {
            for (cattleBought: CattleBought in cattleBoughtList!!) {
                val updatedCattleBought = CattleBought(
                    creditDebtId = null,
                    cattleBoughtId = null,
                    cattleBought.cattleBoughtType,
                    cattleBought.cattlePrice,
                    cattleBought.cattleQty,
                    cattleBought.cattleAmt
                )
                creditDebtViewModel.addCattleBought(creditDebtId, updatedCattleBought)
            }


        }

    }

    private fun toUpdateProductData() {
        if (products != null) productViewModel.addProducts(products!!)
    }


    private fun addMetadata() {
        if (type == Constants.DEBT) {
            confirmTheAmount()
            val totalMoneyReceivedPaid = totalAllPaid.plus(metadata.totalMoneyReceivedPaid)
            val totalMoneyReceivedAmt = totalAllAmount.plus(metadata.totalMoneyReceivedAmt)
            val totalMoneyReceivedBalance =
                totalAllBalance.plus(metadata.totalMoneyReceivedBalance)
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

            confirmTheAmount()

            val totalMoneySentPaid = totalAllPaid.plus(metadata.totalMoneySentPaid)
            val totalMoneySentAmt = totalAllAmount.plus(metadata.totalMoneySentAmt)
            val totalMoneySentBalance = totalAllBalance.plus(metadata.totalMoneySentBalance)
            val metadata = Metadata(
                totalMoneySentPaid, totalMoneySentAmt, totalMoneySentBalance,
                totalMoneyReceivedPaid, totalMoneyReceivedAmt, totalMoneyReceivedBalance
            )
            creditDebtViewModel.addMetadata(metadata, getUserId())
        }


    }

    private fun confirmTheAmount() {
        when {
            (cattleBoughtList != null && products == null) -> {
                totalAllPaid = totalCattleBoughtPaid!!.toInt()
                totalAllAmount = totalCattleBoughtAmount!!.toInt()
                totalAllBalance =
                    totalCattleBoughtAmount!!.toInt().minus(totalCattleBoughtPaid!!.toInt())
            }
            (products != null && cattleBoughtList == null) -> {
                totalAllPaid = this.totalPaid!!.toInt()
                totalAllAmount = this.totalAmount!!.toInt()
                totalAllBalance = this.totalBalance!!.toInt()
            }
            ((cattleBoughtList != null) && (products != null)) -> {
                totalAllPaid = totalCattleBoughtPaid!!.toInt().plus(this.totalPaid!!.toInt())
                totalAllAmount = totalCattleBoughtAmount!!.toInt().plus(this.totalAmount!!.toInt())
                val totalPartBalance = (totalCattleBoughtAmount!!.toInt()
                    .minus(totalCattleBoughtPaid!!.toInt()))
                totalAllBalance = totalPartBalance.plus(this.totalBalance!!.toInt())
            }
        }
    }


    // Upload data to firebase
    private fun toUploadCreditDebtData() {
        val dateCreated = DateCreated.getDateCreated()
        creditDebtId = creditDebtViewModel.getCreditDebtId()
        if (cattleBoughtList != null) cattleBoughtId =
            creditDebtViewModel.getCattleBoughtId(creditDebtId)
        val userId = getUserId()
        val totalCattleBoughtBal =
            totalCattleBoughtAmount!!.toInt().minus(totalCattleBoughtPaid!!.toInt()).toString()
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
                totalAllAmount.toString(),
                totalAllPaid.toString(),
                totalAllBalance.toString(),
                products?.productId,
                totalAmount,
                totalPaid,
                totalBalance,
                totalCattleBoughtPaid,
                totalCattleBoughtBal,
                totalCattleBoughtAmount,
                totalCattleBoughtQty

            )
        creditDebtViewModel.addCreditDebt(creditDebt)
        }

    private fun getUserId(): String {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        return userId!!


    }

    //Navigating back on fragment
    private fun toPreviousPage() {
        val navController: NavController = Navigation.findNavController(binding.root)
        val arg = Bundle()
        if (products != null) arg.putParcelable(Constants.PRODUCTS_BOUGHT, products)
        arg.putString(Constants.DEBTOR_NAME, debtorName)
        arg.putString(Constants.DEBTOR_NUMBER, phoneNumber)
        arg.putString(Constants.DEBTOR_STATUS, status)
        arg.putString(Constants.BALANCE, totalBalance)
        arg.putString(Constants.TOTAL_PAID, totalPaid)
        arg.putString(Constants.TOTAL_AMOUNT, totalAmount)
        if (credit != null) {
            arg.putString(Constants.CREDIT, credit)
        } else if (debt != null) {
            arg.putString(Constants.DEBT, debt)
        }
        if (cattleBoughtList != null) {
            arg.putParcelableArrayList(Constants.CATTLE_BOUGHT_LIST, cattleBoughtList)
            arg.putString(Constants.TOTAL_CATTLE_BOUGHT_AMOUNT, totalCattleBoughtAmount)
            arg.putString(Constants.TOTAL_CATTLE_BOUGHT_PAID, totalCattleBoughtPaid)
            arg.putString(Constants.TOTAL_CATTLE_BOUGHT_QTY, totalCattleBoughtQty)
        }
        navController.navigate(R.id.action_creditPage4Fragment_to_creditPage3Fragment, arg)
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.apply {
            val totalAmount = if (products != null && cattleBoughtList == null) {
                totalAmount?.toInt()
            } else if (cattleBoughtList != null && products == null) {
                totalCattleBoughtAmount?.toInt()
            } else totalAmount!!.toInt().plus(totalCattleBoughtAmount!!.toInt())

            val totalPaid = if (products != null && cattleBoughtList == null) {
                totalPaid?.toInt()
            } else if (cattleBoughtList != null && products == null) {
                totalCattleBoughtPaid?.toInt()
            } else totalPaid?.toInt()?.plus(totalCattleBoughtPaid!!.toInt())

            val totalBal = totalAmount?.minus(totalPaid!!)
            tvDebtorName.text = debtorName
            tvCustomerNumber.text = phoneNumber
            tvDebtorStatus.text = status
            tvDebtorPaymentDate.text = paymentDate
            tvCustomerTotalPaid.text = "Kes: $totalPaid"
            tvDebtorTotalBalance.text = "Kes: $totalBal"
            tvDebtorTotalAllAMount.text = "Kes: $totalAmount"

            checkViews()
        }
    }

    private fun checkViews() {
        when {
            (cattleBoughtList != null && products == null) -> {

                setRecyclerViewAndViews()
            }
            (products != null && cattleBoughtList == null) -> {
                setProductViews()
            }
            ((cattleBoughtList != null) && (products != null)) -> {
                setProductViews()
                setRecyclerViewAndViews()

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setProductViews() {
        binding.apply {
            purchasedProductTable.visibility = View.VISIBLE
            tvProductAmount.visibility = View.VISIBLE
            tvTotalExactProductAmount.visibility = View.VISIBLE
            tvTotalExactProductAmount.text = "KES: $totalAmount"
            toFillTable()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setRecyclerViewAndViews() {
        binding.apply {
            cattleBoughtTitle.visibility = View.VISIBLE
            tvTotalCattleAmount.visibility = View.VISIBLE
            tvTotalCattleExactAmount.visibility = View.VISIBLE
            tvTotalCattleExactAmount.text = "Kes: $totalCattleBoughtAmount"
            tvTotalCattleQty.visibility = View.VISIBLE
            tvTotalExactCattleQty.visibility = View.VISIBLE
            tvTotalExactCattleQty.text = totalCattleBoughtQty
            cattleBoughtRecyclerView.visibility = View.VISIBLE
            setUpRecyclerView()

        }

    }

    private fun setUpRecyclerView() {
        cattleBoughtAdapter.addList(cattleBoughtList!!)
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = RecyclerView.VERTICAL
        binding.cattleBoughtRecyclerView.apply {
            layoutManager = manager
            adapter = cattleBoughtAdapter
            hasFixedSize()
        }

    }

    //Setting the table to only show filled data
    private fun toFillTable() {
        binding.apply {
            if (products?.meatAmt != "0") {
                tvMeatPrice.text = products?.meatPrice
                tvMeatQty.text = products?.meatQty
                tvMeatAmt.text = products?.meatAmt
            } else meatRow.visibility = View.GONE
            if (products?.intestineAmt != "0") {
                tvIntestinesPrice.text = products?.intestinePrice
                tvIntestinesQty.text = products?.intestineQty
                tvIntestinesAmt.text = products?.intestineAmt
            } else intestinesRow.visibility = View.GONE
            if (products?.africanSausageAmt != "0") {
                tvAfricanSausageQty.text = products?.africanSausageQty
                tvAfricanSausagePrice.text = products?.africanSausagePrice
                tvAfricanSausageAmt.text = products?.africanSausageAmt
            } else africanSausageRow.visibility = View.GONE
            if (products?.headAndLegsAmt != "0") {
                tvHeadAndLegsPrice.text = products?.headAndLegsPrice
                tvHeadAndLegsQty.text = products?.headAndLegsQty
                tvHeadAndLegsAmt.text = products?.headAndLegsAmt
            } else headAndToeRow.visibility = View.GONE
            if (products?.liverAmt != "0") {
                tvLiverQty.text = products?.liverQty
                tvLiverPrice.text = products?.liverPrice
                tvLiverAmt.text = products?.liverAmt
            } else liverRow.visibility = View.GONE
            if (products?.skinAmt != "0") {
                tvSkinQty.text = products?.skinQty
                tvSkinPrice.text = products?.skinPrice
                tvSkinAmt.text = products?.skinAmt
            } else skinRow.visibility = View.GONE
            if (products?.filletAmt != "0") {
                tvFilletQty.text = products?.filletQty
                tvFilletPrice.text = products?.filletPrice
                tvFilletAmt.text = products?.filletAmt
            } else filletRow.visibility = View.GONE
        }
    }


}