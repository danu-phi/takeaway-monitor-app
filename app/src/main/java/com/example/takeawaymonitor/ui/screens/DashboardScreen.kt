package com.example.takeawaymonitor.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.takeawaymonitor.R
import com.example.takeawaymonitor.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val isFetching by viewModel.isFetching.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.bg_pos),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Logo in the center
        Image(
            painter = painterResource(id = R.drawable.logo_pizzahut),
            contentDescription = "Logo",
            modifier = Modifier.align(Alignment.Center)
        )

        // Progress bar at the bottom center
        if (isFetching) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
