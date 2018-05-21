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

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import org.cygnux.smdmobile.configuration.ConfigurationPreferences
import org.cygnux.smdmobile.connections.ServerBase


/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : PreferenceActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var mThemeSet = false
    private val mSharedPreferences: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    private val mConfiguration by lazy { ConfigurationPreferences(mSharedPreferences).read() }

    public override fun onResume() {
        super.onResume()
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    public override fun onPause() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()

        // Cambiar el tema visual del contexto si es necesario
        if (mConfiguration.lightThemeEnabled && !mThemeSet) {
            theme.applyStyle(R.style.SmdTheme_Light, true)
            mThemeSet = true
        }

        // you could also use a switch if you have many themes that could apply
        return theme
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("auth_granted")) {
            sAuthGranted = intent.getBooleanExtra("auth_granted", false)
        }

        sAuthEnabled = mConfiguration.configurationAuthEnabled

        if (sAuthEnabled && !sAuthGranted) {
            startActivity(Intent(applicationContext, SettingsAuthActivity::class.java))
            finish()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val root = findViewById<View>(android.R.id.list).parent.parent.parent as LinearLayout
        val appBarLayout = LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false) as AppBarLayout
        root.addView(appBarLayout, 0) // insert at top

        appBarLayout.findViewById<Toolbar>(R.id.toolbar).run {
            setNavigationOnClickListener { finish() }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || ServerPreferenceFragment::class.java.name == fragmentName
                || AuthPreferenceFragment::class.java.name == fragmentName
                || NotificationPreferenceFragment::class.java.name == fragmentName
                || EventFilterPreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * Fragmento para configuración general
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Fragmento para configuración se servidor SMD
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class ServerPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_server)
            setHasOptionsMenu(true)

            setUpListeners()
            setUpValidators()
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        /**
         * Configurar las comprobaciones de los parámetros
         */
        private fun setUpValidators() {

            // Listener para comprobar la URL del servidor de SMD
            (preferenceScreen.findPreference("smd_url") as EditTextPreference)
                    .setOnPreferenceChangeListener({ _: Preference, any: Any ->
                        ServerBase.URL_REGEX.toRegex().matches(any as String)
                    })
        }

        /**
         * Configurar los listener
         */
        private fun setUpListeners() {
            // Listener para borrar los backends
/*            preferenceScreen
                    .findPreference("backends_delete")
                    .setOnPreferenceClickListener {
                        ConfigurationPreferences(preferenceScreen.sharedPreferences).deleteBackends()

                        Toast.makeText(activity, getString(R.string.backends_deleted), Toast.LENGTH_SHORT).show()
                        true
                    }*/
        }
    }

    /**
     * Fragmento para configuración de notificaciones
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class NotificationPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_notification)
            setHasOptionsMenu(true)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Fragmento para configuración autentificación
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AuthPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_auth)
            setHasOptionsMenu(true)

            setUpListeners()
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        /**
         * Configurar los listener
         */
        private fun setUpListeners() {
            // Listener para restablecer la autentificación
            preferenceScreen
                    .findPreference("auth_reset")
                    .setOnPreferenceClickListener {
                        ConfigurationPreferences(preferenceScreen.sharedPreferences).resetAuth()

                        Toast.makeText(activity, getString(R.string.auth_reset), Toast.LENGTH_SHORT).show()
                        true
                    }
        }
    }

    /**
     * Fragmento para configuración de filtrado de eventos
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class EventFilterPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref_event_filters)
            setHasOptionsMenu(true)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     *
     * This callback will be run on your main thread.
     *
     * @param sharedPreferences The [SharedPreferences] that received
     * the change.
     * @param key The key of the preference that was changed, added, or
     * removed.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(LOG_TAG, "onSharedPreferenceChanged()")
    }

    companion object {
        private const val LOG_TAG = "SettingsActivity"

        /**
         * Variable para almacenar si la autentificación está habilitada
         */
        private var sAuthEnabled: Boolean = false
        /**
         * Variable para almacenar si la autentificación es correcta
         */
        private var sAuthGranted: Boolean = false

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }
    }
}
