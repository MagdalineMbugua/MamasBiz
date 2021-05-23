package com.magda.mamasbiz.main.userInterface.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCustomerBinding
import com.magda.mamasbiz.databinding.FragmentSupplierBinding
import com.magda.mamasbiz.main.businessLogic.adapter.CreditDebtAdapter
import com.magda.mamasbiz.main.businessLogic.adapter.ItemClickListener
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.businessLogic.viewModels.ProductViewModel
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.userInterface.activities.CreditDebtActivity
import com.magda.mamasbiz.main.userInterface.activities.DetailsActivity
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.SessionManager
import com.magda.mamasbiz.main.utils.Status
import kotlinx.android.synthetic.main.customer_cardview.view.*
import java.util.*


class SupplierFragment : Fragment(), ItemClickListener {
    private lateinit var binding: FragmentSupplierBinding
    private val _binding get() = binding!!
    private var creditList : MutableList<CreditDebt> = mutableListOf()
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var creditDebtAdapter : CreditDebtAdapter
    private val TAG = "Supplier Fragment"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //instantiate view model
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSupplierBinding.inflate(inflater, container, false)

        creditDebtAdapter= CreditDebtAdapter(requireContext(), this)


        //instantiate the credit list form the view model
        fetchDebtData()



        //Observe the data fetched
        creditDebtViewModel._fetchCDLiveData.observe(viewLifecycleOwner,{


            when (it.status) {
                Status.LOADING -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.progressbar.visibility = View.GONE
                    val creditDebtList = it.data!!

                    Log.d(TAG, "onCreateView: ${creditDebtList.size}")
                    creditList = creditDebtList.filter {credit -> credit.type == Constants.CREDIT}.toMutableList()
                    setViews()
                    creditDebtAdapter.addList(creditList)
                    Log.d(TAG, "onCreateView: ${creditList.size}")
                    initViews()

                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity(), it.error, Toast.LENGTH_SHORT).show()
                    binding.progressbar.visibility = View.GONE
                }
            }
        })





        searchData()





        binding.tvCreateCredit.setOnClickListener {
            val intent = Intent(requireActivity(), CreditDebtActivity::class.java)
            intent.putExtra(Constants.CREDIT,"Credit")
            startActivity(intent) }




        return _binding.root
    }

    private fun searchData() {
        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //method sub
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //method sub
            }

            override fun afterTextChanged(s: Editable?) {
                if( !s.isNullOrEmpty()) {
                    val string = s.toString().toLowerCase(Locale.ROOT)
                    filterUsingTheText(string)

                }else {binding.consumerRecyclerView.visibility = View.VISIBLE
                    creditDebtAdapter.addList(creditList)
                    binding.ivUserNotFound.visibility = View.GONE
                    binding.tvUserNotFound.visibility = View.GONE}
            }

        })
    }

    private fun filterUsingTheText(string: String) {
        val filteredList = creditList.filter {
            it.name?.contains(string)!!  || it.status?.contains(string)!!
        }.toMutableList()
        if(filteredList.size>0){
            creditDebtAdapter.filteredList(filteredList)

        } else {
        binding.ivUserNotFound.visibility = View.VISIBLE
        binding.tvUserNotFound.visibility = View.VISIBLE
        binding.consumerRecyclerView.visibility = View.GONE}


    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.consumerRecyclerView.apply {
            setLayoutManager(layoutManager)
            setHasFixedSize(true)
            adapter = creditDebtAdapter
        }

    }

    private fun setViews() {
        Log.d(TAG, "setViews: passed the set view method")
        if(creditList.size>0){
            binding.apply {
                Log.d(TAG, "setViews: creditList is greater than 0")
                consumerRecyclerView.visibility = View.VISIBLE
                etSearch.visibility = View.VISIBLE
                tvCreateCredit.visibility = View.GONE
                tvRecords.visibility = View.GONE
                ivSplashImage.visibility = View.GONE
            }
        } else{
            binding.apply {
                Log.d(TAG, "setViews: creditList is less than 0")
                consumerRecyclerView.visibility = View.GONE
                etSearch.visibility = View.GONE
                tvCreateCredit.visibility = View.VISIBLE
                tvRecords.visibility = View.VISIBLE
                ivSplashImage.visibility = View.VISIBLE
            }
        }
    }
    //Fetch data from Firestore using the view model
    private fun fetchDebtData() {

        val userId = SessionManager(requireContext()).getUserId()
        creditDebtViewModel.getCreditDebt(userId!!)

    }


    override fun itemClicked(creditDebt: CreditDebt, view: View) {
        if(view.id == R.id.consumerCardView){
            toDetailsActivity(creditDebt)
        }

    }

    private fun toDetailsActivity(creditDebt: CreditDebt) {
        val intent =Intent(requireActivity(), DetailsActivity::class.java)
        intent.putExtra(Constants.CREDIT_DEBT, creditDebt)
        intent.putExtra(Constants.CREDIT, "Credit")
        startActivityForResult(intent,2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==2000){
            if(resultCode== Activity.RESULT_OK){
                creditList.clear()
                fetchDebtData()

            }

        }else Toast.makeText(requireContext(), "Error occurred sending data to this screen", Toast.LENGTH_SHORT).show()

    }




}