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

import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.cygnux.smdmobile.connections.ProxyServer
import org.cygnux.smdmobile.connections.SMDServer
import org.cygnux.smdmobile.events.EventState
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ConfigurationPreferencesKtTest {
    /**
     * Comprobar el contexto
     */
    @Test
    fun check_context() {
        assertThat("org.cygnux.smdmobile").isEqualTo(sAppContext.packageName)
    }

    /**
     * Comprobar que configuración se puede leer/guardar desde las preferencias
     */
    @Test
    fun check_save_and_read() {
        assertThat(sOriginalConfiguration).isNotNull()

        assertThatCode {
            sConfigurationPreferences.save(sConfiguration)
            sConfigurationPreferences.read()
        }.doesNotThrowAnyException()
    }

    /**
     * Comprobar la autentificación guardada en las preferencias
     */
    @Test
    fun check_auth() {
        val configurationAuth = sConfigurationPreferences.read().configurationAuth!!

        assertThat(configurationAuth.check("prueba", "prueba")).isTrue()
        assertThat(configurationAuth.check("prueba1", "prueba")).isFalse()
        assertThat(configurationAuth.check("prueba", "prueba1")).isFalse()
    }

    /**
     * Comprobar los backends guardados en la configuración
     */
    @Test
    fun check_backends_size() {
        assertThat(sConfigurationPreferences.getBackends().size).isEqualTo(3)
    }

    /**
     * Comprobar el manejo de backends duplicados
     */
    @Test
    fun check_backends_duplicated() {
        assertThat(sConfigurationPreferences.addBackend("Prueba1")).isEqualTo(false)
    }

    /**
     * Comporbar el estado de evento guardado
     */
    @Test
    fun check_event_state() {
        assertThat(sConfigurationPreferences.read().eventStateFilter).isEqualTo(EventState(true, true, true))
    }

    companion object {
        private val sAppContext = InstrumentationRegistry.getTargetContext()
        private val sConfigurationPreferences = ConfigurationPreferences(PreferenceManager.getDefaultSharedPreferences(sAppContext))
        private val sConfiguration = Configuration()
        private val sOriginalConfiguration = sConfigurationPreferences.read()

        /**
         * Inicializar las preferencias
         */
        @BeforeClass
        @JvmStatic
        fun setUp() {
            sConfiguration.lightThemeEnabled = true
//            sConfiguration.notificationSound = sAppContext.getString(R.string.pref_ringtone_silent)
            sConfiguration.notificationsEnabled = true
            sConfiguration.displayNotificationEnabled = true
            sConfiguration.vibrateNotificationEnabled = true
            sConfiguration.proxyEnabled = true
            sConfiguration.proxyServer = ProxyServer("192.168.0.1:8080")
            sConfiguration.configurationAuthEnabled = true
            sConfiguration.configurationAuth = ConfigurationAuth("prueba", "prueba", "123")
            sConfiguration.smdServer = SMDServer("http://prueba123.com", "ezgmcDfe0sSXul6bEfE4ZRseaRJohGUm")
            sConfiguration.eventStateFilter = EventState(true, true, true)

            sConfigurationPreferences.addBackend("Prueba1")
            sConfigurationPreferences.addBackend("Prueba2")
            sConfigurationPreferences.addBackend("Prueba3")
        }

        /**
         * Resetear las preferencias tras realizar el test
         */
        @AfterClass
        fun reset() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(sAppContext)
            sharedPreferences.edit().clear().commit()

            Log.i("TEST", "Preferencias borradas")

            sConfigurationPreferences.save(sOriginalConfiguration)

            Log.i("TEST", "Preferencias restablecidas")
        }
    }
}