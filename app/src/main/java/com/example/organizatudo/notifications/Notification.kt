package com.example.organizatudo.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.example.organizatudo.Alarm

@SuppressLint("UnspecifiedImmutableFlag")
fun setAlarm(context: Context, delay: Long, title: String, description: String, alarm: Int) {
    val timeSec = SystemClock.elapsedRealtime() + delay
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, Alarm::class.java)
        .putExtra("title", title)
        .putExtra("description", description)
        .putExtra("alarm", alarm)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeSec, pendingIntent)
}

@SuppressLint("UnspecifiedImmutableFlag")
fun setReminder(context: Context, delay: Long, title: String, description: String, alarm: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, Alarm::class.java)
        .putExtra("title", title)
        .putExtra("description", description)
        .putExtra("alarm", alarm)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay, pendingIntent)
}