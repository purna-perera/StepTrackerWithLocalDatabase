package com.example.steptrackerwithlocaldatabase

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomepageView() {
    val myViewModel = viewModel<HomepageViewModel>()
    val context = LocalContext.current
    LaunchedEffect(myViewModel.getDataWriterSwitchChecked()) {
        if (myViewModel.getDataWriterSwitchChecked()) {
            DataWriterController.startDataWriter(context)
        } else {
            DataWriterController.stopDataWriter(context)
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
                myViewModel.onResetButtonClick()
            }) {
                Text("Reset", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Button({
            myViewModel.onMockStepButtonClick()
        }, Modifier.align(Alignment.BottomStart)) {
            Text("Mock step", style = MaterialTheme.typography.labelSmall)
        }
        Row(Modifier.align(Alignment.BottomEnd)) {
            Text("Record history", Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(4.dp))
            Switch(myViewModel.getDataWriterSwitchChecked(), {
                myViewModel.onDataWriterSwitchClicked(it)
            })
        }
    }
}

class HomepageViewModel() : ViewModel() {
    private var steps by mutableStateOf(0)
    private var dataWriterActive by mutableStateOf(true)

    fun getTotalStepsString(): String = steps.toString()

    fun onMockStepButtonClick() = steps++

    fun onResetButtonClick() { steps = 0 }

    fun onDataWriterSwitchClicked(checked: Boolean) {
        dataWriterActive = checked
    }

    fun getDataWriterSwitchChecked() = dataWriterActive
}