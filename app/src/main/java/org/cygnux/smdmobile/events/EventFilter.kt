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

/**
 * Filtro para eventos
 *
 * @param filterType Indica el tipo de filtro
 * @param filterOrder Indica el orden del filtro
 * que coincidan con éste
 */
class EventFilter(var filterType: FilterType,
                  var filterOrder: FilterOrder = FilterOrder.ASC) {
    /**
     * Indica el texto a buscar en los campos de texto del evento
     */
    var text: String? = null
        private set
    /**
     * Indica el rango a utilizar para el tiempo de los eventos
     */
    var range: IntRange? = null
        private set
    /**
     * Indica el tipo del evento a filtrar
     */
    var eventType: EventType? = null
        private set
    /**
     * Indica el estado del evento a filtrar
     */
    var eventState: EventState? = null
        private set
    private var mUseText: Boolean = false
    private var mUseRange: Boolean = false
    private var mUseEventType: Boolean = false
    private var mUseEventState: Boolean = false

    /**
     * Ejecutar el filtrado y devolver la lista ordenada
     */
    private fun sort(events: List<EventInterface>): List<EventInterface> {
        when (filterType) {
            FilterType.DESCRIPTION -> {
                return when (filterOrder) {
                    FilterOrder.ASC -> events.sortedBy { it.description }
                    FilterOrder.DESC -> events.sortedByDescending { it.description }
                }
            }
            FilterType.TIME -> {
                return when (filterOrder) {
                    FilterOrder.ASC -> events.sortedBy { it.time }
                    FilterOrder.DESC -> events.sortedByDescending { it.time }
                }
            }
            FilterType.SERVER -> {
                return when (filterOrder) {
                    FilterOrder.ASC -> events.sortedBy { it.server }
                    FilterOrder.DESC -> events.sortedByDescending { it.server }
                }
            }
            FilterType.BACKEND -> {
                return when (filterOrder) {
                    FilterOrder.ASC -> events.sortedBy { it.backend }
                    FilterOrder.DESC -> events.sortedByDescending { it.backend }
                }
            }
            FilterType.TYPE -> {
                return when (filterOrder) {
                    FilterOrder.ASC -> events.sortedBy { it.type }
                    FilterOrder.DESC -> events.sortedByDescending { it.type }
                }
            }
        }
    }

    /**
     * Devolver los eventos ordenados por el tipo de filtro
     */
    fun run(events: List<EventInterface>): List<EventInterface> {
        return sort(filterEventStates(when {
            mUseText -> events.filter {
                it.description.contains(text!!, true)
                        || it.backend.contains(text!!)
                        || it.server.contains(text!!)
                        || it.details.contains(text!!)
                        || it.description.contains(text!!)
            }
            mUseRange -> events.filter { it.time.toInt() in range!! }
            mUseEventType -> events.filter { it.type == eventType }
            else -> events
        }))
    }

    /**
     * Filtrar eventos por estados
     */
    private fun filterEventStates(events: List<EventInterface>): List<EventInterface> {
        if (mUseEventState) {
            return events.filter {
                if (eventState?.active == true) {
                    it.state?.active
                }

                true
            }.filterNot {
                (eventState?.acknowledged == false && it.state?.acknowledged == true)
                || (eventState?.scheduledDowntime == false && it.state?.scheduledDowntime == true)
            }
        }

        return events
    }

    /**
     * Filtrar y ordenar los elementos de la lista que contengan el texto indicado en la descripción
     */
    fun withText(text: String): EventFilter {
        this.text = text
        mUseText = true

        return this
    }

    /**
     * Filtrar y ordenar los elementos de la lista que estén entre el tiempo indicado
     */
    fun withTimeRange(range: IntRange): EventFilter {
        if (range.first < 0) {
            throw IllegalArgumentException("Sólo se permiten valores >= 0")
        }

        this.range = range
        mUseRange = true

        return this
    }

    /**
     * Filtrar y ordenar los eventos del tipo indicado
     */
    fun withEventType(type: EventType): EventFilter {
        eventType = type
        mUseEventType = true

        return this
    }

    /**
     * Filtrar los eventos con el estado indicado
     */
    fun withEventState(state: EventState): EventFilter {
        eventState = state
        mUseEventState = true

        return this
    }

    /**
     * Reset de los filtros
     */
    fun resetFilters(): EventFilter {
        text = null
        range = null
        eventType = null
        mUseText = false
        mUseRange = false
        mUseEventType = false

        return this
    }
}