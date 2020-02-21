package com.pakminseok.streetlife.navigation

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.pakminseok.streetlife.R
import com.pakminseok.streetlife.model.SpotDTO
import kotlinx.android.synthetic.main.activity_add_spot.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddSpotActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null

    lateinit var address : Address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        val searchView = findViewById<SearchView>(R.id.sv_location)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.spot_add_map) as SupportMapFragment

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                var location : String? = query
                var addressList = mutableListOf<Address>()
                if(location != null || !location.equals(""))
                {
                    val geocoder = Geocoder(applicationContext, Locale.getDefault())
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e : IOException){
                        e.stackTrace
                    }
                    address = addressList[0]

                    mapFragment.getMapAsync {
                        var googleMap : GoogleMap = it

                        val latlng  = LatLng(address.latitude, address.longitude)

                        googleMap.clear()
                        googleMap.addMarker(MarkerOptions().position(latlng)).isDraggable = true
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
                    }
                }
                return false
            }
        })
        mapFragment.getMapAsync {
            var googleMap : GoogleMap = it

            googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
                override fun onMarkerDragStart(p0: Marker) {
                    //To change body of created functions use File | Settings | File Templates.
                }

                override fun onMarkerDrag(p0: Marker) {
                    //To change body of created functions use File | Settings | File Templates.
                }
                override fun onMarkerDragEnd(p0: Marker) {
                    val geocoder = Geocoder(applicationContext, Locale.getDefault())
                    var addressList : List<Address>
                    p0.isDraggable = true
                    try{
                        addressList = geocoder.getFromLocation(p0.position.latitude, p0.position.longitude, 1)
                        address = addressList[0]
                    } catch (e : IOException){
                        e.stackTrace
                    }
                }
            }
            )
        }
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
                iv_spot_photo.setImageURI(photoUri)
            }else{
                finish()
            }
        }
    }
    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
        var spotImageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(spotImageFileName)

        storageRef?.putFile(photoUri!!)?.continueWithTask{task : Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var spotDTO = SpotDTO()
            spotDTO.title = ev_spot_title.text.toString()
            spotDTO.shortReview = ev_spot_review.text.toString()
            spotDTO.imageUrl = uri.toString()
            spotDTO.timestamp = System.currentTimeMillis()
            spotDTO.geoPoint = GeoPoint(address.latitude, address.longitude)
            spotDTO.uid = auth?.currentUser?.uid
            spotDTO.userId = auth?.currentUser?.email

            firestore?.collection("spots")?.document()?.set(spotDTO)
            setResult(Activity.RESULT_OK)
            Toast.makeText(this, getString(R.string.spot_upload_success), Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
