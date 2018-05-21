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

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import org.cygnux.smdmobile.configuration.ConfigurationPreferences
import org.cygnux.smdmobile.events.EventInterface
import org.cygnux.smdmobile.events.FilterOrder
import org.cygnux.smdmobile.events.FilterType
import org.cygnux.smdmobile.events.actions.Share
import org.cygnux.smdmobile.util.getTimestamp

class MainActivity : AppCompatActivity(), EventFragment.OnListFragmentInteractionListener {
    private var mThemeSet = false
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mLastUpdateView: TextView
    private val mEventFragment by lazy { supportFragmentManager.findFragmentById(R.id.event_fragment) as EventFragment }
    private val mConfiguration by lazy { ConfigurationPreferences(PreferenceManager.getDefaultSharedPreferences(applicationContext)).read() }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()

        // Cambiar el tema visual del contexto si es necesario
        if (mConfiguration.lightThemeEnabled && !mThemeSet) {
            theme.applyStyle(R.style.SmdTheme_Light, true)
            mThemeSet = true
        }

        return theme
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        val refresh = findViewById<FloatingActionButton>(R.id.action_refresh)
        refresh.setOnClickListener { refreshEvents() }

        setUpView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.global, menu)

        // Activar la búsqueda en esta Activity
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView?

        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        if (item != null) {
            when (item.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                }
                R.id.action_sort_backend -> sortEventsBy(FilterType.BACKEND)
                R.id.action_sort_server -> sortEventsBy(FilterType.SERVER)
                R.id.action_sort_time -> sortEventsBy(FilterType.TIME)
                R.id.action_sort_type -> sortEventsBy(FilterType.TYPE)
                R.id.action_sort_reset -> sortEventsReset()
                R.id.action_refresh -> refreshEvents()
                R.id.action_search -> showSearchDialog()
                else -> Toast.makeText(applicationContext, "${item.itemId} selected", Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }

    /**
     * Configurar las variables de la vista
     */
    private fun setUpView() {
        mProgressBar = findViewById(R.id.progressBar)
        mLastUpdateView = findViewById(R.id.last_update)
    }

    /**
     * Ordenar el fragmento de eventos por tipo de filtro
     */
    private fun sortEventsBy(filterType: FilterType) {
        Log.i(LOG_TAG, "Ordenando por '$filterType'")

        with(mEventFragment) {
            // Restablecer el orden si en tipo de filtro es diferente
            if (eventFilter.filterType != filterType) {
                eventFilter.filterOrder = FilterOrder.ASC
            } else {
                eventFilter.filterOrder = if (eventFilter.filterOrder == FilterOrder.ASC) {
                    FilterOrder.DESC
                } else {
                    FilterOrder.ASC
                }
            }

            eventFilter.filterType = filterType

            applyFilter()
        }
    }

    /**
     * Reset de los filtros
     */
    private fun sortEventsReset() {
        Log.i(LOG_TAG, "Reset de filtros")

        showProgressBar()

        mEventFragment.eventFilter.resetFilters()
        mEventFragment.refresh()
    }

    /**
     * Actualizar el Fragment de la lista de eventos
     */
    private fun refreshEvents() {
        Log.i(LOG_TAG, "refreshEvents()")

        showProgressBar()

        mEventFragment.refresh()
    }

    /**
     * Buscar un texto en los eventos
     */
    private fun searchEventByText(text: String) {
        Log.i(LOG_TAG, "Buscando '$text'")

        mEventFragment.eventFilter.withText(text)
        mEventFragment.applyFilter()
    }

    /**
     * Mostrar el diálogo de búsqueda
     */
    private fun showSearchDialog() {
        val input = EditText(applicationContext)
        input.inputType = InputType.TYPE_CLASS_TEXT

        with(AlertDialog.Builder(this)) {
            setTitle(getString(R.string.search_text))
            setView(input)
            setPositiveButton(getString(R.string.ok)) { _, _ -> searchEventByText(input.text.toString()) }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
            show()
        }
    }

    /**
     * Actualizar el reloj
     */
    fun updateLastTimeEventSync() {
        mLastUpdateView.text = getTimestamp(System.currentTimeMillis() / 1000)
    }

    /**
     * Ocultar la barra de progreso
     */
    fun hideProgressBar() {
        mProgressBar.visibility = View.INVISIBLE
    }

    /**
     * Ocultar la barra de progreso
     */
    fun showProgressBar() {
        mProgressBar.visibility = View.VISIBLE
    }

    /**
     * Capturar los eventos de click de los elemntos de la lista
     */
    override fun onListFragmentClick(item: EventInterface?) {
        Log.d(LOG_TAG, item.toString())
    }

    /**
     * Capturar los eventos de click en el menu contextual de los elemntos de la lista
     */
    override fun onListActionMenuClick(menuItem: MenuItem, item: EventInterface?) {
        if (item != null) {
            when (menuItem.itemId) {
                0 -> Share(item).getIntent(this)
                1 -> {
                    val configurationPreferences = ConfigurationPreferences(PreferenceManager.getDefaultSharedPreferences(this))

                    if (configurationPreferences.addBackend(item.backend)) {
                        Toast.makeText(this, getString(R.string.backend_added), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, getString(R.string.backend_not_added), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val LOG_TAG = "MainActivity"
    }
}