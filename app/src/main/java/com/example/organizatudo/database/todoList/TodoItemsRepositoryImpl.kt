package com.example.organizatudo.database.todoList

import kotlinx.coroutines.flow.Flow

class TodoItemsRepositoryImpl(private val dao : TodoItemDao) : TodoItemsRepository {

    override suspend fun insertItem(item: TodoTable) {
        dao.insertItem(item)
    }

    override suspend fun deleteItem(item: TodoTable) {
        dao.deleteItem(item)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override fun getItems(): Flow<List<TodoTable>> {
        return dao.getItems()
    }
}