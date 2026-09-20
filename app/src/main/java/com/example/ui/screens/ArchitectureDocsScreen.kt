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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ArchitectureDocsScreen(
    modifier: Modifier = Modifier
) {
    var expandedSection by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("architecture_docs_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Architecture,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "دليل المعمارية وهندسة النظام (System Architecture)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "المخطط الشامل للحل البرمجي، أمن الـ QR، قواعد البيانات، وخطة التنفيذ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Section 1: Tech Stack Comparison & Architecture
        item {
            DocSectionCard(
                title = "1. هيكلية النظام المقترحة (System Architecture)",
                subtitle = "تقنيات الـ Backend والـ Mobile وقواعد البيانات الأنسب",
                icon = Icons.Default.Computer,
                isExpanded = expandedSection == 0,
                onToggle = { expandedSection = if (expandedSection == 0) -1 else 0 }
            ) {
                TechStackDocContent()
            }
        }

        // Section 2: Database Schema & ERD
        item {
            DocSectionCard(
                title = "2. هيكلة قاعدة البيانات لتتبع المحصول (Database Schema)",
                subtitle = "العلاقات بين المزارع، قطع الحقول، الدفعات، ومحطات التتبع",
                icon = Icons.Default.Storage,
                isExpanded = expandedSection == 1,
                onToggle = { expandedSection = if (expandedSection == 1) -1 else 1 }
            ) {
                DatabaseSchemaDocContent()
            }
        }

        // Section 3: QR Cryptographic Security & Anti-Tampering
        item {
            DocSectionCard(
                title = "3. آلية تشفير وحماية الـ QR لمنع التزوير (Security Protocol)",
                subtitle = "توقيع HMAC-SHA256، الختم التشفيري، وكشف محاولات التكرار",
                icon = Icons.Default.Security,
                isExpanded = expandedSection == 2,
                onToggle = { expandedSection = if (expandedSection == 2) -1 else 2 }
            ) {
                QrSecurityDocContent()
            }
        }

        // Section 4: User Roles & Workflows (RBAC)
        item {
            DocSectionCard(
                title = "4. رحلة المستخدم ومصفوفة الصلاحيات (User Roles & Workflows)",
                subtitle = "تدفق المزارع الميداني، مفتش الجودة، والمستهلك النهائي",
                icon = Icons.Default.Route,
                isExpanded = expandedSection == 3,
                onToggle = { expandedSection = if (expandedSection == 3) -1 else 3 }
            ) {
                UserRolesDocContent()
            }
        }

        // Section 5: Implementation Roadmap & Milestones
        item {
            DocSectionCard(
                title = "5. خطة العمل والمراحل (Implementation Roadmap)",
                subtitle = "المرحلة الأولى MVP -> المرحلة الثانية IoT -> الإطلاق الشامل",
                icon = Icons.Default.Timeline,
                isExpanded = expandedSection == 4,
                onToggle = { expandedSection = if (expandedSection == 4) -1 else 4 }
            ) {
                RoadmapDocContent()
            }
        }

        // Section 6: Field & Logistics Challenges and Mitigations
        item {
            DocSectionCard(
                title = "6. التحديات الميدانية واللوجستية والحلول العملية",
                subtitle = "العمل في الحقول بلا إنترنت، حرارة الشمس، ومتانة الملصقات",
                icon = Icons.Default.Warning,
                isExpanded = expandedSection == 5,
                onToggle = { expandedSection = if (expandedSection == 5) -1 else 5 }
            ) {
                FieldChallengesDocContent()
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DocSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun TechStackDocContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "مقارنة واختيار التقنيات الأنسب لمشروع تتبع الأغذية العضوية:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )

        ArchitectureRow(
            component = "Backend Core",
            recommended = "Node.js (NestJS) أو Go (Golang)",
            reason = "NestJS يوفر معمارية نمطية (Modular) وتوثيق Swagger مدمج مع دعم TypeScript. بينما Go ممتاز لسرعة معالجة استعلامات مسح الـ QR وملايين الطلبات الخفيفة بدون استهلاك موارد."
        )

        ArchitectureRow(
            component = "Mobile App",
            recommended = "Flutter أو Kotlin Multiplatform / Native Compose",
            reason = "Flutter لتوفير تطبيق واحد للمزارعين والمستهلكين على iOS و Android بسرعة، مع أداء عالي بدون إنترنت عبر SQLite/Hive."
        )

        ArchitectureRow(
            component = "Primary Database",
            recommended = "PostgreSQL (Relational)",
            reason = "سلامة البيانات (ACID transactions) ضرورية جداً في سلاسل الإمداد لمنع تكرار أو حذف دفعات المحاصيل، مع دعم JSONB لتفاصيل الفحوصات المتغيرة."
        )

        ArchitectureRow(
            component = "Time-Series & Telemetry",
            recommended = "TimescaleDB أو InfluxDB",
            reason = "تسجيل قراءات مجسات درجات حرارة شاحنات التبريد (Cold Chain) ورطوبة التربة الحقلية بكفاءة عالية وبدون إبطاء الجداول الرئيسية."
        )

        ArchitectureRow(
            component = "Caching & Rate Limiting",
            recommended = "Redis Cluster",
            reason = "تخزين مؤقت لصفحات الهوية الرقمية للمحاصيل السريعة المسح (High-speed Landing Page) وكشف هجمات التكرار (Anti-DDoS / Anti-Bot)."
        )

        ArchitectureRow(
            component = "File Storage",
            recommended = "S3-compatible / Cloud Storage (R2/AWS)",
            reason = "حفظ شهادات الفحص المخبري (PDFs)، صور المزرعة، وسجلات التوثيق الميداني."
        )
    }
}

@Composable
fun DatabaseSchemaDocContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "هيكلة الجداول العلائقية (Relational Traceability Schema):",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )

        SchemaTableBox(
            tableName = "farms (المزارع والمنتجون)",
            columns = listOf(
                "id (UUID - PK)",
                "name, owner_name (VARCHAR)",
                "region, geo_lat, geo_long (DECIMAL)",
                "organic_cert_number, cert_body (VARCHAR)",
                "irrigation_type, soil_type (TEXT)",
                "created_at (TIMESTAMP)"
            )
        )

        SchemaTableBox(
            tableName = "crop_batches (دفعات المحاصيل)",
            columns = listOf(
                "batch_id (VARCHAR - PK / Unique Serial)",
                "farm_id (UUID - FK -> farms.id)",
                "crop_name, variety, category (VARCHAR)",
                "quantity, unit (DECIMAL, VARCHAR)",
                "harvest_date (TIMESTAMP WITH TIME ZONE)",
                "field_plot (VARCHAR)",
                "hmac_signature (VARCHAR - Cryptographic Token)",
                "current_stage (ENUM: Harvested, Packaged, InTransit, Delivered)",
                "lab_report_no, brix_index (VARCHAR)",
                "scan_count (INT DEFAULT 0)"
            )
        )

        SchemaTableBox(
            tableName = "trace_events (محطات سلسلة الإمداد)",
            columns = listOf(
                "id (BIGSERIAL - PK)",
                "batch_id (VARCHAR - FK -> crop_batches.batch_id)",
                "step_order (INT)",
                "stage_name (VARCHAR: القطف، الفرز، التبريد، التعبئة، الشحن)",
                "location_name, geo_coords (VARCHAR)",
                "recorded_at (TIMESTAMP)",
                "operator_name, operator_id (VARCHAR)",
                "temperature_celsius (DECIMAL)",
                "digital_seal_hash (VARCHAR)"
            )
        )
    }
}

