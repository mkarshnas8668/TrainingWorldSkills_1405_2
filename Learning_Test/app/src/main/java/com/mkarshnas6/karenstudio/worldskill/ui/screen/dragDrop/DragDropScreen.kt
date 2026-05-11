package com.mkarshnas6.karenstudio.worldskill.ui.screen.dragDrop

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class DraggableItem(
    val id: Int,
    val title: String,
    val color: Color,
    val emoji: String
)

@Composable
fun DragDropScreen(
    navController: NavController,
    context: Context
) {

    var droppedText by remember { mutableStateOf("هنوز چیزی رها نشده") }
    var isHovering by remember { mutableStateOf(false) }

    // ====== تعریف Callback مقصد ======
    val dropCallback = remember {
        object : DragAndDropTarget {

            override fun onDrop(event: DragAndDropEvent): Boolean {
                val androidEven = event.toAndroidDragEvent()
                val clipData: ClipData? = androidEven.clipData
                val text = clipData?.getItemAt(0)?.text?.toString() ?: "خالی"
                droppedText = text
                return true
            }

            override fun onEntered(event: DragAndDropEvent) {
                isHovering = true  // رنگ رو عوض کن
            }

            override fun onExited(event: DragAndDropEvent) {
                isHovering = false  // رنگ رو برگردون
            }

            override fun onStarted(event: DragAndDropEvent) {}

            override fun onEnded(event: DragAndDropEvent) {
                isHovering = false
            }
        }
    }

    // ====== UI ======
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ===== مبدأ: دایره قرمز =====
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Red, CircleShape)
                // ⭐ این Modifier میگه "از اینجا میشه کشید"
                .dragAndDropSource { _: Offset ->
                    DragAndDropTransferData(
                        ClipData.newPlainText(
                            "label",
                            "من کشیده شدم !"
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text("بکش منو", color = Color.Black, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ===== مقصد: جعبه آبی =====
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    if (isHovering) Color(0xFF4CAF50) else Color(0xFF2196F3),
                    RoundedCornerShape(16.dp)
                )
                // ⭐ این Modifier میگه "اینجا میشه رها کرد"
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        // فقط متن ساده قبول کن
                        event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    },
                    target = dropCallback
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = droppedText,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }

}