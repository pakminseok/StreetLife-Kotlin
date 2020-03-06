package com.pakminseok.streetlife.model

import com.google.firebase.firestore.GeoPoint

data class SpotDTO(
    var title : String? = null,
    var shortReview : String? = null,
    var imageUrl : String? = null,
    var uid : String? = null,
    var userId : String?= null,
    var timestamp :Long?=null,
    var geoPoint: GeoPoint? =null,
    var isSelected : Boolean = false
)
