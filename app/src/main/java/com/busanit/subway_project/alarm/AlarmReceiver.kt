package com.busanit.subway_project.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.busanit.subway_project.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val channelId = "TimerChannel1"

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("타이머 종료")
            .setContentText("타이머가 종료되었습니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(0, notificationBuilder.build())

        Toast.makeText(context, "타이머가 종료되었습니다.", Toast.LENGTH_SHORT).show()
    }
}