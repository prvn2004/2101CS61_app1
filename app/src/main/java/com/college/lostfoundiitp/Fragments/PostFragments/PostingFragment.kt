package com.college.lostfoundiitp.Fragments.PostFragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.college.lostfoundiitp.DataFiles.AdsDataFile
import com.college.lostfoundiitp.Fragments.HomeFragments.MyadsFragment
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.FragmentPostingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class PostingFragment : Fragment() {
    private lateinit var binding: FragmentPostingBinding
    lateinit var ImageUri2: Uri

    private val PICK_IMG = 1
    private val ImageList = ArrayList<Uri>()
    private var uploads = 0
    private val progressDialog: ProgressDialog? = null
    var index = 0

    var AdType: String = ""
    var AdImgUri: HashMap<String, String> = HashMap()
    var AdEmail: String =""
    var AdPhone: String = ""
    var AdName: String = ""
    var AdRollno: String = ""
    var AdUid: String = ""
    var AdDescription: String = ""
    var AdId: String = ""
    var AdLocation: String = ""
    var AdTimestamp: String = ""
    var AdTime: String = ""
    var AdDate: String = ""
    var AdMessage: String = ""
    var AdDocumentId: String =""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentPostingBinding.inflate(inflater, container, false)
        val view = binding.root
        val pr = ""
        ImageUri2 = pr.toUri()

        allFunctionImplementation()

        binding.submit.setOnClickListener {
            checkfields()
        }

        return view
    }

    private fun allFunctionImplementation() {
        if (PostingFragment.isConnectionAvailable(requireActivity())) {
            getDate()
            getUserdetails()
            SelectImage()
            getTime()
            chipsclick()
        } else {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkfields() {
        settingfields()

        if (AdType.isEmpty() || AdDescription
                .isEmpty() || AdMessage.isEmpty() || AdLocation.isEmpty()
        ) {
            Toast.makeText(activity, " Fill empty fields ", Toast.LENGTH_LONG).show()
        } else {
            upload()
            postingData()
        }
    }

    private fun settingfields() {
        val text = binding.AdDescription.text.toString()
        AdDescription = text

        val message = binding.adDescription.text.toString()
        AdMessage = message

        val location = binding.AdLocation.text.toString()
        AdLocation = location

        val currentuser = FirebaseAuth.getInstance().currentUser!!.uid
        AdUid = currentuser

        val tsLong = System.currentTimeMillis()
        AdTimestamp = tsLong.toString()
        AdId = tsLong.toString()

    }

    private fun SelectImage() {
        binding.selectimages.setOnClickListener {
            ImageList.clear()
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, PICK_IMG)
        }
    }

    private fun chipsclick() {
        binding.Lostchip.setOnClickListener {

            binding.Lostchip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireActivity(), R.color.Red)
            )
            binding.Foundchip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireActivity(), R.color.LightGrey)
            )
            AdType = "LOST"
        }
        binding.Foundchip.setOnClickListener {
            binding.Foundchip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireActivity(), R.color.LightGreen)
            )
            binding.Lostchip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(requireActivity(), R.color.LightGrey)
            )
            AdType = "FOUND"

        }
    }

    private fun updateData(){
        val fireStoreDatabase = FirebaseFirestore.getInstance()
        val reference = fireStoreDatabase.collection("Ads").document("RecommandedAds").collection("AllAds")
            .document(AdDocumentId)
        reference.update("AdImgUri", AdImgUri)
    }

    private fun postingData(
    ) {
        val fireStoreDatabase = FirebaseFirestore.getInstance()

        val hashMap = hashMapOf<String, Any>(
            "AdType" to "$AdType",
            "AdImgUri" to AdImgUri,
            "AdEmail" to "$AdEmail",
            "AdPhone" to "$AdPhone",
            "AdName" to "$AdName",
            "AdRollno" to "$AdRollno",
            "AdUid" to "$AdUid",
            "AdDescription" to "$AdDescription",
            "AdId" to "$AdId",
            "AdLocation" to "$AdLocation",
            "AdTimestamp" to "$AdTimestamp",
            "AdTime" to "$AdTime",
            "AdDate" to "$AdDate"
        )

        fireStoreDatabase.collection("Ads").document("RecommandedAds").collection("AllAds")
            .add(hashMap)
            .addOnSuccessListener {
//                Log.d(TAG, "Added document with ID ${it.id}")
                AdDocumentId = it.id
                showFragment(MyadsFragment())
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error adding document $exception")
            }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 200 && resultCode == AppCompatActivity.RESULT_OK) {
//            ImageUri2 = data?.data!!
//            binding.imagetext.text = "1 image selected"
//        }
//    }
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data?.clipData != null) {
                    val count = data.clipData!!.itemCount
                    var CurrentImageSelect = 0
                    while (CurrentImageSelect < count) {
                        val imageuri = data.clipData!!.getItemAt(CurrentImageSelect).uri
                        ImageList.add(imageuri)
                        CurrentImageSelect = CurrentImageSelect + 1
                    }
                }
            }
        }
                binding.imagetext.text = "${ImageList.size} image selected"

}

    fun upload() {
        uploads = 0
        Log.d("titu", ImageList.size.toString())
        val size  = ImageList.size


        while (uploads < size) {
            val Image = ImageList[uploads]
           uploadImage(Image, uploads.toString())
            uploads++
            Log.d("titu", uploads.toString())
        }

    }

    fun showFragment(fragment: Fragment) {
        val fram = activity?.supportFragmentManager?.beginTransaction()
        fram?.replace(R.id.home_container
            , fragment)
        fram?.commit()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getTime() {
        binding.time.setOnClickListener {
            val mTimePicker: TimePickerDialog
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)

            mTimePicker = TimePickerDialog(activity, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    binding.time.setText(String.format("%d : %d", hourOfDay, minute))
                    AdTime = String.format("%d : %d", hourOfDay, minute)
                }
            }, hour, minute, false)

                mTimePicker.show()
        }
    }

    private fun uploadImage(Imageurl: Uri, i : String) {
        val currentuser = FirebaseAuth.getInstance().currentUser!!.uid
        AdsDataFile(AdUid = currentuser)

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val hihi = Imageurl.getLastPathSegment()
        val storageReference = FirebaseStorage.getInstance().getReference("Images/$currentuser/$hihi")

        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Uploading image wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageReference.putFile(Imageurl).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener() {
                val download: Uri = it
                val link = download.toString()
                AdImgUri.put(i, link)
                Log.d("hello", link)
                updateData()
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
            Toast.makeText(activity, "successfully uploaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(activity, "not uploaded", Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing) progressDialog.dismiss()
        }

    }

    private fun getUserdetails() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val Name = preferences.getString("Name", "Name").toString()
        val Phone = preferences.getString("Phone", "Phone").toString()
        val Rollno = preferences.getString("Rollno", "Rollno").toString()
        val Email = preferences.getString("Email", "Email").toString()


        AdName = Name
        AdPhone = Phone
        AdRollno = Rollno
        AdEmail = Email

        binding.NameInput.setText(Name)
        binding.PhoneNumberInput.setText(Phone)

        binding
    }

    private fun getDate() {
        val today = Calendar.getInstance()
        binding.datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)

        ) { view, year, month, day ->
            val month = month + 1
            val msg = "You Selected: $day/$month/$year"
            AdDate = "$day/$month/$year"
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun isConnectionAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && netInfo.isConnected && netInfo.isConnectedOrConnecting && netInfo.isAvailable) {
                    return true
                }
            }
            return false
        }
    }
}