package com.pakminseok.streetlife.model

data class SpotDTO(
    var title : String? = null,
    var shortReview : String? = null,
    var imageUrl : String? = null,
    var uid : String? = null,
    var userId : String?= null,
    var timestamp :Long?=null,
    var favoriteCount : Int = 0,
    var favorites : Map<String, Boolean> = HashMap()
)
