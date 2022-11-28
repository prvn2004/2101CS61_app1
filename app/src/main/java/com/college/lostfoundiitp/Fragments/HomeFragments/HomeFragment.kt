package com.college.lostfoundiitp.Fragments.HomeFragments

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.college.lostfoundiitp.Adapters.RecommandedAdsAdapter
import com.college.lostfoundiitp.DataFiles.AdsDataFile
import com.college.lostfoundiitp.DataFiles.UserDetails
import com.college.lostfoundiitp.Fragments.PostFragments.PostingFragment.Companion.isConnectionAvailable
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var RecommandedAdsLinkModel: ArrayList<AdsDataFile>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        RecommandedAdsLinkModel = arrayListOf<AdsDataFile>()

        allFunctionImplementation()

        return view
    }

    private fun allFunctionImplementation() {
        if (isConnectionAvailable(requireActivity())) {
            getRecommandedAds()
            chipsclick()
            getuserdetails()
        } else {
            setupAnim()
        }
    }



    private fun getuserdetails() {
        firestore = FirebaseFirestore.getInstance()

        val currentuser = FirebaseAuth.getInstance().currentUser!!.uid

        val collectionReference =
            firestore.collection("Users").document(currentuser)
        collectionReference.addSnapshotListener { value, error ->
            value?.toObject(UserDetails::class.java)
            val Name = value?.get("Name").toString()
            val Phone = value?.get("Phone").toString()
            val Rollno = value?.get("Rollno").toString()
            val Email = value?.get("Email").toString()
            val image = value?.get("profileImg").toString()

            Log.d("hey", value.toString())


            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = preferences.edit()
            editor.putString("Name", Name)
            editor.putString("Phone", Phone)
            editor.putString("Rollno", Rollno)
            editor.putString("Email", Email)
            editor.putString("profileImg", image)
            editor.apply()

            if (value == null || error != null) {
                Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
        }
    }

    private fun setupAnim() {
        binding.animationView.setAnimation(R.raw.no_internet)
        binding.wholeViewHome.visibility = View.GONE
        binding.animationView.repeatCount = LottieDrawable.INFINITE
        binding.animationView.playAnimation()
    }

    private fun chipsclick() {
        binding.Lostchip.setOnClickListener {
            binding.Warning.visibility = View.GONE

            binding.Lostchip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireActivity(), R.color.Red)
            )
            binding.Foundchip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireActivity(), R.color.LightGrey)
            )
            filter("LOST")

        }
        binding.Foundchip.setOnClickListener {
            binding.Warning.visibility = View.GONE

            binding.Foundchip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireActivity(), R.color.LightGreen)
            )
            binding.Lostchip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireActivity(), R.color.LightGrey)
            )
            filter("FOUND")
        }
    }

    private fun filter(text: String?) {
        val temp: ArrayList<AdsDataFile> = ArrayList<AdsDataFile>()
        for (d in RecommandedAdsLinkModel) {
            if (d.gettype() == text.toString()) {
                temp.add(d)
            }
        }
        if (temp.isEmpty()) {
            binding.Warning.visibility = View.VISIBLE
        }
        RecommandedAdsAdapter(temp)
        recyclerView.adapter = RecommandedAdsAdapter(temp)

    }

    private fun emptylist(){
        binding.animationView.setAnimation(R.raw.no_data)
        binding.wholeViewHome.visibility = View.GONE
        binding.animationView.repeatCount = LottieDrawable.INFINITE
        binding.animationView.playAnimation()
    }

    private fun getRecommandedAds() {
        val mFrameLayout = binding.recommandedshimmerLayout
        mFrameLayout.startShimmer()

        val tsLong = System.currentTimeMillis()
        val ts = tsLong / 1000

        firestore = FirebaseFirestore.getInstance()
        val collectionReference =
            firestore.collection("Ads").document("RecommandedAds").collection("AllAds").orderBy("AdTimestamp", Query.Direction.DESCENDING).limit(25)

        collectionReference.addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (value.isEmpty){
               emptylist()
            }
//            Log.d("DATA", value.toObjects(AdsDataFile::class.java).toString())
            RecommandedAdsLinkModel.clear()
            RecommandedAdsLinkModel.addAll(value.toObjects(AdsDataFile::class.java))
            recyclerView = binding.recommendationRecyclerview
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = RecommandedAdsAdapter(RecommandedAdsLinkModel)



            mFrameLayout.stopShimmer()
            mFrameLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

        }
    }

    companion object {
        fun isConnectionAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && netInfo.isConnected
                    && netInfo.isConnectedOrConnecting
                    && netInfo.isAvailable
                ) {
                    return true
                }
            }
            return false
        }
    }
}