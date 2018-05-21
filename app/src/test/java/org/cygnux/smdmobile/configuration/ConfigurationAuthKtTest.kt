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

package org.cygnux.smdmobile.configuration

import org.assertj.core.api.Assertions.assertThat
import org.cygnux.smdmobile.configuration.ConfigurationAuth
import org.cygnux.smdmobile.util.Hash
import org.junit.jupiter.api.Test

internal class ConfigurationAuthKtTest {

    /**
     * Comprobar si los hashes almacenados coninciden con las cadenas de entrada
     */
    @Test
    fun check_stored_hashes_vs_input() {
        val userHash = Hash.makeHash("prueba", "123")
        val passwordHash = Hash.makeHash("prueba", "123")
        val configurationAuth = ConfigurationAuth(userHash, passwordHash, "123")

        assertThat(configurationAuth.check("prueba", "prueba")).isTrue()
        assertThat(configurationAuth.check("prueba1", "prueba")).isFalse()
        assertThat(configurationAuth.check("prueba", "prueba1")).isFalse()
    }
}