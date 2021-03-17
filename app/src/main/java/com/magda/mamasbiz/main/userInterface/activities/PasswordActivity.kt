package com.magda.mamasbiz.main.userInterface.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.magda.mamasbiz.databinding.ActivityPasswordBinding
import com.magda.mamasbiz.main.businessLogic.UserViewModel
import com.magda.mamasbiz.main.data.entity.User
import com.magda.mamasbiz.main.utils.Constants
import com.magda.mamasbiz.main.utils.Constants.Companion.PHONE_NUMBER
import com.magda.mamasbiz.main.utils.SessionManager
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordBinding
    private var firstName: String? =""
    private var lastName: String? =""
    private var phoneNumber: String? =""
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getting Extras from the OTP Activity

        val intent = intent
        firstName = intent.getStringExtra(Constants.FIRST_NAME)
        lastName = intent.getStringExtra(Constants.LAST_NAME)
        phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER)


        //check and confirm password match
        binding.btSignUp.setOnClickListener{toCheckPassword()}


    }

    private fun toCheckPassword() {
        binding.apply {
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            if(password.isNotEmpty()){
                if(confirmPassword.isNotEmpty()){
                    if(password == confirmPassword){
                        toDashboardActivity(password)
                    }else{
                        etConfirmPassword.error = "password does not match"
                        etConfirmPassword.requestFocus()
                    }

                }else Toast.makeText(this@PasswordActivity,"Confirm password",Toast.LENGTH_SHORT).show()

            }else Toast.makeText(this@PasswordActivity,"Fill in password",Toast.LENGTH_SHORT).show()

        }
    }

    private fun toDashboardActivity(password: String) {
        val sessionManager = SessionManager(this)
        sessionManager.storeInfo(phoneNumber!!,firstName!!,lastName!!,phoneNumber!!,password,true)
        toStoreInDatabase(phoneNumber!!,firstName!!,lastName!!,password)
        val intent = Intent(this@PasswordActivity, DashboardActivity::class.java)
        intent.putExtra(PHONE_NUMBER,phoneNumber!!)
        startActivity(intent)
        finish()

    }

    //Create user table and store data

    private fun toStoreInDatabase(
        phoneNumber: String,
        firstName: String,
        lastName: String,
        password: String
    ) {
        val dateCreated = getDateCreated()
        val user= User(phoneNumber,firstName,lastName,password,dateCreated)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.adduser(user)
        Log.d(Companion.TAG, "toStoreInDatabase: $user")

    }
    private fun getDateCreated():String{
        var dateToString=""
        dateToString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDateTime=LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
            currentDateTime.format(formatter)


        } else {
            val currentDateTime= Date()
            val formatter = SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.getDefault())
            formatter.format(currentDateTime)

        }
        return dateToString
    }

    companion object {
        private const val TAG = "Password Activity"
    }

}