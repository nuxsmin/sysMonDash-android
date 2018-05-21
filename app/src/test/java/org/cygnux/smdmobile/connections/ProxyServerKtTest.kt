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

internal class ProxyServerKtTest {

    /**
     * Test para comprobar los argumentos pasados a la clase ProxyServer
     */
    @Test
    fun check_valid_arguments() {
        // Comprobar valores correctos
        val proxy1 = ProxyServer("prueba123.com:8080")

        assertThat(proxy1.server).isEqualTo("prueba123.com")
        assertThat(proxy1.port).isEqualTo(8080)

        val proxy2 = ProxyServer("http://prueba123.com:80")

        assertThat( proxy2.server ).isEqualTo("prueba123.com")
        assertThat( proxy2.port ).isEqualTo(80)

        val proxy3 = ProxyServer("http://prueba123.com:8080")

        assertThat( proxy3.server ).isEqualTo("prueba123.com")
        assertThat( proxy3.port ).isEqualTo(8080)

        assertThat(ProxyServer("192.168.0.1").server).isEqualTo("192.168.0.1")
        assertThat(ProxyServer("prueba123.com").server).isEqualTo("prueba123.com")
    }

    /**
     * Test para comprobar los argumentos inválidos pasados a la clase ProxyServer
     */
    @Test
    fun check_invalid_arguments() {
        // Comprobar valores incorrectos
        assertThatThrownBy { ProxyServer("http://prueba123.com:80000") }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { ProxyServer("http://%prueba123.com:80000") }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { ProxyServer("ftp://prueba123.com:80000") }.isInstanceOf(IllegalArgumentException::class.java)
    }
}