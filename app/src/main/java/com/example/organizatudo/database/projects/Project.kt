package com.example.organizatudo.database.projects

import android.os.Parcelable
import androidx.room.*

@Entity(tableName = "Projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "days")
    val days: String,

    @ColumnInfo(name = "creation_date")
    val creationDate: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: String,

    @ColumnInfo(name = "total_time")
    val timeTotal: String
)

@Entity(tableName = "Days")
data class DayProject(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "day_id")
    val dayId: Int,

    @ColumnInfo(name = "project_id")
    val projectId: Int,

    @ColumnInfo(name = "activities")
    val activities: String

)

@Entity(tableName = "Activities")
data class ActivityProject(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activity_id")
    val activityId: Int,

    @ColumnInfo(name = "project_id")
    val projectId: Int,

    @ColumnInfo(name = "activity_name")
    var activity: String,

    @ColumnInfo(name = "time_start")
    var start: String,

    @ColumnInfo(name = "time_end")
    var end: String
)

data class DayWithActivities(
    @Embedded
    val dayProject: DayProject,
    @Relation(
        parentColumn = "day_id",
        entityColumn = "activity_id"
    )
    val days: List<ActivityProject>
)

data class ProjectWithDaysAndActivities(
    @Embedded
    val project: Project,
    @Relation(
        entity = DayProject::class,
        parentColumn = "id",
        entityColumn = "day_id"
    )
    val CompDays: List<DayWithActivities>
)