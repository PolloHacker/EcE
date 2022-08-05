package com.example.organizatudo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizatudo.database.todoList.TodoItemsRepository
import com.example.organizatudo.database.todoList.TodoTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TodoItemViewModel(private val repository: TodoItemsRepository) : ViewModel(){
    val itemList = repository.getItems()

    fun addItem(name: String, description: String, start_date: String, exp_date: String, time: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertItem(TodoTable(0, name, description, start_date, exp_date, time))
        }
    }

    fun removeItem(item : TodoTable){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(item)
        }
    }

    fun clearAll(){
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteAll()
        }
    }

}