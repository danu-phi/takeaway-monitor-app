package com.example.takeawaymonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.takeawaymonitor.ui.screens.DashboardScreen
import com.example.takeawaymonitor.ui.screens.OrdersScreen
import com.example.takeawaymonitor.ui.screens.AnalyticsScreen
import com.example.takeawaymonitor.ui.theme.TakeawayMonitorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {  
            TakeawayMonitorTheme {  
                Surface(  
                    modifier = Modifier.fillMaxSize(),  
                    color = MaterialTheme.colorScheme.background
                ) {  
                    AppNavigation()  
                }  
            }  
        }  
    }  
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") { DashboardScreen(navController) }
        composable("orders") { OrdersScreen(navController) }
        composable("analytics") { AnalyticsScreen(navController) }
    }
}