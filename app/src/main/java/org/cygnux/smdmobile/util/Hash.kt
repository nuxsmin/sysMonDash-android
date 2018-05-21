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

package org.cygnux.smdmobile.util

import com.google.common.hash.HashCode
import com.google.common.hash.Hashing
import java.security.MessageDigest
import java.util.*

/**
 * Clase con utilidades de hashing
 */
class Hash {
    /**
     * Métodos estáticos
     */
    companion object {
        /**
         * Generar un hash seguro de una cadena
         *
         * @param str Cadena a generar el hash
         * @param salt Salt para añadir al hash
         */
        fun makeHash(str: String, salt: String): String {
            // Generar el hash y devolverlo
            return makeHashOfBytes(str, salt).toString()
        }

        /**
         * Generar un hash seguro de una cadena y devolver un array de bytes
         *
         * @param str Cadena a generar el hash
         * @param salt Salt para añadir al hash
         */
        fun makeHashOfBytes(str: String, salt: String): HashCode {
            // Inicializar el algoritmo de hashing SHA-512
            val sha512 = Hashing.sha512()

            // Añadir el salt y la cadena.
            // Generar el hash y devolverlo
            return sha512.newHasher()
                    .putBytes(salt.toByteArray().plus(str.toByteArray()))
                    .hash()
        }

        /**
         * Obtener un salt para el hash
         *
         * @param size Longitud del salt a generar
         */
        fun makeSalt(size: Int = 8): String {
            // Inicializar el generador de números aleatorios para generar un
            // array de bytes de la longitud indicada
            val salt = ByteArray(size * 2)
            Random().nextBytes(salt)

            return Hex.encodeHex(String(salt)).substring(0, size)
        }

        /**
         * Comparar dos cadenas en tiempo constante (evita ataques de temporización)
         */
        fun compare(a: ByteArray, b: ByteArray): Boolean {
            return MessageDigest.isEqual(a, b)
        }
    }
}