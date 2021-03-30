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

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Instantiate view model
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        //Get extras from Password Activity
        val firstName = intent.getStringExtra(FIRST_NAME)
        val lastName = intent.getStringExtra(LAST_NAME)
        val phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        val dateCreated = intent.getStringExtra(DATE_CREATED)


        //SetOnClickListener
        binding.btSubmit.setOnClickListener {
            toVerifyPassword(
                firstName,
                lastName,
                phoneNumber,
                dateCreated
            )
        }
    }
// To validate the password filled in
    private fun toVerifyPassword(
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        dateCreated: String?
    ) {
        binding.apply {
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            if (password.isNotEmpty()) {
                if (confirmPassword.isNotEmpty()) {
                    if (password == confirmPassword) {
                        toChangePassword(firstName, lastName, phoneNumber, password, dateCreated)
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
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        password: String,
        dateCreated: String?
    ) {
        val user = User(phoneNumber!!, firstName!!, lastName!!, password, dateCreated!!)
        userViewModel.updateUser(user)
        Toast.makeText(
            this@ForgotPasswordActivity,
            "Successfully changed password",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
    }
}