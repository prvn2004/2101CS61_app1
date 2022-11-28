package com.college.lostfoundiitp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import com.college.lostfoundiitp.Fragments.HomeFragments.HomeFragment
import com.college.lostfoundiitp.Fragments.HomeFragments.MyadsFragment
import com.college.lostfoundiitp.Fragments.HomeFragments.ProfileFragment
import com.college.lostfoundiitp.Fragments.PostFragments.PostingFragment
import com.college.lostfoundiitp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        actionBar?.hide()
        binding.bottomNavigationView.background = null

        binding.bottomAppBar

        setFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setFragment(HomeFragment())

                R.id.MyStore -> setFragment(MyadsFragment())

                R.id.Account -> setFragment(ProfileFragment())
            }
            true
        }

        binding.postbutton.setOnClickListener {
            replaceFragment(PostingFragment())
        }
    }

    private fun setFragment(fragment: Fragment) {
       val fram =  supportFragmentManager.beginTransaction()
            fram.replace(R.id.home_container, fragment)
            fram.commit()
        }
    private fun replaceFragment(fragment: Fragment) {
        val fram =  supportFragmentManager.beginTransaction()
        fram.replace(R.id.home_container, fragment)
        fram.addToBackStack(null)
        fram.commit()
    }
    }