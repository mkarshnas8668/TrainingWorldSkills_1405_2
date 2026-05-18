package com.mkarshnas6.karenstudio.myfrance.data.remote.model

data class AllDiariesResponse(
    val msg : String,
    val data: List<Map<String, String>>
)