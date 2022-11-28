package com.college.lostfoundiitp.Fragments.HomeFragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.college.lostfoundiitp.Adapters.MyAdsAdapter
import com.college.lostfoundiitp.DataFiles.AdsDataFile
import com.college.lostfoundiitp.MainActivity
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.FragmentMyadsBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class MyadsFragment : Fragment() {
    private lateinit var binding: FragmentMyadsBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var RecommandedAdsLinkModel: ArrayList<AdsDataFile>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyadsBinding.inflate(inflater, container, false)
        val view = binding.root
        RecommandedAdsLinkModel = arrayListOf<AdsDataFile>()

        allFunctionImplementation()



        return view
    }

    private fun allFunctionImplementation() {
        if (isConnectionAvailable(requireActivity())) {
            getRecommandedAds()
        } else {
            setupAnim()
        }
    }

    private fun setupAnim() {
        binding.animationView.setAnimation(R.raw.no_internet)
        binding.wholeViewHome.visibility = View.GONE
        binding.animationView.repeatCount = LottieDrawable.INFINITE
        binding.animationView.playAnimation()
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

        val currentuser = FirebaseAuth.getInstance().currentUser!!.uid.toString()

        firestore = FirebaseFirestore.getInstance()
        val collectionReference =
            firestore.collection("Ads").document("RecommandedAds").collection("AllAds").whereEqualTo("AdUid", currentuser).orderBy("AdTimestamp", Query.Direction.DESCENDING).limit(25)

        collectionReference.addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (value.isEmpty){
                emptylist()
            }
            Log.d("DATA", value.toObjects(AdsDataFile::class.java).toString())
            RecommandedAdsLinkModel.clear()
            RecommandedAdsLinkModel.addAll(value.toObjects(AdsDataFile::class.java))
            recyclerView = binding.recommendationRecyclerview
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = MyAdsAdapter(RecommandedAdsLinkModel)



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

