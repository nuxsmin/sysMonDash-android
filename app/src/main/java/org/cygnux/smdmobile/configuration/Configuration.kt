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

package org.cygnux.smdmobile.configuration

import org.cygnux.smdmobile.connections.ProxyServer
import org.cygnux.smdmobile.connections.SMDServer
import org.cygnux.smdmobile.events.EventState

/**
 * Clase para almacenar los valores de configuración
 *
 * @param version Versión de la configuración
 */
class Configuration(val version: Long = 0L) {
    /**
     * Servidor de SMD
     */
    var smdServer: SMDServer? = null
    /**
     * Indica si se ha de usar proxy
     */
    var proxyEnabled: Boolean = false
    /**
     * Servidor proxy
     */
    var proxyServer: ProxyServer? = null
    /**
     * Indica si están habilitadas las notificaciones
     */
    var notificationsEnabled: Boolean = false
    /**
     * Indica si se muestran las notificaciones
     */
    var displayNotificationEnabled: Boolean = false
    /**
     * Indica si está habilitada la notificación por vibración
     */
    var vibrateNotificationEnabled: Boolean = false
    /**
     * Autentificación para acceso a la configuración
     */
    var configurationAuth: ConfigurationAuth? = null
    /**
     * Indica si está habilitada la autentificación para el acceso a la configuración
     */
    var configurationAuthEnabled: Boolean = false
    /**
     * Habilitar sonido de notificaciones
     */
    var soundNotificationEnabled = false
    /**
     * Indica si está habilitada la notificación con sonido
     */
    var notificationSound: String? = null
    /**
     * Indica el tema visual de la aplicación
     */
    var lightThemeEnabled: Boolean = false
    /**
     * Lista de servidores de backends para filtrado
     */
    var backends: ArrayList<Backend>? = null
    /**
     * Filtro de estado de eventos
     */
    var eventStateFilter: EventState = EventState(false, true, true)
}