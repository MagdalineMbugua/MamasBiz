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
    private  val liveDataAddProduct : MutableLiveData<NetworkResponse<Boolean>> = MutableLiveData<NetworkResponse<Boolean>>()
    val _liveDataAddProduct get() = liveDataAddProduct
    private  val liveDataFetchProduct : MutableLiveData<NetworkResponse<Products>> = MutableLiveData<NetworkResponse<Products>>()
    val _liveDataFetchProduct get() = liveDataFetchProduct

    private val liveDataDeleteProduct : MutableLiveData<NetworkResponse<Boolean>> = MutableLiveData<NetworkResponse<Boolean>>()
    val _liveDataDeleteProduct get() = liveDataDeleteProduct
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
            productsRepository.getProducts(productId){
                when(it){
                    is Results.Success ->{
                        liveDataFetchProduct.postValue(NetworkResponse.success(true,it.data))
                    }
                    is Results.Error -> {
                        liveDataFetchProduct.postValue(NetworkResponse.error(it.error))
                    }
                }
            }

        }
    }
    fun getProductId (): String {
     return productsRepository.getProductId()
        }

    fun deleteProduct (productId: String){
        viewModelScope.launch (Dispatchers.IO) {
            liveDataDeleteProduct.postValue(NetworkResponse.loading())
            productsRepository.deleteProduct(productId){
                when(it){
                    is Results.Success -> {
                        liveDataDeleteProduct.postValue(NetworkResponse.success(true, data = true))
                    }
                    is Results.Error -> {
                        liveDataDeleteProduct.postValue(NetworkResponse.error(it.error))
                    }
                }

            }
        }

    }
    fun updateProduct (product: Products){
        viewModelScope.launch (Dispatchers.IO) {
            liveDataAddProduct.postValue(NetworkResponse.loading())
            productsRepository.updateProduct(product){
                when (it){
                    is Results.Success -> {
                        liveDataAddProduct.postValue(NetworkResponse.success(true, null))
                    }
                    is Results.Error -> {
                        liveDataAddProduct.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }
    }


}

