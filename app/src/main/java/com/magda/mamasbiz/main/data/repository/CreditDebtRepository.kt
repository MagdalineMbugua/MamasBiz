package com.magda.mamasbiz.main.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.magda.mamasbiz.main.data.dao.CreditDebtDao
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Results
import com.magda.mamasbiz.main.utils.Results.*

class CreditDebtRepository  {
    private var creditDebtReference: CollectionReference
    init {
      val database = FirebaseFirestore.getInstance()
        creditDebtReference  = database.collection(Constants.CREDIT_DEBT_REFERENCE)
    }



    fun addCreditDebt(creditDebt: CreditDebt,callback: (Results<Boolean>)-> Unit) {
        creditDebtReference.document(creditDebt.creditDebtId.toString()).set(creditDebt).addOnCompleteListener{task ->
           if(task.isSuccessful) {
              callback(Success(true))
           } else callback(Results.Error("Add document failed"))
        }.addOnFailureListener{
            callback(Results.Error("${it.localizedMessage}"))

        }



    }

}
