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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Messenger
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.cygnux.smdmobile.configuration.Configuration
import org.cygnux.smdmobile.configuration.ConfigurationPreferences
import org.cygnux.smdmobile.connections.Connection
import org.cygnux.smdmobile.connections.ConnectionTask
import org.cygnux.smdmobile.connections.SMDServer
import org.cygnux.smdmobile.events.EventFilter
import org.cygnux.smdmobile.events.EventInterface
import org.cygnux.smdmobile.events.FilterOrder
import org.cygnux.smdmobile.events.FilterType
import org.cygnux.smdmobile.services.event.EventService
import org.cygnux.smdmobile.services.event.EventServiceHandler
import org.cygnux.smdmobile.services.event.EventServiceScheduler
import org.cygnux.smdmobile.services.event.MESSENGER_INTENT_KEY
import org.json.JSONException
import java.net.URL

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [EventFragment.OnListFragmentInteractionListener] interface.
 */
class EventFragment : Fragment() {
    /**
     * Handler para los eventos que produce el servicio de eventos
     */
    private lateinit var mEventServiceHandler: EventServiceHandler
    /**
     * Vista para la lista de eventos
     */
    private lateinit var mRecyclerView: RecyclerView
    /**
     * Cargar las preferencias de forma diferida (cuando sea necesario)
     */
    private lateinit var mConfiguration: Configuration
    /**
     * Número de columnas de la pantalla para seleccionar el layout
     */
    private var mColumnCount = 1
    /**
     * Listener para capturar los eventos en los elementos de la lista
     */
    private var mListener: OnListFragmentInteractionListener? = null
    /**
     * Filtro para los eventos a mostrar
     */
    val eventFilter: EventFilter = EventFilter(FilterType.TIME, FilterOrder.DESC)
    /**
     * Indica si se ha notificado que la red no está disponible
     */
    private var mNetworkUnavailableNotified = false
    /**
     * Variable que contiene el objeto de MainActivity
     */
    private val mMainActivity by lazy { (activity as MainActivity?) }
    /**
     * Manejador de actualización de lista de eventos
     */
    private val mAutoRefreshHandler = Handler()
    /**
     * Runnable para auto actualización de lista de eventos
     */
    private val mAutoRefresRunnable = object : Runnable {
        override fun run() {
            loadContent()

            mAutoRefreshHandler.postDelayed(this, EventService.REQUEST_INTERVAL)
        }
    }

