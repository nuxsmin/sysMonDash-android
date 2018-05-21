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

import android.os.Handler
import android.os.Message
import android.util.Log
import org.cygnux.smdmobile.EventFragment
import java.lang.ref.WeakReference

/**
 * Manejador de eventos del servicio de eventos
 */
class EventServiceHandler(fragment: EventFragment) : Handler() {
    // Prevent possible leaks with a weak reference.
    private val eventFragment: WeakReference<EventFragment> = WeakReference(fragment)

    /**
     * Manejar un mensaje desde el servicio de eventos
     */
    override fun handleMessage(msg: Message?) {
        Log.d(LOG_TAG, "handleMessage()")

        val eventFragment = eventFragment.get() ?: return

        when (msg?.what) {
            MSG_JOB_DATA -> {
                eventFragment.updateData(msg.obj as String)
            }
            MSG_JOB_ERROR -> {
                eventFragment.processError(msg.obj as String)
            }
        }
    }

    companion object {
        private const val LOG_TAG = "EventServiceHandler"
    }
}