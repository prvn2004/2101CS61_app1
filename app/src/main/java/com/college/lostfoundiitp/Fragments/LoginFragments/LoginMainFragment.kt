package com.college.lostfoundiitp.Fragments.LoginFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.FragmentLoginMainBinding

class LoginMainFragment : Fragment()
{
    private lateinit var binding: FragmentLoginMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        binding = FragmentLoginMainBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.SignUpButton.setOnClickListener {
            showFragment(SignupFragment())
        }
        binding.signInButton.setOnClickListener {
            showFragment(SigninFragment())
        }


        return view  }

    private fun showFragment(fragment: Fragment) {
        val fram = activity?.supportFragmentManager?.beginTransaction()
        fram?.replace(R.id.login_container, fragment)
        fram?.addToBackStack(null)
        fram?.commit()
    }
}