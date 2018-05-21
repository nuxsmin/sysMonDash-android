/*
 * sysMonDash
 *
 * @author nuxsmin
 * @link https://github.com/nuxsmin/sysMonDash-android
 * @copyright 2018, Rubén Domínguez nuxsmin@cygnux.org
 *
 * This file is part of sysMonDash.
 *
 * sysMonDash is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sysMonDash is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 *  along with sysMonDash.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cygnux.smdmobile.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import org.cygnux.smdmobile.R
import org.cygnux.smdmobile.configuration.Configuration
import org.cygnux.smdmobile.util.getTimeSinceFormat

/**
 * Clase para notificaciones
 *
 * @param mContext Contexto de la actividad
 */
class Notification(private val mContext: Context, private val configuration: Configuration) {
    private val mBuilder: NotificationCompat.Builder = getBuilder()

    init {
        createChannel("Eventos", mContext.getString(R.string.notification_channel_description))
    }

    /**
     * Mostrar un mensaje de texto
     */
    fun displayText(message: String) {
        Log.i(LOG_TAG, "Notificando mensaje")

        mBuilder.setContentTitle(message)
                .setContentText(null)
                .setStyle(null)

        NotificationManagerCompat.from(mContext).notify(System.currentTimeMillis().toString(), 0, mBuilder.build())
    }

    /**
     * Mostrar un mensaje de evento
     */
    fun displayText(message: NotificationMessage) {
        Log.i(LOG_TAG, "Notificando mensaje")

        mBuilder.setContentTitle(message.text)
                .setContentText(null)
                .setStyle(null)

        NotificationManagerCompat.from(mContext).notify(message.event.uuid, 0, mBuilder.build())
    }

    /**
     * Mostrar un mensaje de evento de SMD
     */
    fun displayEvent(message: NotificationMessage) {
        Log.i(LOG_TAG, "Notificando evento ${message.event.uuid}")

        val event = message.event
        val text = "${event.description} (${event.server})"

        val style = if (event.details.isNotEmpty()) {
            NotificationCompat.BigTextStyle()
                    .bigText("$text\n${event.details}\nBackend: ${event.backend}\nDesde: ${getTimeSinceFormat(event.timeSince)}")
        } else {
            NotificationCompat.BigTextStyle()
                    .bigText("$text\nBackend: ${event.backend}\nDesde: ${getTimeSinceFormat(event.timeSince)}")
        }

        mBuilder.setContentTitle(message.text)
                .setContentText(text)
                .setStyle(style)

        NotificationManagerCompat.from(mContext).notify(message.event.uuid, 0, mBuilder.build())
    }

    /**
     * Devolver el constructor de notificaciones
     */
    private fun getBuilder(): NotificationCompat.Builder {
        val defaults = NotificationCompat.DEFAULT_LIGHTS
        val builder = NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_cloud_teal_24dp)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(0)

        if (configuration.soundNotificationEnabled) {
            defaults.and(NotificationCompat.DEFAULT_SOUND)
            builder.setSound(Uri.parse(configuration.notificationSound))
        }

        if (configuration.vibrateNotificationEnabled) {
            defaults.and(NotificationCompat.DEFAULT_VIBRATE)
        }

        builder.setDefaults(defaults)

        return builder
    }

    /**
     * Reproducir un sonido
     */
    fun notifyBySound() {
        Log.i(LOG_TAG, "Tocando sonido")

        RingtoneManager.getRingtone(mContext, Uri.parse(configuration.notificationSound)).play()
    }

    /**
     * Hacer vibrar el dispositivo
     */
    fun notifyByVibration() {
        val v = mContext.getSystemService(VIBRATOR_SERVICE) as Vibrator

        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            @Suppress("DEPRECATION")
            v.vibrate(VIBRATE_DURATION)
        }
    }

    /**
     * Crear un canal de notificaciones
     */
    private fun createChannel(name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = description

            if (!notificationManager.notificationChannels.contains(channel)) {
                Log.i(LOG_TAG, "Creando canal de notificaciones")

                // Register the channel with the system
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    companion object {
        private const val LOG_TAG = "Notification"
        private const val CHANNEL_ID = "SMD_MOBILE"
        private const val VIBRATE_DURATION = 1000L
    }
}