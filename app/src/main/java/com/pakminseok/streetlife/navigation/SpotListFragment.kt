package com.pakminseok.streetlife.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.pakminseok.streetlife.R
import com.pakminseok.streetlife.SpotDetailActivity
import com.pakminseok.streetlife.model.SpotDTO
import kotlinx.android.synthetic.main.fragment_detail_view.view.*
import kotlinx.android.synthetic.main.spot_thumbnail.view.*

class SpotListFragment : Fragment() {
    var firestore : FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail_view, container, false)

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
            var viewholder = (holder as CustomViewHolder).itemView

            Glide.with(holder.itemView.context).load(spotDTOs!![position].imageUrl).into(viewholder.spot_thumbnail_photo)
            viewholder.spot_thumbnail_title.text = spotDTOs!![position].title

            viewholder.setOnClickListener {
                val intent = Intent(context, SpotDetailActivity::class.java)
                intent.putExtra("spotImgID",spotDTOs!![position].imageUrl)
                context!!.startActivity(intent)
            }
        }

    }

}
