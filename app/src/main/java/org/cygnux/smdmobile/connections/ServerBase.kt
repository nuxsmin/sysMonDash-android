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
 * Clase abstracta para implementación de servidores
 *
 * @param url Url del servidor
 */
abstract class ServerBase(val url: String) {
    var protocol: String? = null
        private set
    var server: String? = null
        private set
    var port: Int = 0
        private set
    var path: String? = null
        private set

    /**
     * Comprobar que la URL del servidor es correcta
     */
    protected fun checkUrl() {
        // Comprobar la expresión regular y extraer los grupos de captura de la misma
        val groups = URL_REGEX.toRegex().matchEntire(url)?.groups
                ?: throw IllegalArgumentException("La URL del servidor no está bien formada")

        // Almacenar los valores capturados en las propiedades de la clase
        protocol = groups[1]?.value
        server = groups[2]?.value
        port = groups[3]?.value?.toInt() ?: 80
        path = groups[4]?.value
    }

    /**
     * Comprobar que el puerto está entre los valores correctos
     */
    protected fun checkPort() {
        if (port < PORT_MIN || port > PORT_MAX) {
            throw IllegalArgumentException("Puerto incorrecto")
        }
    }

    /**
     * Indica si la URL usa https
     */
    fun useHttps(): Boolean {
        return protocol.equals("https", true)
    }

    companion object {
        /**
         * Constantes en tiempo de compilación
         */
        const val URL_REGEX: String = "(https?://)?([\\w\\d.-]+)(?::(\\d{1,5}))?(/.*)?"
        const val PORT_MIN: Int = 1
        const val PORT_MAX: Int = 65535
    }
}