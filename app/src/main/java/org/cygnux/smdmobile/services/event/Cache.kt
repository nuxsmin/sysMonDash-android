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

import android.content.Context
import android.util.Log
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * Clase para la gestión de la cache del servicio de eventos
 */
class Cache(private val mContext: Context) {
    /**
     * Guardar eventos notificados
     */
    fun saveNotifiedEvents(events: MutableSet<String>): Boolean {
        try {
            ObjectOutputStream(mContext.openFileOutput(FILE_NOTIFIED, Context.MODE_PRIVATE)).writeObject(CacheEventsNotified(events))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message)

            return false
        }

        return true
    }

    /**
     * Leer los eventos notificados
     */
    fun readNotifiedEvents(): MutableSet<String> {
        try {
            val cacheEventsNotified = ObjectInputStream(mContext.openFileInput(FILE_NOTIFIED)).readObject() as CacheEventsNotified
            return cacheEventsNotified.events
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message)
        }

        return mutableSetOf()
    }

    /**
     * Clase interna para la cache de eventos notificados
     */
    private class CacheEventsNotified(val events: MutableSet<String>) : Serializable {
        private val time = System.currentTimeMillis()
    }

    companion object {
        private const val LOG_TAG = "Cache"
        private const val FILE_NOTIFIED = "cache_events_notified"
    }
}