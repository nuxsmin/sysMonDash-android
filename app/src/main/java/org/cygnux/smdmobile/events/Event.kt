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

import com.google.common.base.Charsets
import com.google.common.hash.Hashing

/**
 * Clase para contener datos del evento
 *
 * @param type Tipo de evento
 */
data class Event(override var type: EventType = EventType.OK) : EventInterface {
    /**
     * Id único del evento
     */
    override val uuid by lazy { Hashing.murmur3_128().hashString(time.toString().plus(server).plus(backend).plus(description).plus(type), Charsets.UTF_8).toString() }

    /**
     * Tiempo desde que se inció el evento
     */
    override var timeSince: Long = 0L

    /**
     * Marca de tiempo cuando ocurrió el evento
     */
    override var time: Long = 0L
        set(value) {
            field = value
            timeSince = System.currentTimeMillis().div(1000) - value
        }

    /**
     * Backend del evento
     */
    override var backend: String = ""

    /**
     * Descripción del evento
     */
    override var description: String = ""

    /**
     * Detalles del evento
     */
    override var details: String = ""

    /**
     * Servidor del evento
     */
    override var server: String = ""

    /**
     * Objeto con el estado del evento
     */
    override var state: EventState? = null
}