package com.magda.mamasbiz.main.userInterface.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.review.ReviewManagerFactory
import com.magda.mamasbiz.R
import com.magda.mamasbiz.databinding.FragmentAccountBinding
import com.magda.mamasbiz.main.businessLogic.viewModels.UserViewModel
import com.magda.mamasbiz.main.userInterface.activities.LoginActivity
import com.magda.mamasbiz.main.utils.SessionManager
import com.magda.mamasbiz.main.utils.Status


class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private val _binding get() = binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)

        sessionManager = SessionManager(requireContext())

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        initViews()

        initLogoutResponse()


        binding.apply {
            tvTellAFriend.setOnClickListener { toTellAFriend() }
            tvRateApp.setOnClickListener { toRateTheApp() }
            tvReportIssue.setOnClickListener { toReportIssue() }
            tvLogOut.setOnClickListener { toLogOut() }
        }
        return _binding.root
    }

    private fun initLogoutResponse() {
        userViewModel._genericUserResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    sessionManager.clearInfo()
                    Toast.makeText(requireContext(), "Successfully logged out!", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                Status.LOADING -> {
                    //Method sub
                }
            }
        }
    }


    private fun toLogOut() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setIcon(R.drawable.ic_baseline_delete_outline_24)
            .setTitle("Log Out")
            .setMessage("Do you want to log out?")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ -> userViewModel.logOut() }
        builder.show()

    }

    private fun toReportIssue() {
        val emailAddress = "mbuguamagdaline@gmail.com"
        val subject = "Mama's Biz Reported Issue"
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Send email via:"))

        } else Toast.makeText(requireContext(), "Cannot access your email", Toast.LENGTH_SHORT)
            .show()

    }

    private fun toRateTheApp() {
        val manager = ReviewManagerFactory.create(requireContext())
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task->
                if(task.isSuccessful){
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                }
            }


    }



    private fun toTellAFriend() {
        val message =
            "Hey there.You have been invited to Mama's Biz. Mama's Biz helps you to manage " +
                    "your livestock business. Click here to download."
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(intent, "Share via:"))


    }

    private fun initViews() {

        val user = sessionManager.getUser()
        val fullName = "${user.firstName} ${user.lastName}"
        val phoneNumber = "+254${user.userId}"
        binding.apply {
            tvUserName.text = fullName
            tvUserPhoneNumber.text = phoneNumber
        }


    }


}