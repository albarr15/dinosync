package com.mobdeve.s18.group9.dinosync.model

data class TodoItem(
    val id: Int,
    val title: String,
    var isChecked: Boolean = false
)