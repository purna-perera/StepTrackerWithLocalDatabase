package com.example.steptrackerwithlocaldatabase

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val LAYOUT_PADDING = 16
private const val VERTICAL_SPACE_SMALL = 4
private const val VERTICAL_SPACE_MEDIUM = 8
private const val VERTICAL_SPACE_LARGE = 16
private const val TOGGLE_COOLDOWN = 500L

/** The main and first page of the application **/
@Composable
fun HomepageView(historyTabCallback: () -> Unit) {
    val context = LocalContext.current

    Box(Modifier.fillMaxSize().padding(LAYOUT_PADDING.dp)) {
        StepCountDisplay(Modifier.align(Alignment.Center))
        Button(
            { StepDataManager.incrementMockSteps(context) },
            Modifier.align(Alignment.BottomStart)
        ) {
            Text(stringResource(R.string.mock_step_button), style = MaterialTheme.typography.labelSmall)
        }
        StepCounterToggle(Modifier.align(Alignment.BottomEnd))
        Button(historyTabCallback, Modifier.align(Alignment.TopStart)) {
            Text(stringResource(R.string.history_tab_button), style = MaterialTheme.typography.labelSmall)
        }
    }
}

/** Center of the homepage with the main display components including the step count and reset button **/
@Composable
fun StepCountDisplay(modifier: Modifier) {
    val currentlyCalibrating by StepCounterServiceManager.currentlyCalibratingFlow.collectAsState()
    val stepNumber by StepDataManager.stepFlow.collectAsState()
    val context = LocalContext.current

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.step_display_label), style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(VERTICAL_SPACE_MEDIUM.dp))
        Text(stepNumber.toString(), style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(VERTICAL_SPACE_LARGE.dp))
        Button({ StepDataManager.resetSteps(context) }) {
            Text(stringResource(R.string.step_count_reset_button), style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(Modifier.height(VERTICAL_SPACE_MEDIUM.dp))
        Text(
            if (currentlyCalibrating) stringResource(R.string.step_counter_calibrating_message) else "",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

/** Switch to toggle the step counter on and off **/
@Composable
fun StepCounterToggle(modifier: Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isCooldown by remember { mutableStateOf(false) }
    var stepCounterSwitchChecked by
        remember { mutableStateOf(StepCounterServiceManager.serviceStarted) }

    LaunchedEffect(stepCounterSwitchChecked) {
        if (stepCounterSwitchChecked) {
            StepCounterServiceManager.startStepCounter(context)
        } else {
            StepCounterServiceManager.stopStepCounter(context)
        }
    }

    Row(modifier) {
        Text(
            stringResource(R.string.step_counter_toggle),
            Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(Modifier.width(VERTICAL_SPACE_SMALL.dp))
        Switch(stepCounterSwitchChecked, {
            if (!isCooldown) {
                isCooldown = true
                coroutineScope.launch {
                    delay(TOGGLE_COOLDOWN)
                    isCooldown = false
                }
                stepCounterSwitchChecked = checkPermissionAndLaunchStepCounter(it, context)
            }
        })
    }
}

/** Will try to change the step counter status switch and checks for permissions.
 *  Returns the new switch value **/
fun checkPermissionAndLaunchStepCounter(newSwitchCheckedVal: Boolean, context: Context): Boolean {
    return if (PermissionManager.permissionAvailable(context)) {
        newSwitchCheckedVal
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.permission_unavailable_toast),
            Toast.LENGTH_LONG
        ).show()
        PermissionManager.requestUserPermission(context as? Activity)
        false
    }
}