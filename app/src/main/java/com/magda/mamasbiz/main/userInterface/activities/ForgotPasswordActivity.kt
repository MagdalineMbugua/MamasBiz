package com.magda.mamasbiz.main.userInterface.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.magda.mamasbiz.databinding.ActivityForgotPasswordBinding
import com.magda.mamasbiz.main.businessLogic.viewModels.UserViewModel
import com.magda.mamasbiz.main.data.entity.User
import com.magda.mamasbiz.main.utils.Constants.Companion.DATE_CREATED
import com.magda.mamasbiz.main.utils.Constants.Companion.FIRST_NAME
import com.magda.mamasbiz.main.utils.Constants.Companion.LAST_NAME
import com.magda.mamasbiz.main.utils.Constants.Companion.PHONE_NUMBER
import com.magda.mamasbiz.main.utils.Status

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Instantiate view model
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        //Observe the live data
        userViewModel._updateUserLiveData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    //Look into whether will add more
                }
                Status.SUCCESS -> {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Successfully changed password",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
                    finish()
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        //Get extras from Password Activity
        val phoneNumber = intent.getStringExtra(PHONE_NUMBER)


        //SetOnClickListener
        binding.btSubmit.setOnClickListener {
            toVerifyPassword(phoneNumber)
        }
    }

    // To validate the password filled in
    private fun toVerifyPassword(
        phoneNumber: String?
    ) {
        binding.apply {
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            if (password.isNotEmpty()) {
                if (confirmPassword.isNotEmpty()) {
                    if (password == confirmPassword) {
                        toChangePassword(phoneNumber, password)
                    } else Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Password does not match",
                        Toast.LENGTH_SHORT
                    ).show()

                } else Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Fill in confirm password",
                    Toast.LENGTH_SHORT
                ).show()
            } else Toast.makeText(
                this@ForgotPasswordActivity,
                "Fill in password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Change and update password in the database
    private fun toChangePassword(
        phoneNumber: String?,
        password: String,

        ) {
        userViewModel.updateUser(password, phoneNumber!!)

    }
}