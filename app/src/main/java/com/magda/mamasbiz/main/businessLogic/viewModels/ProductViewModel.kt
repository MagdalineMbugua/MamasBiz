package com.magda.mamasbiz.main.businessLogic.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.data.repository.ProductsRepository
import com.magda.mamasbiz.main.data.responses.NetworkResponse
import com.magda.mamasbiz.main.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel:ViewModel() {
    private val productsRepository:ProductsRepository
    private  val liveDataAddProduct : MutableLiveData<NetworkResponse<Products>> = MutableLiveData<NetworkResponse<Products>>()
    val _liveDataAddProduct get() = liveDataAddProduct
    private  val liveDataFetchProduct : MutableLiveData<NetworkResponse<Products>> = MutableLiveData<NetworkResponse<Products>>()
    val _liveDataFetchProduct get() = liveDataFetchProduct
    init {
        productsRepository= ProductsRepository()
    }
    fun addProducts (product: Products){
        viewModelScope.launch(Dispatchers.IO) {

            productsRepository.addProducts(product){
                liveDataAddProduct.postValue(NetworkResponse.loading())
                when(it){
                    is Results.Success->{
                        liveDataAddProduct.postValue(NetworkResponse.success(true, null))
                    }
                    is Results.Error -> {
                        liveDataAddProduct.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }}
    fun getProducts(productId: String){
        viewModelScope.launch (Dispatchers.IO){
            liveDataFetchProduct.postValue(NetworkResponse.loading())
           when(val results = productsRepository.getProducts(productId)){
               is Results.Success ->{
                   liveDataFetchProduct.postValue(NetworkResponse.success(true,results.data))
               }
               is Results.Error -> {
                   liveDataFetchProduct.postValue(NetworkResponse.error(results.error))
               }
           }
        }
    }
    fun getProductId (): String {
     return productsRepository.getProductId()
        }


}

