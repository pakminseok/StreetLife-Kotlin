package com.pakminseok.streetlife

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.pakminseok.streetlife.model.SpotDTO
import kotlinx.android.synthetic.main.activity_spot_detail.*

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
    }
}
