package com.example.organizatudo.database.todoList

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {

    @Insert
    suspend fun insertItem(item: TodoTable)

    @Delete
    suspend fun deleteItem(item: TodoTable)

    @Query("DELETE FROM items_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM items_table")
    fun getItems(): Flow<List<TodoTable>>
}