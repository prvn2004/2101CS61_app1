package com.college.lostfoundiitp.Fragments.LoginFragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.college.lostfoundiitp.MainActivity
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.FragmentSigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SigninFragment : Fragment() {
    private lateinit var binding: FragmentSigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSigninBinding.inflate(inflater, container, false)
        val view = binding.root
        auth = Firebase.auth

        binding.LoginMainButton.setOnClickListener {
            fieldCheck()
        }

        binding.backToMainloginFragment.setOnClickListener {
            showFragment(LoginMainFragment())
        }

        return view
    }

    private fun showFragment(fragment: Fragment) {
        val fram = activity?.supportFragmentManager?.beginTransaction()
        fram?.replace(R.id.login_container, fragment)
        fram?.addToBackStack(null)
        fram?.commit()
    }

    private fun fieldCheck() {
        val email =
            binding.EmailAddressInput.text.toString().trim()
        val password =
            binding.PasswordInput.text.toString().trim()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "enter email and password", Toast.LENGTH_SHORT).show()
        } else {
            if (email.contains("@iitp.ac.in")) {
                signIn(
                    email,
                    password
                )
            } else {
                binding.EmailWarning.visibility = View.VISIBLE
            }
        }

    }

    private fun savePreferance() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = preferences.edit()
        editor.putString("login", "Login")
        editor.apply()
    }

    private fun updateUI() {
        if (auth.currentUser != null) {
            if (auth.currentUser!!.isEmailVerified) {
                savePreferance()
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(activity, "Email not verified", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn(email: String, password: String) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Loading")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    updateUI()
                    if (progressDialog.isShowing) progressDialog.dismiss()
                } else {
                    Toast.makeText(
                        activity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (progressDialog.isShowing) progressDialog.dismiss()

                    return@addOnCompleteListener
                }
            }
    }
}