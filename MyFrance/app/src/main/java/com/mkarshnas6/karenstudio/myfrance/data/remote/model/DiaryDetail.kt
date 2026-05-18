package com.mkarshnas6.karenstudio.myfrance.data.remote.model

data class DiaryDetailResponse(
    val msg: String,
    val data: DiaryDetail
)

data class DiaryDetail(
    val diary_id: String,
    val title: String,
    val publisher_username: String,
    val thumbnail: String,
    val main_text: List<DiaryContent>,
    val publish_datetime: String,
    val images: List<String>
)

data class DiaryContent(
    val type: String,
    val content: String? = null,
    val src: String? = null
)
