package com.college.lostfoundiitp.Adapters

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.college.lostfoundiitp.DataFiles.AdsDataFile
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.MyAdsListItemBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MyAdsAdapter(
    private var LinkList: ArrayList<AdsDataFile>
) :

    RecyclerView.Adapter<MyAdsAdapter.MyViewHolder>() { //class  which will take prameter(list of strings)
// ------------------------------------------------------------------------------------------------------------------------------------------

    private lateinit var binding: MyAdsListItemBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage

    //-------------------------------------------------------------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyAdsAdapter.MyViewHolder {

        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()


        binding =
            MyAdsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return MyAdsAdapter.MyViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(
        holder: MyAdsAdapter.MyViewHolder,
        position: Int
    ) {
        val Link = LinkList[position]
        holder.friction(Link, position, LinkList)

        val urlmap : HashMap<String, String> = LinkList.get(position).getImg()
       val url =  urlmap.get("0").toString()
        Log.d("url", url)

        Glide.with(holder.itemView.context).load(url).placeholder(R.drawable.adtest_image)
            .into(binding.AdImage)

        val myid = LinkList.get(position).getId().toString()

        binding.menubutton.setOnClickListener {
            showDialog(myid, holder.itemView.context, url, urlmap)
        }

    }

    override fun getItemCount(): Int {
        return LinkList.size
    }

    private fun showDialog(myid: String, newcontext: Context, imageurl: String, imagemap: HashMap<String, String>) {
        val dialog = Dialog(newcontext)

        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow()?.getAttributes()!!.windowAnimations = R.style.animation;

        dialog.show();
        val Deletebutton: Button = dialog.findViewById(R.id.button)
        val EditButton: Button = dialog.findViewById(R.id.editbutton)

        val description: TextView = dialog.findViewById(R.id.editdescription)
        val location: TextView = dialog.findViewById(R.id.editlocation)

        Deletebutton.setOnClickListener {
            deletedoc(myid, newcontext, imageurl, imagemap)
            dialog.cancel()
        }

        EditButton.setOnClickListener {
            val text = description.text.toString()
            val loc = location.text.toString()

            editdoc(myid, newcontext, imageurl, text, loc)
            dialog.cancel()

        }
    }

    private fun deletedoc(myid: String, newcontext: Context, imageur: String, imagemap: HashMap<String, String>) {
        var upload = 0
        while (upload < imagemap.size) {
            val imageurl = imagemap.get("$upload").toString()
            val reference =
                firestore.collection("Ads").document("RecommandedAds").collection("AllAds")
            val photoRef: StorageReference = firebaseStorage.getReferenceFromUrl(imageurl)
            photoRef.delete()
            reference.document(myid).delete()
            upload++
        }
    }

    private fun editdoc(
        myid: String,
        newcontext: Context,
        imageurl: String,
        descr: String,
        loc: String
    ) {
        val reference = firestore.collection("Ads").document("RecommandedAds").collection("AllAds")
            .document(myid)
        if (descr.isNotEmpty()) {
            reference.update("AdDescription", descr)
        }
        if (loc.isNotEmpty()) {
            reference.update("AdLocation", loc)
        }


    }


    class MyViewHolder(
        ItemViewBinding: MyAdsListItemBinding,
    ) :
        RecyclerView.ViewHolder(ItemViewBinding.root) {

        private val binding = ItemViewBinding
        fun friction(Link: AdsDataFile, position: Int, list: ArrayList<AdsDataFile>) {

            val context = itemView.getContext()

            binding.AdName.text = Link.AdName.toString()
            binding.AdDescription.text = Link.getDescription().toString()
            binding.AdPhoneno.text = Link.getphone().toString()
            binding.tag.text = Link.gettype().toString()

            val tag = Link.gettype().toString()
            if (tag == "LOST") {
                binding.tag.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.Red)
                )

            } else if (tag == "FOUND") {
                binding.tag.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.LightGreen)
                )
            }

        }

    }
}
