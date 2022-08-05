package com.example.organizatudo.database.projects

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

interface ProjectsRepository {

    suspend fun insertProject(project: Project) : Long
    suspend fun insertDay(dayProject: DayProject)
    suspend fun insertActivity(activityProject: ActivityProject)

    suspend fun updateProject(project: Project)
    suspend fun updateDay(dayProject: DayProject)
    suspend fun updateActivity(activityProject: ActivityProject)

    suspend fun deleteProject(project: Project)
    suspend fun deleteDay(id: Int)
    suspend fun deleteActivity(id: Int)

    suspend fun deleteAll()

    fun getId(name: String): LiveData<Int>
    fun getProjects(): Flow<List<ProjectWithDaysAndActivities>>
}