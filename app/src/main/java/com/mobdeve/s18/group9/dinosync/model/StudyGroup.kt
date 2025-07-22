package com.mobdeve.s18.group9.dinosync.model

data class StudyGroup(
    val groupId: String = "",
    val bio: String = "",
    val image: String = "",
    val name: String = "",
    val rank: Long = 0L,
    val university: String = "", // to fetch in API
    // https://github.com/Hipo/university-domains-list-api
)
