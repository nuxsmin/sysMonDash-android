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

package org.cygnux.smdmobile.services.event

import android.support.test.InstrumentationRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class CacheKtTest {
    private val mEvents = mutableSetOf("aaa", "bbb", "ccc", "ddd", "fff")

    /**
     * Comprobar la lectura/escritura de eventos en la cache
     */
    @Test
    fun check_read_and_save() {
        val mCache = Cache(InstrumentationRegistry.getTargetContext())
        val mOriginalEvents: MutableSet<String> = mCache.readNotifiedEvents()

        // Insertar y leer eventos nuevos
        assertThat(mCache.saveNotifiedEvents(mEvents)).isTrue()
        assertThat(mCache.readNotifiedEvents().size).isEqualTo(mEvents.size)

        // Insetar eventos originales
        assertThat(mCache.saveNotifiedEvents(mOriginalEvents)).isTrue()
        assertThat(mCache.readNotifiedEvents().size).isEqualTo(mOriginalEvents.size)
    }
}