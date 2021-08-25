package com.magda.mamasbiz.main.userInterface.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.widget.Toast
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.ActivityDetailsBinding
import com.magda.mamasbiz.databinding.ActivityEditProductBinding
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.userInterface.fragments.CreditDebtPage2Fragment
import com.magda.mamasbiz.main.utils.Constants

class EditProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get intent extra from details activity
        val creditDebt =intent.getParcelableExtra<CreditDebt>(Constants.CREDIT_DEBT)


        // send arguments to the fragment
        if(creditDebt!=null){
           val fragment = CreditDebtPage2Fragment.newInstance(creditDebt)
            supportFragmentManager.beginTransaction().replace(R.id.fragment,fragment).addToBackStack(null).commit()

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}