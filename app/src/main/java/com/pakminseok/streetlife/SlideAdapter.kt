package com.pakminseok.streetlife

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pakminseok.streetlife.model.SpotDTO
import com.pakminseok.streetlife.model.StreetDTO
import kotlinx.android.synthetic.main.activity_slide_adapter.view.*
import kotlinx.android.synthetic.main.activity_spot_detail.*
import kotlinx.android.synthetic.main.activity_spot_detail.view.*
import kotlinx.android.synthetic.main.activity_spot_detail.view.btn_spot_detail_map
import kotlinx.android.synthetic.main.activity_spot_detail.view.spot_detail_address
import kotlinx.android.synthetic.main.activity_spot_detail.view.spot_detail_photo
import kotlinx.android.synthetic.main.activity_spot_detail.view.spot_detail_short_review
import kotlinx.android.synthetic.main.activity_spot_detail.view.spot_detail_title
import kotlinx.android.synthetic.main.activity_street_detail.view.*
import java.util.*
import kotlin.collections.ArrayList

class SlideAdapter(val street : StreetDTO) : PagerAdapter() {
    private  var latitude : Double = 0.0
    private  var longitude : Double = 0.0
    private val spotList : ArrayList<SpotDTO> = street.spots!!
    private lateinit var view : View

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        if(position == 0)
        {
            view = inflater.inflate(R.layout.activity_street_detail, container, false)
            Glide.with(container.context).load(street!!.imageUrl).into(view.street_detail_photo)
            view.street_detail_title.text = street!!.title
            view.street_detail_review.text = street!!.Review
        }else{
            view = inflater.inflate(R.layout.activity_slide_adapter, container, false)
            view.spot_detail_title.text = spotList[position-1].title
            view.spot_detail_short_review.text = spotList[position-1].shortReview
            Glide.with(container.context).load(spotList[position-1].imageUrl).into(view.spot_detail_photo)
            latitude = spotList[position-1].geoPoint!!.latitude
            longitude = spotList[position-1].geoPoint!!.longitude

            val geocoder = Geocoder(container.context, Locale.getDefault())
            view.spot_detail_address.text = geocoder.getFromLocation(latitude, longitude, 1)[0].getAddressLine(0).toString()

            /*
            view.btn_spot_detail_map.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    view.spot_detail_photo!!.visibility = View.GONE// The toggle is enabled
                    view.spot_detail_map.visibility = View.VISIBLE
                    view.spot_detail_map.getMapAsync {
                        var googleMap : GoogleMap = it

                        val location = LatLng(latitude, longitude)
                        googleMap.addMarker(MarkerOptions().position(location))
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                    }
                } else {
                    view.spot_detail_map.visibility = View.GONE
                    view.spot_detail_photo!!.visibility = View.VISIBLE// The toggle is disabled
                }
            }*/
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View?)
    }

    override fun getCount(): Int {
        return spotList.size +1
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }
}
