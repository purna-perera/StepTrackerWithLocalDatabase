package com.example.steptrackerwithlocaldatabase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private const val TAB_SHADOW_ELEVATION = 8
private const val HORIZONTAL_PADDING = 9
private const val VERTICAL_SPACE = 8
private const val FIRST_COLUMN_WIDTH = 80
private const val DIVIDER_WIDTH = 1

/** Side tab which displays and manages the users step history **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTab() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val dividerColour = MaterialTheme.colorScheme.inversePrimary

    ModalNavigationDrawer({
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(LocalConfiguration.current.screenWidthDp.dp * 4 / 5),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = TAB_SHADOW_ELEVATION.dp
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = HORIZONTAL_PADDING.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.history_tab_title),
                        Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Button({ HistoryManager.clearHistory(context) }) {
                        Text(
                            stringResource(R.string.history_reset_button),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Spacer(Modifier.height(VERTICAL_SPACE.dp))
                HistoryTableHeaders(Modifier.height(IntrinsicSize.Min), dividerColour)
                Divider(Modifier.fillMaxWidth().height(DIVIDER_WIDTH.dp), color = dividerColour)
                HistoryTableContent(dividerColour)
            }
        }
    }, drawerState = drawerState) {
        HomepageView { coroutineScope.launch { drawerState.open() } }
    }
}

@Composable
fun HistoryTableHeaders(modifier: Modifier, dividerColour: Color) {
    Row(modifier) {
        Text(
            stringResource(R.string.step_column_title),
            Modifier.width(FIRST_COLUMN_WIDTH.dp).padding(horizontal = HORIZONTAL_PADDING.dp),
            style = MaterialTheme.typography.headlineSmall
        )
        Divider(Modifier.fillMaxHeight().width(DIVIDER_WIDTH.dp), color = dividerColour)
        Text(
            stringResource(R.string.timestamp_title),
            Modifier.padding(horizontal = HORIZONTAL_PADDING.dp),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun HistoryTableContent(dividerColour: Color) {
    val historyList by HistoryManager.historyListFlow.collectAsState()

    LazyColumn {
        items(historyList.length(), { index ->  HistoryManager.getHistoryItem(index).timestamp }) {
            val item = HistoryManager.getHistoryItem(it)
            Row(Modifier.height(IntrinsicSize.Min)) {
                Text(
                    item.stepCount.toString(),
                    Modifier.width(FIRST_COLUMN_WIDTH.dp).padding(horizontal = HORIZONTAL_PADDING.dp)
                )
                Divider(Modifier.fillMaxHeight().width(DIVIDER_WIDTH.dp), color = dividerColour)
                Text(
                    item.timestamp,
                    Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = HORIZONTAL_PADDING.dp)
                )
            }
        }
    }
}
