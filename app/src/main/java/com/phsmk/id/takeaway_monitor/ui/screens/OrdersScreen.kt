package com.phsmk.id.takeaway_monitor.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phsmk.id.takeaway_monitor.service.PushService
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
import androidx.compose.foundation.shape.CircleShape
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
import com.phsmk.id.takeaway_monitor.R
import com.phsmk.id.takeaway_monitor.data.remote.model.Ad
import com.phsmk.id.takeaway_monitor.data.remote.model.OrderedData
import com.phsmk.id.takeaway_monitor.data.remote.model.CustomerQueueData
import com.phsmk.id.takeaway_monitor.data.remote.model.OrderStatusType
import com.phsmk.id.takeaway_monitor.util.Utils.isYouTubeLink
import com.phsmk.id.takeaway_monitor.ui.viewmodel.CustomerQueueItem
import com.phsmk.id.takeaway_monitor.util.Utils.getVersion
import com.phsmk.id.takeaway_monitor.ui.viewmodel.OrdersViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance
import com.phsmk.id.takeaway_monitor.ui.theme.TakeawayMonitorTheme
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
    val context = LocalContext.current
    val versionName = remember { getVersion(context) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    PushService.ACTION_SEND_CREATED,
                    PushService.ACTION_SEND_UPDATED -> {
                        val orderDetail = intent.getParcelableExtra<OrderedData>(PushService.EXT_ORDER_DETAIL)
                        orderDetail?.let { viewModel.onOrderReceived(intent.action!!, it) }
                    }
                    PushService.ACTION_CUSTOMER_QUEUE_CREATED,
                    PushService.ACTION_CUSTOMER_QUEUE_UPDATED -> {
                        val queueData = intent.getParcelableExtra<CustomerQueueData>(PushService.EXT_CUSTOMER_QUEUE_DETAIL)
                        queueData?.let { viewModel.onCustomerQueueReceived(intent.action!!, it) }
                    }
                    PushService.ACTION_CUSTOMER_QUEUE_DELETED -> {
                        val customerQueueId = intent.getIntExtra(PushService.EXT_CUSTOMER_QUEUE_DETAIL, 0)
                        viewModel.onCustomerQueueDeleted(customerQueueId)
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(PushService.ACTION_SEND_CREATED)
            addAction(PushService.ACTION_SEND_UPDATED)
            addAction(PushService.ACTION_CUSTOMER_QUEUE_CREATED)
            addAction(PushService.ACTION_CUSTOMER_QUEUE_UPDATED)
            addAction(PushService.ACTION_CUSTOMER_QUEUE_DELETED)
        }
        getInstance(context).registerReceiver(receiver, filter)
        onDispose {
            getInstance(context).unregisterReceiver(receiver)
        }
    }

    OrdersScreenContent(
        apiOrders = uiState.apiOrders,
        customerQueueItems = uiState.customerQueueItems,
        ads = uiState.ads,
        serverTime = uiState.serverTime,
        outletLogoPath = uiState.outletLogoPath,
        versionName = versionName,
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
    outletLogoPath: String?,
    versionName: String,
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
            HeaderSection(headerBackground, serverTime, outletLogoPath)

            // Main Content Row
            val hasOrders = apiOrders.isNotEmpty()
            val hasQueue = customerQueueItems.isNotEmpty()
            val showData = hasOrders || hasQueue

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (showData) {
                    // 1. Orders Section (Takeaway Order) - Weight 1
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        ListHeaderRow("Customer", "Order Status")
                        ApiOrdersList(
                            orders = apiOrders,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 2. Customer Queue Section - Weight 1
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        ListHeaderRow("Customer", null)
                        CustomerQueueList(
                            items = customerQueueItems,
                            modifier = Modifier.weight(1f)
                        )

                        // Version display at bottom
                        Text(
                            text = "Version $versionName",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp).align(Alignment.End)
                        )
                    }
                } else if (ads.isNotEmpty()) {
                    // 3. Media Section - Full Width (Weight 1)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
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
fun HeaderSection(backgroundColor: Color, serverTime: Date?, outletLogoPath: String?) {
    val displayTime = remember(serverTime) {
        val date = serverTime ?: Date()
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Black.copy(alpha = 0.6f))
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
                        painter = painterResource(id = R.drawable.ic_takeaway_order),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "TAKEAWAY ORDER",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            // Right Half: Waiting List
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_waiting_list),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "WAITING LIST",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
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
        if (outletLogoPath != null) {
            AsyncImage(
                model = File(outletLogoPath),
                contentDescription = "Outlet Logo",
                modifier = Modifier.align(Alignment.Center).size(200.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            // Fallback to static resource if local file not found
            Image(
                painter = painterResource(id = R.drawable.logo_pizzahut),
                contentDescription = "Logo",
                modifier = Modifier.align(Alignment.Center).size(120.dp)
            )
        }
    }
}

@Composable
fun CustomerQueueList(items: List<CustomerQueueItem>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        items(items.size) { index ->
            when (val item = items[index]) {
                is CustomerQueueItem.Header -> {
                    QueueGroupHeader(item.title)
                }
                is CustomerQueueItem.Order -> {
                    CustomerQueueItemView(item.data)
                }
            }
        }
    }
}

