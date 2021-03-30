package com.magda.mamasbiz.main.userInterface.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.magda.mamasbiz.databinding.ActivityLoginBinding
import com.magda.mamasbiz.main.businessLogic.viewModels.UserViewModel
import com.magda.mamasbiz.main.utils.Constants
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var phoneNumber: String
    private val TAG : String = "Login Activity"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //instantiate view model
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        //Set on Click Listener on login and signup

        binding.apply {
            tvSignUp.setOnClickListener{ startActivity(Intent(this@LoginActivity,
                SignUpActivity::class.java))}

            btLogin.setOnClickListener{checkInfoReceived()}
        }
    }

    // Ensure that the info received is valid
    private fun checkInfoReceived() {
         phoneNumber = etPhoneNumber.text.toString().trim()
        if(phoneNumber.isNotEmpty()){
           if(phoneNumber.length in 10 downTo 9){
               checkDatabase()
           }else Toast.makeText(this,"Wrong phone number input", Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this,"Fill in Phone number", Toast.LENGTH_SHORT).show()


    }

    //Check if the number exists in the database

    private fun checkDatabase() {
       userViewModel.readAllUsers.observe(this, Observer { users ->
           for (user in users) {
               val userPhoneNumber = user.userId
               if (userPhoneNumber == phoneNumber) {
                   val userFirstName = user.firstName
                   val userLastName = user.lastName
                   val userPassword = user.password
                   val userDateCreated = user.dateCreated
                   toOtpActivity(userFirstName, userLastName, userPassword, userDateCreated)
               } else Toast.makeText(this,"$phoneNumber does not exist", Toast.LENGTH_SHORT).show()
           }
       })
    }

    private fun toOtpActivity(
        userFirstName: String,
        userLastName: String,
        userPassword: String,
        userDateCreated: String
    ) {
        Log.d(TAG, "toOtpActivity: $userFirstName $userLastName, $userPassword, $userDateCreated")
        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra(Constants.FIRST_NAME,userFirstName)
        intent.putExtra(Constants.LAST_NAME,userLastName)
        intent.putExtra(Constants.PHONE_NUMBER,phoneNumber)
        intent.putExtra(Constants.PASSWORD,userPassword)
        intent.putExtra(Constants.DATE_CREATED,userDateCreated)

        intent.putExtra(Constants.IS_LOGGED_IN,true)
        startActivity(intent)
    }
}