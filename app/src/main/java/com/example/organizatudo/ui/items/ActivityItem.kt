package com.example.organizatudo.ui.items

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ActivityItem(
    val name: MutableState<String> = mutableStateOf(""),
    var start: MutableState<String>,
    var end: MutableState<String>
)
