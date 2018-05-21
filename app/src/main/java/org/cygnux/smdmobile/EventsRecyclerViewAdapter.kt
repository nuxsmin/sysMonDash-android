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

package org.cygnux.smdmobile


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import org.cygnux.smdmobile.EventFragment.OnListFragmentInteractionListener
import org.cygnux.smdmobile.events.EventColor
import org.cygnux.smdmobile.events.EventFilter
import org.cygnux.smdmobile.events.EventInterface
import org.cygnux.smdmobile.events.EventType
import org.cygnux.smdmobile.util.getTimeSinceFormat
import org.cygnux.smdmobile.util.getTimestamp

/**
 * Clase para el adaptador de eventos usando RecyclerView
 *
 * @param mEvents Colección de eventos
 * @param mListener Listener de la actividad para el menú contextual de cada elemento
 */
class EventsRecyclerViewAdapter(
        private val mEvents: List<EventInterface>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder>() {

    /**
     * Listener del para la acción de puslsación en el menú contextual
     */
    private val mOnClickListener: View.OnClickListener
    /**
     * Listener del elemento para la acción de puslsación en el menú contextual
     */
    private lateinit var mOnMenuItemClickListener: MenuItem.OnMenuItemClickListener
    private val mEventsCopy = ArrayList<EventInterface>()
    private var mBackgroundColor: Int = 0
    private var mTimeBackgroundColor: Int = 0

    init {
        // Listener del elemento para la acción de pulsar
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as EventInterface

            // Notificar a los Listeners el click en el elemento de la lista
            mListener?.onListFragmentClick(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_event, parent, false)

        mBackgroundColor = getColorForAttribute(R.attr.eventListBackgroundColor, parent.context)
        mTimeBackgroundColor = getColorForAttribute(R.attr.colorPrimaryDark, parent.context)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.mView.context
        val item = mEvents[position]

        // Establecer los datos del elemento de la lista

        holder.mHeaderTextView.text = item.description
        holder.mTypeTextView.text = item.type.toString()

        if (item.details.isNotEmpty()) {
            holder.mDescriptionTextView.text = item.details
        }

        holder.mServerTextView.text = context.getString(R.string.server_label, item.server, item.backend)
        holder.mTimeTextView.text = context.getString(R.string.time_label, getTimeSinceFormat(item.timeSince), getTimestamp(item.time))

        // Establecer el color de fondo del elemento de la lista según el estado y el tipo de evento
        when {
            item.state?.acknowledged == true -> holder.mView.setBackgroundColor(Color.parseColor(EventColor.ACKNOWLEDGE.hex))
            item.state?.scheduledDowntime == true -> holder.mView.setBackgroundColor(Color.parseColor(EventColor.DOWNTIME.hex))
            else -> holder.mView.setBackgroundColor(mBackgroundColor)
        }

        when (item.type) {
            EventType.CRITICAL -> holder.mTypeTextView.setBackgroundColor(Color.parseColor(EventColor.CRITICAL.hex))
            EventType.HIGH -> holder.mTypeTextView.setBackgroundColor(Color.parseColor(EventColor.HIGH.hex))
            EventType.AVERAGE -> holder.mTypeTextView.setBackgroundColor(Color.parseColor(EventColor.AVERAGE.hex))
            EventType.WARNING -> holder.mTypeTextView.setBackgroundColor(Color.parseColor(EventColor.WARNING.hex))
            EventType.INFORMATION -> holder.mTypeTextView.setBackgroundColor(Color.parseColor(EventColor.INFORMATION.hex))
            EventType.OK -> holder.mTypeTextView.setBackgroundColor(Color.parseColor(EventColor.OK.hex))
            else -> holder.mTypeTextView.setBackgroundColor(Color.parseColor(EventColor.UNKNOWN.hex))
        }

        if (item.timeSince < MAX_TIME_RECENT) {
            holder.mTimeTextView.setBackgroundColor(Color.parseColor(EventColor.CRITICAL.hex))
            holder.mTimeTextView.setTypeface(null, Typeface.BOLD)
        } else {
            holder.mTimeTextView.setBackgroundColor(mTimeBackgroundColor)
            holder.mTimeTextView.setTypeface(null, Typeface.NORMAL)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }

        mOnMenuItemClickListener = MenuItem.OnMenuItemClickListener {
            // Notificar a los Listeners el click en el elemento de la lista
            mListener?.onListActionMenuClick(it, item)
            true
        }
    }

    override fun getItemCount(): Int = mEvents.size

    /**
     * Clase interna para los objetos de la vista de cada elemento de la lista
     */
    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView), View.OnCreateContextMenuListener {
        val mServerTextView: TextView = mView.findViewById(R.id.item_server) as TextView
        val mHeaderTextView: TextView = mView.findViewById(R.id.item_header) as TextView
        val mTypeTextView: TextView = mView.findViewById(R.id.item_type) as TextView
        val mDescriptionTextView: TextView = mView.findViewById(R.id.item_description) as TextView
        val mTimeTextView: TextView = mView.findViewById(R.id.item_time) as TextView

        init {
            mView.setOnCreateContextMenuListener(this)
        }

        /**
         * Menú contextual con las opciones
         */
        override fun onCreateContextMenu(menu: ContextMenu,
                                         v: View,
                                         menuInfo: ContextMenu.ContextMenuInfo?) {
            val actionsMenu = menu.setHeaderTitle(mView.context.getString(R.string.title_actions))

            //int groupId, int itemId, int order, CharSequence title

            actionsMenu
                    .add(Menu.NONE, 0, Menu.NONE, mView.context.getString(R.string.action_share_event))
                    .setOnMenuItemClickListener(mOnMenuItemClickListener)

//            actionsMenu
//                    .add(Menu.NONE, 1, Menu.NONE, mView.context.getString(R.string.action_add_backend_event))
//                    .setOnMenuItemClickListener(mOnMenuItemClickListener)
        }
    }

    /**
     * Actualizar la lista de eventos y notificarlo
     */
    fun update(events: List<EventInterface>) {
        with(mEvents as ArrayList<EventInterface>) {
            clear()
            addAll(events)
        }

        notifyDataSetChanged()
    }

    /**
     * Actualizar la lista de eventos y notificarlo
     */
    fun applyFilter(eventFilter: EventFilter) {
        if (mEventsCopy.isEmpty()) {
            mEventsCopy.addAll(mEvents)
        }

        update(eventFilter.run(mEventsCopy))
    }

    /**
     * Devolver el color asociado a un atributo
     */
    private fun getColorForAttribute(resId: Int, context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, true)
        return typedValue.data
    }

    companion object {
        /**
         * Tiempo en segundos para marcar eventos recientes
         */
        private const val MAX_TIME_RECENT = 900
    }
}
