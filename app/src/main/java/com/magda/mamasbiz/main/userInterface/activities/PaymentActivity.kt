package com.magda.mamasbiz.main.userInterface.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.magda.mamasbiz.databinding.ActivityPaymentBinding
import com.magda.mamasbiz.main.businessLogic.adapter.ActivityAdapter
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.UpdatePayments
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Status

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private lateinit var updatePaymentList: MutableList<UpdatePayments>
    private lateinit var creditDebt: CreditDebt
    private val activityAdapter = ActivityAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPaymentBinding.inflate(layoutInflater)

        creditDebt = intent.getParcelableExtra(Constants.CREDIT_DEBT)
        activityAdapter.getCreditDebt(creditDebt)

        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)
        fetchUpdatePayments()
        creditDebtViewModel._fetchUpdatePaymentsLiveData.observe(this){
            when(it.status){
                Status.LOADING -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.progressbar.visibility = View.GONE
                    updatePaymentList = it.data!!
                    activityAdapter.addList(updatePaymentList)
                    initViews()
                }
                Status.ERROR -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }



        binding.tvUpdatePayment.setOnClickListener {
            finish()
        }
        setContentView(binding.root)
    }

    private fun initViews() {
        binding.apply {
            tvName.text = creditDebt.name
            tvNumber.text = creditDebt.phoneNumber
        }
        if(updatePaymentList.size>0){
            binding.apply {
                activityRecyclerView.visibility = View.VISIBLE
                tvName.visibility = View.VISIBLE
                tvNumber.visibility = View.VISIBLE
                infoLayout.visibility = View.VISIBLE
                viewLine.visibility = View.VISIBLE
                noPaymentsLayout.visibility= View.GONE
                setUpRecyclerView()
                setUpCardView()
            }
        }else{
            binding.apply {
                activityRecyclerView.visibility = View.GONE
                tvName.visibility = View.GONE
                tvNumber.visibility = View.GONE
                infoLayout.visibility = View.GONE
                viewLine.visibility = View.GONE
                noPaymentsLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpCardView() {
        val totalPaid = "Kes: ${creditDebt.totalAllPaid}"
        val totalBalance = "Kes: ${creditDebt.totalAllBalance}"
        val totalAmt = "Kes: ${creditDebt.totalAllAmount}"
        binding.apply {
            tvTotalExactAmount.text = totalAmt
            tvTotalExactPaid.text = totalPaid
            tvTotalExactBalance.text = totalBalance
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.activityRecyclerView.apply {
            setLayoutManager(layoutManager)
            hasFixedSize()
            adapter = activityAdapter
        }
    }

    private fun fetchUpdatePayments() {
        //Fetch list of update payments to display on the recycler view
        creditDebtViewModel.fetchUpdatePayments(creditDebt.creditDebtId!!)
    }
}