package com.mobdeve.s18.group9.dinosync.model

data class TodoItem(
    val isChecked: Boolean = false,
    val title: String = "",
    val userId: String = "",
    val createdAt: String = ""
)