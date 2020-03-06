package com.pakminseok.streetlife.navigation

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.pakminseok.streetlife.R
import com.pakminseok.streetlife.SpotDetailActivity
import com.pakminseok.streetlife.model.SpotDTO
import com.pakminseok.streetlife.model.StreetDTO
import kotlinx.android.synthetic.main.activity_add_spot.btn_add_photo
import kotlinx.android.synthetic.main.activity_add_spot.btn_upload
import kotlinx.android.synthetic.main.activity_add_street.*
import kotlinx.android.synthetic.main.activity_street_detail.*
import kotlinx.android.synthetic.main.spot_thumbnail.view.*
import java.text.SimpleDateFormat
import java.util.*

class AddStreetActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var spots : ArrayList<SpotDTO> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_street)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"

        rv_spot_thumbnail.adapter = SpotListRecycleViewAdapter()
        rv_spot_thumbnail.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        btn_add_photo.setOnClickListener{
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }
        btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                iv_street_photo.setImageURI(photoUri)
            }else{
                finish()
            }
        }
    }
    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
        var streetImageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(streetImageFileName)

        storageRef?.putFile(photoUri!!)?.continueWithTask{task : Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var streetDTO = StreetDTO()
            streetDTO.title = ev_street_title.text.toString()
            streetDTO.Review = ev_steet_review.text.toString()
            streetDTO.imageUrl = uri.toString()
            streetDTO.timestamp = System.currentTimeMillis()
            streetDTO.uid = auth?.currentUser?.uid
            streetDTO.userId = auth?.currentUser?.email
            streetDTO.spots = spots
            firestore?.collection("streets")?.document()?.set(streetDTO)
            setResult(Activity.RESULT_OK)
            Toast.makeText(this, getString(R.string.street_upload_success), Toast.LENGTH_LONG).show()
            for (spot in spots) {
                spot.isSelected = false
            }
            finish()
        }
    }

    inner class SpotListRecycleViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var spotDTOs: ArrayList<SpotDTO> = arrayListOf()

        init {
            firestore?.collection("spots")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    spotDTOs.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(SpotDTO::class.java)
                        spotDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.spot_thumbnail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return spotDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewholder = (holder as CustomViewHolder).itemView
            viewholder.spot_checkbox.setOnCheckedChangeListener(null)

            Glide.with(holder.itemView.context).load(spotDTOs!![position].imageUrl)
                .into(viewholder.spot_thumbnail_photo)
            viewholder.spot_thumbnail_title.text = spotDTOs!![position].title
            val latitude = spotDTOs!![position].geoPoint!!.latitude
            val longitude = spotDTOs!![position].geoPoint!!.longitude
            viewholder.spot_checkbox.isChecked = spotDTOs!![position].isSelected

            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            viewholder.spot_thumbnail_address.text =
                geocoder.getFromLocation(latitude, longitude, 1).get(0).getAddressLine(0).toString()

            viewholder.spot_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    spotDTOs!![position].isSelected = isChecked
                    spots.add(spotDTOs!![position])
                } else {
                    spotDTOs!![position].isSelected = isChecked
                    spots.remove(spotDTOs!![position])
                }
            }

            viewholder.setOnClickListener {
                val intent = Intent(applicationContext, SpotDetailActivity::class.java)
                intent.putExtra("spotImgID", spotDTOs!![position].imageUrl)
                applicationContext!!.startActivity(intent)
            }
        }
    }
}
