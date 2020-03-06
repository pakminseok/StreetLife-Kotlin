package com.pakminseok.streetlife.navigation

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.pakminseok.streetlife.R
import com.pakminseok.streetlife.SpotDetailActivity
import com.pakminseok.streetlife.model.SpotDTO
import kotlinx.android.synthetic.main.fragment_spot_list.view.*
import kotlinx.android.synthetic.main.spot_thumbnail.view.*
import java.util.*
import kotlin.collections.ArrayList

class SpotListFragment : Fragment() {
    var firestore : FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_spot_list, container, false)

        firestore = FirebaseFirestore.getInstance()

        view.rv_spot_thumbnail.adapter = DetailViewRecycleViewAdapter()
        view.rv_spot_thumbnail.layoutManager = LinearLayoutManager(activity)

        return view
    }
    inner class DetailViewRecycleViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var spotDTOs : ArrayList<SpotDTO> = arrayListOf()

        init {
            firestore?.collection("spots")?.orderBy("timestamp")?.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                spotDTOs.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(SpotDTO::class.java)
                    spotDTOs.add(item!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.spot_thumbnail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return spotDTOs.size
       }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewholder = (holder as CustomViewHolder).itemView
            viewholder.spot_checkbox.setVisibility(View.GONE)

            Glide.with(holder.itemView.context).load(spotDTOs!![position].imageUrl).into(viewholder.spot_thumbnail_photo)
            viewholder.spot_thumbnail_title.text = spotDTOs!![position].title
            val latitude = spotDTOs!![position].geoPoint!!.latitude
            val longitude = spotDTOs!![position].geoPoint!!.longitude

            val geocoder = Geocoder(context, Locale.getDefault())
            viewholder.spot_thumbnail_address.text = geocoder.getFromLocation(latitude, longitude, 1).get(0).getAddressLine(0).toString()


            viewholder.setOnClickListener {
                val intent = Intent(context, SpotDetailActivity::class.java)
                intent.putExtra("spotImgID",spotDTOs!![position].imageUrl)
                context!!.startActivity(intent)
            }
        }
    }

}
