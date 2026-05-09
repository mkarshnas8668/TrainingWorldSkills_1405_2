package com.mkarshnas6.karenstudio.worldskill.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mkarshnas6.karenstudio.worldskill.R
import com.mkarshnas6.karenstudio.worldskill.data.model.CalenderEvent

object JsonHelper {
    fun loadEventsFromAssets(context: Context): List<CalenderEvent> {

        val jsonString =
            try {
                context.resources
                .openRawResource(R.raw.events)
                .bufferedReader()
                .use { it.readText() }

        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        val type = object : TypeToken<List<CalenderEvent>>() {}.type
        return Gson().fromJson(jsonString, type)
    }
}