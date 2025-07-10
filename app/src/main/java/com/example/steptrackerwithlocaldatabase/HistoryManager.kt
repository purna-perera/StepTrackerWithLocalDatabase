package com.example.steptrackerwithlocaldatabase

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object HistoryManager {
    val historyFlow = MutableStateFlow("[]")
    fun appendToHistory(context: Context) {
        val prefs = getPrefs(context)
        val json = prefs.getString("history", "[]") ?: "[]"
        val jsonArray = JSONArray(json)
        val dateString = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).run {
            timeZone = TimeZone.getTimeZone("UTC")
            format(Date())
        }
        val obj = JSONObject().apply {
            put("step_count", StepDataManager.getStepsFromDisk(context))
            put("timestamp", dateString ?: "_")
        }
        jsonArray.put(obj)
        val jsonString = jsonArray.toString()
        prefs.edit()
            .putString("history", jsonString)
            .apply()
        historyFlow.value = jsonString
    }

    fun clearHistory(context: Context) {
        getPrefs(context).edit().putString("history", "[]").apply()
        historyFlow.value = "[]"
    }
}

