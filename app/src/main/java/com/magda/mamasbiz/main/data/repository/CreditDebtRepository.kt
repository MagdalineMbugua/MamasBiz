package com.magda.mamasbiz.main.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Metadata
import com.magda.mamasbiz.main.data.entity.UpdatePayments
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Results
import com.magda.mamasbiz.main.utils.Results.Error
import com.magda.mamasbiz.main.utils.Results.Success
import java.lang.Exception
import java.nio.channels.CancelledKeyException

class CreditDebtRepository {
    private var creditDebtReference: CollectionReference
    private var metadataReference: CollectionReference
    private var updatedCreditDebtList: MutableList<CreditDebt> = mutableListOf()
    private var updatedPaymentsList: MutableList<UpdatePayments> = mutableListOf()
    private val database: FirebaseFirestore


    init {
        database = FirebaseFirestore.getInstance()
        creditDebtReference = database.collection(Constants.CREDIT_DEBT_REFERENCE)
        metadataReference = database.collection(Constants.METADATA_REFERENCE)
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


                creditDebtReference.whereEqualTo(Constants.USER_ID, userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "getCreditDebt: is Successful ${task.result?.size()}")
                        task.result?.let {
                            for (snapshot: DocumentSnapshot in task.result!!) {
                                val creditDebt = snapshot.toObject(CreditDebt::class.java)
                                updatedCreditDebtList.add(creditDebt!!)


                            }
                            callback(Success(updatedCreditDebtList))
                        }
                    } else {
                        callback(Error("error occurred while fetching data"))
                        Log.d(TAG, "getCreditDebt: taskFailed")
                    }


                }


        } catch (e: Exception) {
            callback(Error("Error occurred: ${e.message}"))
        }


    }


    fun getCreditDebtId(): String {
        return creditDebtReference.document().id
    }

    fun deleteCreditDebt(creditDebt: CreditDebt, callback: (Results<Boolean>) -> Unit) {
        try {
            creditDebtReference.document(creditDebt.creditDebtId!!).delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Success(true))
                    } else callback(Error("Deleting was not successful"))

                }
        } catch (e: Exception) {
            callback(Error("Deleting was not successful ${e.message}"))
        }
    }


    fun addUpdatePayments(
        updatePayments: UpdatePayments,
        creditDebt: CreditDebt,
        callback: (Results<Boolean>) -> Unit
    ) {
        try {
            val updateReference = database.collection(Constants.CREDIT_DEBT_REFERENCE)
                .document(creditDebt.creditDebtId!!)
                .collection(Constants.UPDATE_REFERENCE)
            updateReference.document(updatePayments.paymentId!!).set(updatePayments)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Success(true))
                    } else callback(Error("Updating Payments Failed"))
                }
        } catch (e: Exception) {
            callback(Error("Updating Payments Failed ${e.message}"))
        }
    }

    fun updateTotalMoney(
        creditDebtId: String,
        amountPaid: String,
        balance: String,
        status: String,
        callback: (Results<Boolean>) -> Unit
    ) {
        val map: MutableMap<String, String> = mutableMapOf(
            Constants.TOTAL_AMT_PAID to amountPaid, Constants.TOTAL_BALANCE to balance,
            Constants.STATUS to status
        )
        try {
            creditDebtReference.document(creditDebtId).update(map as Map<String, Any>)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Success(true))
                    } else callback(Error("Updating Payment Failed"))
                }
        } catch (e: Exception) {
            callback(Error("Updating Payment Failed ${e.message}"))
        }
    }

    fun getUpdatePaymentId(creditDebt: CreditDebt): String {
        return creditDebtReference.document(creditDebt.creditDebtId!!)
            .collection(Constants.UPDATE_REFERENCE).document().id
    }

    fun getUpdateList(
        creditDebtId: String,
        callback: (Results<MutableList<UpdatePayments>>) -> Unit
    ) {
        try {
            creditDebtReference.document(creditDebtId).collection(Constants.UPDATE_REFERENCE)
                .orderBy(Constants.UPDATE_DATE, Query.Direction.DESCENDING).get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    for (snapshot: DocumentSnapshot in queryDocumentSnapshots) {
                        val updatePayments = snapshot.toObject(UpdatePayments::class.java)
                        Log.d(TAG, "getUpdateList: $updatePayments")
                        updatedPaymentsList.add(updatePayments!!)
                    }
                    callback(Success(updatedPaymentsList))
                }
        } catch (e: Exception) {
            callback(Error("fetching updated payments was not successful ${e.message}"))
        }

    }

    fun addMetadata(metadata: Metadata, userId: String, callback: (Results<Boolean>) -> Unit) {
        try {
            metadataReference.document(userId).set(metadata).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(Success(true))
                } else callback(Error("Error encountered while adding metadata"))
            }

        } catch (e: Exception) {
            callback(Error("Error encountered while adding metadata ${e.message}"))
        }

    }

    fun getMetadata(userId: String, callback: (Results<Metadata>) -> Unit) {
        try {
            metadataReference.document(userId).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val metadata = task.result?.toObject(Metadata::class.java)
                    callback(Success(metadata!!))
                } else callback(Error("Error encountered while fetching metadata"))
            }

        } catch (e: Exception) {
            callback(Error("Error encountered while fetching metadata ${e.message}"))
        }
    }


}
















