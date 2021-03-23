package com.magda.mamasbiz.main.userInterface.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.magda.mamasbiz.databinding.ActivityPasswordBinding
import com.magda.mamasbiz.main.businessLogic.UserViewModel
import com.magda.mamasbiz.main.data.entity.User
import com.magda.mamasbiz.main.utils.ConnectionLiveData
import com.magda.mamasbiz.main.utils.Constants.Companion.DATE_CREATED
import com.magda.mamasbiz.main.utils.Constants.Companion.FIRST_NAME
import com.magda.mamasbiz.main.utils.Constants.Companion.IS_LOGGED_IN
import com.magda.mamasbiz.main.utils.Constants.Companion.LAST_NAME
import com.magda.mamasbiz.main.utils.Constants.Companion.PASSWORD
import com.magda.mamasbiz.main.utils.Constants.Companion.PHONE_NUMBER
import com.magda.mamasbiz.main.utils.SessionManager
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.properties.Delegates

class PasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordBinding
    private var firstName: String? =""
    private var lastName: String? =""
    private var phoneNumber: String? =""
    private var password: String? =""
    private var dateCreated: String? =""
    private var isLoggedIn:Boolean = false
    private lateinit var userViewModel: UserViewModel
    private lateinit var connectionLiveData: ConnectionLiveData
    private var isNetworkAvailable by Delegates.notNull<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Instantiate Connection Live date
        connectionLiveData= ConnectionLiveData(this)
        updateUiOnConnectivity()


        //Instantiate the view model and view model factory

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        //getting Extras from the OTP Activity

        val intent = intent
        firstName = intent.getStringExtra(FIRST_NAME)
        lastName = intent.getStringExtra(LAST_NAME)
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        password = intent.getStringExtra(PASSWORD)
        dateCreated = intent.getStringExtra(DATE_CREATED)
        isLoggedIn = intent.getBooleanExtra(IS_LOGGED_IN, false)


        //check and change views if sign up or log in


        if(isLoggedIn){
            binding.apply {
                tvConfirmPassword.visibility = View.GONE
                etConfirmPassword.visibility = View.GONE
                layoutConfirmPassword.visibility = View.GONE
                tvForgotPassword.visibility = View.VISIBLE
                tvForgotPassword.setOnClickListener { toForgotPasswordActivity() }
                btSignUp.setOnClickListener{toConfirmPassword()}
            }

        }else{
           binding. btSignUp.setOnClickListener{toCheckPassword()}
        }



    }

    private fun toConfirmPassword() {
        val passwordInput = binding.etPassword.text.toString().trim()
        Log.d(TAG, "toConfirmPassword: $passwordInput $password")
        if(passwordInput==password){
            toDashboardActivity(passwordInput)
        } else Toast.makeText(this, "Wrong password. Try again", Toast.LENGTH_SHORT).show()
    }

    private fun toForgotPasswordActivity() {
       val intent = Intent(this@PasswordActivity, ForgotPasswordActivity::class.java)
        intent.putExtra(FIRST_NAME,firstName)
        intent.putExtra(LAST_NAME,lastName)
        intent.putExtra(PHONE_NUMBER,phoneNumber)
        intent.putExtra(DATE_CREATED, dateCreated)
        startActivity(intent)
    }

    private fun updateUiOnConnectivity() {
        connectionLiveData.observe(this, { isNetworkAvailable->
            if(isNetworkAvailable){
                Snackbar.make(binding.btSignUp,"Valid Connection",Snackbar.LENGTH_LONG)
            }else Snackbar.make(binding.btSignUp,"No Connection",Snackbar.LENGTH_LONG)
        })
    }

    private fun toCheckPassword() {
        binding.apply {
           val  passwordInput = binding.etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            if(passwordInput.isNotEmpty()){
                if(confirmPassword.isNotEmpty()){
                    if(passwordInput == confirmPassword){
                        toStoreInDatabase(phoneNumber!!,firstName!!,lastName!!,passwordInput)
                        toDashboardActivity(passwordInput)
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
        sessionManager.storeInfo(phoneNumber!!,firstName!!,lastName!!,password,true)
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

        userViewModel.addUser(user)
        Log.d(TAG, "toStoreInDatabase: $user")
        storeInFirebaseDatabase(user)

    }

    private fun storeInFirebaseDatabase(user: User) {
        TODO("Not yet implemented")
    }

    //Get date and time of the sign up
    private fun getDateCreated():String{
        val dateToString: String
        dateToString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDateTime=LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
            currentDateTime.format(formatter)


        } else {
            val currentDateTime= Date()
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            formatter.format(currentDateTime)

        }
        return dateToString
    }

    companion object {
        private const val TAG = "Password Activity"
    }

}