package com.mkarshnas6.karenstudio.myfrance.data.remote.model

data class GetMyFavoriteResponse(
    val msg: String,
    val data: List<Map<String, String>>
)