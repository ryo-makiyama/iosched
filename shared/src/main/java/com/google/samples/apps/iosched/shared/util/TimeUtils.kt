/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.iosched.shared.util

import androidx.annotation.StringRes
import com.google.samples.apps.iosched.model.ConferenceDay
import com.google.samples.apps.iosched.model.Session
import com.google.samples.apps.iosched.shared.BuildConfig
import com.google.samples.apps.iosched.shared.R
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

object TimeUtils {

    val CONFERENCE_TIMEZONE: ZoneId = ZoneId.of(BuildConfig.CONFERENCE_TIMEZONE)

    val ConferenceDays = listOf(
        ConferenceDay(
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY1_START),
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY1_END)
        ),
        ConferenceDay(
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY2_START),
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY2_END)
        ),
        ConferenceDay(
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY3_START),
            ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY3_END)
        )
    )

    enum class SessionRelativeTimeState { BEFORE, DURING, AFTER, UNKNOWN }

    /** Determine whether the current time is before, during, or after a Session's time slot **/
    fun getSessionState(
        session: Session?,
        currentTime: ZonedDateTime = ZonedDateTime.now()
    ): SessionRelativeTimeState {
        return when {
            session == null -> SessionRelativeTimeState.UNKNOWN
            currentTime < session.startTime -> SessionRelativeTimeState.BEFORE
            currentTime > session.endTime -> SessionRelativeTimeState.AFTER
            else -> SessionRelativeTimeState.DURING
        }
    }

    /**
     * Returns a string resource to use for the label of this day.
     */
    @StringRes
    fun getLabelResForDay(day: ConferenceDay, inConferenceTimeZone: Boolean = true): Int {
        return when (day) {
            ConferenceDays[0] -> if (inConferenceTimeZone) R.string.day1_date else R.string.day1
            ConferenceDays[1] -> if (inConferenceTimeZone) R.string.day2_date else R.string.day2
            ConferenceDays[2] -> if (inConferenceTimeZone) R.string.day3_date else R.string.day3
            else -> throw IllegalArgumentException("Unknown ConferenceDay")
        }
    }

    /**
     * Converts a [dateTime] to a short string.
     *
     * The returned string contains the date, time and weekday, but no year
     * Examples: <code>Tuesday, May 9, 1:55pm</code> in EN_US and
     * <code>Dienstag, Mai 9., 13:55</code> in DE_DE.
     */
    fun timeString(dateTime: ZonedDateTime): String =
        DateTimeFormatter.ofPattern("EEE, MMM d,  h:mm a").format(dateTime)

    fun zonedTime(time: ZonedDateTime, zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
        return ZonedDateTime.ofInstant(time.toInstant(), zoneId)
    }

    fun isConferenceTimeZone(zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        return zoneId == CONFERENCE_TIMEZONE
    }

    fun abbreviatedTimeString(startTime: ZonedDateTime): String {
        return DateTimeFormatter.ofPattern("EEE, MMM d").format(startTime)
    }

    fun timeString(startTime: ZonedDateTime, endTime: ZonedDateTime): String {
        val sb = StringBuilder()
        sb.append(DateTimeFormatter.ofPattern("EEE, MMM d, h:mm ").format(startTime))

        val startTimeMeridiem: String = DateTimeFormatter.ofPattern("a").format(startTime)
        val endTimeMeridiem: String = DateTimeFormatter.ofPattern("a").format(endTime)
        if (startTimeMeridiem != endTimeMeridiem) {
            sb.append(startTimeMeridiem).append(" ")
        }

        sb.append(DateTimeFormatter.ofPattern("- h:mm a").format(endTime))
        return sb.toString()
    }

    fun abbreviatedDayForAr(startTime: ZonedDateTime): String {
        return DateTimeFormatter.ofPattern("MM/dd").format(startTime)
    }

    fun abbreviatedTimeForAr(startTime: ZonedDateTime): String {
        return DateTimeFormatter.ofPattern("HH:mm").format(startTime)
    }

    fun conferenceHasStarted(): Boolean {
        return ZonedDateTime.now().isAfter(ConferenceDays.first().start)
    }

    fun conferenceHasEnded(): Boolean {
        return ZonedDateTime.now().isAfter(ConferenceDays.last().end)
    }

    fun conferenceWifiOfferingStarted(): Boolean {
        val wifiStartedTime = ZonedDateTime.parse(BuildConfig.CONFERENCE_WIFI_OFFERING_START)
        return ZonedDateTime.now().isAfter(wifiStartedTime)
    }
}