@Composable
fun QrSecurityDocContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "بروتوكول حماية الـ QR Code من التزوير وإعادة الاستخدام غير المشروع:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )

        SecurityStepBox(
            stepNumber = "1",
            title = "التوقيع التشفيري للدفعة (HMAC-SHA256)",
            details = "يتم توليد توقيع رقمي لا يمكن تزويره باستخدام مفتاح سري محمي في وحدة أمان سحابية (Cloud KMS / HSM):" +
                    "\nSignature = HMAC_SHA256(batchId + farmId + harvestDate + cropName, SECRET_KEY)" +
                    "\nأي محاولة لتغيير كود الـ QR ستفشل فوراً أثناء التحقق."
        )

        SecurityStepBox(
            stepNumber = "2",
            title = "ربط الـ Batch ID برمز QR ذكي (GS1 Digital Link)",
            details = "يحتوي الرمز على رابط مباشر للمنتج بصيغة:\nhttps://trace.hasad.farm/v/{Batch_ID}?sig={HMAC_Token}\nيمكّن أي مستهلك من فتحه بكاميرا الهاتف العادية دون الحاجة لتطبيق خاص، مع إمكانية التحقق في أجزاء من الثانية."
        )

        SecurityStepBox(
            stepNumber = "3",
            title = "نظام كشف التكرار والمسح المشبوه (Scan Anomaly Detection)",
            details = "يراقب النظام السحابي معدل المسح الجغرافي: إذا تم مسح نفس الرمز 50 مرة في مدن مختلفة في نفس الساعة، يتم وسم الدفعة فوراً بـ 'مشبوهة ومحتملة النسخ' وتحذير المستهلك لحمايته."
        )

        SecurityStepBox(
            stepNumber = "4",
            title = "الختم المادي ضد العبث (Tamper-Evident Physical Seals)",
            details = "طباعة الـ QR على ملصقات أمنية تتلف ذاتياً عند محاولة نزعها (Destructible Vinyl)، أو طبقة خدش تغطي كود التحقق الإضافي (Scratch-off PIN) للعبوات عالية القيمة."
        )
    }
}

