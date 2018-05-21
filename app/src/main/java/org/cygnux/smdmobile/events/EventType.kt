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
 * Listado de códigos de tipos de evento posibles
 */
enum class EventType(val type: Int) {
    UNKNOWN(10),
    CRITICAL(5),
    HIGH(4),
    AVERAGE(3),
    WARNING(2),
    INFORMATION(1),
    OK(0);

    companion object {
        /**
         * Obtener el enum a partir de su valor
         */
        fun valueOf(value: Int): EventType? {
            return try {
                EventType.values().first { it.type == value }
            } catch (e: NoSuchElementException) {
                UNKNOWN
            }
        }
    }
}