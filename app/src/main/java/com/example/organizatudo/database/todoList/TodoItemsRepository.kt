package com.example.organizatudo.database.todoList

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface TodoItemsRepository {

    suspend fun insertItem(item: TodoTable)
    suspend fun deleteItem(item: TodoTable)
    suspend fun deleteAll()
    fun getItems() : Flow<List<TodoTable>>
}