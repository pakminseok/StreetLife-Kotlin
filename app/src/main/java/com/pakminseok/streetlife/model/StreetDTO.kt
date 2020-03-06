package com.pakminseok.streetlife.model

data class StreetDTO(
    var title : String? = null,
    var Review : String? = null,
    var imageUrl : String? = null,
    var uid : String? = null,
    var userId : String?= null,
    var timestamp :Long?=null,
    var spots : ArrayList<SpotDTO> ?= null
)
