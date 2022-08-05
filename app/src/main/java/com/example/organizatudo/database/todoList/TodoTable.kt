package com.example.organizatudo.database.todoList

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items_table")
data class TodoTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    val id: Int,

    @ColumnInfo(name = "item_name")
    val name : String,

    @ColumnInfo(name = "item_description")
    val description : String,

    @ColumnInfo(name = "item_start_date")
    val start_date : String,

    @ColumnInfo(name = "item_exp_date")
    val exp_date : String,

    @ColumnInfo(name = "item_time")
    val time : String,
)
