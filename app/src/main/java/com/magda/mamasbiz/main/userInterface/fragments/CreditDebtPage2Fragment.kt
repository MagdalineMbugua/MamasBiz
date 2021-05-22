package com.magda.mamasbiz.main.userInterface.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPage2Binding
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.businessLogic.viewModels.ProductViewModel
import com.magda.mamasbiz.main.data.entity.CattleBought
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Metadata
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Status
import kotlinx.android.synthetic.main.fragment_credit_debt_page2.*
import java.util.ArrayList

class CreditDebtPage2Fragment : Fragment() {
    private lateinit var binding: FragmentCreditDebtPage2Binding
    private val TAG = "CreditDebtPage2Fragment"
    private lateinit var meatPrice: String
    private lateinit var meatQty: String
    private lateinit var intestinePrice: String
    private lateinit var intestineQty: String
    private lateinit var headAndLegsPrice: String
    private lateinit var headAndLegsQty: String
    private lateinit var africanSausagePrice: String
    private lateinit var africanSausageQty: String
    private lateinit var liverPrice: String
    private lateinit var liverQty: String
    private lateinit var filletPrice: String
    private lateinit var filletQty: String
    private lateinit var skinPrice: String
    private lateinit var skinQty: String
    private lateinit var name: String
    private var phoneNumber: String? = ""
    private var creditDebt: CreditDebt? = null
    private var credit: String? = ""
    private var debt: String? = ""
    private var cattleBoughtList: ArrayList<CattleBought>? = null
    private lateinit var status: String
    private  var totalCattleBoughtAmount: String? = null
    private  var totalCattleBoughtQty: String? = null
    private  var totalCattleBoughtPaid: String?= null
    private val _binding get() = binding!!
    private lateinit var productViewModel: ProductViewModel
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private var metadata: Metadata? = null


