package com.phsmk.id.takeaway_monitor

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.phsmk.id.takeaway_monitor.service.PushService
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.phsmk.id.takeaway_monitor.ui.components.UpdateDialog
import com.phsmk.id.takeaway_monitor.ui.screens.AnalyticsScreen
import com.phsmk.id.takeaway_monitor.ui.screens.DashboardScreen
import com.phsmk.id.takeaway_monitor.ui.screens.OrdersScreen
import com.phsmk.id.takeaway_monitor.ui.theme.TakeawayMonitorTheme
import com.phsmk.id.takeaway_monitor.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            TakeawayMonitorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val errorState by viewModel.errorState.collectAsState()
                    val updateDialogState by viewModel.showUpdateDialog.collectAsState()
                    val downloadProgress by viewModel.downloadProgress.collectAsState()

                    // Show error dialog if fetch fails
                    errorState?.let { error ->
                        AlertDialog(
                            onDismissRequest = { viewModel.dismissError() },
                            title = { Text("Error") },
                            text = { Text(error) },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.dismissError()
                                    viewModel.fetchConfigs()
                                }) {
                                    Text("Retry")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { viewModel.dismissError() }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    updateDialogState?.let { (versionData, size) ->
                        UpdateDialog(
                            versionData = versionData,
                            fileSize = size,
                            onCancel = {
                                viewModel.onUpdateDialogDismissed()
                                window.decorView.post {
                                    if (!isFinishing) {
                                        finish()
                                    }
                                }
                            },
                            onUpdate = {
                                viewModel.onUpdateDialogDismissed()
                                checkDownload()
                            }
                        )
                    }

                    downloadProgress?.let { progress ->
                        DownloadProgressDialog(progress)
                    }

                    LaunchedEffect(Unit) {
                        viewModel.fetchConfigs()
                    }

                    LaunchedEffect(Unit) {
                        viewModel.startPushService.collectLatest {
                            startService(Intent(this@MainActivity, PushService::class.java))
                        }
                    }

                    LaunchedEffect(viewModel.navigateToOrders) {
                        viewModel.navigateToOrders.collectLatest {
                            navController.navigate("orders") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    }

                    AppNavigation(navController)
                }
            }
        }
    }

    private fun checkDownload() {
        startDownload()
    }

    private fun startDownload() {
        viewModel.startDownload { file ->
            openNewVersion(file)
        }
    }

    private fun openNewVersion(file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
                Toast.makeText(this, "Please allow installation from this source", Toast.LENGTH_LONG).show()
                return
            }
        }
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val apkUri = FileProvider.getUriForFile(
                this@MainActivity,
                "${applicationContext.packageName}.provider",
                file
            )
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        try {
            startActivity(intent)
            window.decorView.postDelayed({
                if (!isFinishing) {
                    finish()
                }
            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error opening APK: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun DownloadProgressDialog(progress: Int) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (progress > 99) "Finishing..." else "Downloading... $progress%",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AppNavigation(navController: androidx.navigation.NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") { DashboardScreen(navController) }
        composable("orders") { OrdersScreen(navController) }
        composable("analytics") { AnalyticsScreen(navController) }
    }
}
