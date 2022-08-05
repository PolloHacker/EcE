package com.example.organizatudo.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizatudo.database.projects.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectViewModel(private val repository: ProjectsRepository) : ViewModel() {

    val projectsList = repository.getProjects()

    suspend fun addProject(name: String, description: String, days: String, creationDate: String, timestamp: String, time: String) : Long{
        return repository.insertProject(Project(0, name, description, days, creationDate, timestamp, time))
    }

    fun addDay(projectId: Int, activities: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDay(DayProject(0, projectId.toInt(), activities))
        }
    }

    fun addActivity(projectId: Int, activity: String, start: String, end: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertActivity(ActivityProject(0, projectId.toInt(), activity, start, end))
        }
    }


    fun removeProject(project: Project) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProject(project)
        }
    }

    fun removeDay(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDay(id)
        }
    }

    fun removeActivity(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteActivity(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun getId(name: String) : LiveData<Int> {
        return repository.getId(name)
    }
}