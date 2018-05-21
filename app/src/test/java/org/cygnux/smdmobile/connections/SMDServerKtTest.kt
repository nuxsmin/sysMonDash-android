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

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.*

internal class SMDServerKtTest {
    /**
     * Test para comprobar los argumentos pasados a la clase SMDServer
     */
    @Test
    fun check_valid_arguments() {
        // Token válido
        val validToken = "ezgmcDfe0sSXul6bEfE4ZRseaRJohGUm"

        val smd1 = SMDServer("http://prueba_123.com", validToken)

        assertThat(smd1.server).isEqualTo("prueba_123.com")
        assertThat(smd1.protocol).isEqualTo("http://")
        assertThat(smd1.port).isEqualTo(80)

        val smd2 = SMDServer("http://prueba_123.com:8080", validToken)

        assertThat(smd2.server).isEqualTo("prueba_123.com")
        assertThat(smd2.protocol).isEqualTo("http://")
        assertThat(smd2.port).isEqualTo(8080)

        val smd3 = SMDServer("https://prueba_123.com:443/api/...", validToken)

        assertThat(smd3.server).isEqualTo("prueba_123.com")
        assertThat(smd3.protocol).isEqualTo("https://")
        assertThat(smd3.port).isEqualTo(443)

        val smd4 = SMDServer("prueba_123.com:8080", validToken)

        assertThat(smd4.server).isEqualTo("prueba_123.com")
        assertThat(smd4.protocol).isBlank()
        assertThat(smd4.port).isEqualTo(8080)

        val smd5 = SMDServer("prueba_123.com:8080/api/...", validToken)

        assertThat(smd5.server).isEqualTo("prueba_123.com")
        assertThat(smd5.protocol).isBlank()
        assertThat(smd5.port).isEqualTo(8080)
    }

    /**
     * Test para comprobar los argumentos inválidos pasados a la clase SMDServer
     */
    @Test
    fun check_invalid_arguments() {
        assertThatThrownBy { SMDServer("http://prueba_123.com:80000", "") }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { SMDServer("http://prueba_%123.com", "") }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { SMDServer("ftp://prueba_123.com", "") }.isInstanceOf(IllegalArgumentException::class.java)
    }
}