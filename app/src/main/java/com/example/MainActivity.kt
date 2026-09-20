package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ArchitectureDocsScreen
import com.example.ui.screens.ConsumerPassportScreen
import com.example.ui.screens.FarmerPortalScreen
import com.example.ui.screens.SupplyChainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.TraceViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TraceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val currentTab by viewModel.currentTab.collectAsState()
                    val userMessage by viewModel.userMessage.collectAsState()
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(userMessage) {
                        userMessage?.let {
                            snackbarHostState.showSnackbar(it)
                            viewModel.clearUserMessage()
                        }
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Eco,
                                                    contentDescription = "حصاد تريس",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "حصاد تريس | HasadTrace",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier
                                    .testTag("bottom_nav_bar")
                                    .windowInsetsPadding(WindowInsets.navigationBars),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == AppNavTab.CONSUMER_PASSPORT,
                                    onClick = { viewModel.setTab(AppNavTab.CONSUMER_PASSPORT) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = "بطاقة المنتج"
                                        )
                                    },
                                    label = {
                                        Text(
                                            "بطاقة المنتج",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier.testTag("nav_passport")
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppNavTab.FARMER_PORTAL,
                                    onClick = { viewModel.setTab(AppNavTab.FARMER_PORTAL) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Agriculture,
                                            contentDescription = "بوابة المزارع"
                                        )
                                    },
                                    label = {
                                        Text(
                                            "بوابة المزارع",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier.testTag("nav_farmer")
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppNavTab.SUPPLY_CHAIN_LOGS,
                                    onClick = { viewModel.setTab(AppNavTab.SUPPLY_CHAIN_LOGS) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.LocalShipping,
                                            contentDescription = "سلسلة الإمداد"
                                        )
                                    },
                                    label = {
                                        Text(
                                            "سلسلة الإمداد",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier.testTag("nav_supply_chain")
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppNavTab.SYSTEM_ARCHITECTURE,
                                    onClick = { viewModel.setTab(AppNavTab.SYSTEM_ARCHITECTURE) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Architecture,
                                            contentDescription = "المعمارية"
                                        )
                                    },
                                    label = {
                                        Text(
                                            "المعمارية التقنية",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier.testTag("nav_architecture")
                                )
                            }
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                AppNavTab.CONSUMER_PASSPORT -> {
                                    ConsumerPassportScreen(viewModel = viewModel)
                                }
                                AppNavTab.FARMER_PORTAL -> {
                                    FarmerPortalScreen(viewModel = viewModel)
                                }
                                AppNavTab.SUPPLY_CHAIN_LOGS -> {
                                    SupplyChainScreen(viewModel = viewModel)
                                }
                                AppNavTab.SYSTEM_ARCHITECTURE -> {
                                    ArchitectureDocsScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
