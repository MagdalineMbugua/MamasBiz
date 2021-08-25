package com.magda.mamasbiz.main.userInterface.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.magda.mamasbiz.databinding.ActivityOtpBinding
import com.magda.mamasbiz.main.utils.Constants
import java.util.*
import java.util.concurrent.TimeUnit
import com.magda.mamasbiz.R
import com.magda.mamasbiz.main.utils.ConnectionLiveData


class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var verificationId: String
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var phoneNumber: String? = ""
    private var password: String? = ""
    private var isLoggedIn: Boolean? = false
    private var dateCreated: String? = ""
    private lateinit var connectionLiveData: ConnectionLiveData
    private  var hasInternet:Boolean = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectionLiveData= ConnectionLiveData(this)
        connectionLiveData.observe(this){
            hasInternet = it
            if(!hasInternet){
                binding.btConnectivity.visibility = View.VISIBLE

            } else {
                binding.btConnectivity.visibility = View.GONE
            }
        }








        //get Extra intent from the signUp Activity
        firstName = intent.getStringExtra(Constants.FIRST_NAME)
        lastName = intent.getStringExtra(Constants.LAST_NAME)
        phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER)
        password = intent.getStringExtra(Constants.PASSWORD)
        isLoggedIn = intent.getBooleanExtra(Constants.IS_LOGGED_IN,false)
        dateCreated = intent.getStringExtra(Constants.DATE_CREATED)

        val infoText = "One time pin will be sent to +254$phoneNumber within 60 seconds. Standard SMS rates apply."
        binding.tvInfo.text = infoText







    }

    override fun onStart() {
        super.onStart()



        // Send verification code
        toSendVerificationCode()

        //when Retrying it takes you to the whole process
        binding.tvRetry.setOnClickListener { toSendVerificationCode() }



        //SetClickListener on Signup button
        binding.btSignUp.setOnClickListener {
            toVerifyOtp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectionLiveData.removeObservers(this)
    }



    private fun setTimer() {
        var duration = 60000
        val countDownTimer = object: CountDownTimer(duration.toLong(),1000){
            override fun onTick(millisUntilFinished: Long) {
                duration = millisUntilFinished.toInt()
                val minutes = duration.div(60000)
                val seconds= duration.div(1000)
                val actualSeconds = if(seconds<10){
                    "0$seconds"
                } else seconds

                val period ="0$minutes : $actualSeconds"
                binding.tvTimer.text = period
                binding.tvTimer.visibility = View.VISIBLE

            }

            override fun onFinish() {
                binding.tvTimer.visibility = View.GONE
                val retryText = "Otp not sent? Retry."
                val highlightedText = SpannableString(retryText)
                val color = ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                highlightedText.setSpan(color,14, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.tvRetry.text =highlightedText
                binding.tvRetry.visibility = View.VISIBLE


            }

        }
        countDownTimer.start()
    }


    private fun toSendVerificationCode() {
        binding.progressbar.visibility = View.VISIBLE
        //setTimer
        setTimer()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+254$phoneNumber")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)           // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(
            mVerificationId: String,
            resendingToken: PhoneAuthProvider.ForceResendingToken
        ) { binding.progressbar.visibility = View.GONE
            verificationId = mVerificationId
            super.onCodeSent(mVerificationId, resendingToken)
        }

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            val code = credential.smsCode
            if (code!=null) {
               signInWithPhoneAuthCredential(credential)
                Toast.makeText(this@OtpActivity, "Verification completed", Toast.LENGTH_SHORT)
                    .show()

            } else  Toast.makeText(this@OtpActivity, "Fill in the OTP", Toast.LENGTH_SHORT)
                .show()

        }

        override fun onVerificationFailed(e: FirebaseException) {
            binding.progressbar.visibility = View.GONE
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(
                    this@OtpActivity,
                    "Verification failed: Invalid credentials",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(
                    this@OtpActivity,
                    "Verification failed: Quota exceeded",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    private fun toVerifyOtp() {
        val otp =binding.otpView.text .toString()
        if(otp.isNotEmpty()){
            binding.progressbar.visibility = View.VISIBLE
            manualVerification(otp)
        } else Toast.makeText(this@OtpActivity, "Fill in the OTP",Toast.LENGTH_SHORT).show()

    }

    private fun manualVerification(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@OtpActivity, "Successful OTP verification", Toast.LENGTH_SHORT)
                    .show()
                toPasswordActivity()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                this@OtpActivity,
                "Unsuccessful OTP verification ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun toPasswordActivity() {
        val intent = Intent(this@OtpActivity, PasswordActivity::class.java)
        intent.putExtra(Constants.FIRST_NAME, firstName)
        intent.putExtra(Constants.LAST_NAME, lastName)
        intent.putExtra(Constants.PHONE_NUMBER, phoneNumber)
        intent.putExtra(Constants.PASSWORD, password)
        intent.putExtra(Constants.DATE_CREATED, dateCreated)

        if(isLoggedIn == true){
            intent.putExtra(Constants.IS_LOGGED_IN,true)
        } else intent.putExtra(Constants.IS_LOGGED_IN,false)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }


}