package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.example.data.model.CropBatch
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.TraceViewModel

@Composable
fun SupplyChainScreen(
    viewModel: TraceViewModel,
    modifier: Modifier = Modifier
) {
    val allBatches by viewModel.allBatches.collectAsState()
    val selectedBatch by viewModel.selectedBatch.collectAsState()
    val events by viewModel.selectedBatchEvents.collectAsState()

    var activeBatchId by remember(selectedBatch) {
        mutableStateOf(selectedBatch?.batchId ?: allBatches.firstOrNull()?.batchId ?: "")
    }

    // New Checkpoint Form States
    var selectedStageType by remember { mutableStateOf("الشحن المبرد (Cold Chain)") }
    var locationInput by remember { mutableStateOf("طريق الرياض السريع - شاحنة تبريد ذكية #402") }
    var operatorInput by remember { mutableStateOf("سائق التبريد المعتمد / مراقب سلاسل الإمداد") }
    var detailsInput by remember { mutableStateOf("متابعة مستمرة لحرارة الشحنة عبر مجسات IoT والتأكد من ثبات درجات الحفظ.") }
    var temperatureInput by remember { mutableStateOf("4.2°C") }

    val currentTargetBatch = allBatches.find { it.batchId == activeBatchId } ?: selectedBatch

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("supply_chain_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "محطات التتبع وسلسلة التبريد (Cold Chain)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "تسجيل نقاط الفحص الميدانية والنقل حتى وصول المنتج للمستهلك",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // 2. Batch Selection
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "اختر الدفعة المراد تحديث مرحلتها:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allBatches.forEach { b ->
                        val isSelected = b.batchId == activeBatchId
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    activeBatchId = b.batchId
                                    viewModel.selectBatch(b.batchId)
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = b.cropName.take(14),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = b.batchId.takeLast(8),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. New Milestone Input Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "تسجيل محطة تتبع جديدة للدفعة: ${currentTargetBatch?.batchId ?: ""}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Stage presets
                    Text("المرحلة / المحطة:", style = MaterialTheme.typography.labelSmall)
                    val stages = listOf(
                        "الفرز ومراقبة الجودة",
                        "التعبئة والتغليف والختم",
                        "الشحن المبرد (Cold Chain)",
                        "الاستلام بنقطة البيع"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        stages.take(2).forEach { s ->
                            val isSel = s == selectedStageType
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSel) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedStageType = s }
                            ) {
                                Text(
                                    text = s,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        stages.takeLast(2).forEach { s ->
                            val isSel = s == selectedStageType
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSel) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedStageType = s }
                            ) {
                                Text(
                                    text = s,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = { locationInput = it },
                        label = { Text("الموقع الجغرافي / محطة الفحص") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = operatorInput,
                            onValueChange = { operatorInput = it },
                            label = { Text("المسؤول / المفتش") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = temperatureInput,
                            onValueChange = { temperatureInput = it },
                            label = { Text("درجة الحرارة") },
                            modifier = Modifier.weight(0.7f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = detailsInput,
                        onValueChange = { detailsInput = it },
                        label = { Text("ملاحظات الفحص والبيانات الفنية") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (activeBatchId.isNotBlank() && locationInput.isNotBlank()) {
                                viewModel.addCheckpointEvent(
                                    batchId = activeBatchId,
                                    stageName = selectedStageType,
                                    location = locationInput,
                                    operatorName = operatorInput,
                                    details = detailsInput,
                                    temperature = temperatureInput.ifBlank { null }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("add_checkpoint_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تسجيل وتوثيق المحطة في السلسلة")
                    }
                }
            }
        }

        // 4. Events Log for this batch
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "سجل المراحل الموثقة للدفعة المختارة (${events.size} مراحل):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(events) { ev ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${ev.stepOrder}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ev.stageName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${ev.location} • ${ev.timestamp}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = ev.details,
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (ev.temperatureRecorded != null) {
                            Text(
                                text = "الحرارة: ${ev.temperatureRecorded}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF0369A1),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
