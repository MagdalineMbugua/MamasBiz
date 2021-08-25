package com.magda.mamasbiz.main.userInterface.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.magda.mamasbiz.databinding.ActivitySignUpBinding
import com.magda.mamasbiz.main.utils.Constants

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //To navigate to login page and password page

        binding.tvLogin.setOnClickListener {startActivity(Intent(this, LoginActivity::class.java))}
        binding.btSignUp.setOnClickListener {toCheckFilledData()}
    }

    private fun toCheckFilledData() {
        binding.apply {
            val firstName =etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val phoneNumber= etPhoneNumber.text.toString().trim()
            if(firstName.isNotEmpty()){
               if(lastName.isNotEmpty()) {
                   if(phoneNumber.isNotEmpty()){
                       if(phoneNumber.length in 9..10){
                           toTheOTPActivity(firstName,lastName,phoneNumber)
                       }else Toast.makeText(this@SignUpActivity, "Enter a valid phone number.",Toast.LENGTH_SHORT).show()

                   }else Toast.makeText(this@SignUpActivity, "Enter your phone number.",Toast.LENGTH_SHORT).show()
               }else Toast.makeText(this@SignUpActivity, "Enter your last name.",Toast.LENGTH_SHORT).show()
            }else Toast.makeText(this@SignUpActivity, "Enter your first name.",Toast.LENGTH_SHORT).show()
        }

    }

    private fun toTheOTPActivity(firstName: String,lastName: String, phoneNumber: String) {
        val intent = Intent(this@SignUpActivity, OtpActivity::class.java)
        intent.putExtra(Constants.FIRST_NAME, firstName)
        intent.putExtra(Constants.LAST_NAME, lastName)
        intent.putExtra(Constants.PHONE_NUMBER, phoneNumber)
        intent.putExtra(Constants.IS_LOGGED_IN, false)
        startActivity(intent)

    }
}