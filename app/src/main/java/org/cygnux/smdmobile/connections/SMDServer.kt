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

/**
 * Implementación de servidor SMDServer
 *
 * @param url Url de conexión al servidor
 * @param apiToken Token para usar la API del servidor
 * @param auth Autentificación del servidor de SMD
 */
class SMDServer(url: String, val apiToken: String, val auth: ConnectionAuth? = null) : ServerBase(url) {
    /**
     * Inicialización de la clase
     */
    init {
        checkUrl()
        checkPort()
    }

    /**
     * Devolver la URL del servidor para una acción
     *
     * @param action Tipo de acción
     */
    fun getUrlForAction(action: Action = Action.EVENTS): String {

        val path = if (!this.path.isNullOrEmpty()) {
            this.path
        } else {
            "/"
        }
        return if (port != 80) {
            "$protocol$server:$port${path}api.php?token=$apiToken&action=${action.type}&useJson=1"
        } else {
            "$protocol$server${path}api.php?token=$apiToken&action=${action.type}&useJson=1"
        }
    }

    /**
     * Clase interna para los tipos de acciones
     */
    enum class Action(var type: Int) {
        EVENTS(1),
        DOWNTIMES(2),
        CHECK(10)
    }
}