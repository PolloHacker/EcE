package com.example.organizatudo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat


class Alarm : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val title = intent?.getStringExtra("title")
            val description = intent?.getStringExtra("description")
            val alarm = intent?.getIntExtra("alarm", R.raw.alarm14)
            showNotification(context, title!!, description!!, alarm!!)
        }
        catch (e: Exception) {
            Log.d("Reveiving Exception", e.printStackTrace().toString())
        }
    }

     private fun showNotification(context : Context, title : String, description : String, alarm : Int) {
         val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         val channelName = "channel_name"
         val channelId = "channel_id"

         val att = AudioAttributes.Builder()
             .setUsage(AudioAttributes.USAGE_ALARM)
             .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
             .build()

         val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
         channel.setSound(Uri.parse("android.resource://${context.packageName}/$alarm"), att)
         channel.vibrationPattern = longArrayOf(0, 500, 1000, 1000)
         channel.enableVibration(true)
         manager.createNotificationChannel(channel)

         val builder = NotificationCompat.Builder(context, title)
             .setSmallIcon(R.drawable.ic_timer)
             .setContentTitle(title)
             .setContentText(description)
             .setUsesChronometer(true)
             .setChronometerCountDown(true)
             .setPriority(NotificationCompat.PRIORITY_MAX)
             .setCategory(NotificationCompat.CATEGORY_ALARM)
             .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


         manager.notify(1, builder.build())

     }
}