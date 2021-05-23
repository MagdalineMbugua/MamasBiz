package com.magda.mamasbiz.main.businessLogic.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magda.mamasbiz.main.data.entity.CattleBought
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Metadata
import com.magda.mamasbiz.main.data.entity.UpdatePayments
import com.magda.mamasbiz.main.data.repository.CreditDebtRepository
import com.magda.mamasbiz.main.data.responses.NetworkResponse
import com.magda.mamasbiz.main.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreditDebtViewModel : ViewModel() {
    private var creditDebtRepository: CreditDebtRepository

    init {
        creditDebtRepository = CreditDebtRepository()
    }

    private val loadCDLiveData: MutableLiveData<NetworkResponse<Boolean>> =
        MutableLiveData<NetworkResponse<Boolean>>()
    val _loadCDLiveData: MutableLiveData<NetworkResponse<Boolean>> get() = loadCDLiveData

    private val fetchCDLiveData: MutableLiveData<NetworkResponse<MutableList<CreditDebt>>> =
        MutableLiveData<NetworkResponse<MutableList<CreditDebt>>>()
    val _fetchCDLiveData: MutableLiveData<NetworkResponse<MutableList<CreditDebt>>> get() = fetchCDLiveData

    private val fetchCDDocumentLiveData: MutableLiveData<NetworkResponse<CreditDebt>> =
        MutableLiveData<NetworkResponse<CreditDebt>>()
    val _fetchCDDocumentLiveData: MutableLiveData<NetworkResponse<CreditDebt>> get() = fetchCDDocumentLiveData

    private val deleteCDLiveData: MutableLiveData<NetworkResponse<Boolean>> =
        MutableLiveData<NetworkResponse<Boolean>>()
    val _deleteCDLiveData: MutableLiveData<NetworkResponse<Boolean>> get() = deleteCDLiveData

    private val updatePaymentLiveData: MutableLiveData<NetworkResponse<Boolean>> =
        MutableLiveData<NetworkResponse<Boolean>>()
    val _updatePaymentLiveData: MutableLiveData<NetworkResponse<Boolean>> get() = updatePaymentLiveData


    private val updatePaymentIdLiveData: MutableLiveData<NetworkResponse<Boolean>> =
        MutableLiveData<NetworkResponse<Boolean>>()
    val _updatePaymentIdLiveData: MutableLiveData<NetworkResponse<Boolean>> get() = updatePaymentIdLiveData

    private val updateTotalAmountLiveData: MutableLiveData<NetworkResponse<Boolean>> =
        MutableLiveData<NetworkResponse<Boolean>>()
    val _updateTotalAmountLiveData: MutableLiveData<NetworkResponse<Boolean>> get() = updateTotalAmountLiveData


    private val fetchUpdatePaymentsLiveData: MutableLiveData<NetworkResponse<MutableList<UpdatePayments>>> =
        MutableLiveData<NetworkResponse<MutableList<UpdatePayments>>>()
    val _fetchUpdatePaymentsLiveData: MutableLiveData<NetworkResponse<MutableList<UpdatePayments>>> get() = fetchUpdatePaymentsLiveData


    private val fetchMetadataLiveData: MutableLiveData<NetworkResponse<Metadata>> =
        MutableLiveData<NetworkResponse<Metadata>>()
    val _fetchMetadataLiveData: MutableLiveData<NetworkResponse<Metadata>> get() = fetchMetadataLiveData

    private val fetchCattleBoughtLiveData: MutableLiveData<NetworkResponse<ArrayList<CattleBought>>> =
        MutableLiveData<NetworkResponse<ArrayList<CattleBought>>>()
    val _fetchCattleBoughtLiveData: MutableLiveData<NetworkResponse<ArrayList<CattleBought>>> get() = fetchCattleBoughtLiveData


    private val addMetadataLiveData: MutableLiveData<NetworkResponse<Boolean>> =
        MutableLiveData<NetworkResponse<Boolean>>()
    val _addMetadataLiveData: MutableLiveData<NetworkResponse<Boolean>> get() = addMetadataLiveData

    private val updateMetadataLiveData: MutableLiveData<NetworkResponse<Boolean>> =
        MutableLiveData<NetworkResponse<Boolean>>()
    val _updateMetadataLiveData: MutableLiveData<NetworkResponse<Boolean>> get() = updateMetadataLiveData

    private val addCattleBoughtLiveData: MutableLiveData<NetworkResponse<Boolean>> =
        MutableLiveData<NetworkResponse<Boolean>>()
    val _addCattleBoughtLiveData: MutableLiveData<NetworkResponse<Boolean>> get() = addCattleBoughtLiveData

    private val deleteCattleBoughtLiveData: MutableLiveData<NetworkResponse<Boolean>> =
        MutableLiveData<NetworkResponse<Boolean>>()
    val _deleteCattleBoughtLiveData: MutableLiveData<NetworkResponse<Boolean>> get() = deleteCattleBoughtLiveData


    fun addCreditDebt(creditDebt: CreditDebt) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "addCreditDebt:on the viewmodel $creditDebt ")
            creditDebtRepository.addCreditDebt(creditDebt) {
                loadCDLiveData.postValue(NetworkResponse.loading())
                when (it) {
                    is Results.Success -> {
                        loadCDLiveData.postValue(NetworkResponse.success(true, null))

                    }
                    is Results.Error -> {
                        loadCDLiveData.postValue(NetworkResponse.error("An Error has occurred"))

                    }
                }

            }

        }
    }

    fun getCreditDebt(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchCDLiveData.postValue(NetworkResponse.loading())
            creditDebtRepository.getCreditDebt(userId) {
                when (it) {
                    is Results.Success -> {
                        fetchCDLiveData.postValue(NetworkResponse.success(true, it.data))
                    }
                    is Results.Error -> {
                        fetchCDLiveData.postValue(NetworkResponse.error("An Error has occurred while fetching data"))

                    }
                }

            }

        }


    }
    fun getCreditDebtDocument (creditDebtId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchCDDocumentLiveData.postValue(NetworkResponse.loading())
            creditDebtRepository.getCreditDebtDocument(creditDebtId) {
                when (it) {
                    is Results.Success -> {
                        fetchCDDocumentLiveData.postValue(NetworkResponse.success(true, it.data))
                    }
                    is Results.Error -> {
                        fetchCDDocumentLiveData.postValue(NetworkResponse.error("An Error has occurred while fetching data"))

                    }
                }

            }

        }


    }


    fun getCreditDebtId(): String {
        return creditDebtRepository.getCreditDebtId()
    }

    fun getUpdatePaymentId(creditDebt: CreditDebt): String {
        return creditDebtRepository.getUpdatePaymentId(creditDebt)
    }

    fun getCattleBoughtId(creditDebtId: String): String {
        return creditDebtRepository.getCattleBoughtId(creditDebtId)
    }

    fun deleteCreditDebt(creditDebt: CreditDebt) {
        deleteCDLiveData.value = NetworkResponse.loading()
        creditDebtRepository.deleteCreditDebt(creditDebt) {
            when (it) {
                is Results.Success -> {
                    deleteCDLiveData.value = NetworkResponse.success(true, data = true)
                }
                is Results.Error -> {
                    deleteCDLiveData.value = NetworkResponse.error(it.error)
                }
            }
        }

    }


    fun addUpdatePayments(creditDebt: CreditDebt, updatePayments: UpdatePayments) {
        updatePaymentLiveData.value = NetworkResponse.loading()
        creditDebtRepository.addUpdatePayments(updatePayments, creditDebt) {
            when (it) {
                is Results.Success -> {
                    updatePaymentLiveData.value = NetworkResponse.success(true, null)
                }
                is Results.Error -> {
                    updatePaymentLiveData.value = NetworkResponse.error(it.error)
                }
            }
        }


    }

    fun addCattleBought(creditDebtId: String, cattleBought: CattleBought) {
        viewModelScope.launch(Dispatchers.IO) {
            addCattleBoughtLiveData.postValue(NetworkResponse.loading())
            creditDebtRepository.addCattleBought(cattleBought, creditDebtId) {
                when (it) {
                    is Results.Success -> {
                        addCattleBoughtLiveData.postValue(NetworkResponse.success(true, null))
                    }
                    is Results.Error -> {
                        addCattleBoughtLiveData.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }
    }

    fun updateTotalMoney(
        creditDebtId: String,
        totalAmountPaid: String,
        balance: String,
        status: String,
        productPaid: String,
        productBal : String,
        cattleBoughtPaid: String,
        cattleBoughtBal: String,
    ) {
        updateTotalAmountLiveData.value = NetworkResponse.loading()
        creditDebtRepository.updateTotalMoney(creditDebtId, totalAmountPaid, balance, status, productPaid, productBal,
        cattleBoughtPaid,cattleBoughtBal) {
            when (it) {
                is Results.Success -> {
                    updateTotalAmountLiveData.value = NetworkResponse.success(true, null)
                }
                is Results.Error -> {
                    updateTotalAmountLiveData.value = NetworkResponse.error(it.error)
                }
            }
        }

    }

    fun fetchUpdatePayments(creditDebtId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchUpdatePaymentsLiveData.postValue(NetworkResponse.loading())
            creditDebtRepository.getUpdateList(creditDebtId) {
                when (it) {
                    is Results.Success -> {
                        fetchUpdatePaymentsLiveData.postValue(
                            NetworkResponse.success(
                                true,
                                it.data
                            )
                        )
                    }
                    is Results.Error -> {
                        fetchUpdatePaymentsLiveData.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }
    }

    fun fetchCattleBought(creditDebtId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchCattleBoughtLiveData.postValue(NetworkResponse.loading())
            creditDebtRepository.getCattleBoughtList(creditDebtId) {
                when (it) {
                    is Results.Success -> {
                        fetchCattleBoughtLiveData.postValue(NetworkResponse.success(true,
                            it.data))
                    }
                    is Results.Error -> {
                        fetchCattleBoughtLiveData.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }
    }

    fun deleteCattleBought(creditDebtId: String){
    viewModelScope.launch (Dispatchers.IO) {
        deleteCattleBoughtLiveData.postValue(NetworkResponse.loading())
        creditDebtRepository.deleteCattleBoughtList(creditDebtId){
            when (it) {
                is Results.Success -> {
                    deleteCattleBoughtLiveData.postValue(NetworkResponse.success(true, it.data))
                }
                is Results.Error -> {
                    deleteCattleBoughtLiveData.postValue(NetworkResponse.error(it.error))
                }
            }
        }
    }

    }

    fun fetchMetadata(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchMetadataLiveData.postValue(NetworkResponse.loading())
            creditDebtRepository.getMetadata(userId) {
                when (it) {
                    is Results.Success -> {
                        fetchMetadataLiveData.postValue(NetworkResponse.success(true, it.data))
                    }
                    is Results.Error -> {
                        fetchMetadataLiveData.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }
    }

    fun addMetadata(metadata: Metadata, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addMetadataLiveData.postValue(NetworkResponse.loading())
            creditDebtRepository.addMetadata(metadata, userId) {
                when (it) {
                    is Results.Success -> {
                        addMetadataLiveData.postValue(NetworkResponse.success(true, null))
                    }
                    is Results.Error -> {
                        addMetadataLiveData.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }
    }

    fun updateMetadata( userId: String, amountPaid: Int, balance: Int, amount:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            updateMetadataLiveData.postValue(NetworkResponse.loading())
            creditDebtRepository.updateCattleBoughtMetadata( userId, amountPaid,balance,amount) {
                when (it) {
                    is Results.Success -> {
                        updateMetadataLiveData.postValue(NetworkResponse.success(true, null))
                    }
                    is Results.Error -> {
                        updateMetadataLiveData.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }
    }


}