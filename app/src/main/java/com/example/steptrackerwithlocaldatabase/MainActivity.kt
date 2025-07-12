package com.example.steptrackerwithlocaldatabase

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.steptrackerwithlocaldatabase.ui.theme.HistoryTab
import com.example.steptrackerwithlocaldatabase.ui.theme.StepTrackerWithLocalDatabaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        StepDataManager.init(this)
        HistoryManager.init(this)
        super.onCreate(savedInstanceState)
        setContent {
            StepTrackerWithLocalDatabaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HistoryTab()
                }
            }
        }
    }
}