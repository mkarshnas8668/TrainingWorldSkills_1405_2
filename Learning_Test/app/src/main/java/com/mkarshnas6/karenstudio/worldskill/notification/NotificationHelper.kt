package com.mkarshnas6.karenstudio.worldskill.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mkarshnas6.karenstudio.worldskill.MainActivity
import com.mkarshnas6.karenstudio.worldskill.R

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "test_channel"
        const val CHANNEL_NAME = "my notif" // show this to user
        const val GROUP_KEY = "my_group"
    }

    // start one time : create the chnnel
    fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "this description show in settings"
            setShowBadge(true)
            enableVibration(true)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun showSimpleNotification(title: String, message: String, icon: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(icon)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(11, notification)
    }

    // click on notif and open app
    fun showNotificationWithClick(title: String, message: String, icon: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(icon)
            .setOngoing(false)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(29, notification)
    }

    // click on buttons notif
    fun showNotificationWithAction(title: String, message: String, icon: Int) {
        val openIntent = Intent(context, MainActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // button stop
        val actionIntent = Intent(context, NotificationActionReceiver::class.java)
        actionIntent.putExtra("action", "stop")
        val actionPendingIntent = PendingIntent.getActivity(
            context, 0, actionIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(icon)
            .setOngoing(false)
            .setContentIntent(openPendingIntent) // click on notification
            .addAction(
                R.drawable.ic_stop,
                "stop",
                actionPendingIntent
            )
            .addAction(
                R.drawable.ic_location,
                "open",
                openPendingIntent
            )
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(11, notification)
    }

    fun showGroupNotifications() {
        val manager = context.getSystemService(NotificationManager::class.java)
        // summery of group in start of all them
        val summeryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("3 new messages")
            .setContentText("you have 3 new message")
            .setGroup(GROUP_KEY)
            .setSmallIcon(R.drawable.ic_lock_open)
            .setAutoCancel(true)
            .setGroupSummary(true)
            .build()

        val notification_1 = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("message 1")
            .setContentText("mamad love you ...")
            .setGroup(GROUP_KEY)
            .setSmallIcon(R.drawable.ic_lock_open)
            .build()

        val notification_2 = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("message 2")
            .setContentText("mamad love you twice ...")
            .setGroup(GROUP_KEY)
            .setSmallIcon(R.drawable.ic_lock_open)
            .build()

        val notification_3 = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("message 3")
            .setContentText("mamad love to three ...")
            .setSmallIcon(R.drawable.ic_lock_open)
            .setGroup(GROUP_KEY)
            .build()

        manager.notify(100, summeryNotification) // order is important
        manager.notify(101, notification_1)
        manager.notify(102, notification_2)
        manager.notify(103, notification_3)
    }
}