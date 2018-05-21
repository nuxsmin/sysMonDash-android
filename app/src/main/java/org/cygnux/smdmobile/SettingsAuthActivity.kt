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

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.cygnux.smdmobile.configuration.ConfigurationPreferences

/**
 * Actividad para solicitar la autentificación para desbloquear las preferencias
 */
class SettingsAuthActivity : AppCompatActivity() {
    private val mConfiguration by lazy { ConfigurationPreferences(PreferenceManager.getDefaultSharedPreferences(applicationContext)).read() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (mConfiguration.lightThemeEnabled) {
            setTheme(R.style.SmdTheme_Light)
        }
        
        setContentView(R.layout.activity_settings_auth)
        setSupportActionBar(findViewById(R.id.toolbar))

        val authButton = findViewById<Button>(R.id.auth_btn_unlock)
        authButton.setOnClickListener {
            doAuth()
        }
    }

    /**
     * Comprobar las credenciales e iniciar la actividad de preferencias si son correctas
     */
    private fun doAuth() {
        val authUser = findViewById<TextView>(R.id.auth_user)
        val authPass = findViewById<TextView>(R.id.auth_pass)
        val authGranted = mConfiguration.configurationAuth?.check(authUser.text.toString(), authPass.text.toString())
                ?: false

        if (authGranted) {
            startActivity(Intent(this, SettingsActivity::class.java).putExtra("auth_granted", authGranted))
        } else {
            authUser.text = ""
            authPass.text = ""

            Toast.makeText(this, getString(R.string.wrong_auth), Toast.LENGTH_SHORT).show()
        }
    }
}
