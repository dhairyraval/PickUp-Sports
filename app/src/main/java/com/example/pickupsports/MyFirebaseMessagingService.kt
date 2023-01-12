package com.example.pickupsports

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "event_channel"
const val channelName ="pickupsports"

/**
 * This class handles push notifications sent by firebase.
 */
class MyFirebaseMessagingService: FirebaseMessagingService() {

    /*
     * Much of this code is based on this tutorial.
     * I had to follow the code very closely as I was not familiar with
     * much of this functionality before.
     * Android Push Notification Using Firebase Cloud Messaging in Kotlin
     * https://www.youtube.com/watch?v=2xoJi-ZHmNI
     * Accessed: November 23, 2022
     */
    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.example.pickupsports", R.layout.event_notification)

        remoteView.setTextViewText(R.id.notification_title, title)
        remoteView.setTextViewText(R.id.notification_body, message)

        remoteView.setImageViewResource(R.id.app_logo, R.drawable.app_logo)

        return remoteView
    }

    //Using notification builder to create android alert.
    fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivty::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
        channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(500, 500, 500, 500))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}