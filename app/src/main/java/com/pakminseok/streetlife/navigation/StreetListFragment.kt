package com.pakminseok.streetlife.navigation

import android.content.Intent
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
import com.pakminseok.streetlife.StreetDetailActivity
import com.pakminseok.streetlife.model.StreetDTO
import kotlinx.android.synthetic.main.fragment_street_list.view.*
import kotlinx.android.synthetic.main.street_thumbnail.view.*
import kotlin.collections.ArrayList

class StreetListFragment : Fragment() {

    var firestore : FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_street_list, container, false)

        firestore = FirebaseFirestore.getInstance()

        view.rv_street_thumbnail.adapter = StreetListRecycleViewAdapter()
        view.rv_street_thumbnail.layoutManager = LinearLayoutManager(activity)

        return view
    }
    inner class StreetListRecycleViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var streetDTOs : ArrayList<StreetDTO> = arrayListOf()

        init {
            firestore?.collection("streets")?.orderBy("timestamp")?.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                streetDTOs.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(StreetDTO::class.java)
                    streetDTOs.add(item!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.street_thumbnail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return streetDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewholder = (holder as CustomViewHolder).itemView

            Glide.with(holder.itemView.context).load(streetDTOs!![position].imageUrl).into(viewholder.street_thumbnail_photo)
            viewholder.street_thumbnail_title.text = streetDTOs!![position].title

            viewholder.setOnClickListener {
                val intent = Intent(context, StreetDetailActivity::class.java)
                intent.putExtra("streetImgID",streetDTOs!![position].imageUrl)
                context!!.startActivity(intent)
            }
        }
    }

}
