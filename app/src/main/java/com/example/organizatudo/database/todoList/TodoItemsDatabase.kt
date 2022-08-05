package com.example.organizatudo.database.todoList

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TodoTable::class], version = 1)
abstract class TodoItemsDatabase: RoomDatabase() {

    abstract val itemDAO : TodoItemDao

    companion object{
        @Volatile
        private var INSTANCE : TodoItemsDatabase? = null
        fun getInstance(context: Context): TodoItemsDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TodoItemsDatabase::class.java,
                        "todo_items_data_database"
                    ).build()
                }
                return instance
            }
        }
    }
}