package com.example.steptrackerwithlocaldatabase

import android.widget.ToggleButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomepageView() {
    val myViewModel = viewModel<HomepageViewModel>()
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Steps", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(8.dp))
            Text(myViewModel.getStepsString(), style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(16.dp))
            Button({
                myViewModel.resetSteps()
            }) {
                Text("Reset", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Button({
            myViewModel.incrementSteps()
        }, Modifier.align(Alignment.BottomStart)) {
            Text("Mock step", style = MaterialTheme.typography.labelSmall)
        }
    }
}

class HomepageViewModel() : ViewModel() {
    private var steps by mutableStateOf(0)
    private var dataWriterActive by mutableStateOf(true)

    fun getStepsString(): String = steps.toString()

    fun incrementSteps() = steps++

    fun resetSteps() { steps = 0 }
}