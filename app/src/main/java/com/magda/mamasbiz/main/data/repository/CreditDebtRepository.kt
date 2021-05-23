package com.magda.mamasbiz.main.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.*
import com.magda.mamasbiz.main.data.entity.CattleBought
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Metadata
import com.magda.mamasbiz.main.data.entity.UpdatePayments
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Results
import com.magda.mamasbiz.main.utils.Results.Error
import com.magda.mamasbiz.main.utils.Results.Success
import java.lang.Exception

class CreditDebtRepository {
    private var creditDebtReference: CollectionReference
    private var metadataReference: CollectionReference
    private var updatedCreditDebtList: MutableList<CreditDebt> = mutableListOf()
    private var updatedPaymentsList: MutableList<UpdatePayments> = mutableListOf()
    private var updatedCattleBoughtList: ArrayList<CattleBought> = arrayListOf()
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()


    init {
        creditDebtReference = database.collection(Constants.CREDIT_DEBT_REFERENCE)
        metadataReference = database.collection(Constants.METADATA_REFERENCE)
    }

    fun getCreditDebtDocument(creditDebtId: String, callback: (Results<CreditDebt>) -> Unit){
        try {creditDebtReference.document(creditDebtId).get().addOnCompleteListener{ task ->
            if (task.isComplete){
                val creditDebt = task.result?.toObject(CreditDebt::class.java)
                callback(Success(creditDebt!!))
            }else callback(Error("Fetching this data was unsuccessful"))
        }

        } catch (e:Exception){
            callback(Error("Fetching this data was unsuccessful ${e.message}"))
        }

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
                            val updateList = it.documents
                                .map { snapshot -> snapshot.toObject(CreditDebt::class.java) }
                                .sortedByDescending {creditDebt -> creditDebt?.dateCreated  }
                                .filterNotNull()

                            callback(Success(updateList.toMutableList()))

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
    fun deleteCattleBoughtList (creditDebtId: String,callback: (Results<Boolean>) -> Unit) {
        try {
            val cattleBoughtReference = database.collection(Constants.CREDIT_DEBT_REFERENCE)
                .document(creditDebtId)
                .collection(Constants.CATTLE_BOUGHT_REFERENCE).document()
            cattleBoughtReference.delete()
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
    fun addCattleBought(
        cattleBought: CattleBought,
        creditDebtId: String,
        callback: (Results<Boolean>) -> Unit
    ) {
        try {
            val cattleBoughtReference = database.collection(Constants.CREDIT_DEBT_REFERENCE)
                .document(creditDebtId)
                .collection(Constants.CATTLE_BOUGHT_REFERENCE)
            val cattleBoughtId = cattleBoughtReference.document().id
            cattleBought.cattleBoughtId = cattleBoughtId
            cattleBought.creditDebtId = creditDebtId
            Log.d(TAG, "addCattleBought: $creditDebtId, $cattleBoughtId")
            cattleBoughtReference.document(cattleBoughtId).set(cattleBought)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Success(true))
                    } else callback(Error("Cattle bought update Failed"))
                }
        } catch (e: Exception) {
            callback(Error("Cattle bought update Failed ${e.message}"))
        }
    }

    fun updateTotalMoney(
        creditDebtId: String,
        amountPaid: String,
        balance: String,
        status: String,
        productPaid: String,
        productBal : String,
        cattleBoughtPaid: String,
        cattleBoughtBal: String,
        callback: (Results<Boolean>) -> Unit
    ) {
        val map: MutableMap<String, String> = mutableMapOf(
            Constants.TOTAL_AMT_PAID to amountPaid, Constants.TOTAL_BALANCE to balance,
            Constants.STATUS to status, Constants.PRODUCT_PAID to productPaid,
            Constants.PRODUCT_BALANCE to productBal, Constants.CATTLE_BOUGHT_BALANCE to cattleBoughtBal,
            Constants.CATTLE_BOUGHT_PAID to cattleBoughtPaid
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
    fun updateCattleBoughtMetadata(
       userId: String,
        amountPaid: Int,
        balance: Int,
        amount:Int,
        callback: (Results<Boolean>) -> Unit
    ) {
        val map: MutableMap<String, Int> = mutableMapOf(
            Constants.TOTAL_MONEY_SENT_PAID to amountPaid, Constants.TOTAL_MONEY_SENT_BAL to balance,
            Constants.TOTAL_MONEY_SENT_AMOUNT to amount
        )
        try {
            metadataReference.document(userId).update(map as Map<String, Any>)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Success(true))
                    } else callback(Error("Updating Metadata Failed"))
                }
        } catch (e: Exception) {
            callback(Error("Updating Metadata Failed ${e.message}"))
        }
    }

    fun getUpdatePaymentId(creditDebt: CreditDebt): String {
        return creditDebtReference.document(creditDebt.creditDebtId!!)
            .collection(Constants.UPDATE_REFERENCE).document().id
    }
    fun getCattleBoughtId(creditDebtId: String): String {
        return creditDebtReference.document(creditDebtId)
            .collection(Constants.CATTLE_BOUGHT_REFERENCE).document().id
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
    fun getCattleBoughtList(
        creditDebtId: String,
        callback: (Results<ArrayList<CattleBought>>) -> Unit
    ) {
        try {
            creditDebtReference.document(creditDebtId).collection(Constants.CATTLE_BOUGHT_REFERENCE).get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    for (snapshot: DocumentSnapshot in queryDocumentSnapshots) {
                        val cattleBought = snapshot.toObject(CattleBought::class.java)
                        Log.d(TAG, "getUpdateList: $cattleBought")
                        updatedCattleBoughtList.add(cattleBought!!)
                    }
                    callback(Success(updatedCattleBoughtList))
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
















