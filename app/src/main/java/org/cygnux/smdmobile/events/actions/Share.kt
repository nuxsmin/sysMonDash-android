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

package org.cygnux.smdmobile.events.actions

import android.app.Activity
import android.content.Context
import android.support.v4.app.ShareCompat
import org.cygnux.smdmobile.R
import org.cygnux.smdmobile.events.EventInterface
import org.cygnux.smdmobile.util.getTimeSinceFormat
import org.cygnux.smdmobile.util.getTimestamp

/**
 * Clase para compartir los eventos
 *
 * @param mEvent Evento a compartir
 */
class Share(private val mEvent: EventInterface) {
    /**
     * Compartir un evento
     */
    fun getIntent(activity: Activity) {
        val context = activity.applicationContext

        ShareCompat.IntentBuilder
                .from(activity)
                .setSubject(context.getString(R.string.smd_event_subject))
                .setText(getTextFromEvent(context))
                .setHtmlText(getHtmlFromEvent(context))
                .setType("text/plain")
                .setChooserTitle(context.getString(R.string.share_event_title))
                .startChooser()
    }

    /**
     * Devolver el texto con los datos del evento a compartir
     */
    private fun getTextFromEvent(context: Context): String {
        return context.getString(R.string.share_event_text, mEvent.description, mEvent.server, mEvent.type, mEvent.backend, getTimestamp(mEvent.time), getTimeSinceFormat(mEvent.timeSince), mEvent.details).trimMargin()
    }

    /**
     * Devolver el texto en HTML con los datos del evento a compartir
     */
    private fun getHtmlFromEvent(context: Context): String {
        return context.getString(R.string.share_event_html, mEvent.description, mEvent.server, mEvent.type, mEvent.backend, getTimestamp(mEvent.time), getTimeSinceFormat(mEvent.timeSince), mEvent.details).trimMargin()
    }
}