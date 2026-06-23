package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.TasbihRepository
import com.example.ui.TasbihViewModel
import com.example.ui.TasbihViewModelFactory
import com.example.ui.screens.AddCounterModal
import com.example.ui.screens.CounterWorkspaceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen {
    Dashboard, Workspace
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TasbihRepository(database.tasbihDao())
        val viewModel = ViewModelProvider(this, TasbihViewModelFactory(repository))[TasbihViewModel::class.java]

        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.Dashboard) }
                var showAddCounterSheet by remember { mutableStateOf(false) }

                when (currentScreen) {
                    AppScreen.Dashboard -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToWorkspace = { currentScreen = AppScreen.Workspace },
                            onShowAddCounterModal = { showAddCounterSheet = true }
                        )
                    }
                    AppScreen.Workspace -> {
                        CounterWorkspaceScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = AppScreen.Dashboard }
                        )
                    }
                }

                if (showAddCounterSheet) {
                    AddCounterModal(
                        viewModel = viewModel,
                        onDismiss = { showAddCounterSheet = false }
                    )
                }
            }
        }
    }
}

