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

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.util.Log

/**
 * Clase para programar la ejecución del servicio de eventos
 */
class EventServiceScheduler(val context: Context?) {
    /**
     * Id del trabajo para ejecutar el servicio de eventos
     */
    private var mJobId = 1000

    /**
     * Programar el trabajo de actualizaciones de eventos
     */
    fun scheduleJob() {
        Log.d(LOG_TAG, "scheduleJob()")

        val builder = JobInfo.Builder(mJobId, ComponentName(context, EventService::class.java))

        // Finish configuring the builder
        builder.run {
            setPersisted(true)
            setPeriodic(EventService.REQUEST_INTERVAL)
            setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            setRequiresDeviceIdle(false)
            setRequiresCharging(false)
        }

        // Schedule job
        (context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler)
                .schedule(builder.build())
    }

    /**
     * Cancelar el trabajo
     */
    private fun cancelAllJobs() {
        Log.d(LOG_TAG, "cancelAllJobs()")

        (context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).cancelAll()
    }

    /**
     * Finalizar el trabajo
     */
    private fun finishJob() {
        Log.d(LOG_TAG, "finishJob()")

        val jobScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val allPendingJobs = jobScheduler.allPendingJobs
        if (allPendingJobs.size > 0) {
            // Finish the last one.
            // Example: If jobs a, b, and c are queued in that order, this method will cancel job c.
            jobScheduler.cancel(allPendingJobs.first().id)
        }
    }

    companion object {
        private const val LOG_TAG = "EventServiceScheduler"
    }
}