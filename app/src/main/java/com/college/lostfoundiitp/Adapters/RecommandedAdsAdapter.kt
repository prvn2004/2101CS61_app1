package com.college.lostfoundiitp.Adapters

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build.VERSION_CODES.S
import android.preference.PreferenceManager
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.college.lostfoundiitp.DataFiles.AdsDataFile
import com.college.lostfoundiitp.R
import com.college.lostfoundiitp.databinding.RecommandedAdsListItemBinding
import com.google.android.gms.common.data.DataHolder


class RecommandedAdsAdapter(
    private var LinkList: ArrayList<AdsDataFile>
) :

    RecyclerView.Adapter<RecommandedAdsAdapter.MyViewHolder>() { //class  which will take prameter(list of strings)
// ------------------------------------------------------------------------------------------------------------------------------------------


    private lateinit var binding: RecommandedAdsListItemBinding


    fun updateList(list: ArrayList<AdsDataFile>) {
        LinkList = list
        notifyDataSetChanged()
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {


        binding =
            RecommandedAdsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return MyViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val Link = LinkList[position]
        holder.friction(Link, position, LinkList)

      val urlmap : HashMap<String, String> = LinkList.get(position).getImg()
       val url =  urlmap.get("0").toString()
        Log.d("url", url)

        binding.AdClaim.setOnClickListener {
//            val Name = LinkList.get(position).getName().toString()
//            val Phone = LinkList.get(position).getphone().toString()
            val type = LinkList.get(position).gettype().toString()
            val email = LinkList.get(position).getemail().toString()
            val description = LinkList.get(position).getDescription().toString()
            val date = LinkList.get(position).getDate().toString()
            val time = LinkList.get(position).getTime().toString()
            val preferences = PreferenceManager.getDefaultSharedPreferences(it.context)
            val Name = preferences.getString("Name", "")
            val Phone =  preferences.getString("Phone", "")
           val myemail =  preferences.getString("Email", "")
            val Rollno =  preferences.getString("Rollno", "")


            var text = ""
            var html = ""

            if (type == "LOST") {
                html =
                    " <h1> '  $description. <br> $date <br >$time  </h1> I have Found your Lost item. Please contact me here for details :<br> <h1> Name - $Name <br> Phone No. - $Phone <br> Email - $myemail <br> Rollno. - $Rollno</h1>  "
                text = " I have found your LOST item."
            } else {
                html =
                    " <h1>'  $description.' <br> $date <br >$time </h1> Hello, the item that you have found might've mine. Please contact me here for details :<br> <h1> Name - $Name <br> Phone No. - $Phone <br> Email - $myemail <br> Rollno. - $Rollno </h1> "
                text = "I am claiming your found item"
            }
            val send = Intent(Intent.ACTION_SENDTO)
            var uriText =
                "mailto:$email" + "?subject=$text" + "&body=" + Html.fromHtml(html)
            uriText = uriText.replace(" ", "%20")
            val uri: Uri = Uri.parse(uriText)
            send.data = uri
            it.context.startActivity(Intent.createChooser(send, "Send mail..."))
        }


        Glide.with(holder.itemView.context).load(url).placeholder(R.drawable.adtest_image)
            .into(binding.AdImage)

    }

    override fun getItemCount(): Int {
        return LinkList.size
    }

    class MyViewHolder(
        ItemViewBinding: RecommandedAdsListItemBinding,
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
                binding.AdClaim.text = "I FOUND IT"
                binding.tag.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.Red)
                )

            } else if (tag == "FOUND") {
                binding.AdClaim.text = "Claim It"
                binding.tag.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.LightGreen)
                )
            }

        }

    }
}
