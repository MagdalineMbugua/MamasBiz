package com.magda.mamasbiz.main.businessLogic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.magda.mamasbiz.R
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.MyDiffUtil
import kotlinx.android.synthetic.main.customer_cardview.view.*
import androidx.recyclerview.widget.DiffUtil as DiffUtil1

class CreditDebtAdapter (private val context: Context, private val itemClickListener: ItemClickListener) :
    PagingDataAdapter<CreditDebt, CreditDebtAdapter.ViewHolder>(DIFF_CALLBACK)
{
    private  var creditDebtList:MutableList<CreditDebt> = mutableListOf()
    companion object{
        private val DIFF_CALLBACK = object: DiffUtil1.ItemCallback<CreditDebt>() {
            override fun areItemsTheSame(oldItem: CreditDebt, newItem: CreditDebt): Boolean {
                return oldItem.creditDebtId == newItem.creditDebtId
            }

            override fun areContentsTheSame(oldItem: CreditDebt, newItem: CreditDebt): Boolean {
                return when {
                    oldItem.creditDebtId == newItem.creditDebtId ->{true}
                    oldItem.productId == newItem.productId ->{true}
                    else -> {false}

                }
            }


        }
    }




    fun addList(newList:MutableList<CreditDebt> ){
       val diffUtil = MyDiffUtil(creditDebtList, newList)
        val diffResults = DiffUtil1.calculateDiff(diffUtil)
        creditDebtList = newList
        diffResults.dispatchUpdatesTo(this)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView =layoutInflater.inflate(R.layout.customer_cardview, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(creditDebtList[position])

    }

    override fun getItemCount(): Int {
        return creditDebtList.size
    }

    fun filteredList(filteredList: MutableList<CreditDebt>) {
        creditDebtList = filteredList
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val mCustomerName = itemView.tvCustomerName
        private val mAmount = itemView.tvAmountPaid
        private val mDateCreated = itemView.tvDate
        private val mStatus = itemView.tvBalance
        private val mImage = itemView.ivCowIcon
        private val mCardView = itemView.consumerCardView

        fun bind(creditDebt: CreditDebt) {
            if(creditDebt.type==Constants.CREDIT){
                mImage.setImageResource(R.drawable.consumer_cow)

            }else if (creditDebt.type==Constants.DEBT){
                mImage.setImageResource(R.drawable.chop)

            }
            val kes = "KES: ${creditDebt.totalAllAmount}"
            mCustomerName.text = creditDebt.name
            mAmount.text = kes
            mDateCreated.text = creditDebt.dateCreated
            mStatus.text = creditDebt.status
            mCardView.setOnClickListener(this)


        }

        override fun onClick(v: View?) {
            val creditDebt = creditDebtList[adapterPosition]
            itemClickListener.itemClicked(creditDebt,v!!)

        }

    }




}
interface ItemClickListener{
    fun itemClicked(creditDebt: CreditDebt, view :View)
}