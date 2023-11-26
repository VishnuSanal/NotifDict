package phone.vishnu.notifdict

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import kotlin.math.min

object NotificationHelper {
    fun createNotification(context: Context, word: String, meaning: String) {

        val builder = NotificationCompat.Builder(
            context, Constants.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_icon)
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentTitle(
                word
            )
            .setContentText(meaning.substring(0, min(meaning.length, 100)))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(meaning)
            )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            builder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID,
                    Constants.NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        notificationManager.notify(
            Constants.NOTIFICATION_REQUEST_CODE, builder.build()
        )
    }
}