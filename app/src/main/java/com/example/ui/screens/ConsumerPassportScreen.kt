package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CropBatch
import com.example.data.model.Farm
import com.example.data.model.TraceEvent
import com.example.qrcode.QrCodeView
import com.example.security.CryptoTraceHelper
import com.example.security.SecurityAudit
import com.example.ui.viewmodel.TraceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerPassportScreen(
    viewModel: TraceViewModel,
    modifier: Modifier = Modifier
) {
    val selectedBatch by viewModel.selectedBatch.collectAsState()
    val selectedFarm by viewModel.selectedFarm.collectAsState()
    val events by viewModel.selectedBatchEvents.collectAsState()
    val allBatches by viewModel.allBatches.collectAsState()
    val securityAudit by viewModel.securityAudit.collectAsState()
    val isSignatureValid by viewModel.isSignatureValid.collectAsState()

    var showScannerDialog by remember { mutableStateOf(false) }
    var showQrModal by remember { mutableStateOf(false) }
    var scanInputText by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("consumer_passport_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Quick Switcher & Scanner Bar
        item {
            QuickSelectorBar(
                allBatches = allBatches,
                currentBatchId = selectedBatch?.batchId,
                onSelectBatch = { viewModel.selectBatch(it) },
                onOpenScanner = { showScannerDialog = true }
            )
        }

        if (selectedBatch == null || selectedFarm == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "جاري تحميل بطاقة الهوية الرقمية للمنتج...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            val batch = selectedBatch!!
            val farm = selectedFarm!!

            // 2. Hero Header with Farm Landscape & Title
            item {
                ProductHeroCard(
                    batch = batch,
                    farm = farm,
                    onViewQr = { showQrModal = true }
                )
            }

            // 3. Authenticity & Cryptographic Verification Card
            item {
                AuthenticityBadgeCard(
                    batch = batch,
                    securityAudit = securityAudit,
                    isSignatureValid = isSignatureValid,
                    onViewQr = { showQrModal = true }
                )
            }

            // 4. Farm Origin & Farmer Profile Card
            item {
                FarmOriginCard(farm = farm)
            }

            // 5. Agricultural Practices (Irrigation, Soil, Pest Control)
            item {
                AgriPracticesCard(batch = batch, farm = farm)
            }

            // 6. Quality & Lab Analysis Metrics
            item {
                QualityMetricsCard(batch = batch, farm = farm)
            }

            // 7. Interactive Journey Timeline (Farm-to-Table)
            item {
                JourneyTimelineCard(events = events)
            }

            // 8. Footer Trust & Share Actions
            item {
                TrustActionCard(
                    batch = batch,
                    farm = farm,
                    onShare = {
                        val shareText = "بطاقة الهوية الرقمية للمنتج: ${batch.cropName} من ${farm.name}\n" +
                                "الرقم التسلسلي: ${batch.batchId}\n" +
                                "الرابط الموثق: ${CryptoTraceHelper.buildQrPayloadUrl(batch.batchId, farm.id, batch.hmacSignature)}"
                        clipboardManager.setText(AnnotatedString(shareText))
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Scanner / Manual QR Code Tester Dialog
    if (showScannerDialog) {
        AlertDialog(
            onDismissRequest = { showScannerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مسح رمز QR أو فحص دفعة")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "امسح الرمز الملصق على عبوة المنتج، أو اختر من الدفعات النموذجية المتاحة أدناه لتجربة التحقق الفوري:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = scanInputText,
                        onValueChange = { scanInputText = it },
                        label = { Text("أدخل رمز الدفعة أو رابط التحقق") },
                        placeholder = { Text("مثال: BATCH-AHS-2026-0982") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(
                        text = "دفعات نموذجية للتجربة:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    allBatches.forEach { b ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scanInputText = b.batchId
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = b.cropName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = b.batchId.takeLast(9),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (scanInputText.isNotBlank()) {
                            viewModel.scanQrCode(scanInputText)
                        }
                        showScannerDialog = false
                    },
                    modifier = Modifier.testTag("submit_scan_button")
                ) {
                    Text("تحقق الآن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScannerDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // QR Code Large Modal
    if (showQrModal && selectedBatch != null && selectedFarm != null) {
        val b = selectedBatch!!
        val f = selectedFarm!!
        val qrUrl = CryptoTraceHelper.buildQrPayloadUrl(b.batchId, f.id, b.hmacSignature)

        AlertDialog(
            onDismissRequest = { showQrModal = false },
            title = {
                Text(
                    text = "رمز التتبع الرقمي المعتمد",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = b.cropName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = b.batchId,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    QrCodeView(
                        data = qrUrl,
                        size = 230.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "يحمل هذا الرمز توقيعاً تشفيرياً HMAC-SHA256 يربط العبوة بسجل المزرعة في السحابة لمنع أي تزوير.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showQrModal = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun QuickSelectorBar(
    allBatches: List<CropBatch>,
    currentBatchId: String?,
    onSelectBatch: (String) -> Unit,
    onOpenScanner: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "استعراض الهوية الرقمية للمحاصيل:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onOpenScanner,
                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("open_scanner_bar_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "مسح QR",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مسح كود", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allBatches) { batch ->
                    val isSelected = batch.batchId == currentBatchId
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectBatch(batch.batchId) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.LocalFlorist,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = batch.cropName.take(18) + if (batch.cropName.length > 18) "..." else "",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductHeroCard(
    batch: CropBatch,
    farm: Farm,
    onViewQr: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.farm_hero_banner),
                    contentDescription = "صورة المزرعة العضوية",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xCC0D2817)
                                )
                            )
                        )
                )

                // Top Floating Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xDD15803D),
                        contentColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "منتج عضوي معتمد 100%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onViewQr() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "عرض QR",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Bottom Content on Image
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = batch.cropName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${batch.variety} • ${farm.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD4F4E2)
                    )
                }
            }

            // Info Bar below image
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                HeroStatItem(
                    icon = Icons.Default.Agriculture,
                    label = "تاريخ الجني",
                    value = batch.harvestDate.substringBefore(" ")
                )
                HeroStatItem(
                    icon = Icons.Default.LocationOn,
                    label = "المصدر",
                    value = farm.region.substringBefore("،").substringBefore("-")
                )
                HeroStatItem(
                    icon = Icons.Default.FactCheck,
                    label = "الدرجة",
                    value = batch.qualityGrade.substringBefore("(")
                )
            }
        }
    }
}

@Composable
fun HeroStatItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AuthenticityBadgeCard(
    batch: CropBatch,
    securityAudit: SecurityAudit?,
    isSignatureValid: Boolean,
    onViewQr: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSignatureValid) Color(0xFFEFFBF3) else Color(0xFFFEE2E2)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSignatureValid) Color(0xFF86EFAC) else Color(0xFFFCA5A5)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSignatureValid) Color(0xFF16A34A) else Color(0xFFDC2626),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSignatureValid) Icons.Default.Shield else Icons.Default.Security,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isSignatureValid) "بطاقة هوية رقمية موثقة وآمنة" else "تنبيه: توقيع رقمي غير مطابق!",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSignatureValid) Color(0xFF14532D) else Color(0xFF7F1D1D)
                        )
                        Text(
                            text = "نظام تشفير HMAC-SHA256 • شهادة صالحة",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF3F6212)
                        )
                    }
                }

                TextButton(onClick = onViewQr) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("رمز QR")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "الرقم التسلسلي للدفعة:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = batch.batchId,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "البصمة التشفيرية (HMAC):",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = batch.hmacSignature,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "عدد مرات التحقق من الرمز:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "${batch.scanCount} مرات",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (securityAudit != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = securityAudit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1E3A1E)
                )
            }
        }
    }
}

