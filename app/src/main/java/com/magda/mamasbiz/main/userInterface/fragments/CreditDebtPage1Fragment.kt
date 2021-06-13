package com.magda.mamasbiz.main.userInterface.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentCreditDebtPage1Binding
import com.magda.mamasbiz.main.userInterface.activities.DashboardActivity
import com.magda.mamasbiz.main.utils.Constants
import java.util.*


class CreditDebtPage1Fragment : Fragment() {
    private lateinit var _binding: FragmentCreditDebtPage1Binding
    private val binding get() = _binding!!
    private lateinit var status: String
    private  var credit: String? =""
    private  var debt: String? = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreditDebtPage1Binding.inflate(inflater, container, false)

        creditOrDebtFragment()

        setSpinner()

        binding.nextLayout.tvNext.setOnClickListener { checkData() }
        binding.nextLayout.tvBack.setOnClickListener { toPreviousPage() }
        return binding.root
    }

    private fun creditOrDebtFragment() {
        val intent = requireActivity().intent
        if (intent != null) {
           credit = intent.getStringExtra(Constants.CREDIT)
           debt= intent.getStringExtra(Constants.DEBT)
            toAlterCreditWords(credit, debt)
        }

    }

    private fun toAlterCreditWords(credit: String?, debt: String?) {
        if (credit != null && debt == null) {
            val name = resources.getString(R.string.name_of_the_creditor)
            val phoneNumber = resources.getString(R.string.phone_number_of_the_creditor_optional)
            binding.apply {
                tvName.text = name
                tvPhone.text = phoneNumber
            }
        }
    }

    private fun toPreviousPage() {
        startActivity(Intent(requireActivity(), DashboardActivity::class.java))

    }

    private fun setSpinner() {
        val statusAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.status, R.layout.textview_spinner
        )
        statusAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        with(binding.statusSPinner) {
            adapter = statusAdapter
            setSelection(0, false)
            prompt = "Select the status of payment"
            gravity = Gravity.CENTER
        }

    }

    private fun checkData() {
        binding.apply {
            status = if (statusSPinner.selectedItemPosition == 0) {
                ""
            } else statusSPinner.selectedItem.toString().trim().toLowerCase(Locale.ROOT)
            val name = etName.text.toString().toLowerCase(Locale.ROOT).trim()
            var phoneNumber: String? = etPhone.text.toString().toLowerCase(Locale.ROOT).trim()
            if (phoneNumber.isNullOrEmpty()) phoneNumber = "+254 XXX XXX XXX"

            if (name.isNotEmpty()) {
                if (status.isNotEmpty()) {
                    toTheNextPage(name, phoneNumber, status)


                } else Toast.makeText(requireActivity(), "Select status", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireActivity(), "Fill in the name", Toast.LENGTH_SHORT).show()
                etName.requestFocus()
            }
        }
    }

    private fun toTheNextPage(name: String, phoneNumber: String?, status: String) {
        val navController = Navigation.findNavController(binding.root)
        val arg = Bundle()
        arg.putString(Constants.DEBTOR_NAME, name)
        arg.putString(Constants.DEBTOR_NUMBER, phoneNumber)
        arg.putString(Constants.DEBTOR_STATUS, status)
        if(credit!=null){
            arg.putString(Constants.CREDIT, credit)
            navController.navigate(R.id.action_creditPage1Fragment_to_creditDebtPageFragment, arg)
        } else if (debt!=null){
            arg.putString(Constants.DEBT, debt)
            navController.navigate(R.id.action_creditPage1Fragment_to_creditPage2Fragment, arg)
        }
        Log.d(TAG, "toTheNextPage: $name $phoneNumber $status")



    }


}