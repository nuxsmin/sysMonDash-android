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

package org.cygnux.smdmobile.configuration.preference

import android.content.Context
import android.preference.EditTextPreference
import android.util.AttributeSet
import org.cygnux.smdmobile.util.Hash

/**
 * Tipo de preferencia para valores de autentificación
 */
class HashedEditTextPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs) {
    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
    }

    override fun persistString(value: String?): Boolean {
        // Se obtiene el salt para generar el hash de la cadena
        var salt = sharedPreferences.getString("auth_salt", "")

        // Se genera y guarda un nuevo salt si está en blanco
        if (salt.isEmpty()) {
            salt =  Hash.makeSalt()
            sharedPreferences.edit().putString("auth_salt", salt).apply()
        }

        // Se guarda el valor como hash
        return super.persistString(Hash.makeHash(value ?: "", salt))
    }
}