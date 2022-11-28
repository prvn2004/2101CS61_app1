package com.college.lostfoundiitp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.college.lostfoundiitp.MainActivity
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.ActivitySplashscreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class splashscreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashscreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        actionBar?.hide()

        auth = Firebase.auth

        val currentuser = auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
                                       if (currentuser !=null)        {
                                           val intent = Intent(this, MainActivity::class.java)
                                           startActivity(intent)
                                           finish()
                                       }     else{
                                           val intent = Intent(this, LoginActivity::class.java)
                                           startActivity(intent)
                                           finish()
                                       }
        }, 1000)
    }
}