@Composable
fun FarmOriginCard(farm: Farm) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مصدر المنتج والمزرعة المنتجة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = farm.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "المالك / المهندس المسؤول: ${farm.ownerName}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            DetailRow(label = "الموقع الجغرافي", value = farm.region)
            DetailRow(label = "إحداثيات الحقل", value = "${farm.latitude}° N, ${farm.longitude}° E")
            DetailRow(label = "مساحة المزرعة", value = "${farm.areaHectares} هكتار")
            DetailRow(label = "رقم الشهادة العضوية", value = farm.organicCertNumber)
            DetailRow(label = "جهة التوثيق والاعتماد", value = farm.certBody)
            DetailRow(label = "سنة التأسيس", value = "${farm.establishedYear} م")
        }
    }
}

@Composable
fun AgriPracticesCard(batch: CropBatch, farm: Farm) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الممارسات الزراعية والبيئية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PracticeItem(
                icon = Icons.Default.WaterDrop,
                title = "نظام ومصدر مياه الري",
                description = farm.irrigationType + " • " + batch.irrigationDetails
            )

            PracticeItem(
                icon = Icons.Default.Eco,
                title = "طريقة الزراعة والتربة",
                description = "${farm.soilType} • ${batch.farmingMethod}"
            )

            PracticeItem(
                icon = Icons.Default.LocalFlorist,
                title = "التسميد والمغذيات العضوية",
                description = batch.fertilizerUsed
            )

            PracticeItem(
                icon = Icons.Default.Shield,
                title = "المكافحة والوقاية النباتية",
                description = batch.pestControl
            )
        }
    }
}

@Composable
fun PracticeItem(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QualityMetricsCard(batch: CropBatch, farm: Farm) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FactCheck,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الفحوصات المخبرية ومعايير الجودة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricBox(
                    title = "متبقيات المبيدات",
                    value = "0.00 ppm",
                    subtitle = "خالي تماماً",
                    color = Color(0xFF15803D),
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    title = "مؤشر النضج / Brix",
                    value = batch.sugarBrix,
                    subtitle = "حلاوة طبيعية",
                    color = Color(0xFFB45309),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            DetailRow(label = "تقرير الفحص المخبري", value = batch.labReportNumber)
            DetailRow(label = "نتيجة الفحص", value = batch.labTestStatus)
            DetailRow(label = "التصنيف والفرز", value = batch.qualityGrade)
        }
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun JourneyTimelineCard(events: List<TraceEvent>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "رحلة المنتج من المزرعة إلى المائدة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "سلسلة محطات الشفافية الموثقة زمنياً",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            events.forEachIndexed { index, event ->
                val isLast = index == events.lastIndex
                TimelineStepItem(event = event, isLast = isLast)
            }
        }
    }
}

@Composable
fun TimelineStepItem(event: TraceEvent, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${event.stepOrder}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Text(
                text = event.stageName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• ${event.timestamp}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = event.details,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (event.temperatureRecorded != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE0F2FE),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = Color(0xFF0369A1),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "الحرارة المسجلة: ${event.temperatureRecorded}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF0369A1),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TrustActionCard(
    batch: CropBatch,
    farm: Farm,
    onShare: () -> Unit
) {
    var isCopied by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "الشفافية هي جوهر الثقة الزراعية",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "كل دفعة تمر بنظام تدقيق رقمي متصل بالمزارع ومحطات الفحص لضمان وصول غذاء نقي 100% لمائدتك.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onShare()
                    isCopied = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("share_passport_button")
            ) {
                Icon(
                    imageVector = if (isCopied) Icons.Default.CheckCircle else Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isCopied) "تم نسخ رابط التوثيق!" else "مشاركة بطاقة المنتج الرقمية")
            }
        }
    }
}
