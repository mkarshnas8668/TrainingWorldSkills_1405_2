package com.mkarshnas6.karenstudio.worldskill.ui.screen.canvas

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.R

@Composable
fun CanvasScreen(
    navController: NavController,
    context: Context
) {
    // draw image
    val imageBitmap = ImageBitmap.imageResource(R.drawable.img_sniper)
    // this : mean type of DrawScope
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // gradient just for circle
        val circleCenter = Offset(500f, 1000f)
        val circleRadius = 150f

        drawIntoCanvas { canvas ->
            // set paint
            val myPaint = Paint().apply {
                color = Color.Red
                style = PaintingStyle.Fill // full color inside shape
                strokeWidth = 5f
                color = Color.Green
            }
            // draw circle with custom pain
            canvas.drawCircle(
                center = Offset(500f, 1000f),
                radius = 150f,
                paint = myPaint
            )
        }

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Red,
                    Color.Blue
                )
            ),
            size = Size(200f, 303f),
            topLeft = Offset(153f, 300f),
            cornerRadius = CornerRadius(10f)
        )

        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Red,
                    Color.Yellow,
                    Color.Blue
                )
            ),
            start = Offset(300f, 700f),
            end = Offset(600f, 200f),
            strokeWidth = 20f
        )

        // draw path
        val path = Path().apply {
            // point start
            moveTo(300f, 1100f)
            lineTo(300f, 1000f)
            lineTo(400f, 1000f)
            close() // this mean back to start point
        }

        drawPath(
            path = path,
            color = Color.Cyan,
            style = Fill
        )

        drawContext.canvas.nativeCanvas.drawText(
            "سلام دنیا!",
            100f,  // X
            800f,  // Y
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLUE
                textSize = 60f
                isFakeBoldText = true
            }
        )

        // grouping move
        translate(left = 300f, top = 500f) {
            // draw path
            val path = Path().apply {
                // point start
                moveTo(300f, 1100f)
                lineTo(300f, 1000f)
                lineTo(400f, 1000f)
                close() // this mean back to start point
            }
            // draw path
            val path2 = Path().apply {
                // point start
                moveTo(500f, 1100f)
                lineTo(300f, 1200f)
                lineTo(400f, 1200f)
                close() // this mean back to start point
            }
            // draw path
            val path3 = Path().apply {
                // point start
                moveTo(300f, 1100f)
                lineTo(300f, 1800f)
                lineTo(400f, 1800f)
                close() // this mean back to start point
            }

            drawPath(
                path = path,
                color = Color.Cyan,
                style = Fill
            )

            drawPath(
                path = path2,
                color = Color.Red,
                style = Fill
            )

            drawPath(
                path = path3,
                color = Color.Blue,
                style = Fill
            )

        }

        // drawing image
        drawImage(
            image = imageBitmap,
            topLeft = Offset(40f, 22f),
        )

    }
}