package com.pakminseok.streetlife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.pakminseok.streetlife.model.SpotDTO
import kotlinx.android.synthetic.main.activity_spot_detail.*
import com.google.android.gms.maps.CameraUpdateFactory
import android.location.Geocoder
import java.util.*


class SpotDetailActivity : AppCompatActivity() {

    private  var latitude : Double = 0.0
    private  var longitude : Double = 0.0

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

                latitude = curr_spot.geoPoint!!.latitude
                longitude = curr_spot.geoPoint!!.longitude

                val geocoder = Geocoder(this, Locale.getDefault())
                spot_detail_address.text = geocoder.getFromLocation(latitude, longitude, 1)[0].getAddressLine(0).toString()
            }
        }


        var mapFragment = supportFragmentManager.findFragmentById(R.id.spot_detail_map) as SupportMapFragment
        btn_spot_detail_map.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                spot_detail_photo.visibility = View.GONE// The toggle is enabled
                map_layout.visibility = View.VISIBLE
                mapFragment.getMapAsync {
                    var googleMap : GoogleMap = it

                    val location = LatLng(latitude, longitude)
                    googleMap.addMarker(MarkerOptions().position(location))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            } else {
                map_layout.visibility = View.GONE
                spot_detail_photo.visibility = View.VISIBLE// The toggle is disabled
            }
        }
    }
}
