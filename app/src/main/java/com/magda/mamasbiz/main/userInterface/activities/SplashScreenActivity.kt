package com.magda.mamasbiz.main.userInterface.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.ActivitySplashScreenBinding
import com.magda.mamasbiz.main.utils.SessionManager
import android.util.Pair as UtilPair

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
            toNextScreen()
        }, 3000)

    }

    private fun toNextScreen () {
        val sessionManager = SessionManager(this)
        if(sessionManager.isLoggedIn()){
            startActivity(Intent(this@SplashScreenActivity, DashboardActivity::class.java))
            finish()
        } else {
            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this,
                UtilPair.create(binding.ivImageLogo, "imageLogoTransition"),
                UtilPair.create(binding.ivTextLogo, "textLogoTransition"))
            startActivity(intent,options.toBundle())
            finish()
        }

    }
}