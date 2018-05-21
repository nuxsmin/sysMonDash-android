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

internal class HashKtTest {
    private val mValidSha512Hash: String = "288289CDD6EDF145926F8A9D354C4B93547AC48660CBD7BE38C58102C87A77B5927FDE7A08DE457A00E46A9B62E82C469A13A6FDD7448AACF9691CBFB5E5A294"
    private val mSalt = "123"
    private val mText = "prueba"

    /**
     * Comprobar que el hash generado es igual al de prueba
     */
    @Test
    fun check_generate_hash() {
        // Comprobar que el hash con un salt conocido coincide
        assertThat(Hash.makeHash(mText, mSalt).toUpperCase()).isEqualTo(mValidSha512Hash)

        // Comprobar que el hash con un salt aleatorio es de 128 bytes
        assertThat(Hash.makeHash(mText, Hash.makeSalt()).length).isEqualTo(128)
    }

    /**
     * Comprobar que dos hashes coinciden
     */
    @Test
    fun check_compare_hash() {
        val compareBytes = mValidSha512Hash.toByteArray()

        assertThat(Hash.compare(compareBytes, compareBytes)).isTrue()
        assertThat(Hash.compare(compareBytes, "123".toByteArray())).isFalse()
        assertThat(Hash.compare("123".toByteArray(), compareBytes)).isFalse()
    }

    /**
     * Comprobar que el salt generado tiene 16 bytes
     */
    @Test
    fun check_generate_salt() {
        assertThat(Hash.makeSalt().length).isEqualTo(8)
        assertThat(Hash.makeSalt(16).length).isEqualTo(16)
        assertThat(Hash.makeSalt(32).length).isEqualTo(32)
        assertThat(Hash.makeSalt(64).length).isEqualTo(64)
    }
}