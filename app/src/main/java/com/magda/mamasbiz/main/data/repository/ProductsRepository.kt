package com.magda.mamasbiz.main.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.Products
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Results
import java.lang.Exception

class ProductsRepository {
    private var productReference: CollectionReference
    private var  product: Products? = null

    init {
        val database = FirebaseFirestore.getInstance()
        productReference = database.collection(Constants.PRODUCT_REFERENCE)
    }
    fun addProducts(product: Products,callback: (Results<Boolean>)-> Unit){
        product.productId?.let {
            productReference.document(it!!).set(product).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    callback(Results.Success(true))
                }else Results.Error("Error occurred while adding products")
            }.addOnFailureListener { e ->
                callback(Results.Error("Error occurred: ${e.message}"))
            }
        }
    }
    fun getProductId(): String{
        return productReference.document().id

    }

    fun getProducts(productId: String, callback: (Results<Products>) -> Unit){
     try {
         productReference.document(productId).get().addOnCompleteListener { task ->
             if(task.isSuccessful){
                 val snapshot = task.result
                 snapshot?.let {
                     product =snapshot.toObject(Products::class.java)
                     callback( Results.Success(product!!))
                 }
             } else callback(Results.Error("Fetching Product was not successful"))
         }

     }catch (e: Exception) {
         callback(Results.Error("Fetching Product was not successful ${e.message!!}"))
     }

    }
    fun deleteProduct (productId: String, callback: (Results<Boolean>) -> Unit){
        try { productReference.document(productId).delete().addOnCompleteListener { task ->
            if (task.isSuccessful){
                callback(Results.Success(true))
            } else callback(Results.Error("Deleting product was not successful"))
        }
        } catch (e: Exception){
            callback(Results.Error("Deleting product was not successful ${e.message}"))
        }

    }

    fun updateProduct (product: Products, callback: (Results<Boolean>) -> Unit){
        try {productReference.document(product.productId!!).update(Constants.PRODUCTS_BOUGHT, product).addOnCompleteListener {
            if(it.isSuccessful){
              callback(Results.Success(true))
            } else callback(Results.Error("Updating product was not successful"))
        }


        }catch (e : Exception){
            callback(Results.Error("Updating product was not successful ${e.message}"))
        }
    }
}

