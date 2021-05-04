package com.magda.mamasbiz.main.businessLogic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.magda.mamasbiz.R
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.UpdatePayments
import com.magda.mamasbiz.main.utils.Constants
import kotlinx.android.synthetic.main.update_payment.view.*

class ActivityAdapter() :  PagedListAdapter<UpdatePayments, ActivityAdapter.ViewHolder>(DIFF_CALLBACK) {
    private val updatePaymentList = mutableListOf<UpdatePayments>()
    private lateinit var creditDebt: CreditDebt
    private lateinit var updatePayments: UpdatePayments

    companion object{
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UpdatePayments>(){
            override fun areItemsTheSame(oldItem: UpdatePayments, newItem: UpdatePayments): Boolean {
                return oldItem.paymentId == newItem.paymentId
            }

            override fun areContentsTheSame(oldItem: UpdatePayments, newItem: UpdatePayments): Boolean {
                return when (oldItem.paymentId) {
                    newItem.paymentId -> {true}
                    else -> {false}
                }
            }

        }
    }

    fun addList(newList:MutableList<UpdatePayments> ){
        updatePaymentList.addAll(newList)
        notifyDataSetChanged()

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.update_payment, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        updatePayments= updatePaymentList[position]
        holder.bind(updatePayments)
    }

    override fun getItemCount(): Int {
        return updatePaymentList.size
    }

     fun getCreditDebt(newCreditDebt: CreditDebt){
         this.creditDebt = newCreditDebt

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(updatePayments: UpdatePayments) {
            if (creditDebt.type == Constants.CREDIT) {
                val info ="Creditor"
                itemView.tvCustomerType.text = info

            } else if (creditDebt.type == Constants.DEBT) {
                val info ="Debtor"
                itemView.tvCustomerType.text = info
            }
            itemView.tvBalance.text = updatePayments.updateBalance
            itemView.tvDate.text = updatePayments.updateDate
            val amount = "KES: ${updatePayments.amountPaid}"
            itemView.tvCustomerName.text = amount

        }



    }

}