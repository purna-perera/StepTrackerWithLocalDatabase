package com.example.steptrackerwithlocaldatabase

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun HomepageView() {
    val myViewModel = viewModel<HomepageViewModel>()
    val context = LocalContext.current
    LaunchedEffect(myViewModel.getStepCounterSwitchChecked()) {
        if (myViewModel.getStepCounterSwitchChecked()) {
            StepCounterServiceController.startStepCounter(context)
        } else {
            StepCounterServiceController.stopStepCounter(context)
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Steps", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(8.dp))
            Text(myViewModel.getTotalStepsString(), style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(16.dp))
            Button({
                StepDataManager.resetSteps(context)
            }) {
                Text("Reset", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Button({
            StepDataManager.incrementMockSteps(context)
        }, Modifier.align(Alignment.BottomStart)) {
            Text("Mock step", style = MaterialTheme.typography.labelSmall)
        }
        Row(Modifier.align(Alignment.BottomEnd)) {
            Text("Count steps", Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(4.dp))
            Switch(myViewModel.getStepCounterSwitchChecked(), {
                if (!myViewModel.tryToggleService(it, PermissionManager.permissionAvailable(context))) {
                    Toast.makeText(
                        context,
                        "Permission unavailable, try again after granting permission",
                        Toast.LENGTH_LONG
                    ).show()
                    PermissionManager.requestUserPermission(context as? Activity)
                }
            })
        }
    }
}

class HomepageViewModel() : ViewModel() {
    private var steps by mutableStateOf(0)
    private var stepCounterActive by mutableStateOf(false)

    init {
        viewModelScope.launch {
            StepDataManager.stepFlow.collect { newSteps ->
                steps = newSteps
            }
        }
    }

    fun getTotalStepsString(): String = steps.toString()

    /** Will return false if activity recognition permission is unavailable else true */
    fun tryToggleService(checked: Boolean, permissionAvailable: Boolean): Boolean {
        return if (permissionAvailable) {
            stepCounterActive = checked
            true
        } else {
            stepCounterActive = false
            false
        }
    }

    fun getStepCounterSwitchChecked() = stepCounterActive
}