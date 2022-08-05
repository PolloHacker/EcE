package com.example.organizatudo.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.organizatudo.database.todoList.TodoItemsRepository
import com.example.organizatudo.ui.viewmodel.TodoItemViewModel

class TodoItemViewModelFactory(private val repository: TodoItemsRepository) : NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TodoItemViewModel(repository) as T
}