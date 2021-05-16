package com.magda.mamasbiz.main.businessLogic.adapter

import android.content.Context
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.magda.mamasbiz.R
import com.magda.mamasbiz.main.data.entity.CattleBought
import kotlinx.android.synthetic.main.cattle_bought_view.view.*

class CattleBoughtAdapter(context: Context) : PagingDataAdapter<CattleBought, CattleBoughtAdapter.ViewHolder>(
    DIFF_CALLBACK) {
    private var cattleBoughtList = arrayListOf<CattleBought>()
    private val TAG = "CattleBoughtAdapter"


    companion object{
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<CattleBought>() {
            override fun areItemsTheSame(oldItem: CattleBought, newItem: CattleBought): Boolean {
                return oldItem.cattleBoughtId == newItem.cattleBoughtId
            }

            override fun areContentsTheSame(oldItem: CattleBought, newItem: CattleBought): Boolean {
                return when {
                   oldItem.cattleBoughtId == newItem.cattleBoughtId -> {true}
                    oldItem.cattleBoughtType == newItem.cattleBoughtType -> {true}
                    else -> {false}
                }
            }}
    }

    fun addList(newCattleBoughtList: ArrayList<CattleBought>){
        cattleBoughtList = newCattleBoughtList
        Log.d(TAG, "addList: ${cattleBoughtList.size}")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView =layoutInflater.inflate(R.layout.cattle_bought_view, parent, false)
        Log.d(TAG, "onCreateViewHolder: created")
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cattleBought = cattleBoughtList[position]
        holder.bind(cattleBought)
        Log.d(TAG, "onBindViewHolder: $cattleBought")
    }

    override fun getItemCount(): Int {
        return cattleBoughtList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(cattleBought: CattleBought) {
            itemView.tvCattleType .text = cattleBought.cattleBoughtType
            itemView.tvPrice .text = cattleBought.cattlePrice
            itemView.tvQty .text = cattleBought.cattleQty
            itemView.tvCattleAmount .text = cattleBought.cattleAmt
        }


    }




}