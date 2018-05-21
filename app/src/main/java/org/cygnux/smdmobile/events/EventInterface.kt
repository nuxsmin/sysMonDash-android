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

package org.cygnux.smdmobile.events

/**
 * Interface para eventos
 */
interface EventInterface {
    /**
     * Id único del evento
     */
    val uuid: String
    /**
     * Tipo de evento
     */
    var type: EventType
    /**
     * Marca de tiempo cuando ocurrió el evento
     */
    var time: Long
    /**
     * Marca de tiempo desde que se inció el evento
     */
    val timeSince: Long
    /**
     * Backend del evento
     */
    var backend: String
    /**
     * Descripción del evento
     */
    var description: String
    /**
     * Detalles del evento
     */
    var details: String
    /**
     * Servidor del evento
     */
    var server: String
    /**
     * Objeto con el estado del evento
     */
    var state: EventState?
}