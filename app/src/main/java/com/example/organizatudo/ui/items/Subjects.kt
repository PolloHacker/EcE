package com.example.organizatudo.ui.items

import com.google.gson.Gson

data class Subjects(
    val name: String,
    var a1: Float = 0f,
    var a2: Float = 0f,
    var a3: Float = 0f,
    var a4: Float = 0f,
    var a5: Float = 0f,
    var a6: Float = 0f,
    var a7: Float = 0f,
    var a8: Float = 0f,
    var a9: Float = 0f,
    var media: Float = 0f
) {
    private var grades: Set<Float>? = null
    init {
        grades = mutableSetOf(a1, a2, a3, a4, a5, a6, a7, a8, a9, media)
    }
    override fun toString(): String {
        return "Subject(name=$name, grades=$grades"
    }
}

fun serialize(subject: Subjects) : String {
    val gson = Gson()

    return gson.toJson(subject)
}

fun deserialize(subject: String) : Subjects {
    val gson = Gson()

    return gson.fromJson(subject, Subjects::class.java)
}