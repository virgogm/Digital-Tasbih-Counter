package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TasbihViewModel
import com.example.ui.components.IslamicPatternBackdrop
import com.example.ui.theme.PastelPalettes
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterWorkspaceScreen(
    viewModel: TasbihViewModel,
    onNavigateBack: () -> Unit
) {
    val activeCounter by viewModel.activeCounter.collectAsState()
    
    // Read high-frequency isolated states
    val count by viewModel.activeCurrentCount.collectAsState()
    val lapCount by viewModel.activeLapCount.collectAsState()
    val totalAccumulated by viewModel.activeTotalAccumulated.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    val currentCounter = activeCounter ?: return
    val currentTheme = PastelPalettes.getThemeByPrimaryHex(currentCounter.themeColorHex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentCounter.title,
                            fontWeight = FontWeight.Bold,
                            color = PastelPalettes.defaultText,
                            fontSize = 20.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "Historical session: $totalAccumulated total",
                            fontSize = 12.sp,
                            color = PastelPalettes.defaultText.copy(alpha = 0.5f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.saveActiveCounterImmediately()
                            onNavigateBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Save and Back to Dashboard",
                            tint = PastelPalettes.defaultText
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showEditDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = currentTheme.backgroundColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Edit Counter Settings",
                            tint = currentTheme.accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
        containerColor = currentTheme.backgroundColor // Dynamic Pastel Theme Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Re-render optimized backdrop decoration
            IslamicPatternBackdrop(
                patternColor = currentTheme.accentColor.copy(alpha = 0.035f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Info Tag (Lap Trigger Display)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.7f))
                            .border(1.dp, currentTheme.primaryColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (currentCounter.lapLimit > 0) "Lap resets at ${currentCounter.lapLimit}" else "No auto-lap reset",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = currentTheme.accentColor
                        )
                    }
                }

                // Massive Interactive Dial Area
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape, clip = false)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            viewModel.incrementActiveCounter()
                        }
                        .testTag("dial_tap_zone"),
                    contentAlignment = Alignment.Center
                ) {
                    // Optimized Circular Progress Ring Canvas
                    ProgressRing(
                        count = count,
                        targetLimit = currentCounter.targetLimit,
                        primaryColor = currentTheme.primaryColor,
                        accentColor = currentTheme.accentColor,
                        modifier = Modifier.size(276.dp)
                    )

                    // Core Digital Typography Counter
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (lapCount > 0) {
                            Text(
                                text = "LAP $lapCount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.accentColor,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Text(
                            text = "$count",
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = PastelPalettes.defaultText
                        )

                        if (currentCounter.targetLimit > 0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "GOAL ${currentCounter.targetLimit}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = PastelPalettes.defaultText.copy(alpha = 0.4f),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Fine-tuning Manual Adjustments Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fine-tune adjustments",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PastelPalettes.defaultText.copy(alpha = 0.4f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // -1 button
                        IconButton(
                            onClick = { viewModel.decrementActiveCounter() },
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White,
                                contentColor = PastelPalettes.defaultText
                            )
                        ) {
                            Text("-1", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        // Reset button
                        IconButton(
                            onClick = {
                                viewModel.quickResetCounter(currentCounter)
                                viewModel.selectActiveCounter(currentCounter.copy(currentCount = 0, lapCount = 0))
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White,
                                contentColor = PastelPalettes.defaultText
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Current Progress",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // +1 button
                        IconButton(
                            onClick = { viewModel.incrementActiveCounter() },
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White,
                                contentColor = PastelPalettes.defaultText
                            )
                        ) {
                            Text("+1", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                // In-View Quick Palette Swapper
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Quick Palette Swapper",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PastelPalettes.defaultText.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            PastelPalettes.themes.forEach { theme ->
                                val isSelected = currentCounter.themeColorHex.equals(theme.primaryHex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(theme.primaryColor)
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) theme.accentColor else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            viewModel.updateCounterMetadata(
                                                id = currentCounter.id,
                                                title = currentCounter.title,
                                                targetLimit = currentCounter.targetLimit,
                                                lapLimit = currentCounter.lapLimit,
                                                themeColorHex = theme.primaryHex
                                            )
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Settings Modification Overlay
    if (showEditDialog) {
        var editTitle by remember { mutableStateOf(currentCounter.title) }
        var editTargetStr by remember { mutableStateOf(if (currentCounter.targetLimit > 0) "${currentCounter.targetLimit}" else "") }
        var editLapStr by remember { mutableStateOf("${currentCounter.lapLimit}") }
        var isEditCustomLimit by remember { mutableStateOf(currentCounter.targetLimit > 0) }
        var selectedEditPresetLimit by remember { mutableStateOf(if (currentCounter.targetLimit in listOf(33, 100, 300, 1000)) currentCounter.targetLimit else 33) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val limit = if (isEditCustomLimit) {
                            editTargetStr.toIntOrNull() ?: 0
                        } else {
                            selectedEditPresetLimit
                        }
                        val lapLimit = editLapStr.toIntOrNull() ?: 33

                        viewModel.updateCounterMetadata(
                            id = currentCounter.id,
                            title = editTitle,
                            targetLimit = limit,
                            lapLimit = lapLimit,
                            themeColorHex = currentCounter.themeColorHex
                        )
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor, contentColor = PastelPalettes.defaultText),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Settings", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = PastelPalettes.defaultText)
                ) {
                    Text("Cancel")
                }
            },
            title = { Text("Modify Counter Settings", fontWeight = FontWeight.Bold, color = PastelPalettes.defaultText) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Title
                    Text("Counter Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PastelPalettes.defaultText)
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.primaryColor,
                            focusedTextColor = PastelPalettes.defaultText
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Target Selector
                    Text("Goal Target Limit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PastelPalettes.defaultText)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { isEditCustomLimit = false }) {
                            RadioButton(selected = !isEditCustomLimit, onClick = { isEditCustomLimit = false }, colors = RadioButtonDefaults.colors(selectedColor = currentTheme.accentColor))
                            Text("Presets", fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { isEditCustomLimit = true }) {
                            RadioButton(selected = isEditCustomLimit, onClick = { isEditCustomLimit = true }, colors = RadioButtonDefaults.colors(selectedColor = currentTheme.accentColor))
                            Text("Custom / Infinite", fontSize = 13.sp)
                        }
                    }

                    if (!isEditCustomLimit) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(33, 100, 300, 1000).forEach { limit ->
                                val isSelected = selectedEditPresetLimit == limit
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) currentTheme.primaryColor else Color(0xFFF1F5F9))
                                        .clickable { selectedEditPresetLimit = limit }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$limit", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = editTargetStr,
                            onValueChange = { editTargetStr = it },
                            placeholder = { Text("Infinite / continuous") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = currentTheme.primaryColor)
                        )
                    }

                    // Lap Limit
                    Text("Lap Reset Trigger", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PastelPalettes.defaultText)
                    OutlinedTextField(
                        value = editLapStr,
                        onValueChange = { editLapStr = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = currentTheme.primaryColor)
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// Highly optimized dedicated progress ring drawing canvas
@Composable
fun ProgressRing(
    count: Int,
    targetLimit: Int,
    primaryColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    // Dynamic smooth progress mapping
    val progress = if (targetLimit > 0) {
        (count.toFloat() / targetLimit.toFloat()).coerceIn(0f, 1f)
    } else {
        0.08f // Subtle aesthetic ring indicators when target is continuous
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 150)
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)
        val radius = width / 2f - 18f
        
        // 1. Draw track ring
        drawCircle(
            color = primaryColor.copy(alpha = 0.15f),
            radius = radius,
            center = center,
            style = Stroke(width = 16f)
        )

        // 2. Draw progress ring arc
        drawArc(
            color = accentColor,
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = 18f, cap = StrokeCap.Round)
        )

        // 3. Draw mechanical/aesthetic tick marks along track for premium finish
        val ticks = 33
        for (i in 0 until ticks) {
            val angle = (i * 360f / ticks) - 90f
            val rad = Math.toRadians(angle.toDouble())
            
            val innerR = radius - 14f
            val outerR = radius - 8f
            
            val startX = center.x + innerR * cos(rad).toFloat()
            val startY = center.y + innerR * sin(rad).toFloat()
            
            val endX = center.x + outerR * cos(rad).toFloat()
            val endY = center.y + outerR * sin(rad).toFloat()
            
            drawOnProgressSegment(
                index = i,
                ticksCount = ticks,
                activeProgress = animatedProgress,
                drawSegment = { isActive ->
                    drawLine(
                        color = if (isActive) accentColor.copy(alpha = 0.8f) else primaryColor.copy(alpha = 0.25f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (isActive) 3f else 2f
                    )
                }
            )
        }
    }
}

inline fun drawOnProgressSegment(
    index: Int,
    ticksCount: Int,
    activeProgress: Float,
    drawSegment: (isActive: Boolean) -> Unit
) {
    val tickFraction = index.toFloat() / ticksCount.toFloat()
    drawSegment(tickFraction <= activeProgress)
}
