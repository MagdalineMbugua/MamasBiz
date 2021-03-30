package com.magda.mamasbiz.main.utils

import androidx.recyclerview.widget.DiffUtil
import com.magda.mamasbiz.main.data.entity.CreditDebt

class MyDiffUtil(private val oldList: MutableList<CreditDebt>,
                 private val newList: MutableList<CreditDebt>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return  newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].creditDebtId == newList[newItemPosition].creditDebtId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].creditDebtId == newList[newItemPosition].creditDebtId ->{true}
            oldList[oldItemPosition].productId == newList[newItemPosition].productId ->{true}
            else -> {false}

        }
    }
}