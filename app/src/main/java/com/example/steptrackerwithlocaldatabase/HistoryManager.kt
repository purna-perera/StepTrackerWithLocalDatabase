package com.example.steptrackerwithlocaldatabase

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Object to manage the user's step history **/
object HistoryManager {
    private const val TAG = "HistoryManager"
    private const val KEY = "history"
    private const val EMPTY_ARRAY_STRING = "[]"
    private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val TIME_ZONE = "UTC"
    private const val STEP_COUNT_FIELD = "step_count"
    private const val TIMESTAMP_FIELD = "timestamp"
    private const val EMPTY_DATE = "_"

    val historyListFlow = MutableStateFlow(JSONArray())
    private val jsonConfig = Json { ignoreUnknownKeys }

    /** Save the current timestamp and step count to disk **/
    fun appendToHistory(context: Context) {
        val prefs = getPrefs(context)
        val jsonArray = JSONArray(
            prefs.getString(KEY, EMPTY_ARRAY_STRING) ?: EMPTY_ARRAY_STRING
        )
        val dateString = SimpleDateFormat(DATE_FORMAT, Locale.US).run {
            timeZone = TimeZone.getTimeZone(TIME_ZONE)
            format(Date())
        }
        val obj = JSONObject().apply {
            put(STEP_COUNT_FIELD, StepDataManager.getStepsFromDisk(context))
            put(TIMESTAMP_FIELD, dateString ?: EMPTY_DATE)
        }
        jsonArray.put(obj)
        val jsonString = jsonArray.toString()
        prefs.edit().putString(KEY, jsonString).apply()
        Log.d(TAG, "Saved history $obj")
        CoroutineScope(Dispatchers.Main).launch {
            historyListFlow.value = getHistoryListFromDisk(context)
        }
    }

    /** Clear the history from disk **/
    @MainThread
    fun clearHistory(context: Context) {
        Log.d(TAG, "Cleared history")
        getPrefs(context).edit().putString(KEY, EMPTY_ARRAY_STRING).apply()
        historyListFlow.value = JSONArray()
    }

    private fun getHistoryListFromDisk(context: Context): JSONArray {
        return JSONArray(
            getPrefs(context).getString(KEY, EMPTY_ARRAY_STRING) ?: EMPTY_ARRAY_STRING
        )
    }

    /** Get the history entry at the given index, ordered from latest to earliest **/
    fun getHistoryItem(index: Int): HistoryItem {
        val list = historyListFlow.value
        return jsonConfig.decodeFromString(
            list.getJSONObject(list.length() - 1 - index).toString()
        )

    }

    @MainThread
    fun init(context: Context) {
        historyListFlow.value = getHistoryListFromDisk(context)
    }

    @Serializable
    data class HistoryItem(
        @SerialName(STEP_COUNT_FIELD) val stepCount: Int,
        @SerialName(TIMESTAMP_FIELD) val timestamp: String
    )
}

