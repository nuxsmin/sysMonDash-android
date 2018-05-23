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

import org.assertj.core.api.Assertions.*
import org.cygnux.smdmobile.events.actions.List
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EventsKtTest {
    private lateinit var jsonDataValid: String
    private lateinit var jsonDataInvalid: String
    private lateinit var jsonDataIncomplete: String

    @BeforeAll
    fun setUp() {
        val is1 = this.javaClass.classLoader.getResourceAsStream("data_valid_base64.json")
        val is2 = this.javaClass.classLoader.getResourceAsStream("data_invalid.json")
        val is3 = this.javaClass.classLoader.getResourceAsStream("data_incomplete.json")

        jsonDataValid = BufferedReader(InputStreamReader(is1) as Reader?).use { reader -> reader.readText() }
        jsonDataInvalid = BufferedReader(InputStreamReader(is2) as Reader?).use { reader -> reader.readText() }
        jsonDataIncomplete = BufferedReader(InputStreamReader(is3) as Reader?).use { reader -> reader.readText() }
    }

    /**
     * Comprobar el comportamiento del parseo de eventos en JSON
     */
    @Test
    fun check_list_events() {
        assertThatCode { List(jsonDataValid).getEvents() }.doesNotThrowAnyException()

        // Comprobar que la lista tiene 1 evento
        assertThat(List(jsonDataValid).getEvents().size).isEqualTo(7)

        // Comprobar que se lanzan excepciones
        assertThatThrownBy { List(jsonDataInvalid).getEvents() }.isInstanceOf(RuntimeException::class.java)
        assertThatThrownBy { List(jsonDataIncomplete).getEvents() }.isInstanceOf(RuntimeException::class.java)
    }

    /**
     * Comprobar la ordenación de eventos
     */
    @Test
    fun check_sort_list_events() {
        val events = List(jsonDataValid).getEvents()

        assertThat(EventFilter(FilterType.DESCRIPTION).run(events).size).isEqualTo(events.size)
        assertThat(EventFilter(FilterType.DESCRIPTION, FilterOrder.DESC).run(events).size).isEqualTo(events.size)

        assertThat(EventFilter(FilterType.TIME).run(events).size).isEqualTo(events.size)
        assertThat(EventFilter(FilterType.TIME, FilterOrder.DESC).run(events).size).isEqualTo(events.size)

        assertThat(EventFilter(FilterType.SERVER).run(events).size).isEqualTo(events.size)
        assertThat(EventFilter(FilterType.SERVER, FilterOrder.DESC).run(events).size).isEqualTo(events.size)

        assertThat(EventFilter(FilterType.TYPE).run(events).size).isEqualTo(events.size)
        assertThat(EventFilter(FilterType.TYPE, FilterOrder.DESC).run(events).size).isEqualTo(events.size)

        assertThat(EventFilter(FilterType.BACKEND).run(events).size).isEqualTo(events.size)
        assertThat(EventFilter(FilterType.BACKEND, FilterOrder.DESC).run(events).size).isEqualTo(events.size)
    }

    /**
     * Comprobar el filtrado de eventos
     */
    @Test
    fun check_filter_list_events() {
        val events = List(jsonDataValid).getEvents()

        test_filter(EventFilter(FilterType.DESCRIPTION), events)
        test_filter(EventFilter(FilterType.BACKEND), events)
        test_filter(EventFilter(FilterType.SERVER), events)
        test_filter(EventFilter(FilterType.TIME), events)
        test_filter(EventFilter(FilterType.TYPE), events)
    }

    /**
     * Comprobar un filtro con eventos
     */
    private fun test_filter(eventFilter: EventFilter, events: kotlin.collections.List<EventInterface>) {
        assertThat(eventFilter.withText("INFO").run(events).size).isEqualTo(1)
        eventFilter.resetFilters()

        assertThat(eventFilter.withText("Prueba2").run(events).size).isEqualTo(2)
        eventFilter.resetFilters()

        assertThat(eventFilter.withText("Internet").run(events).size).isEqualTo(1)

        assertThat(eventFilter.withText("85%").run(events).size).isEqualTo(5)
        eventFilter.resetFilters()

        assertThat(eventFilter.withText("CPU").run(events).size).isEqualTo(1)
        eventFilter.resetFilters()

        assertThat(eventFilter.withTimeRange(IntRange(1524995036, 1524995100)).run(events).size).isEqualTo(5)
        eventFilter.resetFilters()

        assertThat(eventFilter.withTimeRange(IntRange(0, 1524995026)).run(events).size).isEqualTo(2)
        eventFilter.resetFilters()

        assertThat(eventFilter.withEventType(EventType.OK).run(events).size).isEqualTo(1)
        eventFilter.resetFilters()

        assertThat(eventFilter.withEventType(EventType.INFORMATION).run(events).size).isEqualTo(2)
        eventFilter.resetFilters()

        assertThat(eventFilter.withEventType(EventType.UNKNOWN).run(events).size).isEqualTo(1)
        eventFilter.resetFilters()

        assertThatThrownBy {
            eventFilter.withTimeRange(IntRange(-1, 1524995026)).run(events)
        }.isInstanceOf(IllegalArgumentException::class.java)
        eventFilter.resetFilters()

        assertThat(eventFilter.withEventState(EventState(true)).run(events).size).isEqualTo(4)
        assertThat(eventFilter.withEventState(EventState(true, true)).run(events).size).isEqualTo(5)
        assertThat(eventFilter.withEventState(EventState(true, false, true)).run(events).size).isEqualTo(5)
        assertThat(eventFilter.withEventState(EventState(false, false, false)).run(events).size).isEqualTo(4)
    }
}