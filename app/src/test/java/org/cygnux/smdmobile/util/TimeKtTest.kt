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

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

internal class TimeKtTest {
    /**
     * Comprobar la cadena de tiempo devuelta
     */
    @Test
    fun check_time_since_format() {
        assertThat(getTimeSinceFormat(30L)).isEqualTo("30s")
        assertThat(getTimeSinceFormat(60L)).isEqualTo("1m")
        assertThat(getTimeSinceFormat(3600L)).isEqualTo("1h")
        assertThat(getTimeSinceFormat(86400L)).isEqualTo("1d")
        assertThat(getTimeSinceFormat(864000L)).isEqualTo("10d")
    }

    /**
     * Comprobar la cadena de fecha devuelta
     */
    @Test
    fun check_timestamp() {
        assertThat(getTimestamp(1526842817)).isEqualTo("20/05/2018 21:00:17")
    }
}