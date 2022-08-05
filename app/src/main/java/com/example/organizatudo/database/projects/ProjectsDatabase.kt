package com.example.organizatudo.database.projects

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Project::class, DayProject::class, ActivityProject::class], version = 1)
abstract class ProjectsDatabase : RoomDatabase() {

    abstract val projectDao: ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: ProjectsDatabase? = null
        fun getInstance(context: Context): ProjectsDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ProjectsDatabase::class.java,
                        "projects_data_database"
                    ).build()
                }
                return instance
            }
        }
    }
}