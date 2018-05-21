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

package org.cygnux.smdmobile.connections

import android.os.AsyncTask
import android.util.Log
import org.cygnux.smdmobile.events.EventFilter

/**
 * Clase para realizar conexiones asíncronas para obtener eventos
 *
 * @param mConnection Conexión al servidor de SMD
 * @param mOnSuccessCallback Retrollamada para ejecutar después de la tarea asíncrona.
 * La función se ejecuta en el thread de la UI
 * @param mOnProgressCallback Retrollamada para ejecutar durante el progreso de la tarea asíncrona.
 * La función se ejecuta en el thread de la UI
 * @param mEventFilter Filtro para obtener los eventos desde el servidor de SMD
 */
class ConnectionTask(private val mConnection: Connection,
                     private val mOnSuccessCallback: ((String) -> Unit)? = null,
                     private val mOnProgressCallback: ((String) -> Unit)? = null,
                     private val mEventFilter: EventFilter) : AsyncTask<Void, String, String>() {

    override fun doInBackground(vararg urls: Void): String {
        publishProgress("Obteniendo eventos")

        Log.i(LOG_TAG, "Obteniendo eventos")

        try {
            return mConnection.getData()
        } catch (e: Throwable) {
            Log.e(LOG_TAG, "Error al obtener eventos: ${e.message}")

            e.printStackTrace()

            publishProgress("Error al obtener eventos")
        }

        return ""
    }

    /**
     * Informar del progreso de la tarea
     */
    override fun onProgressUpdate(vararg values: String) {
        super.onProgressUpdate(*values)

        mOnProgressCallback?.invoke(values[0])
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)

        Log.i(LOG_TAG, "Fin obtener eventos")

        mOnSuccessCallback?.invoke(result)
    }

    companion object {
        private const val LOG_TAG = "ConnectionTask"
    }
}