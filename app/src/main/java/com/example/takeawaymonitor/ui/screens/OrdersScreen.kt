package com.example.takeawaymonitor.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.takeawaymonitor.R
import com.example.takeawaymonitor.data.remote.model.Ad
import com.example.takeawaymonitor.data.remote.model.OrderedData
import com.example.takeawaymonitor.util.Utils.isYouTubeLink
import com.example.takeawaymonitor.ui.viewmodel.CustomerQueueItem
import com.example.takeawaymonitor.ui.viewmodel.OrdersViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.takeawaymonitor.ui.theme.TakeawayMonitorTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

@Composable
fun OrdersScreen(
    navController: NavController,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    OrdersScreenContent(
        apiOrders = uiState.apiOrders,
        customerQueueItems = uiState.customerQueueItems,
        ads = uiState.ads,
        serverTime = uiState.serverTime,
        timeFetchError = uiState.timeFetchError,
        onDismissTimeError = { viewModel.dismissTimeError() },
        onConfirmTimeError = {
            viewModel.dismissTimeError()
            viewModel.fetchSystemTime()
        }
    )
}

@Composable
fun OrdersScreenContent(
    apiOrders: List<OrderedData>,
    customerQueueItems: List<CustomerQueueItem>,
    ads: List<Ad>,
    serverTime: Date?,
    timeFetchError: String?,
    onDismissTimeError: () -> Unit,
    onConfirmTimeError: () -> Unit
) {
    // Show Alert Dialog if time fetch fails
    timeFetchError?.let { error ->
        AlertDialog(
            onDismissRequest = onDismissTimeError,
            title = { Text("Oops!") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = onConfirmTimeError) {
                    Text("Okay")
                }
            }
        )
    }

    // Background color based on bg_pos (approximated)
    val headerBackground = Color(0xCC333333) // item_order_monitor_trans

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_pos),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Header Section
            HeaderSection(headerBackground, serverTime)

            // Main Content Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // 1. Orders Section (Takeaway Order) - Weight 1
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    ListHeader("TAKEAWAY ORDER")
                    ApiOrdersList(
                        orders = apiOrders.filter { it.orderStatusId == "1" || it.orderStatusId == "2" }, // Pending/Preparing
                        modifier = Modifier.weight(1f)
                    )
                }

                // Divider
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.DarkGray))

                // 2. Customer Queue Section - Weight 1
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    ListHeader("CUSTOMER QUEUE")
                    CustomerQueueList(
                        items = customerQueueItems,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Version display at bottom
                    Text(
                        text = "v1.0.0",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp).align(Alignment.End)
                    )
                }

                // 3. Media Section - Weight 1.3
                if (ads.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                            .background(Color.Black)
                    ) {
                        MediaSection(ads)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(backgroundColor: Color, serverTime: Date?) {
    val displayTime = remember(serverTime) {
        val date = serverTime ?: Date()
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Half: Takeaway Order
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "TAKEAWAY ORDER",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = 2.sp
                    )
                }
            }

            // Right Half: Customer Queue
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "CUSTOMER QUEUE",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        // Clock on the far right
        Text(
            text = displayTime,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp)
        )
        
        // Logo in the absolute center
        Surface(
            modifier = Modifier.align(Alignment.Center).size(60.dp),
            color = Color.Transparent
        ) {
            // Placeholder for logo
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Image(...)
            }
        }
    }
}

@Composable
fun CustomerQueueList(items: List<CustomerQueueItem>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { index ->
            when (val item = items[index]) {
                is CustomerQueueItem.Header -> {
                    QueueGroupHeader(item.title)
                }
                is CustomerQueueItem.Order -> {
                    ApiOrderItem(item.data, index + 1)
                }
            }
        }
    }
}

@Composable
fun QueueGroupHeader(title: String) {
    Surface(
        color = Color.DarkGray.copy(alpha = 0.5f),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(8.dp),
            color = Color.Yellow,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ApiOrdersList(orders: List<OrderedData>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(orders) { index, order ->
            ApiOrderItem(order, index + 1)
        }
    }
}

@Composable
fun ApiOrderItem(order: OrderedData, displayIndex: Int) {
    Surface(
        color = Color(0xFF2D2D2D),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.code ?: displayIndex.toString(),
                color = if (order.orderStatusId == "3") Color.Green else Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 48.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ListHeader(title: String) {
    Surface(
        color = Color.Gray.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaSection(ads: List<Ad>) {
    if (ads.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { ads.size * 1000 }) // Large number for infinite-like scroll
    val actualSize = ads.size

    LaunchedEffect(Unit) {
        while (true) {
            delay(50000) // Auto scroll every 5 seconds
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val ad = ads[page % actualSize]
        MediaItemDisplay(ad)
    }
}

@Composable
fun MediaItemDisplay(ad: Ad) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            ad.url.isYouTubeLink() -> {
                // Placeholder for YouTube
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("YouTube Video", color = Color.White)
                }
            }
            ad.type == 1 -> { // Assuming 1 is video based on the JSON example
                val videoPath = ad.localPath
                if (videoPath != null && File(videoPath).exists()) {
                    VideoPlayer(File(videoPath))
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
            else -> {
                // Image
                val imageSource = ad.localPath?.let { File(it) } ?: ad.url
                AsyncImage(
                    model = imageSource,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun VideoPlayer(file: File) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(file.absolutePath)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
        }
    }

    DisposableEffect(
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false // Hide controls for monitor display
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun OrdersScreenPreview() {
    val sampleAds = listOf(
        Ad(url = "", type = 0),
        Ad(url = "", type = 1)
    )

    val sampleApiOrders = listOf(
        OrderedData(id = "1", code = "A101", orderStatusId = "1"),
        OrderedData(id = "2", code = "A102", orderStatusId = "2"),
        OrderedData(id = "3", code = "B201", orderStatusId = "3", capacity = "2"),
        OrderedData(id = "4", code = "B202", orderStatusId = "3", capacity = "2"),
        OrderedData(id = "5", code = "C301", orderStatusId = "3", capacity = "4")
    )

    val sampleCustomerQueueItems = listOf(
        CustomerQueueItem.Header("2-3", listOf(2, 3)),
        CustomerQueueItem.Order(sampleApiOrders[2]),
        CustomerQueueItem.Order(sampleApiOrders[3]),
        CustomerQueueItem.Header("4-5", listOf(4, 5)),
        CustomerQueueItem.Order(sampleApiOrders[4]),
        CustomerQueueItem.Header("Group", emptyList())
    )

    TakeawayMonitorTheme {
        OrdersScreenContent(
            apiOrders = sampleApiOrders,
            customerQueueItems = sampleCustomerQueueItems,
            ads = sampleAds,
            serverTime = Date(),
            timeFetchError = null,
            onDismissTimeError = {},
            onConfirmTimeError = {}
        )
    }
}
