package com.example.steptrackerwithlocaldatabase

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/** Object to manage the current step count **/
object StepDataManager {
    private const val TAG = "StepDataManager"
    private const val MOCKED_STEPS_KEY = "mocked_steps"
    private const val ACTUAL_STEPS_KEY = "actual_steps"

    val stepFlow = MutableStateFlow(0)

    @MainThread
    fun incrementMockSteps(context: Context) {
        Log.d(TAG, "incrementMockSteps")
        val prefs = getPrefs(context)
        val mockedSteps = prefs.getInt(MOCKED_STEPS_KEY, 0)
        prefs.edit().putInt(MOCKED_STEPS_KEY, mockedSteps + 1).apply()
        stepFlow.value = mockedSteps + 1 + prefs.getInt(ACTUAL_STEPS_KEY, 0)
    }

    fun incrementActualSteps(context: Context, increment: Int) {
        Log.d(TAG, "incrementActualSteps")
        val prefs = getPrefs(context)
        val actualSteps = prefs.getInt(ACTUAL_STEPS_KEY, 0)
        prefs.edit().putInt(ACTUAL_STEPS_KEY, actualSteps + increment).apply()
        CoroutineScope(Dispatchers.Main).launch {
            stepFlow.value = getStepsFromDisk(context)
        }
    }

    /** Resets the step count to 0 **/
    @MainThread
    fun resetSteps(context: Context) {
        Log.d(TAG, "resetSteps")
        val prefs = getPrefs(context)
        prefs.edit().putInt(MOCKED_STEPS_KEY, 0).putInt(ACTUAL_STEPS_KEY, 0).apply()
        stepFlow.value = 0
    }

    fun getStepsFromDisk(context: Context): Int {
        val prefs = getPrefs(context)
        return prefs.getInt(MOCKED_STEPS_KEY, 0) +
                prefs.getInt(ACTUAL_STEPS_KEY, 0)
    }

    @MainThread
    fun init(context: Context) {
        stepFlow.value = getStepsFromDisk(context)
    }
}