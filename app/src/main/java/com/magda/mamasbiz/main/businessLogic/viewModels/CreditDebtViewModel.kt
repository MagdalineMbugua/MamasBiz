package com.magda.mamasbiz.main.businessLogic.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.repository.CreditDebtRepository
import com.magda.mamasbiz.main.data.responses.NetworkResponse
import com.magda.mamasbiz.main.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreditDebtViewModel  : ViewModel() {
    private var creditDebtRepository : CreditDebtRepository
    init {
      creditDebtRepository = CreditDebtRepository()
    }

    private val loadCDLiveData: MutableLiveData<NetworkResponse<Boolean>> = MutableLiveData<NetworkResponse<Boolean>>()
    val _loadCDLiveData:MutableLiveData<NetworkResponse<Boolean>>  get() = loadCDLiveData
    private  val fetchCDLiveData:MutableLiveData<NetworkResponse<MutableList<CreditDebt>>> = MutableLiveData<NetworkResponse<MutableList<CreditDebt>>>()
    val _fetchCDLiveData:MutableLiveData<NetworkResponse<MutableList<CreditDebt>>>  get() = fetchCDLiveData
    fun addCreditDebt(creditDebt : CreditDebt){
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "addCreditDebt:on the viewmodel $creditDebt ")
            creditDebtRepository.addCreditDebt(creditDebt){
               loadCDLiveData.postValue(NetworkResponse.loading())
                when(it){
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
    fun getCreditDebt(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            fetchCDLiveData.postValue(NetworkResponse.loading())
            creditDebtRepository.getCreditDebt(userId) {
                when(it ){
                    is Results.Success ->{
                        fetchCDLiveData.postValue(NetworkResponse.success(true,it.data))
                    }
                    is Results.Error -> {
                        fetchCDLiveData.postValue(NetworkResponse.error("An Error has occurred while fetching data"))

                    }
                }

            }

        }



    }
    fun getCreditDebtId (): String{
        return creditDebtRepository.getCreditDebtId()
    }

}