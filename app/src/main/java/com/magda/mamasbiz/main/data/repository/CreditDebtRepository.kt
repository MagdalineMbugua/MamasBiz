package com.magda.mamasbiz.main.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.*
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Results
import com.magda.mamasbiz.main.utils.Results.Error
import com.magda.mamasbiz.main.utils.Results.Success
import java.lang.Exception

class CreditDebtRepository {
    private var creditDebtReference: CollectionReference
    private var updatedCreditDebtList: MutableList<CreditDebt> = mutableListOf()

    init {
        val database = FirebaseFirestore.getInstance()
        creditDebtReference = database.collection(Constants.CREDIT_DEBT_REFERENCE)
    }


    fun addCreditDebt(creditDebt: CreditDebt, callback: (Results<Boolean>) -> Unit) {
        creditDebtReference.document(creditDebt.creditDebtId.toString()).set(creditDebt)
            .addOnCompleteListener { task ->
                Log.d(TAG, "addCreditDebt: $creditDebt ")
                if (task.isSuccessful) {
                    callback(Success(true))
                    Log.d(TAG, "addCreditDebt: was successful")
                } else callback(Error("Add document failed"))
            }.addOnFailureListener {
                callback(Error(it.localizedMessage!!))

            }
    }

    fun getCreditDebt(userId: String, callback: (Results<MutableList<CreditDebt>>) -> Unit) {

        try {
            val query = creditDebtReference.
            whereEqualTo(Constants.USER_ID, userId)

            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "getCreditDebt: is Successful ${task.result?.size()}")
                    task.result?.let {
                        for (snapshot: DocumentSnapshot in task.result!!) {
                            val creditDebt = snapshot.toObject(CreditDebt::class.java)
                            updatedCreditDebtList.add(creditDebt!!)


                        }
                        callback(Results.Success(updatedCreditDebtList))
                    }
                } else {
                    callback(Results.Error("error occurred while fetching data"))
                    Log.d(TAG, "getCreditDebt: taskFailed")
                }


            }


        } catch (e: Exception) {
            callback(Results.Error("Error occurred: ${e.message}"))
        }


    }


    fun getCreditDebtId(): String {
        return creditDebtReference.document().id
    }


}


