package com.example.steptrackerwithlocaldatabase

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object HistoryManager {
    val historyListFlow = MutableStateFlow(JSONArray())
    private val jsonConfig = Json { ignoreUnknownKeys }

    fun appendToHistory(context: Context) {
        val prefs = getPrefs(context)
        val jsonArray = JSONArray(prefs.getString("history", "[]") ?: "[]")
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
        CoroutineScope(Dispatchers.Main).launch {
            historyListFlow.value = getHistoryListFromDisk(context)
        }
    }

    fun clearHistory(context: Context) {
        getPrefs(context).edit().putString("history", "[]").apply()
        historyListFlow.value = JSONArray()
    }

    private fun getHistoryListFromDisk(context: Context): JSONArray {
        return JSONArray(getPrefs(context).getString("history", "[]") ?: "[]")
    }

    fun getHistoryItem(index: Int): HistoryItem {
        val list = historyListFlow.value
        return jsonConfig.decodeFromString(
            list.getJSONObject(list.length() - 1 - index).toString()
        )

    }

    fun init(context: Context) {
        historyListFlow.value = getHistoryListFromDisk(context)
    }

    @Serializable
    data class HistoryItem(
        @SerialName("step_count") val stepCount: Int,
        @SerialName("timestamp") val timestamp: String
    )
}

