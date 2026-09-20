package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.model.CropBatch
import com.example.data.model.Farm
import com.example.data.model.TraceEvent
import com.example.security.CryptoTraceHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TraceabilityRepository(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "hasad_traceability.db"
    ).build()

    private val farmDao = database.farmDao()
    private val batchDao = database.cropBatchDao()
    private val eventDao = database.traceEventDao()

    val allBatches: Flow<List<CropBatch>> = batchDao.getAllBatches()
    val allFarms: Flow<List<Farm>> = farmDao.getAllFarms()

    suspend fun getBatchById(batchId: String): CropBatch? = batchDao.getBatchById(batchId)
    suspend fun getFarmById(farmId: String): Farm? = farmDao.getFarmById(farmId)
    fun getEventsForBatch(batchId: String): Flow<List<TraceEvent>> = eventDao.getEventsForBatch(batchId)

    suspend fun incrementScanCount(batchId: String) {
        batchDao.incrementScanCount(batchId)
    }

    suspend fun insertBatch(batch: CropBatch) {
        batchDao.insertBatch(batch)
    }

    suspend fun updateBatch(batch: CropBatch) {
        batchDao.updateBatch(batch)
    }

    suspend fun insertEvent(event: TraceEvent) {
        eventDao.insertEvent(event)
    }

    suspend fun insertFarm(farm: Farm) {
        farmDao.insertFarm(farm)
    }

    /**
     * Seeds initial production-like organic farms, batches, and timeline milestones if empty.
     */
    suspend fun seedInitialDataIfEmpty() {
        val existingFarms = farmDao.getAllFarms().first()
        if (existingFarms.isNotEmpty()) return

        // 1. Initial Farms
        val farm1 = Farm(
            id = "FARM-AHS-01",
            name = "مزارع واحة النخيل العضوية",
            ownerName = "م. عبد الرحمن النعيم",
            region = "الأحساء - الواحة الشرقية، المملكة العربية السعودية",
            latitude = 25.3833,
            longitude = 49.5855,
            irrigationType = "ري بالتنقيط الذكي من آبار جوفية عذبة مفحوصة",
            soilType = "تربة طميية واحاتية غنية بالمعادن والمخصبات الطبيعية",
            organicCertNumber = "SA-ORG-99214",
            certBody = "الجمعية السعودية للزراعة العضوية (SOFA) & Saudi GAP",
            areaHectares = 45.0,
            establishedYear = 2011,
            phoneContact = "+966 50 123 4567"
        )

        val farm2 = Farm(
            id = "FARM-JOUF-02",
            name = "مزارع زيتون وادي السرحان",
            ownerName = "الشيخ فهد الشراري",
            region = "الجوف - وادي السرحان، شمال المملكة",
            latitude = 29.8115,
            longitude = 39.3175,
            irrigationType = "ري سطحي مقنن بمياه متجددة عالية النقاوة",
            soilType = "تربة كلسية بركانية ملائمة لشجر الزيتون المعمر",
            organicCertNumber = "SA-ORG-88420",
            certBody = "شهادة العضوية الأوروبية EU Organic & SOFA",
            areaHectares = 120.0,
            establishedYear = 2008,
            phoneContact = "+966 55 987 6543"
        )

        val farm3 = Farm(
            id = "FARM-KHARJ-03",
            name = "مزارع نماء الخرج الذكية",
            ownerName = "د. سلطان الدوسري",
            region = "الخرج - منطقة الرفيعة الزراعية",
            latitude = 24.1500,
            longitude = 47.3000,
            irrigationType = "نظام هيدروبونيك متطور ومغلق لتدوير المياه بنسبة 95%",
            soilType = "أوساط زراعية طبيعية معقمة (ألياف جوز الهند وبيرلايت)",
            organicCertNumber = "SA-ORG-77192",
            certBody = "GlobalG.A.P ومعتمد الزراعة النظيفة بدون مبيدات",
            areaHectares = 15.0,
            establishedYear = 2019,
            phoneContact = "+966 53 456 7890"
        )

        farmDao.insertFarms(listOf(farm1, farm2, farm3))

        // 2. Initial Batches
        val batch1Id = "BATCH-AHS-2026-0982"
        val sig1 = CryptoTraceHelper.generateBatchSignature(
            batch1Id, farm1.id, "2026-09-18", "تمور خلاص الأحساء العضوية الفاخرة"
        )
        val batch1 = CropBatch(
            batchId = batch1Id,
            farmId = farm1.id,
            cropName = "تمور خلاص الأحساء العضوية الفاخرة",
            cropCategory = "تمور ونخيل",
            variety = "خلاص الأحساء الملكي - نخب أول",
            quantityDesc = "2,500 كجم (500 كرتون × 5 كجم)",
            harvestDate = "2026-09-18 06:15 ص",
            fieldPlot = "حقل النخيل المعمر - القطعة 4B",
            farmingMethod = "زراعة عضوية موثقة 100% بدون أي أسمدة كيميائية أو هرمونات",
            irrigationDetails = "ري بالتنقيط المتوازن في الساعات الأولى من الصباح الباكر",
            fertilizerUsed = "سماد بلدي عضوي معالج حرارياً ومخمر طبيعياً",
            pestControl = "مصائد ضوئية وفيرمونية بيولوجية ومكافحة طبيعية",
            hmacSignature = sig1,
            currentStage = "DELIVERED",
            qualityGrade = "نخب أول ممتاز (Grade A+ Premium)",
            labTestStatus = "سليم ومطابق - خلو تام من متبقيات المبيدات (0.00 ppm)",
            labReportNumber = "LAB-IDAC-2026-9948",
            sugarBrix = "68.5° Brix",
            scanCount = 3
        )

        val batch2Id = "BATCH-JOUF-2026-3310"
        val sig2 = CryptoTraceHelper.generateBatchSignature(
            batch2Id, farm2.id, "2026-09-15", "زيت زيتون الجوف البكر الممتاز العضوي"
        )
        val batch2 = CropBatch(
            batchId = batch2Id,
            farmId = farm2.id,
            cropName = "زيت زيتون الجوف البكر الممتاز العضوي",
            cropCategory = "زيوت طبيعية",
            variety = "زيتون نيبالي وقيسي معصور على البارد",
            quantityDesc = "800 لتر (1,600 عبوة زجاجية معتمة × 500 مل)",
            harvestDate = "2026-09-15 05:30 ص",
            fieldPlot = "بستان الزيتون الشمالي - حوض السرحان",
            farmingMethod = "زراعة بيئية مستدامة معتمدة دولياً ومحلياً",
            irrigationDetails = "ري ذكي متحكم به عبر مجسات رطوبة التربة IoT",
            fertilizerUsed = "مستخلصات نباتية بحرية وكومبوست عضوي",
            pestControl = "ذبابة الزيتون تكافح بمصائد جاذبة غير سامة",
            hmacSignature = sig2,
            currentStage = "PACKAGED",
            qualityGrade = "بكر ممتاز فائق الجودة (Ultra Premium)",
            labTestStatus = "نسبة حموضة قياسية 0.2% مع بيروكسيد منخفض جداً",
            labReportNumber = "LAB-FOOD-JOUF-2026-1102",
            sugarBrix = "حموضة < 0.2%",
            scanCount = 1
        )

        val batch3Id = "BATCH-KHR-2026-5541"
        val sig3 = CryptoTraceHelper.generateBatchSignature(
            batch3Id, farm3.id, "2026-09-19", "طماطم كرزية عضوية هيدروبونيك"
        )
        val batch3 = CropBatch(
            batchId = batch3Id,
            farmId = farm3.id,
            cropName = "طماطم كرزية عضوية هيدروبونيك",
            cropCategory = "خضار محمية",
            variety = "Cherry Sweet Ruby F1",
            quantityDesc = "600 كجم (1,200 علبة × 500 جم)",
            harvestDate = "2026-09-19 07:00 ص",
            fieldPlot = "البيت المحمي الذكي رقم 2",
            farmingMethod = "زراعة مائية نظيفة بدون تربة وبيئة معقمة ومغلقة",
            irrigationDetails = "محاليل عضوية مغذية ومياه محلاة خالية من المعادن الثقيلة",
            fertilizerUsed = "عناصر غذائية عضوية مسجلة بوزارة البيئة والمياه والزراعة",
            pestControl = "حشرات نافذة بيولوجية (Biocontrol) وشباك مانعة للحشرات",
            hmacSignature = sig3,
            currentStage = "IN_TRANSIT",
            qualityGrade = "درجة أولى تصديرية (Export Grade A)",
            labTestStatus = "معتمد خالٍ من النترات الزائدة والمبيدات",
            labReportNumber = "LAB-AGRI-RUH-8823",
            sugarBrix = "11.2° Brix (طعم سكري مركز)",
            scanCount = 1
        )

        batchDao.insertBatch(batch1)
        batchDao.insertBatch(batch2)
        batchDao.insertBatch(batch3)

        // 3. Trace Events for Batch 1 (Full Journey)
        val eventsBatch1 = listOf(
            TraceEvent(
                batchId = batch1Id,
                stepOrder = 1,
                stageName = "الجني والقطف اليدوي من الحقل",
                location = "واحة الأحساء - قطعة النخيل 4B",
                timestamp = "18 سبتمبر 2026 - 06:15 ص",
                operatorName = "م. خالد المهنا (رئيس فريق الجني)",
                details = "تم الجني اليدوي لتمور الخلاص المكتملة النضج، في درجة حرارة جوية 23°C، واستبعاد أي حبات غير مطابقة مباشرة بالحقل.",
                temperatureRecorded = "23.0°C"
            ),
            TraceEvent(
                batchId = batch1Id,
                stepOrder = 2,
                stageName = "الفحص الأولي ومراقبة الجودة",
                location = "مركز استقبال المحاصيل بالمزرعة",
                timestamp = "18 سبتمبر 2026 - 08:30 ص",
                operatorName = "م. سارة الصالح (أخصائية جودة وسلامة غذاء)",
                details = "فحص بصري وميكانيكي، قياس نسبة السكر (68.5° Brix)، ووزن الحبات بمعدل 14-16 جم للحبة الواحدة مع نقاوة 99.8%.",
                temperatureRecorded = "22.5°C"
            ),
            TraceEvent(
                batchId = batch1Id,
                stepOrder = 3,
                stageName = "الفرز والتعقيم العضوي بالحرارة الجافة",
                location = "محطة معالجة التمور الحديثة - الهفوف",
                timestamp = "18 سبتمبر 2026 - 11:00 ص",
                operatorName = "فني التشغيل عادل الدوسري",
                details = "تنظيف بالهواء المضغوط المعقم، ثم تعقيم حراري جاف عند 60°C لمدة محددة بدون إضافة أي مواد حافظة أو غازات كيميائية.",
                temperatureRecorded = "60.0°C (مرحلة التعقيم)"
            ),
            TraceEvent(
                batchId = batch1Id,
                stepOrder = 4,
                stageName = "التعبئة والختم بالـ QR الذكي المشفر",
                location = "وحدة التغليف المعتمدة SOFA",
                timestamp = "18 سبتمبر 2026 - 02:45 م",
                operatorName = "مشرف التعبئة فهد القحطاني",
                details = "تعبئة مفرغة جزئياً من الهواء مع غاز النيتروجين الغذائي للحفاظ على الطراوة، وإلصاق بطاقة التتبع الرقمية برمز QR المشفّر بتوقيع HMAC.",
                temperatureRecorded = "18.0°C"
            ),
            TraceEvent(
                batchId = batch1Id,
                stepOrder = 5,
                stageName = "الشحن بسلسلة التبريد (Cold Chain)",
                location = "طريق الرياض - الأحساء السريع (شاحنة تبريد #778)",
                timestamp = "19 سبتمبر 2026 - 01:00 ص",
                operatorName = "سائق النقل المعتمد ممدوح العنزي",
                details = "نقل مباشر بشاحنة مجهزة بنظام تتبع حراري GPS متصل بالسحابة، رصدت درجة حرارة مستمرة بين 4.0°C و 5.2°C.",
                temperatureRecorded = "4.5°C"
            ),
            TraceEvent(
                batchId = batch1Id,
                stepOrder = 6,
                stageName = "الوصول والاستلام في متجر الأغذية العضوية",
                location = "أسواق الأغذية العضوية - طريق الملك عبد العزيز، الرياض",
                timestamp = "19 سبتمبر 2026 - 09:30 ص",
                operatorName = "مدير الفرع راكان السبيعي",
                details = "تم فحص الرمز الذكي QR ومطابقة التوقيع الرقمي بنجاح، ووضع المنتج في أرفف العرض المبردة بانتظار المستهلك النهائي.",
                temperatureRecorded = "5.0°C"
            )
        )
        eventDao.insertEvents(eventsBatch1)

        // Events for Batch 2
        val eventsBatch2 = listOf(
            TraceEvent(
                batchId = batch2Id,
                stepOrder = 1,
                stageName = "جني الزيتون الأخضر والأسود",
                location = "حوض السرحان - الجوف",
                timestamp = "15 سبتمبر 2026 - 05:30 ص",
                operatorName = "فريق القطف اليدوي بمزارع السرحان",
                details = "قطف مباشر بشباك نظيفة مع تفادي سقوط الحبات على الأرض للحفاظ على جودة الزيت ومنع التلف الحمضي.",
                temperatureRecorded = "19.0°C"
            ),
            TraceEvent(
                batchId = batch2Id,
                stepOrder = 2,
                stageName = "العصر البارد في أقل من 4 ساعات من الجني",
                location = "معصرة الجوف الحديثة",
                timestamp = "15 سبتمبر 2026 - 09:15 ص",
                operatorName = "خبير العصر الإيطالي ماركو & م. أحمد الرويلي",
                details = "عصر ميكانيكي بحت بدرجة حرارة لا تتجاوز 21.8°C، بدون إضافة ماء ساخن أو مذيبات، وفلترة قطنية طبيعية.",
                temperatureRecorded = "21.8°C"
            ),
            TraceEvent(
                batchId = batch2Id,
                stepOrder = 3,
                stageName = "الفحص المخبري والتعبئة في زجاج داكن",
                location = "مختبر معايير الجودة بالجوف",
                timestamp = "16 سبتمبر 2026 - 11:30 ص",
                operatorName = "د. نادية الحربي",
                details = "اجتياز فحص الحموضة بنتيجة 0.20%، وتعبئة الزيت في عبوات داكنة مانعة للضوء مع سدادة أمان محكمة وإصدار QR الدفعة.",
                temperatureRecorded = "18.5°C"
            )
        )
        eventDao.insertEvents(eventsBatch2)
    }
}
