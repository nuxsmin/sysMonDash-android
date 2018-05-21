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

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URL

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class ConnectionKtTest {
    /**
     * Comprobar que no se utiliza conexión mediante proxy
     */
    @Test
    fun check_proxy_not_used() {
        val smdServer = SMDServer("http://test123.com:80", "")
        val connection = Connection(URL(smdServer.url))

        assertThat(connection.useProxy).isFalse()
    }

    /**
     * Comprobar que se utiliza conexión mediante proxy
     */
    @Test
    fun check_proxy_used() {
        val smdServer = SMDServer("http://test123.com:80", "")
        val connection = Connection(URL(smdServer.url), ProxyServer("192.168.0.1:8080"))

        assertThat(connection.useProxy).isTrue()
    }
}