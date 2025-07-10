package com.example.steptrackerwithlocaldatabase

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow

object StepDataManager {
    val stepFlow = MutableStateFlow(0)

    fun incrementMockSteps(context: Context) {
        val prefs = getPrefs(context)
        val mockedSteps = prefs.getInt("mocked_steps", 0)
        prefs.edit().putInt("mocked_steps", mockedSteps + 1).apply()
        stepFlow.value = mockedSteps + 1
    }

    fun resetSteps(context: Context) {
        getPrefs(context).edit().putInt("mocked_steps", 0).apply()
        stepFlow.value = 0
    }

    fun getStepsFromDisk(context: Context): Int {
        return getPrefs(context).getInt("mocked_steps", 0)
    }

    fun init(context: Context) {
        stepFlow.value = getPrefs(context).getInt("mocked_steps", 0)
    }
}