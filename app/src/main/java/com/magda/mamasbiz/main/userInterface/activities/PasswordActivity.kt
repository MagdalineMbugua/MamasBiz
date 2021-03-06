package com.magda.mamasbiz.main.userInterface.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.magda.mamasbiz.databinding.ActivityPasswordBinding
import com.magda.mamasbiz.main.businessLogic.viewModels.CreditDebtViewModel
import com.magda.mamasbiz.main.businessLogic.viewModels.UserViewModel
import com.magda.mamasbiz.main.data.entity.Metadata
import com.magda.mamasbiz.main.data.entity.User
import com.magda.mamasbiz.main.utils.ConnectionLiveData
import com.magda.mamasbiz.main.utils.Constants.Companion.DATE_CREATED
import com.magda.mamasbiz.main.utils.Constants.Companion.FIRST_NAME
import com.magda.mamasbiz.main.utils.Constants.Companion.IS_LOGGED_IN
import com.magda.mamasbiz.main.utils.Constants.Companion.LAST_NAME
import com.magda.mamasbiz.main.utils.Constants.Companion.PASSWORD
import com.magda.mamasbiz.main.utils.Constants.Companion.PHONE_NUMBER
import com.magda.mamasbiz.main.utils.DateCreated
import com.magda.mamasbiz.main.utils.SessionManager
import com.magda.mamasbiz.main.utils.Status
import java.util.*

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
    private lateinit var creditDebtViewModel: CreditDebtViewModel
    private lateinit var passwordInput: String
    private val metadata = Metadata()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Instantiate Connection Live date
        connectionLiveData= ConnectionLiveData(this)
        updateUiOnConnectivity()


        //Instantiate the view model and view model factory

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        //To create the metadata digits as this is the first time they have signed up it should be 0
        creditDebtViewModel = ViewModelProvider(this).get(CreditDebtViewModel::class.java)



        //getting Extras from the OTP Activity

        val intent = intent
        firstName = intent.getStringExtra(FIRST_NAME)
        lastName = intent.getStringExtra(LAST_NAME)
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        password = intent.getStringExtra(PASSWORD)
        dateCreated = intent.getStringExtra(DATE_CREATED)
        isLoggedIn = intent.getBooleanExtra(IS_LOGGED_IN, false)



        //Observe the live data (user stored in firebase)


        userViewModel._addUserLiveData.observe(this){
            when (it.status){
                Status.LOADING ->{
                    //Nothing much
                }
                Status.SUCCESS -> {
                    Toast.makeText(this, "Successfully in", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }





        //check and change the views if it is sign up or log in


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

        creditDebtViewModel._addMetadataLiveData.observe(this){
            when(it.status){
                Status.LOADING -> {
                    //method sub
                }
                Status.SUCCESS -> {
                    toDashboardActivity()
                }
                Status.ERROR -> {
                    Toast.makeText(this,it.error,Toast.LENGTH_SHORT).show()
                }
            }
        }



    }

    private fun toConfirmPassword() {
        passwordInput = binding.etPassword.text.toString().trim()
        if(passwordInput==password){
            toDashboardActivity()
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
            passwordInput = binding.etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            if(passwordInput.isNotEmpty()){
                if(confirmPassword.isNotEmpty()){
                    if(passwordInput == confirmPassword){
                        creditDebtViewModel.addMetadata(metadata,phoneNumber!!)
                        toStoreInDatabase(phoneNumber!!,firstName!!,lastName!!,passwordInput)


                    }else{
                        etConfirmPassword.error = "password does not match"
                        etConfirmPassword.requestFocus()
                    }

                }else Toast.makeText(this@PasswordActivity,"Confirm password",Toast.LENGTH_SHORT).show()

            }else Toast.makeText(this@PasswordActivity,"Fill in password",Toast.LENGTH_SHORT).show()

        }
    }

    private fun toDashboardActivity() {
        val sessionManager = SessionManager(this)
        sessionManager.storeInfo(phoneNumber!!,firstName!!,lastName!!,passwordInput,true)
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
        //Get date and time of the sign up
        val dateCreated = DateCreated.getDateCreated()
        val user= User(phoneNumber,firstName,lastName,password,dateCreated)

        userViewModel.addUser(user)
    }


}