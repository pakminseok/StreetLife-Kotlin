package com.pakminseok.streetlife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.pakminseok.streetlife.model.StreetDTO
import kotlinx.android.synthetic.main.street_view.*


class StreetDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.street_view)

        val imageUrl = intent.getStringExtra("streetImgID")

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("streets").whereEqualTo("imageUrl", imageUrl).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            for (snapshot in querySnapshot!!.documents) {
                var curr_street = snapshot.toObject(StreetDTO::class.java)
                mViewPager.adapter = SlideAdapter(curr_street!!)
            }
        }
    }
}
