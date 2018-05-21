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
 * Enum para los colores de los eventos
 */
enum class EventColor(val hex: String) {
    UNKNOWN("#7e57c2"),
    CRITICAL("#ef5350"),
    HIGH("#ef9a9a"),
    AVERAGE("#fb8c00"),
    WARNING("#ffa726"),
    INFORMATION("#5c6bc0"),
    OK("#66bb6a"),
    DOWNTIME("#42a5f5"),
    ACKNOWLEDGE("#78909c"),
    FLAPPING("#d4e157");
}