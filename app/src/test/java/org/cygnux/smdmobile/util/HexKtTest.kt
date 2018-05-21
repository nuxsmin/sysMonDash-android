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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class HexKtTest {
    /**
     * Cadena de ejemplo "prueba123"
     */
    private val hexSample = "707275656261313233"
    private val stringSample = "prueba123"

    /**
     * Comprobar la decodificación de una cadena de ejemplo en array de bytes
     */
    @Test
    fun check_decode() {
        assertThat(Hex.decodeHex(hexSample)).isEqualTo(stringSample.toByteArray())
        assertThat(Hex.decodeHex(hexSample)).isNotEqualTo("prueba1234".toByteArray())
    }

    @Test
    fun check_encode() {
        assertThat(Hex.encodeHex(stringSample)).isEqualTo(hexSample)
        assertThat(Hex.encodeHex("prueba1234")).isNotEqualTo(hexSample)
    }
}