package com.mkarshnas6.karenstudio.worldskill.ui.screen.chart

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ChartScreen(
    navController: NavController, context: Context
) {

    val data = listOf(
        "فروردین" to 70f,
        "اردیبهشت" to 45f,
        "خرداد" to 90f,
        "تیر" to 60f,
        "مرداد" to 80f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SimpleBarChart(data)

        CanvasBarChart()

        CanvasLineChart()

        CanvasPieChart()
    }

}

@Composable
fun SimpleBarChart(data: List<Pair<String, Float>>) {

    // find max value for datas
    val maxValue = data.maxOf { it.second }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text("Chart of sell in Year", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        //chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (label, value) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // self mile
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height((value / maxValue * 250).dp) // ارتفاع نسبی
                            .background(
                                Color.Blue,
                                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // label in bottom mile
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        color = Color.White
                    )

                }
            }
        }

    }

}

@Composable
fun CanvasBarChart() {
    val data = listOf(70f, 45f, 90f, 60f, 80f, 55f)
    val labels = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور")
    val maxValue = data.max()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(40.dp)
    ) {
        val chartWidth = size.width
        val chartHeight = size.height - 60f  // جا برای برچسب‌ها
        val barWidth = chartWidth / data.size * 0.6f  // ۶۰٪ فضا برای میله
        val gapWidth = chartWidth / data.size * 0.4f   // ۴۰٪ فضا برای فاصله

        // رسم میله‌ها
        data.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * chartHeight
            val x = index * (barWidth + gapWidth) + gapWidth / 2
            val y = chartHeight - barHeight

            val showBar by mutableStateOf(false)
//            val animationShowBar by animateDpAsState(
//                targetValue = if (showBar) barHeight.dp else 0.dp,
//                animationSpec = tween(1000, delayMillis = (index) * 100, easing = LinearEasing)
//            )

            // میله
            drawRect(
                color = Color.Blue,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )

            // برچسب
            drawContext.canvas.nativeCanvas.drawText(
                labels[index],
                x + barWidth / 2 - 15f,  // وسط میله
                size.height - 10f,
                android.graphics.Paint().apply {
                    textSize = 28f
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )

            // عدد روی میله
            drawContext.canvas.nativeCanvas.drawText(
                "${value.toInt()}",
                x + barWidth / 2,
                y - 10f,
                android.graphics.Paint().apply {
                    textSize = 24f
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
            )
        }
    }
}

@Composable
fun CanvasLineChart() {
    val data = listOf(20f, 50f, 30f, 80f, 45f, 90f, 60f)
    val maxValue = data.max()

    // یه متغیر برای پیشرفت انیمیشن (۰ تا ۱)
    var animationProgress by remember { mutableStateOf(0f) }

    // شروع انیمیشن وقتی صفحه باز شد
    LaunchedEffect(Unit) {
        // از ۰ شروع کن، با گام‌های کوچک برو به سمت ۱
        while (animationProgress < 1f) {
            animationProgress += 0.02f
            withFrameNanos { }
        }
        animationProgress = 1f  // دقیقاً به ۱ برسون
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(40.dp)
    ) {

        val chartWidth = size.width
        val chartHeight = size.height - 40f
        val stepX = chartWidth / (data.size - 1)

        // رسم خط نمودار
        val path = Path()

        data.forEachIndexed { index, value ->
            val x = index * stepX
            // موقعیت نهایی نقطه
            val finalY = chartHeight - (value / maxValue * chartHeight)
            // موقعیت شروع (پایین نمودار)
            val startY = chartHeight
            // محاسبه موقعیت فعلی با توجه به progress
            val currentY = startY + (finalY - startY) * animationProgress

            if (index == 0) {
                path.moveTo(x, currentY)
            } else {
                path.lineTo(x, currentY)
            }
        }

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // رسم نقاط با همون progress
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val finalY = chartHeight - (value / maxValue * chartHeight)
            val startY = chartHeight
            val currentY = startY + (finalY - startY) * animationProgress

            drawCircle(Color.White, 8f, Offset(x, currentY))
            drawCircle(Color.Blue, 5f, Offset(x, currentY))
        }
    }
}


@Composable
fun CanvasPieChart() {
    val data = listOf(
        "محصول الف" to 30f,
        "محصول ب" to 20f,
        "محصول ج" to 15f,
        "محصول د" to 25f,
        "محصول ه" to 10f
    )

    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta)
    val total = data.sumOf { it.second.toDouble() }.toFloat()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(20.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(size.width, size.height) / 3
        var startAngle = -90f  // از بالای دایره شروع کن

        // رسم هر تکه
        data.forEachIndexed { index, (label, value) ->
            val sweepAngle = (value / total) * 360f  // زاویه هر تکه

            // رسم کمان
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,  // از مرکز هم خط بکش (تبدیل به تکه پای)
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2)
            )

            // متن راهنما (Legend)
            val legendY = size.height - 100f + index * 30f
            drawCircle(colors[index], 10f, Offset(50f, legendY))
            drawContext.canvas.nativeCanvas.drawText(
                "$label (${value.toInt()}%)",
                70f, legendY + 8f,
                android.graphics.Paint().apply {
                    textSize = 24f
                    color = android.graphics.Color.BLACK
                }
            )

            startAngle += sweepAngle
        }
    }
}
