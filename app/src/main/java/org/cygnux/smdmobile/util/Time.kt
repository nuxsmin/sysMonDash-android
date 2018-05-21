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

package org.cygnux.smdmobile.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Devolver una cadena con los días, horas, minutos y segundos desde una fecha
 *
 * @param timeInSeconds Marca de tiempo en formato UNIX
 */
fun getTimeSinceFormat(timeInSeconds: Long): String {
    val days = timeInSeconds.div(24 * 60 * 60)

    if (days > 0) {
        return "${days}d"
    }

    val hours = timeInSeconds.div(60 * 60)

    if (hours > 0) {
        return "${hours}h"
    }

    val minutes = timeInSeconds.div(60)

    if (minutes > 0) {
        return "${minutes}m"
    }

    return "${timeInSeconds}s"
}

/**
 * Devuelve la cadena de fecha
 *
 * @param time Marca de tiempo en formato UNIX
 */
fun getTimestamp(time: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).format(Date(time * 1000))
}