package com.pakminseok.streetlife

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.pakminseok.streetlife.model.SpotDTO
import kotlinx.android.synthetic.main.activity_spot_detail.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class SpotDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_detail)

        val imageUrl = intent.getStringExtra("spotImgID")

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("spots").whereEqualTo("imageUrl", imageUrl).addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
            for(snapshot in querySnapshot!!.documents) {
                var curr_spot = snapshot.toObject(SpotDTO::class.java)
                Glide.with(this).load(curr_spot!!.imageUrl).into(spot_detail_photo)
                spot_detail_title.text = curr_spot.title
                spot_detail_short_review.text = curr_spot.shortReview
            }
        }

        var mapFragment = supportFragmentManager.findFragmentById(R.id.spot_detail_map) as SupportMapFragment
        btn_spot_detail_map.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                spot_detail_photo.visibility = View.GONE// The toggle is enabled
                map_layout.visibility = View.VISIBLE
                mapFragment.getMapAsync {
                    var googleMap : GoogleMap = it

                    val location = LatLng(13.03, 77.60)
                    googleMap.addMarker(MarkerOptions().position(location))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
                }
            } else {
                map_layout.visibility = View.GONE
                spot_detail_photo.visibility = View.VISIBLE// The toggle is disabled
            }
        }
    }
}
