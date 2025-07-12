package com.example.steptrackerwithlocaldatabase.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    ModalNavigationDrawer({
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(LocalConfiguration.current.screenWidthDp.dp * 4 / 5),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 8.dp
        ) {

            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "My History",
                        Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Button({ HistoryManager.clearHistory(context) }) {
                        Text("Clear history", style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Column() {
                    Row(Modifier.height(IntrinsicSize.Min)) {
                        Text(
                            "Steps",
                            Modifier
                                .width(80.dp)
                                .padding(horizontal = 9.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Divider(
                            Modifier
                                .fillMaxHeight()
                                .width(1.dp),
                            color = MaterialTheme.colorScheme.inversePrimary
                        )
                        Text(
                            "Time (UTC +0)",
                            Modifier.padding(horizontal = 9.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                    LazyColumn {
                        items(historyList.length()) {
                            val item = HistoryManager.getHistoryItem(it)
                            Row(Modifier.height(IntrinsicSize.Min)) {
                                Text(item.stepCount.toString(),
                                    Modifier
                                        .width(80.dp)
                                        .padding(horizontal = 9.dp))
                                Divider(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.inversePrimary
                                )
                                Text(
                                    item.timestamp,
                                    Modifier
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 9.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }, drawerState = drawerState) {
        HomepageView { coroutineScope.launch { drawerState.open() } }
    }
}
