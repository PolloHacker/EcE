package com.example.organizatudo.database.projects

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    //Insert Methods
    @Insert
    suspend fun insertProject(project: Project): Long
    @Insert
    suspend fun insertDay(dayProject: DayProject)
    @Insert
    suspend fun insertActivity(activityProject: ActivityProject)

    //Update Methods
    @Update
    suspend fun updateProject(project: Project)
    @Update
    suspend fun updateDay(dayProject: DayProject)
    @Update
    suspend fun updateActivity(activityProject: ActivityProject)

    //Delete Methods
    @Delete
    suspend fun deleteProject(project: Project)

    //Query Methods
    @Query("DELETE FROM Projects")
    suspend fun deleteAll()

    @Query("DELETE FROM Days WHERE project_id = :id")
    suspend fun deleteDay(id: Int)

    @Query("DELETE FROM Activities WHERE project_id = :id")
    suspend fun deleteActivity(id: Int)

    @Query("SELECT id FROM Projects WHERE name = :name")
    fun getId(name: String) : LiveData<Int>

    @Transaction
    @Query("SELECT * FROM Projects")
    fun getProjects(): Flow<List<ProjectWithDaysAndActivities>>
}