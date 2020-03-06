package com.pakminseok.streetlife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.pakminseok.streetlife.model.StreetDTO
import kotlinx.android.synthetic.main.activity_street_detail.*


class StreetDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_detail)

        val imageUrl = intent.getStringExtra("streetImgID")

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("streets").whereEqualTo("imageUrl", imageUrl).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            for (snapshot in querySnapshot!!.documents) {
                var curr_street = snapshot.toObject(StreetDTO::class.java)
                Glide.with(this).load(curr_street!!.imageUrl).into(street_detail_photo)
                street_detail_title.text = curr_street!!.title
                street_detail_review.text = curr_street!!.Review
            }
        }
    }
}
