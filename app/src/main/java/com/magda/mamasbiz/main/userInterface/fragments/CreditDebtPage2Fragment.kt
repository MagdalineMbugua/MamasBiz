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
import com.magda.mamasbiz.main.businessLogic.viewModels.ProductViewModel
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Status
import kotlinx.android.synthetic.main.fragment_credit_debt_page2.*

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
    private lateinit var total: String
    private var phoneNumber: String? = ""
    private var creditDebt: CreditDebt? = null
    private var credit: String? = ""
    private var debt: String? = ""
    private var hasCreditDebt: String? = ""
    private lateinit var status: String
    private val _binding get() = binding!!
    private lateinit var productViewModel: ProductViewModel


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
        //Getting arguments from the previous fragment: CreditPage1fragment
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditDebtPage2Binding.inflate(inflater, container, false)

        //Observe the update product in view model: this will include instantiating of the view model
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
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


// Calculate the total amount of products bought using the text listener
        toCalculate()

        //Get the extra from the fragment activity (edit product activity)
        creditDebt = requireArguments().getParcelable(Constants.CREDIT_DEBT)
        if (creditDebt != null) {
            binding.apply {
                nextLayout.tvNext.visibility = View.GONE
                nextLayout.tvBack.visibility = View.GONE
                btUpdate.visibility = View.VISIBLE
                etAmountPaid.setText(creditDebt!!.totalPaid)
                btUpdate.setOnClickListener { checkIfFilled() }
            }}

// Set on click listener
            binding.apply {
               nextLayout.tvNext.setOnClickListener {
                    // Check the EditText if filled
                    checkIfFilled()
                }
                nextLayout.tvBack.setOnClickListener { previousPage() }}
        return _binding.root
            }

            private fun checkIfFilled() {
                getText()
                if (meatPrice.isNotEmpty() || meatQty.isNotEmpty() || intestinePrice.isNotEmpty() || intestineQty.isNotEmpty() ||
                    africanSausagePrice.isNotEmpty() || africanSausageQty.isNotEmpty() || headAndLegsPrice.isNotEmpty() ||
                    headAndLegsPrice.isNotEmpty() || liverPrice.isNotEmpty() || liverQty.isNotEmpty() || skinPrice.isNotEmpty() ||
                    skinQty.isNotEmpty() || filletPrice.isNotEmpty() || filletQty.isNotEmpty()
                ) {
                    total = tvSum.text.toString()
                    if (total.isNotEmpty()) {
                        //To either update the product on firebase or proceed to the next page
                        if(creditDebt!=null){
                            val product = getProduct()
                            productViewModel.addProducts(product)
                        } else toNextPage()


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

            private fun toNextPage() {
                val amountPaid = binding.etAmountPaid.text.toString().trim()
                var balance = ""
                if (amountPaid.isNotEmpty()) {
                    val totalBought = binding.tvSum.text.toString().toInt()
                   balance = totalBought.minus(amountPaid.toInt()).toString()
                }
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
                navController.navigate(R.id.action_creditPage2Fragment_to_creditPage3Fragment, arg)
            }

            private fun getProduct(): Products {
                val productId = if (creditDebt != null) {
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




