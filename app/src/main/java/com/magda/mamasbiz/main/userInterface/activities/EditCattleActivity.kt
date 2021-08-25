package com.magda.mamasbiz.main.userInterface.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.ActivityEditCattleBinding
import com.magda.mamasbiz.main.data.entity.CattleBought
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.userInterface.fragments.CreditDebtPageFragment
import com.magda.mamasbiz.main.utils.Constants

class EditCattleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCattleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCattleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get intent extra from details activity
        val creditDebt =intent.getParcelableExtra<CreditDebt>(Constants.CREDIT_DEBT)
        // send arguments to the fragment
        if(creditDebt!=null){
            val fragment = CreditDebtPageFragment.newInstance(creditDebt)
            supportFragmentManager.beginTransaction().replace(R.id.fragment,fragment).addToBackStack(null).commit()

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}