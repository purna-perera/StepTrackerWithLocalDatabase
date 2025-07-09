package com.example.steptrackerwithlocaldatabase.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow

object StepDataManager {
    val stepFlow = MutableStateFlow(0)

    fun incrementSteps() = stepFlow.value ++

    fun resetSteps() {
        stepFlow.value = 0
    }
}