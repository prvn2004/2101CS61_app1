package com.college.lostfoundiitp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.college.lostfoundiitp.Fragments.LoginFragments.LoginMainFragment
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)
        showFragment(LoginMainFragment())

    }

    fun showFragment(fragment: Fragment) {
        val fram = supportFragmentManager.beginTransaction()
        fram.replace(R.id.login_container
            , fragment)
        fram.commit()
    }
}