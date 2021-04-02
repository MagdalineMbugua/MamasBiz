package com.magda.mamasbiz.main.userInterface.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.ActivityDashboardBinding
import com.magda.mamasbiz.main.userInterface.fragments.SupplierFragment
import com.magda.mamasbiz.main.utils.UpdateFragment

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController : NavController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.dashboardBottomNav,navController)



    }

}