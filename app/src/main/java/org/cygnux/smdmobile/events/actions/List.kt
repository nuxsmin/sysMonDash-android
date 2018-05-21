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

package org.cygnux.smdmobile.events.actions

import android.util.Log
import com.google.common.io.BaseEncoding
import org.cygnux.smdmobile.events.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Clase para obtener el listado de eventos
 *
 * @param mData Los datos obtenidos desde el servidor de SMD
 */
class List(private val mData: String) {
    /**
     * Lista de eventos
     */
    private val mEvents: ArrayList<EventInterface> = ArrayList()

    /**
     * Mostrar los eventos
     */
    fun getEvents(): kotlin.collections.List<EventInterface> {
        return getEventsFromJSON(mData)
    }

    /**
     * Devuelve los eventos según el tipo de filtro aplicado
     */
    fun getEvents(eventFilter: EventFilter): kotlin.collections.List<EventInterface> {
        return eventFilter.run(getEventsFromJSON(mData))
    }

    /**
     * Devolver los eventos a partir de los datos en JSON
     */
    private fun getEventsFromJSON(data: String): ArrayList<EventInterface> {
        val jsonResult = JSONObject(data)

        Log.i(LOG_TAG, "Parseando eventos")

        if (jsonResult.getInt("status") == 1) {
            Log.e(LOG_TAG, "Se recibe una respuesta de error desde la API")

            throw RuntimeException(jsonResult.getString("description"))
        }

        Log.d(LOG_TAG, "Decodificando eventos")

        val eventsData = String(BaseEncoding.base64().decode(jsonResult.getString("data")))
        val jsonArray = JSONArray(eventsData)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val event = Event()

            event.type = EventType.valueOf(jsonObject.getInt("state")) ?: EventType.UNKNOWN
            event.time = jsonObject.getInt("lastHardStateChange").toLong()
            event.server = jsonObject.getString("hostDisplayName")
            event.backend = jsonObject.getString("backendAlias")
            event.description = jsonObject.getString("displayName")
            event.details = jsonObject.getString("pluginOutput")

            val active = jsonObject.getBoolean("activeChecksEnabled") && jsonObject.getBoolean("notificationsEnabled")
            val scheduledDowntime = jsonObject.getInt("hostScheduledDowntimeDepth") > 0 || jsonObject.getInt("scheduledDowntimeDepth") > 0
            val acknowledged = jsonObject.getBoolean("acknowledged")

            event.state = EventState(active, acknowledged, scheduledDowntime)

            mEvents.add(event)

            Log.d(LOG_TAG, "Evento añadido")
        }

        Log.d(LOG_TAG, "Devolviendo eventos")

        return mEvents
    }

    companion object {
        private const val LOG_TAG = "List"
    }
}