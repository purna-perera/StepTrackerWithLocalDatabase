package com.example.steptrackerwithlocaldatabase.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steptrackerwithlocaldatabase.HistoryManager
import com.example.steptrackerwithlocaldatabase.HomepageView
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTab() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val historyList by HistoryManager.historyListFlow.collectAsState()
    ModalNavigationDrawer({
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(LocalConfiguration.current.screenWidthDp.dp * 4 / 5),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 8.dp
        ) {
            Column(Modifier.fillMaxWidth()) {
                Text("My History")
                Spacer(Modifier.height(16.dp))
                LazyColumn {
                    items(historyList.length()) {
                        val item = HistoryManager.getHistoryItem(it)
                        Row {
                            Text(item.stepCount.toString())
                            Text(item.timestamp)
                        }
                    }
                }
            }
        }
    }, drawerState = drawerState) {
        HomepageView()
    }
}
