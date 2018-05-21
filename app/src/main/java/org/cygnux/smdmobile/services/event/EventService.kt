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

package org.cygnux.smdmobile.services.event

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.arch.core.BuildConfig
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.preference.PreferenceManager
import android.util.Log
import org.cygnux.smdmobile.R
import org.cygnux.smdmobile.configuration.ConfigurationPreferences
import org.cygnux.smdmobile.connections.Connection
import org.cygnux.smdmobile.events.EventFilter
import org.cygnux.smdmobile.events.FilterOrder
import org.cygnux.smdmobile.events.FilterType
import org.cygnux.smdmobile.events.actions.List
import org.cygnux.smdmobile.notifications.Notification
import org.cygnux.smdmobile.notifications.NotificationMessage
import org.json.JSONException
import java.net.URL

const val MESSENGER_INTENT_KEY = "${BuildConfig.APPLICATION_ID}.MESSENGER_INTENT_KEY"
const val MSG_JOB_START = 0
const val MSG_JOB_STOP = 1
const val MSG_JOB_DATA = 2
const val MSG_JOB_ERROR = 3

/**
 * Servicio para la obtención de eventos desde el servidor de SMD y notificarlos
 */
class EventService : JobService() {
    private var activityMessenger: Messenger? = null
    private var mNotificationsEnabled: Boolean = false
    private var mDisplayNotificationsEnabled: Boolean = false
    private var mSoundNotificationsEnabled: Boolean = false
    private var mVibrateNotificationsEnabled: Boolean = false
    private var mConnection: Connection? = null
    private val mEventFilter = EventFilter(FilterType.TIME, FilterOrder.DESC)
    private val mCache by lazy { Cache(applicationContext) }
    private lateinit var mEventsNotified: MutableSet<String>
    private lateinit var mNotification: Notification

    /**
     * This method is called if the system has determined that you must stop execution of your job
     * even before you've had a chance to call [.jobFinished].
     *
     *
     * This will happen if the requirements specified at schedule time are no longer met. For
     * example you may have requested WiFi with
     * [android.app.job.JobInfo.Builder.setRequiredNetworkType], yet while your
     * job was executing the user toggled WiFi. Another example is if you had specified
     * [android.app.job.JobInfo.Builder.setRequiresDeviceIdle], and the phone left its
     * idle maintenance window. You are solely responsible for the behaviour of your application
     * upon receipt of this message; your app will likely start to misbehave if you ignore it. One
     * immediate repercussion is that the system will cease holding a wakelock for you.
     *
     * @param params Parameters specifying info about this job.
     * @return True to indicate to the JobManager whether you'd like to reschedule this job based
     * on the retry criteria provided at job creation-time. False to drop the job. Regardless of
     * the value returned, your job must stop executing.
     */
    override fun onStopJob(params: JobParameters?): Boolean {
        Log.i(LOG_TAG, "onStopJob(): ${params?.jobId}")

        // Stop tracking these job parameters, as we've 'finished' executing.
        sendMessage(MSG_JOB_STOP, params?.jobId)

        // Return false to drop the job.
        return false
    }

    /**
     * Override this method with the callback logic for your job. Any such logic needs to be
     * performed on a separate thread, as this function is executed on your application's main
     * thread.
     *
     * @param params Parameters specifying info about this job, including the extras bundle you
     * optionally provided at job-creation time.
     * @return True if your service needs to process the work (on a separate thread). False if
     * there's no more work to be done for this job.
     */
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.i(LOG_TAG, "onStartJob(): ${params?.jobId}")

        sendMessage(MSG_JOB_START, params?.jobId)

        setUp()

        requestDataOnThread(params)