@Composable
fun CustomerQueueItemView(data: CustomerQueueData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.1f))
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = data.customerName ?: "",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QueueGroupHeader(title: String) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 16.dp),
                color = Color.LightGray,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            )
            // Bottom Divider for the header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.1f))
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun ApiOrdersList(orders: List<OrderedData>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(orders) { index, order ->
            ApiOrderItem(order, index)
        }
    }
}

@Composable
fun ApiOrderItem(order: OrderedData, index: Int) {
    val backgroundColor = if (index % 2 == 0) Color(0xCC333333) else Color.Transparent

    val statusText = when (order.orderStatusId) {
        OrderStatusType.ORDERED.statusNumber -> "Received"
        OrderStatusType.PICKEDUP.statusNumber,
        OrderStatusType.FINISHED.statusNumber -> "Ready"
        OrderStatusType.COOKING.statusNumber,
        OrderStatusType.CHECKOUT.statusNumber,
        OrderStatusType.COOKED.statusNumber -> "Cooking"
        else -> OrderStatusType.statusOf(order.orderStatusId).toString()
    }

    val statusColor = when (order.orderStatusId) {
        OrderStatusType.PICKEDUP.statusNumber,
        OrderStatusType.CHECKOUT.statusNumber,
        OrderStatusType.FINISHED.statusNumber -> Color(0xFF63F561)
        else -> Color(0xFFFFF291)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .height(60.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.name ?: "",
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp
            )
            Text(
                text = statusText,
                color = statusColor,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun ListHeaderRow(leftTitle: String, rightTitle: String?) {
    Surface(
        color = Color.Black.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = leftTitle,
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            if (rightTitle != null) {
                Text(
                    text = rightTitle,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.End
                )
            }
        }
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
        OrderedData(id = "2", code = "A102", orderStatusId = "2")
    )

    val sampleCustomerQueueItems = listOf(
        CustomerQueueItem.Header("2-3", listOf(2, 3)),
        CustomerQueueItem.Order(CustomerQueueData(id = "3", customerName = "Najwa", capacity = "8")),
        CustomerQueueItem.Header("Group", emptyList())
    )

    TakeawayMonitorTheme {
        OrdersScreenContent(
            apiOrders = sampleApiOrders,
            customerQueueItems = sampleCustomerQueueItems,
            ads = sampleAds,
            serverTime = Date(),
            outletLogoPath = null,
            versionName = "2.0.5",
            timeFetchError = null,
            onDismissTimeError = {},
            onConfirmTimeError = {}
        )
    }
}