@Composable
fun UserRolesDocContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "مصفوفة الأدوار والصلاحيات (Role-Based Access Control):",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )

        RoleCard(
            role = "المزارع / المنتج (Farmer)",
            permissions = "إدخال بيانات الجني والقطف، اختيار القطعة الحقلية، توليد الـ QR المشفّر، وطباعة الملصقات الأولية.",
            device = "تطبيق جوال مبسط مخصص للعمل الميداني بدون إنترنت."
        )

        RoleCard(
            role = "مفتش الجودة والمختبر (Quality Inspector)",
            permissions = "تسجيل نتائج فحص العينات، نسبة السكر/الحموضة، شهادات خلو المبيدات، وختم الدفعة كمعتمدة عضوياً.",
            device = "بوابة الويب / تطبيق تابلت مع إرفاق ملفات الـ PDF."
        )

        RoleCard(
            role = "مسؤول التعبئة والخدمات اللوجستية (Logistics & Cold Chain)",
            permissions = "تسجيل درجة حرارة الشاحنات المبردة، موقع الانطلاق والوصول، وتأكيد سلامة الأختام.",
            device = "ماسحات باركود محمولة أو تطبيق جوال مع حساسات IoT."
        )

        RoleCard(
            role = "المستهلك النهائي (Consumer)",
            permissions = "مسح الرمز والاطلاع الفوري على بطاقة الهوية الرقمية، مصدر المزرعة، التوقيع التشفيري، والتقييم.",
            device = "متصفح الجوال أو كاميرا الهاتف الذكي بدون أي تسجيل دخول مطلوب."
        )
    }
}

@Composable
fun RoadmapDocContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        RoadmapMilestone(
            phase = "المرحلة الأولى: نموذج العمل الأساسي (MVP - 3 أشهر)",
            goals = listOf(
                "بناء محرك تسجيل المزارع والدفعات وتوليد الـ QR بتوقيع HMAC.",
                "صفحة الهوية الرقمية للمستهلك (Landing Page) سريعة ومحسنة للجوال.",
                "تطبيق جوال أولي للمزارع مع إمكانية العمل بدون إنترنت وحفظ محلي.",
                "طباعة ملصقات الباركود على الطابعات الحرارية الميدانية."
            )
        )

        RoadmapMilestone(
            phase = "المرحلة الثانية: إنترنت الأشياء وسلاسل التبريد (Phase 2 - 3 أشهر)",
            goals = listOf(
                "ربط حساسات درجات حرارة شاحنات التبريد (BLE Temperature Dataloggers) بالسحابة آلياً.",
                "نظام إنذارات مبكرة عند انخفاض أو ارتفاع الحرارة عن المعيار المعتمد.",
                "تطوير لوحة تحكم متقدمة لشركات التوزيع وإدارة المخزون."
            )
        )

        RoadmapMilestone(
            phase = "المرحلة الثالثة: التوسع والشهادات الدولية (Full Release - 6 أشهر)",
            goals = listOf(
                "الاعتماد الكامل لمعيار GS1 Digital Link العالمي لتسهيل التصدير.",
                "سجل توثيق غير قابل للتعديل (Immutable Audit Log / Consortium Ledger).",
                "منظومة مكافآت للمزارعين الأكثر التزاماً بالمعايير العضوية الفائقة."
            )
        )
    }
}

@Composable
fun FieldChallengesDocContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "التحديات الميدانية والحلول المعمارية المعمول بها:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )

        ChallengeCard(
            challenge = "انقطاع أو ضعف شبكة الإنترنت في المزارع النائية",
            solution = "معمارية Offline-First: التطبيق يخزن كافة الدفعات محلياً عبر Room/SQLite ويولد توقيع الـ QR والملصق فورياً على الجهاز، ثم تتم المزامنة تلقائياً عند توفر الشبكة."
        )

        ChallengeCard(
            challenge = "صعوبة تعامل بعض المزارعين والعمالة مع التطبيقات المعقدة",
            solution = "واجهات ميدانية ذات أزرار كبيرة، نصوص واضحة باللغة العربية، وتقليل خطوات الإدخال إلى 3 نقرات فقط مع خيارات افتراضية ذكية لتاريخ الحصاد ونمط الري."
        )

        ChallengeCard(
            challenge = "تلف أو تلطخ ملصقات الـ QR بسبب الرطوبة والحرارة",
            solution = "استخدام ملصقات حرارية صناعية من البولي بروبيلين المقاوم للماء والتبريد، مع معامل تصحيح أخطاء عالي (Error Correction Level M أو Q) لضمان سهولة المسح حتى في حال تضرر 25% من الملصق."
        )

        ChallengeCard(
            challenge = "سرعة استجابة صفحة المستهلك في المتاجر",
            solution = "تخزين صفحة الهوية الرقمية على شبكات توزيع المحتوى (CDN & Edge Caching) مع ضغط الصور، لتفتح الصفحة في أقل من 0.5 ثانية فور مسح الرمز."
        )
    }
}

@Composable
fun ArchitectureRow(component: String, recommended: String, reason: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = component,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = recommended,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SchemaTableBox(tableName: String, columns: List<String>) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF1F5F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = tableName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))
            columns.forEach { col ->
                Text(
                    text = "• $col",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF334155)
                )
            }
        }
    }
}

@Composable
fun SecurityStepBox(stepNumber: String, title: String, details: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF15803D),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = details,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RoleCard(role: String, permissions: String, device: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = role,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = permissions,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = "الواجهة: $device",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RoadmapMilestone(phase: String, goals: List<String>) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = phase,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            goals.forEach { g ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = g, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: String, solution: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = challenge,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "الحل: $solution",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
