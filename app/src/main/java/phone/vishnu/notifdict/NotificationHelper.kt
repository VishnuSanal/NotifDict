/*
 * Copyright (C) 2019 - 2023 Vishnu Sanal. T
 *
 * This file is part of Quotes Status Creator.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phone.vishnu.notifdict

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat

object NotificationHelper {
    fun createNotification(context: Context, word: String, meaning: String) {

        val builder = NotificationCompat.Builder(
            context, Constants.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.color.black)
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentTitle(
                word
            )
            .setContentText(meaning.substring(0, 200))
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