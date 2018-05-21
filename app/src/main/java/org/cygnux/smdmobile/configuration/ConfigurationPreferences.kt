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

import android.content.SharedPreferences
import android.util.Log
import org.cygnux.smdmobile.connections.ConnectionAuth
import org.cygnux.smdmobile.connections.ProxyServer
import org.cygnux.smdmobile.connections.SMDServer
import org.cygnux.smdmobile.events.EventState
import org.cygnux.smdmobile.util.Hash

/**
 * Clase para guardar la configuración en las preferencias de Android
 *
 * @param mSharedPreferences Preferencias de la aplicación
 */
class ConfigurationPreferences(private val mSharedPreferences: SharedPreferences) : ConfigurationInterface {
    /**
     * Leer la configuración
     */
    override fun read(): Configuration {
        val config = Configuration(mSharedPreferences.getLong("version", 0L))

        // Obtener las preferencias y guardarlas en el objeto de configuración
        config.notificationsEnabled = mSharedPreferences.getBoolean("notifications_enabled", false)
        config.displayNotificationEnabled = mSharedPreferences.getBoolean("notifications_display", false)
        config.vibrateNotificationEnabled = mSharedPreferences.getBoolean("notifications_vibrate", false)
        config.soundNotificationEnabled = mSharedPreferences.getBoolean("notifications_sound_enabled", false)
        config.notificationSound = mSharedPreferences.getString("notifications_sound", null)

        config.lightThemeEnabled = mSharedPreferences.getBoolean("general_light_theme_enabled", false)

        config.eventStateFilter = EventState(mSharedPreferences.getBoolean("event_filter_active", false),
                mSharedPreferences.getBoolean("event_filter_acknowledged", true),
                mSharedPreferences.getBoolean("event_filter_scheduled", true))

        config.backends = getBackends()

        config.configurationAuthEnabled = mSharedPreferences.getBoolean("auth_enabled", false)

        if (config.configurationAuthEnabled) {
            val authUser = mSharedPreferences.getString("auth_user", null)
            val authPass = mSharedPreferences.getString("auth_pass", null)
            val authSalt = mSharedPreferences.getString("auth_salt", "")

            if (authUser != null && authPass != null) {
                config.configurationAuth = ConfigurationAuth(authUser, authPass, authSalt)
            }
        }

        config.proxyEnabled = mSharedPreferences.getBoolean("proxy_enabled", false)

        if (config.proxyEnabled) {
            val proxyUser = mSharedPreferences.getString("proxy_user", null)
            val proxyPass = mSharedPreferences.getString("proxy_pass", null)

            config.proxyServer = if (proxyUser != null && proxyPass != null) {
                ProxyServer(mSharedPreferences.getString("proxy_url", null), ConnectionAuth(proxyUser, proxyPass))
            } else {
                ProxyServer(mSharedPreferences.getString("proxy_url", null))
            }
        }

        val smdServer = mSharedPreferences.getString("smd_url", null)

        if (smdServer != null) {
            val smdAuthUser = mSharedPreferences.getString("smd_auth_user", null)
            val smdAuthPass = mSharedPreferences.getString("smd_auth_pass", null)

            config.smdServer = if (smdAuthUser != null && smdAuthPass != null) {
                SMDServer(smdServer, mSharedPreferences.getString("smd_token", ""), ConnectionAuth(smdAuthUser, smdAuthPass))
            } else {
                SMDServer(smdServer, mSharedPreferences.getString("smd_token", ""))
            }
        }


        Log.i(LOG_TAG, "Preferencias cargadas")

        return config
    }

    /**
     * Guardar la configuración
     *
     * @param config Configuración a guardar
     */
    override fun save(config: Configuration) {
        val editor = mSharedPreferences.edit()

        editor.putLong("version", System.currentTimeMillis())

        editor.putBoolean("notifications_enabled", config.notificationsEnabled)
        editor.putBoolean("notifications_display", config.displayNotificationEnabled)
        editor.putBoolean("notifications_vibrate", config.vibrateNotificationEnabled)
        editor.putBoolean("notifications_sound_enabled", config.soundNotificationEnabled)
        editor.putString("notifications_sound", config.notificationSound)

        editor.putBoolean("general_light_theme_enabled", config.lightThemeEnabled)

        editor.putBoolean("event_filter_active", config.eventStateFilter.active)
        editor.putBoolean("event_filter_acknowledged", config.eventStateFilter.acknowledged)
        editor.putBoolean("event_filter_scheduled", config.eventStateFilter.scheduledDowntime)

        editor.putBoolean("auth_enabled", config.configurationAuthEnabled)

        if (config.configurationAuthEnabled) {
            editor.putString("auth_user", Hash.makeHash(config.configurationAuth!!.user, config.configurationAuth!!.salt))
            editor.putString("auth_pass", Hash.makeHash(config.configurationAuth!!.password, config.configurationAuth!!.salt))
            editor.putString("auth_salt", config.configurationAuth!!.salt)
        }

        editor.putBoolean("proxy_enabled", config.proxyEnabled)

        if (config.proxyEnabled) {
            editor.putString("proxy_url", config.proxyServer?.url)
            editor.putString("proxy_user", config.proxyServer?.auth?.user)
            editor.putString("proxy_pass", config.proxyServer?.auth?.pass)
        }

        editor.putString("smd_url", config.smdServer?.url)
        editor.putInt("smd_port", config.smdServer?.port ?: 0)
        editor.putString("smd_token", config.smdServer?.apiToken)

        editor.apply()

        Log.i(LOG_TAG, "Preferencias guardadas")
    }

    companion object {
        private const val LOG_TAG = "Preferences"
    }

    /**
     * Añadir un backend
     */
    fun addBackend(name: String): Boolean {
        val backend = Backend(name)
        val backendsList = getBackends()

        if (backendsList.contains(backend)) {
            Log.i(LOG_TAG, "El backend ya existe")

            return false
        }

        backendsList.add(backend)

        val editor = mSharedPreferences.edit()
        editor.putString("smd_backends", backendsList.joinToString(";"))
        editor.apply()

        Log.i(LOG_TAG, "Backend añadido")

        return true
    }

    /**
     * Obtener los backends guardados
     */
    fun getBackends(): ArrayList<Backend> {
        val backends = mSharedPreferences.getString("smd_backends", null)

        if (backends.isNullOrEmpty() || backends == "-1" || backends == "0") {
            return arrayListOf()
        }

        return backends
                .split(";")
                .map {
                    Backend.fromHash(it)
                } as ArrayList<Backend>
    }

    /**
     * Eliminar los backends guardados
     */
    fun deleteBackends() {
        Log.i(LOG_TAG, "deleteBackends()")

        mSharedPreferences.edit().putString("smd_backends", "").apply()
    }

    /**
     * Restablecer la autentificación de la configuración
     */
    fun resetAuth() {
        Log.i(LOG_TAG, "resetAuth()")

        mSharedPreferences.edit()
                .putString("auth_salt", "")
                .putString("auth_user", "")
                .putString("auth_pass", "")
                .apply()
    }
}