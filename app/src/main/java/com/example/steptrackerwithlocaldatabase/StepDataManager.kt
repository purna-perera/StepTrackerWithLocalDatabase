package com.example.steptrackerwithlocaldatabase

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow

object StepDataManager {
    val stepFlow = MutableStateFlow(0)

    fun incrementMockSteps(context: Context) {
        val prefs = getPrefs(context)
        val mockedSteps = prefs.getInt("mocked_steps", 0)
        prefs.edit().putInt("mocked_steps", mockedSteps + 1).apply()
        stepFlow.value = mockedSteps + 1 + prefs.getInt("actual_steps", 0)
    }

    fun incrementActualSteps(context: Context, increment: Int) {
        val prefs = getPrefs(context)
        val actualSteps = prefs.getInt("actual_steps", 0)
        prefs.edit().putInt("actual_steps", actualSteps + increment).apply()
        stepFlow.value = actualSteps + increment + prefs.getInt("mocked_steps", 0)
    }

    fun resetSteps(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().putInt("mocked_steps", 0).putInt("actual_steps", 0).apply()

        stepFlow.value = 0
    }

    fun getStepsFromDisk(context: Context): Int {
        val prefs = getPrefs(context)
        return prefs.getInt("mocked_steps", 0) +
                prefs.getInt("actual_steps", 0)
    }

    fun init(context: Context) {
        stepFlow.value = getStepsFromDisk(context)
    }
}