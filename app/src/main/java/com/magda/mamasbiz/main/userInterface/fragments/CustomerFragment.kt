package com.magda.mamasbiz.main.userInterface.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCustomerBinding
import com.magda.mamasbiz.main.businessLogic.adapter.CreditDebtAdapter
import com.magda.mamasbiz.main.businessLogic.adapter.ItemClickListener
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.userInterface.activities.CreditDebtActivity
import com.magda.mamasbiz.main.userInterface.activities.DetailsActivity
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.SessionManager
import com.magda.mamasbiz.main.utils.Status
import com.magda.mamasbiz.main.utils.Status.LOADING
import kotlinx.android.synthetic.main.customer_cardview.view.*
import java.util.*


class CustomerFragment : Fragment(), ItemClickListener {
    private lateinit var binding: FragmentCustomerBinding
    private val _binding get() = binding!!
    private var debtList : MutableList<CreditDebt> = mutableListOf()
    private lateinit var creditDebtViewModel:CreditDebtViewModel
    private lateinit var creditDebtAdapter: CreditDebtAdapter
    private val TAG = "Customer Fragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //instantiate view model
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCustomerBinding.inflate(inflater, container, false)

        //instantiate the debt list form the view model
        fetchDebtData()



        //Observe the data fetched
        creditDebtViewModel._fetchCDLiveData.observe(viewLifecycleOwner,{


            when (it.status) {
                LOADING -> {
                    binding.progressbar.visibility = VISIBLE
                }
                Status.SUCCESS -> {
                    binding.progressbar.visibility = GONE
                    val creditDebtList = it.data!!

                    Log.d(TAG, "onCreateView: ${creditDebtList.size}")
                    getDebtList(creditDebtList)
                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity(), it.error, Toast.LENGTH_SHORT).show()
                    binding.progressbar.visibility = GONE
                }
            }
        })


        searchData()





        binding.tvCreateDebt.setOnClickListener {
        val intent = Intent(requireActivity(), CreditDebtActivity::class.java)
        intent.putExtra(Constants.DEBT,"Debt")
            startActivity(intent) }




        return _binding.root
    }

    private fun searchData() {
        binding.etSearch.addTextChangedListener(object:TextWatcher{
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
                    creditDebtAdapter.addList(debtList)
                    binding.ivUserNotFound.visibility = View.GONE
                    binding.tvUserNotFound.visibility = View.GONE}
            }
            }

        )
    }

    private fun filterUsingTheText(string: String) {
      val filteredList = debtList.filter {
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
        setViews()
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = VERTICAL
        binding.consumerRecyclerView.apply {
            setLayoutManager(layoutManager)
            setHasFixedSize(true)
            adapter = creditDebtAdapter
        }
    }

    private fun setViews() {
        if(debtList.size>0){
            binding.apply {
                consumerRecyclerView.visibility = View.VISIBLE
                etSearch.visibility = View.VISIBLE
                tvCreateDebt.visibility = View.GONE
                tvRecords.visibility = View.GONE
                ivSplashImage.visibility = View.GONE
            }
        } else{
            binding.apply {
                consumerRecyclerView.visibility = View.GONE
                etSearch.visibility = View.GONE
                tvCreateDebt.visibility = View.VISIBLE
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
//Get debt list from the list with both credit and debt
    private fun getDebtList(creditDebtList: MutableList<CreditDebt>) {
     debtList .addAll(creditDebtList.filter {it.type == Constants.DEBT  }
         .toMutableList())
            creditDebtAdapter = CreditDebtAdapter(requireContext(), this)
            creditDebtAdapter.addList(debtList)
    Log.d(TAG, "getDebtList: ${debtList.size}")
            initViews()


}

    override fun itemClicked(creditDebt: CreditDebt, view: View) {
        if(view.id == R.id.consumerCardView){
            toDetailsActivity(creditDebt)
        }
    }

    private fun toDetailsActivity(creditDebt: CreditDebt) {
        val intent =Intent(requireActivity(), DetailsActivity::class.java)
        intent.putExtra(Constants.CREDIT_DEBT, creditDebt)
        intent.putExtra(Constants.DEBT, "Debt")
        startActivityForResult(intent, 2000)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==2000) {
            if(resultCode == Activity.RESULT_OK){
                debtList.clear()
                fetchDebtData()
            }

            } else Toast.makeText(requireContext(), "Error occurred sending data to this screen", Toast.LENGTH_SHORT).show()



    }


}