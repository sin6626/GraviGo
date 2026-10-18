package io.github.sin6626.gravigo.data.service

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.R

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build

object NotificationChannelManager {
    const val CHANNEL_LIVE_ACTIVITY = "antigravity_live_activity"
    const val CHANNEL_ALERTS = "antigravity_alerts"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        // 1. Live Activity Ongoing Channel (Low importance: silent progress in drawer/lockscreen)
        val liveChannel = NotificationChannel(
            CHANNEL_LIVE_ACTIVITY,
            appString(R.string.msg_624467c34c58),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = appString(R.string.msg_f231b46e55d6)
            setShowBadge(false)
            enableLights(false)
            enableVibration(false)
        }

        // 2. High-Priority Alerts Channel (High importance: heads-up pop-up, sound, vibration for approvals & completions)
        val alertChannel = NotificationChannel(
            CHANNEL_ALERTS,
            appString(R.string.msg_d7d762ad0806),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = appString(R.string.msg_78526cceb9df)
            setShowBadge(true)
            enableLights(true)
            lightColor = Color.parseColor("#4F46E5")
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 150, 250)

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            setSound(soundUri, audioAttributes)
        }

        notificationManager.createNotificationChannel(liveChannel)
        notificationManager.createNotificationChannel(alertChannel)
    }
}
