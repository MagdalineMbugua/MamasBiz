package com.magda.mamasbiz.main.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.magda.mamasbiz.main.data.entity.User
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Results

class UserRepository{
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val userRef : CollectionReference
    private  val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    init {
        userRef = database.collection(Constants.USER_REFERENCE)

    }
     fun logOut (callback: (Results<Boolean>) -> Unit) {
         try {
             mAuth.signOut()
             callback(Results.Success(mAuth.currentUser==null))

         }catch (e:Exception){
             callback(Results.Error("Signing out was not successful, ${e.message}"))
         }

     }

    suspend fun addUser (user: User, callback: (Results<Boolean>)-> Unit){
        try{
            userRef.document(user.userId!!).set(user).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    callback(Results.Success(true))
                } else callback(Results.Error("User addition was not successful"))

            }
        } catch (e: Exception){
            callback(Results.Error("User addition was not successful ${e.message}"))
        }


    }
    fun updateUser (userPassword: String, userId : String, callback: (Results<Boolean>) -> Unit){
        try {
            userRef.document(userId).update(Constants.PASSWORD, userPassword).addOnCompleteListener{
                task->
                if(task.isSuccessful){
                    callback(Results.Success(true))
                } else callback(Results.Error("User addition was not successful"))
            }

        }catch (e: java.lang.Exception){
            callback(Results.Error("User addition was not successful ${e.message}"))
        }

    }
    fun readUsers(userId: String, callback: (Results<User>) -> Unit){
        try {userRef.document(userId).get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(User::class.java)
            if (user!= null){
                callback(Results.Success(user))
            } else  callback(Results.Error("User does not exist"))

        }
        } catch (e: Exception){
            callback(Results.Error("User fetching was not successful ${e.message}"))
        }
    }






}