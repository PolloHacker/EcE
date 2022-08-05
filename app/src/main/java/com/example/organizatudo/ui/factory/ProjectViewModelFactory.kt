package com.example.organizatudo.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.organizatudo.database.projects.ProjectsRepository
import com.example.organizatudo.ui.viewmodel.ProjectViewModel

class ProjectViewModelFactory(private val repository: ProjectsRepository) : NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ProjectViewModel(repository) as T
}