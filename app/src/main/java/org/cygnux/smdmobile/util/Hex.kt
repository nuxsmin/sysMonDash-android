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

import java.math.BigInteger
import java.nio.charset.Charset

/**
 * Clase con utilidades de conversión en hexadecimal
 *
 * Esta clase es necesaria ya que no está disponible en la librería estándard de Java y si se
 * utiliza la librería de Apache Commons, se genera un conflicto con la versión de la librería
 * en la plataforma Android
 */
class Hex {
    /**
     * Métodos estáticos
     */
    companion object {
        /**
         * Decodificar una cadena hexadecimal en array de bytes
         *
         * @param data Cadena a decodificar
         * @from https://commons.apache.org/proper/commons-codec/apidocs/src-html/org/apache/commons/codec/binary/Hex.html
         */
        fun decodeHex(data: String): ByteArray {
            val len = data.length

            if (len and 0x01 != 0) {
                throw RuntimeException("Odd number of characters.")
            }

            val out = ByteArray(len shr 1)

            // two characters form the hex value.
            var i = 0
            var j = 0
            while (j < len) {
                var f = toDigit(data[j], j) shl 4
                j++
                f = f or toDigit(data[j], j)
                j++
                out[i] = (f and 0xFF).toByte()
                i++
            }

            return out
        }

        /**
         * Codificar una cadena en hexadecimal
         */
        fun encodeHex(data: String): String {
            return String.format("%x", BigInteger(1, data.toByteArray(Charset.defaultCharset())))

        }

        /**
         * Convertir un caracter a entero en base 16
         *
         * @from https://commons.apache.org/proper/commons-codec/apidocs/src-html/org/apache/commons/codec/binary/Hex.html
         */
        private fun toDigit(ch: Char, index: Int): Int {
            val digit = Character.digit(ch, 16)
            if (digit == -1) {
                throw RuntimeException("Illegal hexadecimal charcter $ch at index $index")
            }
            return digit
        }
    }
}