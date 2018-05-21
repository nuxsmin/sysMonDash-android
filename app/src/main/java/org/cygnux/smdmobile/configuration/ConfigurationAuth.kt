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

import org.cygnux.smdmobile.util.Hash
import org.cygnux.smdmobile.util.Hex

/**
 * Clase para guardar la configuración de autentificación de acceso
 *
 * @param user Usuario de acceso
 * @param password Clave de acceso
 * @param salt Salt del hash utilizado
 */
data class ConfigurationAuth(val user: String, val password: String, val salt: String) {
    /**
     * Comprobar si el usuario y la clave coinciden
     */
    fun check(user: String, password: String): Boolean {
        return Hash.compare(Hex.decodeHex(this.user), Hash.makeHashOfBytes(user, salt).asBytes())
                && Hash.compare(Hex.decodeHex(this.password), Hash.makeHashOfBytes(password, salt).asBytes())
    }
}