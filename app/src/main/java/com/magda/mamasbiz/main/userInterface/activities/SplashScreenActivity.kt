package com.magda.mamasbiz.main.userInterface.activities

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Pair as UtilPair
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)


        //Animate the image view
        val cowAnimation = AnimationUtils.loadAnimation(this,R.anim.splash_animation)
        binding.ivImageLogo.animation = cowAnimation

        // To the next screen after a period of time
        Handler(Looper.getMainLooper()).postDelayed({
            toSignUpActivity()
        }, 3000)

    }

    private fun toSignUpActivity() {
        val intent = Intent(this@SplashScreenActivity, SignUpActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(this,
            UtilPair.create<View, String>(binding.ivImageLogo, "imageLogoTransition"),
            UtilPair.create<View,String>(binding.ivTextLogo, "textLogoTransition"))
        startActivity(intent,options.toBundle())
        finish()
    }
}