package com.example.steptrackerwithlocaldatabase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.steptrackerwithlocaldatabase.ui.theme.StepTrackerWithLocalDatabaseTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        StepDataManager.init(this)
        HistoryManager.init(this)
        super.onCreate(savedInstanceState)
        setContent {
            StepTrackerWithLocalDatabaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Homepage is wrapped in history tab
                    HistoryTab()
                }
            }
        }
    }
}