package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CropBatch
import com.example.data.model.Farm
import com.example.qrcode.QrCodeView
import com.example.security.CryptoTraceHelper
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.TraceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerPortalScreen(
    viewModel: TraceViewModel,
    modifier: Modifier = Modifier
) {
    val allBatches by viewModel.allBatches.collectAsState()
    val allFarms by viewModel.allFarms.collectAsState()

    var isAddingNewBatch by remember { mutableStateOf(false) }
    var selectedLabelBatch by remember { mutableStateOf<CropBatch?>(null) }

    // Form states
    var selectedFarmId by remember(allFarms) { mutableStateOf(allFarms.firstOrNull()?.id ?: "") }
    var cropName by remember { mutableStateOf("رمان الطائف العضوي الفاخر") }
    var cropCategory by remember { mutableStateOf("فواكه جبلية") }
    var variety by remember { mutableStateOf("رمان وادي محرم - نخب ممتاز") }
    var quantityDesc by remember { mutableStateOf("1,500 كجم (300 صندوق × 5 كجم)") }
    var fieldPlot by remember { mutableStateOf("حقل البساتين الشمالي - قطعة 3C") }
    var irrigationDetails by remember { mutableStateOf("ري من عيون وادي محرم العذبة بالتنقيط المتوازن") }
    var fertilizerUsed by remember { mutableStateOf("سماد بلدي طبيعي مخمر بدون أسمدة كيماوية") }
    var pestControl by remember { mutableStateOf("مصائد فرمونية طبيعية ومكافحة حيوية متكاملة") }
    var qualityGrade by remember { mutableStateOf("نخب أول ممتاز (Grade A+)") }
    var labTestStatus by remember { mutableStateOf("خلو تام من متبقيات المبيدات 0.00 ppm") }
    var labReportNumber by remember { mutableStateOf("LAB-TAIF-2026-4410") }
    var sugarBrix by remember { mutableStateOf("16.4° Brix (حلاوة مركزة)") }

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.format(Date())
    }
    var harvestDate by remember { mutableStateOf("$currentDateStr ص") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("farmer_portal_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Farmer Dashboard Header & High-Contrast Mode Card
        item {
            FarmerHeaderCard(
                totalBatches = allBatches.size,
                onAddNewClick = { isAddingNewBatch = !isAddingNewBatch },
                isAdding = isAddingNewBatch
            )
        }

        // 2. New Batch Creation Form (Expandable)
        if (isAddingNewBatch) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.primary
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Agriculture,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تسجيل دفعة محصول جديدة وتوليد QR",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { isAddingNewBatch = false }) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق")
                            }
                        }

                        Text(
                            text = "واجهة ميدانية مبسطة: أدخل تفاصيل الجني ليقوم النظام بتوليد رمز الـ QR المشفر بتوقيع HMAC لطباعة الملصق فوراً.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Divider()

                        // Farm Selector
                        Text("اختر المزرعة التابعة للدفعة:", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            allFarms.forEach { f ->
                                val isSelected = f.id == selectedFarmId
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedFarmId = f.id }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = f.name.take(16),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // Crop Info Fields
                        OutlinedTextField(
                            value = cropName,
                            onValueChange = { cropName = it },
                            label = { Text("اسم المحصول التجاري") },
                            modifier = Modifier.fillMaxWidth().testTag("crop_name_input"),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = variety,
                                onValueChange = { variety = it },
                                label = { Text("الصنف / النخب") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = cropCategory,
                                onValueChange = { cropCategory = it },
                                label = { Text("التصنيف") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = quantityDesc,
                                onValueChange = { quantityDesc = it },
                                label = { Text("كمية الدفعة والوزن") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = fieldPlot,
                                onValueChange = { fieldPlot = it },
                                label = { Text("القطعة الحقلية / الحوض") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = harvestDate,
                            onValueChange = { harvestDate = it },
                            label = { Text("تاريخ وساعة الجني") },
                            trailingIcon = {
                                TextButton(onClick = {
                                    harvestDate = "$currentDateStr ص"
                                }) {
                                    Text("الآن", style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Agri Specs
                        OutlinedTextField(
                            value = irrigationDetails,
                            onValueChange = { irrigationDetails = it },
                            label = { Text("نظام ومصدر مياه الري") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = fertilizerUsed,
                            onValueChange = { fertilizerUsed = it },
                            label = { Text("المخصبات والتسميد العضوي") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = sugarBrix,
                                onValueChange = { sugarBrix = it },
                                label = { Text("نسبة السكر / الحموضة") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = labReportNumber,
                                onValueChange = { labReportNumber = it },
                                label = { Text("رقم تقرير المختبر") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (cropName.isNotBlank() && selectedFarmId.isNotBlank()) {
                                    viewModel.createNewBatch(
                                        farmId = selectedFarmId,
                                        cropName = cropName,
                                        cropCategory = cropCategory,
                                        variety = variety,
                                        quantityDesc = quantityDesc,
                                        harvestDate = harvestDate,
                                        fieldPlot = fieldPlot,
                                        farmingMethod = "عضوي موثق 100% - خالي من الكيماويات والمبيدات",
                                        irrigationDetails = irrigationDetails,
                                        fertilizerUsed = fertilizerUsed,
                                        pestControl = pestControl,
                                        qualityGrade = qualityGrade,
                                        labTestStatus = labTestStatus,
                                        labReportNumber = labReportNumber,
                                        sugarBrix = sugarBrix
                                    )
                                    isAddingNewBatch = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("generate_qr_submit_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "توليد كود الـ QR المشفر وإصدار بطاقة الدفعة",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. Section Title: Registered Batches
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سجل الدفعات الحقلية المعتمدة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${allBatches.size} دفعات مسجلة",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 4. Batches List
        items(allBatches) { batch ->
            val farm = allFarms.find { it.id == batch.farmId }
            FarmerBatchCard(
                batch = batch,
                farmName = farm?.name ?: "مزرعة عضوية معتمدة",
                onInspect = {
                    viewModel.selectBatch(batch.batchId)
                    viewModel.setTab(AppNavTab.CONSUMER_PASSPORT)
                },
                onPrintLabel = { selectedLabelBatch = batch }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Label Print / Preview Modal
    if (selectedLabelBatch != null) {
        val b = selectedLabelBatch!!
        val farm = allFarms.find { it.id == b.farmId } ?: allFarms.firstOrNull()
        val qrUrl = if (farm != null) {
            CryptoTraceHelper.buildQrPayloadUrl(b.batchId, farm.id, b.hmacSignature)
        } else b.batchId

        AlertDialog(
            onDismissRequest = { selectedLabelBatch = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ملصق الدفعة الذكي (Packaging Label)")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "هذا النموذج مخصص للطباعة الحرارية الميدانية وإلصاقه على صناديق المحصول والعبوات:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Physical Label Simulation
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1E5E3A)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = Color(0xFF1E5E3A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "منتج عضوي فاخر",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E5E3A)
                                    )
                                }
                                Text(
                                    text = "ISO 22000 / GAP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = b.cropName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF14241B),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = farm?.name ?: "المزرعة المنتجة",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4B6354)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            QrCodeView(
                                data = qrUrl,
                                size = 170.dp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "الرمز التسلسلي: ${b.batchId}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "تاريخ الجني: ${b.harvestDate.substringBefore(" ")} • القطعة: ${b.fieldPlot.take(15)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "امسح الرمز بكاميرا الجوال لعرض شهادة الفحص والهوية الرقمية",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = Color(0xFF1E5E3A),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedLabelBatch = null
                        viewModel.clearUserMessage()
                    }
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إرسال للطابعة الحرارية")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedLabelBatch = null }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun FarmerHeaderCard(
    totalBatches: Int,
    onAddNewClick: () -> Unit,
    isAdding: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "بوابة المزارع والمنتج الحقلية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "نظام التوثيق الميداني الفوري وتوليد الملصقات",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                Button(
                    onClick = onAddNewClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAdding) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("toggle_new_batch_btn")
                ) {
                    Icon(
                        imageVector = if (isAdding) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isAdding) "إلغاء" else "دفعة جديدة")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FarmerStatChip(
                    label = "الدفعات النشطة",
                    value = "$totalBatches",
                    modifier = Modifier.weight(1f)
                )
                FarmerStatChip(
                    label = "التوقيع التشفيري",
                    value = "HMAC-256",
                    modifier = Modifier.weight(1f)
                )
                FarmerStatChip(
                    label = "وضع العمل",
                    value = "متصل وسحابي",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FarmerStatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun FarmerBatchCard(
    batch: CropBatch,
    farmName: String,
    onInspect: () -> Unit,
    onPrintLabel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = batch.cropName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$farmName • ${batch.fieldPlot}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (batch.currentStage) {
                        "DELIVERED" -> Color(0xFFDCFCE7)
                        "IN_TRANSIT" -> Color(0xFFE0F2FE)
                        "PACKAGED" -> Color(0xFFFEF3C7)
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                ) {
                    Text(
                        text = when (batch.currentStage) {
                            "DELIVERED" -> "تم التوصيل"
                            "IN_TRANSIT" -> "قيد النقل المبرد"
                            "PACKAGED" -> "تم التغليف"
                            else -> "تم الجني"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (batch.currentStage) {
                            "DELIVERED" -> Color(0xFF15803D)
                            "IN_TRANSIT" -> Color(0xFF0369A1)
                            "PACKAGED" -> Color(0xFFB45309)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "الرقم: ${batch.batchId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "الكمية: ${batch.quantityDesc.substringBefore("(")}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "تاريخ الجني: ${batch.harvestDate} • نسبة السكر: ${batch.sugarBrix}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrintLabel,
                    contentPadding = ButtonDefaults.TextButtonContentPadding
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("معاينة الملصق والـ QR", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onInspect,
                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("عرض الهوية الرقمية", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