    /**
     * Procesar tipos de errores manejables
     */
    fun processError(error: String, type: String? = null) {
        mMainActivity?.hideProgressBar()

        when (type) {
            "network_unavailable" -> {
                if (!mNetworkUnavailableNotified) {
                    Toast.makeText(context, getString(R.string.network_connection_unavailable), Toast.LENGTH_LONG).show()

                    mNetworkUnavailableNotified = true
                }
            }
            else -> Toast.makeText(context, getString(R.string.error, error), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Actualizar los datos que envía el servicio de eventos
     */
    fun updateData(data: String) {
        Log.d(LOG_TAG, "updateData()")

        mMainActivity?.hideProgressBar()

        try {
            // Restablecer la notificación de red no disponible
            mNetworkUnavailableNotified = false


            // Generar la lista de eventos y actualizar la vista
            val events = org.cygnux.smdmobile.events.actions.List(data).getEvents(eventFilter)
            (mRecyclerView.adapter as EventsRecyclerViewAdapter)
                    .update(events)

            // Actualizar el reloj de última sincronización
            mMainActivity?.updateLastTimeEventSync()
        } catch (e: JSONException) {
            Toast.makeText(context, getString(R.string.wrong_json_data), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mColumnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        mEventServiceHandler = EventServiceHandler(this)

        // Obtener la configuración y establecer el filtro de estado para los eventos
        mConfiguration = ConfigurationPreferences(PreferenceManager.getDefaultSharedPreferences(context)).read()
        eventFilter.withEventState(mConfiguration.eventStateFilter)

        // Programar el trabajo de actualización en segundo plano
        EventServiceScheduler(context).scheduleJob()
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to [Activity.onResume] of the containing
     * Activity's lifecycle.
     */
    override fun onResume() {
        super.onResume()

        mConfiguration = ConfigurationPreferences(PreferenceManager.getDefaultSharedPreferences(context)).read()
        eventFilter.withEventState(mConfiguration.eventStateFilter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRecyclerView = inflater.inflate(R.layout.fragment_event_list, container, false) as RecyclerView

        with(mRecyclerView) {
            layoutManager = when {
                mColumnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, mColumnCount)
            }
            layoutManager.scrollToPosition(0)
            adapter = EventsRecyclerViewAdapter(ArrayList(), mListener)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        registerForContextMenu(mRecyclerView)

        return mRecyclerView
    }

    override fun onStart() {
        super.onStart()

        // Start service and provide it a way to communicate with this class.
        val startServiceIntent = Intent(context, EventService::class.java)
        val messengerIncoming = Messenger(mEventServiceHandler)
        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
        context?.startService(startServiceIntent)

        setUpAutoRefresh()
    }

    override fun onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        context?.stopService(Intent(context, EventService::class.java))

        // Eliminar las llamadas de actualización de la lista de eventos
        mAutoRefreshHandler.removeCallbacks(mAutoRefresRunnable)

        super.onStop()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnListFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    @Suppress("OverridingDeprecatedMember")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (activity is OnListFragmentInteractionListener) {
                mListener = activity
            } else {
                throw RuntimeException(activity.toString() + " must implement OnListFragmentInteractionListener")
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * Actualizar los eventos a intérvalos
     */
    private fun setUpAutoRefresh() {
        Log.d(LOG_TAG, "setUpAutoRefresh()")

        mAutoRefreshHandler.postDelayed(mAutoRefresRunnable, EventService.REQUEST_INTERVAL)
    }

    /**
     * Cargar el contenido desde el servidor de SMD
     */
    private fun loadContent() {
        Log.d(LOG_TAG, "loadContent")

        val smdServer = mConfiguration.smdServer

        if (smdServer != null && smdServer.url.isNotEmpty()) {
            Log.d(LOG_TAG, "Servidor: ${smdServer.url} - Puerto: ${smdServer.port} - Token: ${smdServer.apiToken}")

            // Obtener los eventos con una tarea asíncrona
            // Se utiliza un retrollamada para actualizar la lista de eventos del adaptador de RecyclerView
            ConnectionTask(Connection(URL(smdServer.getUrlForAction(SMDServer.Action.EVENTS)), mConfiguration.proxyServer, smdServer.auth),
                    { events ->
                        updateData(events)
                    }, null,
                    eventFilter).execute()
        } else {
            Log.i(LOG_TAG, getString(R.string.undefined_smd_server))

            Toast.makeText(context, getString(R.string.undefined_smd_server), Toast.LENGTH_LONG).show()
        }

        mMainActivity?.hideProgressBar()
    }

    /**
     * Refrescar el contenido de la lista
     */
    fun refresh() {
        Log.i(LOG_TAG, "refresh()")

        loadContent()

        Toast.makeText(context, getString(R.string.updating_events), Toast.LENGTH_LONG).show()
    }

    /**
     * Aplicar un filtro a los elementos de la lista
     */
    fun applyFilter() {
        (mRecyclerView.adapter as EventsRecyclerViewAdapter).applyFilter(eventFilter)
    }

    /**
     * Interfaz para enviar los eventos de click sobre los items de la lista
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentClick(item: EventInterface?)
        fun onListActionMenuClick(menuItem: MenuItem, item: EventInterface?)
    }

    companion object {
        private const val LOG_TAG = "EventFragment"

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
                EventFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
