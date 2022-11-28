package com.college.lostfoundiitp.Fragments.HomeFragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.college.lostfoundiitp.Activities.LoginActivity
import com.college.lostfoundiitp.DataFiles.UserDetails
import com.college.lostfoundiitp.databinding.FragmentProfileBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.math.sign

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private val PICK_IMG = 1
    lateinit var ImageUri2: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        auth = FirebaseAuth.getInstance()

        SelectImage()
        getimage()

        binding.logoutContainer.setOnClickListener {
            showDialoglogout()
        }

        binding.changeContainer.setOnClickListener {
            showDialogforgot()
        }

        return view

    }

    private fun showDialogforgot() {
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setTitle("Change Password")
        dialogBuilder.setMessage("If you change password, you will logout out of app")
            .setCancelable(false)
            .setPositiveButton("ok", DialogInterface.OnClickListener { dialog, id ->
               forgotpass()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.show()
    }
    private fun showDialoglogout() {
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setTitle("Log Out")

        dialogBuilder.setMessage("Do you want to Log out")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                signout()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun getimage(){
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val link = preferences.getString("profileImg", "profile")
        Glide.with(this).load(link).centerCrop().into(binding.profileimage)
    }

    private fun signout(){
        auth.signOut()

        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun forgotpass(){
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
       val email =  preferences.getString("Email", "")
        FirebaseAuth.getInstance().sendPasswordResetEmail(email.toString())
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                override fun onComplete(@NonNull task: Task<Void?>) {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity, "Email is sent", Toast.LENGTH_LONG).show()
                        signout()
                    }
                }
            })
    }

    private fun SelectImage() {
        binding.uploadimage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, 200)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == AppCompatActivity.RESULT_OK) {
            ImageUri2 = data?.data!!
            uploadImage(ImageUri2)
        }
    }
    private fun updateData(url: String){
        val currentuser = FirebaseAuth.getInstance().currentUser!!.uid

        val fireStoreDatabase = FirebaseFirestore.getInstance()
        val reference = fireStoreDatabase.collection("Users").document(currentuser)
        reference.update("profileImg", url)
    }

    private fun uploadImage(Imageurl: Uri) {
        val currentuser = FirebaseAuth.getInstance().currentUser!!.uid

        val storageReference = FirebaseStorage.getInstance().getReference("Images/$currentuser/profile")

        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Uploading image wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageReference.putFile(Imageurl).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener() {
                val download: Uri = it
                val link = download.toString()
                Log.d("hello", link)
                updateData(link)
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
            Toast.makeText(activity, "successfully uploaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(activity, "not uploaded", Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing) progressDialog.dismiss()
        }

    }

}