package com.magda.mamasbiz.main.userInterface.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.magda.mamasbiz.databinding.ActivityOtpBinding
import com.magda.mamasbiz.main.utils.Constants
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private val auth: FirebaseAuth= FirebaseAuth.getInstance()
    private  val TAG = OtpActivity::class.simpleName
    private lateinit var verificationId: String
    private   var firstName: String?=""
    private var lastName: String?=""
    private var phoneNumber: String?=""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //get Extra intent from the signUp Activity

        val intent = intent
        firstName = intent.getStringExtra(Constants.FIRST_NAME)
        lastName = intent.getStringExtra(Constants.LAST_NAME)
        phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER)


        toSendVerificationCode()

        binding.btSignUp.setOnClickListener{
            toVerifyOtp()
        }    }

    private fun toVerifyOtp() {
        binding.otpView.setOtpCompletionListener{otp: String? ->

            val credential = PhoneAuthProvider.getCredential(verificationId, otp!!)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun toSendVerificationCode() {
        val phoneNumber1 = "+254$phoneNumber"
        Log.d(TAG, "toSendVerificationCode: $phoneNumber1")


        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber1)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)           // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            val code = credential.smsCode
            Toast.makeText(this@OtpActivity, "Verification completed", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "onVerificationCompleted: $code")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if(e is FirebaseAuthInvalidCredentialsException){
                Toast.makeText(this@OtpActivity, "Verification failed: Invalid credentials", Toast.LENGTH_SHORT).show()
            }else if(e is FirebaseTooManyRequestsException){
                Toast.makeText(this@OtpActivity, "Verification failed: Quota exceeded", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCodeSent(mVerificationId: String, resendingToken: PhoneAuthProvider.ForceResendingToken) {
            verificationId = mVerificationId
            super.onCodeSent(mVerificationId, resendingToken)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task->
            if(task.isSuccessful){
                Toast.makeText(this@OtpActivity, "Successful OTP verification", Toast.LENGTH_SHORT).show()
                toPasswordActivity()
            }
        }.addOnFailureListener { e->
           Toast.makeText(this@OtpActivity, "Unsuccessful OTP verification ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun toPasswordActivity() {
       val intent = Intent(this@OtpActivity, PasswordActivity::class.java)
        intent.putExtra(Constants.FIRST_NAME,firstName)
        intent.putExtra(Constants.LAST_NAME,lastName)
        intent.putExtra(Constants.PHONE_NUMBER,phoneNumber)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)

    }


}