        // Return true as there's more work to be done with this job.
        return true
    }

    /**
     * Notificar un mensaje
     */
    private fun sendMessage(messageID: Int, params: Any?) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.
        if (activityMessenger == null) {
            Log.d(LOG_TAG, "Service is bound, not started. There's no callback to send a message to.")
            return
        }

        val message = Message.obtain()
        message.run {
            what = messageID
            obj = params
        }

        try {
            activityMessenger?.send(message)
        } catch (e: RemoteException) {
            Log.e(LOG_TAG, "Error passing service object back to activity.")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand()")

        activityMessenger = intent?.getParcelableExtra(MESSENGER_INTENT_KEY)

        setUp()

        requestDataOnThread()

        // No reiniciar el servicio si se termina
        return Service.START_NOT_STICKY
    }

    /**
     * Obtener los datos desde el servidor de SMD y enviarlos a los receptores
     */
    private fun requestData() {
        Log.d(LOG_TAG, "requestData()")

        if (mConnection != null) {
            try {
                // Comprobar la conectividad de red
                if (getActiveNetworkInfo().isConnected) {
                    val data = mConnection?.getData()

                    sendMessage(MSG_JOB_DATA, data)

                    if (mNotificationsEnabled) {
                        notifyEvents(data!!)
                    }
                } else {
                    sendMessage(MSG_JOB_ERROR, "network_unavailable")
                }
            } catch (e: JSONException) {
                Log.e(LOG_TAG, e.message)

                e.printStackTrace()

                sendMessage(MSG_JOB_ERROR, getString(R.string.wrong_json_data))
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.message)

                e.printStackTrace()

                sendMessage(MSG_JOB_ERROR, e.message)
            }
        }
    }

    /**
     * Obtener los datos utilizando un hilo de ejecución
     */
    private fun requestDataOnThread(params: JobParameters? = null) {
        Log.d(LOG_TAG, "requestDataOnThread()")

        val handler = Handler()
        val thread = Thread({
            requestData()

            if (params != null) {
                handler.post({
                    sendMessage(MSG_JOB_STOP, params.jobId)
                    jobFinished(params, false)
                })
            }
        })

        thread.name = "RequestData"
        thread.start()
    }

    /**
     * Notificar los eventos no notificados
     */
    private fun notifyEvents(data: String) {
        Log.d(LOG_TAG, "notifyEvents()")

        val events = List(data).getEvents(mEventFilter)

        // Parsear los eventos que ya han sido leídos
        val newEvents = events.filterNot { mEventsNotified.contains(it.uuid) }

        if (newEvents.isNotEmpty()) {
            // Comprobar si hay más de 10 eventos para evitar el envío masivo de notificaciones
            if (newEvents.size > EVENTS_MAX_DETAIL_NOTIFIED) {
                Log.d(LOG_TAG, "El número de nuevos eventos es de  ${newEvents.size}")

                // Notificar evento
                if (mDisplayNotificationsEnabled) {
                    mNotification.displayText(getString(R.string.check_smd_events, events.size))
                }
            } else {
                Log.d(LOG_TAG, "Notificar nuevos eventos")

                if (mDisplayNotificationsEnabled) {
                    // Notificar los eventos
                    newEvents.forEach {
                        mNotification.displayEvent(NotificationMessage(getString(R.string.smd_event_notify_title, it.type), it))
                    }
                }
            }

            if (!mDisplayNotificationsEnabled) {
                if (mSoundNotificationsEnabled) mNotification.notifyBySound()
                if (mVibrateNotificationsEnabled) mNotification.notifyByVibration()
            }

            Log.d(LOG_TAG, "Guardar eventos notificados")

            // Limpiar la lista de eventos notificados
            mEventsNotified.clear()

            // Añadir los eventos como notificados
            mEventsNotified.addAll(events.map { it.uuid })

            // Guardar los eventos en la cache
            mCache.saveNotifiedEvents(mEventsNotified)
        }
    }

    /**
     * Inicializar la configuración del servicio
     */
    private fun setUp() {
        Log.d(LOG_TAG, "setUp()")

        val configuration = ConfigurationPreferences(PreferenceManager.getDefaultSharedPreferences(applicationContext)).read()
        val url = configuration.smdServer?.getUrlForAction()

        if (url.isNullOrEmpty()) {
            sendMessage(MSG_JOB_ERROR, getString(R.string.url_not_set))
            stopSelf()
        } else {
            mConnection = if (configuration.proxyEnabled) {
                Connection(URL(url), configuration.proxyServer, configuration.smdServer?.auth)
            } else {
                Connection(URL(url), null, configuration.smdServer?.auth)
            }
        }

        mNotificationsEnabled = configuration.notificationsEnabled
        mDisplayNotificationsEnabled = configuration.displayNotificationEnabled
        mVibrateNotificationsEnabled = configuration.vibrateNotificationEnabled
        mSoundNotificationsEnabled = configuration.soundNotificationEnabled
        mNotification = Notification(applicationContext, configuration)
        mEventFilter.withEventState(configuration.eventStateFilter)
        mEventsNotified = mCache.readNotifiedEvents()
    }

    /**
     * Devuelve la información de red
     */
    private fun getActiveNetworkInfo(): NetworkInfo {
        return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
    }


    companion object {
        const val REQUEST_INTERVAL = 60000L
        const val LOG_TAG = "EventService"
        /**
         * Número máximo de eventos con detalle notificados
         */
        const val EVENTS_MAX_DETAIL_NOTIFIED = 5
    }
}