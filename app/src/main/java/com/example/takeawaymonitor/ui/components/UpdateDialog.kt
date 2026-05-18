package com.example.takeawaymonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.takeawaymonitor.data.remote.model.VersionData

@Composable
fun UpdateDialog(
    versionData: VersionData,
    fileSize: String,
    onCancel: () -> Unit,
    onUpdate: () -> Unit
) {
    val blueLight = Color(0xFF03A9F4)
    val bgColor = Color(0xFFF5F5F5)

    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                // Title
                Text(
                    text = "App Update",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                // Content Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // App Information Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "App Information",
                            color = blueLight,
                            fontSize = 20.sp
                        )
                    }

                    // Version, Size, Date
                    Text(text = "Version: ${versionData.version}", fontSize = 16.sp)
                    Text(text = "Size: $fileSize MB", fontSize = 16.sp)
                    Text(text = "Release Date: ${versionData.createdDate}", fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // What's new Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "What's new",
                            color = blueLight,
                            fontSize = 20.sp
                        )
                    }

                    // Description
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = versionData.description,
                            fontSize = 16.sp
                        )
                    }
                }

                // Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cancel",
                        color = blueLight,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .clickable { onCancel() }
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Update",
                        color = blueLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onUpdate() }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
