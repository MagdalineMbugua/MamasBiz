package com.magda.mamasbiz.main.businessLogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.repository.CreditDebtRepository
import com.magda.mamasbiz.main.data.responses.FirebaseUserResponse
import com.magda.mamasbiz.main.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreditDebtViewModel : ViewModel() {
    private val creditDebtRepository : CreditDebtRepository
    init {
        creditDebtRepository = CreditDebtRepository()
    }
    fun addCreditDebt(creditDebt : CreditDebt){
        viewModelScope.launch(Dispatchers.IO) {
            creditDebtRepository.addCreditDebt(creditDebt){
                FirebaseUserResponse.loading()
                when(it){
                    is Results.Success -> {

                    }
                    is Results.Error -> {

                    }
                }

            }

        }
    }

}