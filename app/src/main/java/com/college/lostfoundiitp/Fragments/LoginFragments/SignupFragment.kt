package com.college.lostfoundiitp.Fragments.LoginFragments

import android.app.ProgressDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        val view = binding.root
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        binding.SignupMainButton.setOnClickListener {
            fieldchecker()
        }

        binding.backToMainloginFragment.setOnClickListener {
            showFragment(LoginMainFragment())
        }
        onback()

        return view
    }

    private fun onback() {
        activity?.onBackPressedDispatcher?.addCallback(
            this.requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showDialog()
                }
            })
    }

    private fun showDialog() {
        showFragment(LoginMainFragment())
    }

    private fun fieldchecker() {
        val Name = binding.NameInput.text.toString().trim()
        val Email = binding.EmailAddressInput.text.toString().trim()
        val Password = binding.PasswordInput.text.toString().trim()
        val MobileNo = binding.PhoneNumberInput.text.toString().trim()
        val Rollno = binding.RollnoInput.text.toString().trim()

        if (Name.isEmpty() || Email.isEmpty() || Password.isEmpty() || MobileNo.isEmpty() || Rollno.isEmpty()) {
            Toast.makeText(
                activity,
                "fill empty field",
                Toast.LENGTH_SHORT
            ).show()
            if (Email.contains("@iitp.ac.in")) {

            } else {
                binding.EmailWarning.visibility = View.VISIBLE;
            }
        } else {
            if (Email.contains("@iitp.ac.in")) {
                Register(Name, MobileNo, Email, Password, Rollno)
            } else {
                binding.EmailWarning.visibility = View.VISIBLE;
            }
        }
    }

    private fun Register(
        Name: String,
        Phone: String,
        email: String,
        password: String,
        rollno: String
    ) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Loading")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                auth.currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener { task ->
                        val user = auth.currentUser
                        if (task.isSuccessful) {
                            if (progressDialog.isShowing) progressDialog.dismiss()

                            Toast.makeText(activity, "Email is sent", Toast.LENGTH_SHORT)
                                .show()
                            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                            WriteNewUser(Name, email, uid, Phone, rollno)
                            showFragment(SigninFragment())

                        } else {
                            if (progressDialog.isShowing) progressDialog.dismiss()

                            Toast.makeText(
                                activity, "failed to create an account.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(activity, "already an account", Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) progressDialog.dismiss()

            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        val fram = activity?.supportFragmentManager?.beginTransaction()
        fram?.replace(R.id.login_container, fragment)
        fram?.addToBackStack(null)
        fram?.commit()
    }


    private fun WriteNewUser(
        personName: String,
        personEmail: String,
        uid: String,
        personPhone: String,
        personRollno: String
    ) {
        val fireStoreDatabase = FirebaseFirestore.getInstance()

        val hashMap = hashMapOf<String, Any>(
            "Name" to "$personName",
            "Email" to "$personEmail",
            "Phone" to "$personPhone",
            "Rollno" to "$personRollno"
        )

        fireStoreDatabase.collection("Users").document(uid)
            .set(hashMap)
            .addOnSuccessListener {
//                Log.d(TAG, "Added document with ID ${it.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error adding document $exception")
            }
    }

}