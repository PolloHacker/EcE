package com.example.organizatudo.database.projects

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class ProjectsRepositoryImpl(private val dao: ProjectDao) : ProjectsRepository {

    override suspend fun insertProject(project: Project): Long {
        return dao.insertProject(project)
    }

    override suspend fun insertDay(dayProject: DayProject) {
        dao.insertDay(dayProject)
    }

    override suspend fun insertActivity(activityProject: ActivityProject) {
        dao.insertActivity(activityProject)
    }

    override suspend fun updateProject(project: Project) {
        dao.updateProject(project)
    }

    override suspend fun updateDay(dayProject: DayProject) {
        dao.updateDay(dayProject)
    }

    override suspend fun updateActivity(activityProject: ActivityProject) {
        dao.updateActivity(activityProject)
    }

    override suspend fun deleteProject(project: Project) {
        dao.deleteProject(project)
    }

    override suspend fun deleteDay(id: Int) {
        dao.deleteDay(id)
    }

    override suspend fun deleteActivity(id: Int) {
        dao.deleteActivity(id)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override fun getId(name: String): LiveData<Int> {
        return dao.getId(name)
    }

    override fun getProjects(): Flow<List<ProjectWithDaysAndActivities>> {
        return dao.getProjects()
    }
}