    companion object {
        //This new instance allows the fragment activity to share the extras from the Details activity
        fun newInstance(creditDebt: CreditDebt) = CreditDebtPage2Fragment().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.CREDIT_DEBT, creditDebt)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Getting arguments from the previous fragment: CreditPage1fragment or CreditDebtPageFragment
        getExtraArguments()


    }

    private fun getExtraArguments() {
        requireArguments().getString(Constants.DEBTOR_NAME)?.let { debtorName ->
            name = debtorName
        }
        requireArguments().getString(Constants.DEBTOR_STATUS)?.let { status ->
            this.status = status
        }
        requireArguments().getString(Constants.DEBTOR_NUMBER)?.let { phoneNumber ->
            this.phoneNumber = phoneNumber
        }

        requireArguments().getString(Constants.DEBT)?.let { debt ->
            this.debt = debt
        }
        requireArguments().getString(Constants.CREDIT)?.let { credit ->
            this.credit = credit
        }
        requireArguments().getParcelableArrayList<CattleBought>(Constants.CATTLE_BOUGHT_LIST)
            ?.let { cattleBoughtList ->
                this.cattleBoughtList = cattleBoughtList
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
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditDebtPage2Binding.inflate(inflater, container, false)

        //Observe the update product in view model: this will include instantiating of the view model
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)

        addProductLiveData()
        fetchMetadataLiveData()
        addMetadataLiveData()
        updateCreditDebtLiveData()


// Calculate the total amount of products bought using the text listener
        toCalculate()

        //Get the extra from the fragment activity (edit product activity)
        getCreditDebtArguments()


// Set on click listener
        binding.apply {
            nextLayout.tvNext.setOnClickListener {
                // Check the EditText if filled
                checkIfFilled()
            }
            nextLayout.tvBack.setOnClickListener { previousPage() }
        }
        return _binding.root
    }

    private fun updateCreditDebtLiveData() {
        creditDebtViewModel._loadCDLiveData.observe(viewLifecycleOwner){
            when (it.status) {
                Status.LOADING -> {
                    //Method sub
                }
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "Successfully added", Toast.LENGTH_SHORT).show()

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
                    //Method sub
                }
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "Successfully added", Toast.LENGTH_SHORT).show()

                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchMetadataLiveData() {
        creditDebtViewModel._fetchMetadataLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    //Method sub
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

    private fun getCreditDebtArguments() {
        creditDebt = requireArguments().getParcelable(Constants.CREDIT_DEBT)
        if (creditDebt != null) {
            creditDebtViewModel.fetchMetadata(creditDebt!!.userId!!)
            binding.apply {
                nextLayout.tvNext.visibility = View.GONE
                nextLayout.tvBack.visibility = View.GONE
                btUpdate.visibility = View.VISIBLE
                etAmountPaid.setText(creditDebt!!.totalAllPaid)
                btUpdate.setOnClickListener { checkIfFilled() }
            }
        }
    }

    private fun addProductLiveData() {
        productViewModel._liveDataAddProduct.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()

                }
                Status.SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "product update was successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().finish()

                }
                Status.LOADING -> {
                    //Will figure this out
                }
            }

        }
    }

    private fun checkIfFilled() {
        if(creditDebt!=null)  deleteMetadata()
        getText()
        if (meatPrice.isNotEmpty() || meatQty.isNotEmpty() || intestinePrice.isNotEmpty() || intestineQty.isNotEmpty() ||
            africanSausagePrice.isNotEmpty() || africanSausageQty.isNotEmpty() || headAndLegsPrice.isNotEmpty() ||
            headAndLegsPrice.isNotEmpty() || liverPrice.isNotEmpty() || liverQty.isNotEmpty() || skinPrice.isNotEmpty() ||
            skinQty.isNotEmpty() || filletPrice.isNotEmpty() || filletQty.isNotEmpty()
        ) {
            val total = binding.tvSum.text.toString()
            if (total.isNotEmpty()) {
                val amountPaid = binding.etAmountPaid.text.toString().trim()
                if (amountPaid.isNotEmpty()) {
                    val balance = total.toInt().minus(amountPaid.toInt()).toString()
                    //To either update the product on firebase or proceed to the next page
                    if (creditDebt != null) {
                        addMetadata(amountPaid, balance,total)
                        updateCreditDebt(amountPaid,balance,total)
                        val product = getProduct()
                        Log.d(TAG, "checkIfFilled: $product")
                        productViewModel.addProducts(product)


                    } else toNextPage(amountPaid, balance)

                } else Toast.makeText(
                    requireContext(),
                    "Fill in the paid amount",
                    Toast.LENGTH_SHORT
                ).show()


            } else {
                Toast.makeText(
                    requireContext(),
                    "Kindly fill the table",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else Toast.makeText(requireContext(), "Kindly fill the table", Toast.LENGTH_SHORT)
            .show()
    }

    private fun deleteMetadata() {
        val productPaid = if(creditDebt?.productPaid!!.isNotEmpty()){
            creditDebt?.productPaid!!
        } else "0"
        val productBalance = if(creditDebt?.productBalance!!.isNotEmpty()){
            creditDebt?.productBalance!!
        } else "0"
        val productAmt = if(creditDebt?.productAmount!!.isNotEmpty()){
            creditDebt?.productAmount!!
        } else "0"
        if (creditDebt!!.type == "Credit") {

            val metadata = Metadata(
                metadata?.totalMoneySentPaid!!.minus(productPaid.toInt()),
                metadata?.totalMoneySentAmt!!.minus(productAmt.toInt()),
                metadata?.totalMoneySentBalance!!.minus(productBalance.toInt()),
                metadata?.totalMoneyReceivedPaid!!,
                metadata?.totalMoneyReceivedAmt!!,
                metadata?.totalMoneyReceivedBalance!!
            )
            creditDebtViewModel.addMetadata(metadata, creditDebt!!.userId!!)
        } else if (creditDebt!!.type == "Debt") {
            val metadata = Metadata(
                metadata?.totalMoneySentPaid!!,
                metadata?.totalMoneySentAmt!!,
                metadata?.totalMoneySentBalance!!,
                metadata?.totalMoneyReceivedPaid!!.minus(productPaid.toInt()),
                metadata?.totalMoneyReceivedAmt!!.minus(productAmt.toInt()),
                metadata?.totalMoneyReceivedBalance!!.minus(productBalance.toInt())
            )
            creditDebtViewModel.addMetadata(metadata, creditDebt!!.userId!!)
        }

    }

    private fun addMetadata(paid: String, balance: String, total: String) {
        if (creditDebt!!.type == "Credit") {
            val metadata = Metadata(
                metadata?.totalMoneySentPaid!!.plus(paid.toInt()),
                metadata?.totalMoneySentAmt!!.plus(total.toInt()),
                metadata?.totalMoneySentBalance!!.plus(balance.toInt()),
                metadata?.totalMoneyReceivedPaid!!,
                metadata?.totalMoneyReceivedAmt!!,
                metadata?.totalMoneyReceivedBalance!!
            )
            creditDebtViewModel.addMetadata(metadata, creditDebt!!.userId!!)
        } else if (creditDebt!!.type == "Debt") {
            val metadata = Metadata(
                metadata?.totalMoneySentPaid!!,
                metadata?.totalMoneySentAmt!!,
                metadata?.totalMoneySentBalance!!,
                metadata?.totalMoneyReceivedPaid!!.plus(paid.toInt()),
                metadata?.totalMoneyReceivedAmt!!.plus(total.toInt()),
                metadata?.totalMoneyReceivedBalance!!.plus(balance.toInt())
            )
            creditDebtViewModel.addMetadata(metadata, creditDebt!!.userId!!)
        }

    }

    private fun getText() {
        meatPrice = etMeatPrice.text.toString()
        meatQty = etMeatQty.text.toString()
        intestinePrice = etIntestinesPrice.text.toString()
        intestineQty = etIntestinesQty.text.toString()
        africanSausageQty = etAfricanSausageQty.text.toString()
        africanSausagePrice = etAfricanSausagePrice.text.toString()
        liverPrice = etLiverPrice.text.toString()
        liverQty = etLiverQty.text.toString()
        filletPrice = etFilletPrice.text.toString()
        filletQty = etFilletQty.text.toString()
        skinQty = etSkinQty.text.toString()
        skinPrice = etSkinPrice.text.toString()
        headAndLegsQty = etHeadAndLegsQty.text.toString()
        headAndLegsPrice = etHeadAndLegsPrice.text.toString()

    }

    private fun toCalculate() {
        val textWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //method sub
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.apply {
                    meatPrice = if (etMeatPrice.text.toString().isNotEmpty()) {
                        etMeatPrice.text.toString()
                    } else "0"
                    meatQty = if (etMeatQty.text.toString().isNotEmpty()) {
                        etMeatQty.text.toString()
                    } else "0"
                    tvMeatAmt.text = meatPrice.toInt().times(meatQty.toInt()).toString()
                    intestinePrice = if (etIntestinesPrice.text.toString().isNotEmpty()) {
                        etIntestinesPrice.text.toString()
                    } else "0"
                    intestineQty = if (etIntestinesQty.text.toString().isNotEmpty()) {
                        etIntestinesQty.text.toString()
                    } else "0"
                    tvIntestinesAmt.text =
                        intestinePrice.toInt().times(intestineQty.toInt()).toString()
                    headAndLegsPrice =
                        if (etHeadAndLegsPrice.text.toString().isNotEmpty()) {
                            etHeadAndLegsPrice.text.toString()
                        } else "0"
                    headAndLegsQty = if (etHeadAndLegsQty.text.toString().isNotEmpty()) {
                        etHeadAndLegsQty.text.toString()
                    } else "0"
                    tvHeadAndLegsAmt.text =
                        headAndLegsPrice.toInt().times(headAndLegsQty.toInt()).toString()
                    africanSausagePrice =
                        if (etAfricanSausagePrice.text.toString().isNotEmpty()) {
                            etAfricanSausagePrice.text.toString()
                        } else "0"
                    africanSausageQty =
                        if (etAfricanSausageQty.text.toString().isNotEmpty()) {
                            etAfricanSausageQty.text.toString()
                        } else "0"
                    tvAfricanSausageAmt.text =
                        africanSausagePrice.toInt().times(africanSausageQty.toInt())
                            .toString()
                    liverPrice = if (etLiverPrice.text.toString().isNotEmpty()) {
                        etLiverPrice.text.toString()
                    } else "0"
                    liverQty = if (etLiverQty.text.toString().isNotEmpty()) {
                        etLiverQty.text.toString()
                    } else "0"
                    tvLiverAmt.text = liverPrice.toInt().times(liverQty.toInt()).toString()
                    filletPrice = if (etFilletPrice.text.toString().isNotEmpty()) {
                        etFilletPrice.text.toString()
                    } else "0"
                    filletQty = if (etFilletQty.text.toString().isNotEmpty()) {
                        etFilletQty.text.toString()
                    } else "0"
                    tvFilletAmt.text =
                        filletPrice.toInt().times(filletQty.toInt()).toString()
                    skinPrice = if (etSkinPrice.text.toString().isNotEmpty()) {
                        etSkinPrice.text.toString()
                    } else "0"
                    skinQty = if (etSkinQty.text.toString().isNotEmpty()) {
                        etSkinQty.text.toString()
                    } else "0"
                    tvSkinAmt.text = skinPrice.toInt().times(skinQty.toInt()).toString()

                    tvSum.text = (tvAfricanSausageAmt.text.toString().toInt()
                        .plus(tvFilletAmt.text.toString().toInt())
                        .plus(tvHeadAndLegsAmt.text.toString().toInt())
                        .plus(tvLiverAmt.text.toString().toInt())
                        .plus(tvSkinAmt.text.toString().toInt())
                        .plus(tvIntestinesAmt.text.toString().toInt())
                        .plus(tvMeatAmt.text.toString().toInt()).toString())


                }
            }

            override fun afterTextChanged(s: Editable?) {
                //method sub
            }

        }
        binding.apply {
            etMeatPrice.addTextChangedListener(textWatcher)
            etMeatQty.addTextChangedListener(textWatcher)
            etIntestinesQty.addTextChangedListener(textWatcher)
            etIntestinesPrice.addTextChangedListener(textWatcher)
            etAfricanSausagePrice.addTextChangedListener(textWatcher)
            etAfricanSausageQty.addTextChangedListener(textWatcher)
            etHeadAndLegsPrice.addTextChangedListener(textWatcher)
            etHeadAndLegsQty.addTextChangedListener(textWatcher)
            etLiverPrice.addTextChangedListener(textWatcher)
            etLiverQty.addTextChangedListener(textWatcher)
            etSkinPrice.addTextChangedListener(textWatcher)
            etSkinQty.addTextChangedListener(textWatcher)
            etFilletPrice.addTextChangedListener(textWatcher)
            etFilletQty.addTextChangedListener(textWatcher)
        }
    }


    private fun toNextPage(amountPaid: String, balance: String) {

        val navController = Navigation.findNavController(binding.root)
        val productsBought = getProduct()
        val arg = Bundle()
        arg.putParcelable(Constants.PRODUCTS_BOUGHT, productsBought)
        arg.putString(Constants.DEBTOR_NAME, name)
        arg.putString(Constants.DEBTOR_NUMBER, phoneNumber)
        arg.putString(Constants.DEBTOR_STATUS, status)
        arg.putString(Constants.BALANCE, balance)
        arg.putString(Constants.TOTAL_PAID, amountPaid)
        arg.putString(Constants.TOTAL_AMOUNT, tvSum.text.toString())
        if (credit != null) {
            arg.putString(Constants.CREDIT, credit)
        } else if (debt != null) {
            arg.putString(Constants.DEBT, debt)
        }
        Log.d(TAG, "toNextPage: ${cattleBoughtList?.size}")
        if (cattleBoughtList != null) {
            arg.putParcelableArrayList(Constants.CATTLE_BOUGHT_LIST, cattleBoughtList)
            Log.d(TAG, "toNextPage: ${cattleBoughtList?.size}")
            arg.putString(Constants.TOTAL_CATTLE_BOUGHT_AMOUNT, totalCattleBoughtAmount)
            arg.putString(Constants.TOTAL_CATTLE_BOUGHT_PAID, totalCattleBoughtPaid)
            arg.putString(Constants.TOTAL_CATTLE_BOUGHT_QTY, totalCattleBoughtQty)
        }
        navController.navigate(R.id.action_creditPage2Fragment_to_creditPage3Fragment, arg)
    }
    private fun updateCreditDebt(amountPaid: String, balance: String, total: String) {
        val productPaid = if(creditDebt?.productPaid!!.isNotEmpty()){
            creditDebt?.productPaid!!
        } else "0"
        val productBalance = if(creditDebt?.productBalance!!.isNotEmpty()){
            creditDebt?.productBalance!!
        } else "0"
        val productAmt = if(creditDebt?.productAmount!!.isNotEmpty()){
            creditDebt?.productAmount!!
        } else "0"
        val updatedTotalAmount =
            (creditDebt?.totalAllAmount?.toInt()!!.minus(productAmt.toInt())).plus(total.toInt())
        val updatedTotalPaid =
            (creditDebt?.totalAllPaid?.toInt()!!.minus(productPaid.toInt())).plus(amountPaid.toInt())
        val updatedTotalBalance =
            creditDebt?.totalAllBalance?.toInt()!!.minus(productBalance.toInt()).plus(balance.toInt())
        val creditDebt = CreditDebt(
            creditDebt?.creditDebtId,
            creditDebt?.userId,
            creditDebt?.type,
            creditDebt?.name,
            creditDebt?.phoneNumber,
            creditDebt?.status,
            creditDebt?.paymentDate,
            creditDebt?.dateCreated,
            updatedTotalAmount.toString(),
            updatedTotalPaid.toString(),
            updatedTotalBalance.toString(),
            creditDebt?.productId,
            creditDebt?.cattleBoughtId,
            amountPaid,
            balance,
            total,
            creditDebt?.cattleBoughtPaid,
            creditDebt?.cattleBoughtBalance,
            creditDebt?.cattleBoughtAmount,
            totalCattleBoughtQty,
            creditDebt?.updatedId
        )
        Log.d(TAG, "updateCreditDebt: $amountPaid, $balance, $total")
        creditDebtViewModel.addCreditDebt(creditDebt)
    }

    private fun getProduct(): Products {
        val productId = if (creditDebt?.productId!= null) {
            creditDebt?.productId
        } else productViewModel.getProductId()
        return Products(
            productId,
            meatPrice,
            meatQty,
            tvMeatAmt.text.toString(),
            intestinePrice,
            intestineQty,
            tvIntestinesAmt.text.toString(),
            africanSausagePrice,
            africanSausageQty,
            tvAfricanSausageAmt.text.toString(),
            headAndLegsPrice,
            headAndLegsQty,
            tvHeadAndLegsAmt.text.toString(),
            liverPrice,
            liverQty,
            tvLiverAmt.text.toString(),
            filletPrice,
            filletQty,
            tvFilletAmt.text.toString(),
            skinPrice,
            skinQty,
            tvSkinAmt.text.toString()
        )


    }


    private fun previousPage() {
        val navController: NavController = Navigation.findNavController(binding.root)
        navController.navigate(R.id.action_creditPage2Fragment_to_creditPage1Fragment)

    }


}




