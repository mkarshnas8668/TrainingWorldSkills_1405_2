package com.mkarshnas6.karenstudio.worldskill.ui.screen.dragDrop

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
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

    // items for draggable list
    var items by remember {
        mutableStateOf(
            listOf(
                DraggableItem(1, "توپ قرمز", Color.Red, "🔴"),
                DraggableItem(2, "ستاره آبی", Color.Blue, "⭐"),
                DraggableItem(3, "قلب سبز", Color.Green, "💚"),
                DraggableItem(4, "خورشید زرد", Color.Yellow, "☀️"),
                DraggableItem(5, "ماه بنفش", Color.Magenta, "🌙"),
            )
        )
    }

//     var for draggable list
    // item when dragged
    var draggedItem by remember { mutableStateOf<DraggableItem?>(null) }
    // item when hovered
    var hoveredItemId by remember { mutableStateOf<Int?>(null) }

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
                            "کشیده شدم !!"
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

        HorizontalDivider()
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            LazyColumn {
                items(items.size) { index ->
                    val item = items[index]
                    DraggableListItem(
                        item = item,
                        isHovered = hoveredItemId == item.id,
                        onDragDrop = { droppedItemId ->
                            val dragged = draggedItem ?: return@DraggableListItem
                            val targetIndex = items.indexOfFirst { it.id == droppedItemId }
                            val draggedIndex = items.indexOfFirst { it.id == dragged.id }

                            if (targetIndex != -1 && draggedIndex != -1 && targetIndex != draggedIndex) {
                                val mutableList = items.toMutableList()
                                mutableList.removeAt(draggedIndex)
                                mutableList.add(targetIndex, dragged)
                                items = mutableList
                                Toast.makeText(context, "جا به جا شد ✔✔", Toast.LENGTH_SHORT).show()
                            }

                            draggedItem = null
                            hoveredItemId = null
                        },
                        onDragStart = {
                            draggedItem = item
                            Toast.makeText(context, "در حال کشیدن !!", Toast.LENGTH_SHORT).show()
                        },
                        onDragEnd = { draggedItem = null; hoveredItemId = null },
                        onDragEnter = { hoveredItemId = it },
                        onDragExit = { hoveredItemId = null }
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }


}

@Composable
fun DraggableListItem(
    item: DraggableItem,
    isHovered: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDragDrop: (Int) -> Unit,
    onDragEnter: (Int) -> Unit,
    onDragExit: () -> Unit,
) {

    val backgroundColor = if (isHovered) Color(0xFF4CAF50) else Color(0xFF2A2A4A)

    val itemDropCallBack = remember(item.id) {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                onDragDrop(item.id)
                return true
            }

            override fun onStarted(event: DragAndDropEvent) {}

            override fun onExited(event: DragAndDropEvent) {
                onDragExit()
            }

            override fun onEntered(event: DragAndDropEvent) {
                onDragEnter(item.id)
            }

            override fun onEnded(event: DragAndDropEvent) {
                onDragEnd()
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .dragAndDropSource { _: Offset ->
                onDragStart()
                DragAndDropTransferData(
                    ClipData.newPlainText(
                        "item",
                        "${item.id}|${item.title}"
                    )
                )
            }
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = itemDropCallBack
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ایموجی
            Text(text = item.emoji, fontSize = 28.sp)

            Spacer(modifier = Modifier.width(16.dp))

            // عنوان
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            // آیکون drag
            Text(text = "⠿", color = Color.White.copy(alpha = 0.5f), fontSize = 24.sp)
        }
    }

}