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

package org.cygnux.smdmobile.connections

import android.util.Log
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Clase para realizar conexiones a servidores SMD
 *
 * @param url URL de conexión al servidor de SMD
 * @param mProxyServer Servidor proxy a utilizar para realizar la conexión
 * @param mAuth Autentificación básica de la conexión
 */
class Connection(val url: URL,
                 private val mProxyServer: ProxyServer? = null,
                 private val mAuth: ConnectionAuth? = null) : ConnectionInterface {
    /**
     * Usar SSL inseguro (se confia en todos los certificados)
     */
    var useUnsecureSSL: Boolean = true
    /**
     * Indica si la conexión está activa
     */
    var isConnected: Boolean = false
        private set
    /**
     * Indica si la coneción usa prosy
     */
    val useProxy: Boolean
        get() = mProxyServer != null
    /**
     * Cliente HTTP (OkHttp)
     */
    private lateinit var mOkHttpClient: OkHttpClient
    /**
     * Respuesta de la petición
     */
    private var mResponse: String? = null

    init {
        setUp()
    }

    /**
     * Inicializa la conexión para usar un proxy o no
     */
    private fun setUp() {
        val builder = OkHttpClient.Builder()

        // Establecer el proxy para la conexión si es necesario
        if (useProxy) {
            builder.proxy(mProxyServer!!.getProxy())
        }

        // Establecer la autentificación para la conexión si es necesaria
        if (mAuth != null) {
            builder.authenticator({ _, response ->
                val request = response.request()

                if (request.header("Authorization") != null) {
                    null
                }

                request
                        .newBuilder()
                        .header("Authorization", Credentials.basic(mAuth.user, mAuth.pass))
                        .build()
            })
        }

        if (useUnsecureSSL) {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, sslTrustAll(), SecureRandom())

            builder.sslSocketFactory(sslContext.socketFactory, sslTrustAll()[0] as X509TrustManager)
            builder.hostnameVerifier({ _, _ ->
                true
            })
        }

        mOkHttpClient = builder
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build()
    }

    /**
     * Devuelve un array de entidades certificadoras permitidas
     */
    private fun sslTrustAll(): Array<TrustManager> {
        return arrayOf(object : X509TrustManager {
            /**
             * Return an array of certificate authority certificates
             * which are trusted for authenticating peers.
             *
             * @return a non-null (possibly empty) array of acceptable
             * CA issuer certificates.
             */
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>,
                                            authType: String) {
            }

            override fun checkClientTrusted(chain: Array<X509Certificate>,
                                            authType: String) {
            }
        })
    }

    /**
     * Comprobar si la conexión es posible
     */
    override fun check(): Boolean {
        connect()

        return isConnected
    }

    /**
     * Conectar con el servidor
     */
    override fun connect() {
        if (!isConnected) {
            Log.d(LOG_TAG, "Conectando a URL: $url")

            // Inicializar la conexión para datos del tipo JSON
            val request = Request.Builder()
                    .addHeader("Content-type", "application/json")
                    .addHeader("Accept-Language", "en,en-US;q=0.7,es-ES;q=0.3")
                    .url(url)
                    .build()

            mOkHttpClient.newCall(request).execute().use {
                if (!it.isSuccessful) {
                    Log.e(LOG_TAG, "Request URL: $url")
                    Log.e(LOG_TAG, "Response code: ${it.code()}")

                    throw IOException("Error no esperado al realizar la petición HTTP")
                }

                mResponse = it.body()?.string()
            }

            isConnected = true
        }
    }

    /**
     * Desconectar del servidor
     */
    override fun disconnect() {
        if (isConnected) {
            mOkHttpClient.dispatcher().executorService().shutdown()

            isConnected = false
        }
    }

    /**
     * Obtener los datos del servidor
     */
    override fun getData(): String {
        connect()

        return mResponse
                ?: throw IOException("No se recibieron datos desde el servidor: " + url.host)
    }

    companion object {
        private const val LOG_TAG = "Connection"
        private const val TIMEOUT: Long = 20L
    }
}