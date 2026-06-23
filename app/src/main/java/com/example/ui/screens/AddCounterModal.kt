package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TasbihViewModel
import com.example.ui.theme.PastelPalettes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCounterModal(
    viewModel: TasbihViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf("") }
    var initialCountStr by remember { mutableStateOf("") }
    
    // Target system
    var isCustomLimit by remember { mutableStateOf(false) }
    var selectedPresetLimit by remember { mutableStateOf(33) }
    var customLimitStr by remember { mutableStateOf("") }
    
    // Lap trigger system
    var lapLimitStr by remember { mutableStateOf("33") }
    
    // Theme choice
    var selectedThemeHex by remember { mutableStateOf("#B8C6C3") } // Sage Green default

    var titleError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Tasbih Counter",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelPalettes.defaultText
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss Dialog",
                        tint = PastelPalettes.defaultText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Counter Title/Name
            Text(
                text = "Counter Title / Name *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPalettes.defaultText.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                placeholder = { Text("e.g. SubhanAllah, Alhamdulillah...", color = PastelPalettes.defaultText.copy(alpha = 0.4f)) },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Title field is required.", color = Color(0xFFB37E7E)) }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("counter_title_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB8C6C3),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedTextColor = PastelPalettes.defaultText,
                    unfocusedTextColor = PastelPalettes.defaultText
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Initial Count
            Text(
                text = "Starting Count",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPalettes.defaultText.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = initialCountStr,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        initialCountStr = it
                    }
                },
                placeholder = { Text("0 (Defaults to zero)", color = PastelPalettes.defaultText.copy(alpha = 0.4f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("initial_count_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB8C6C3),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedTextColor = PastelPalettes.defaultText,
                    unfocusedTextColor = PastelPalettes.defaultText
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target Limit System
            Text(
                text = "Target / Complete Limit",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPalettes.defaultText.copy(alpha = 0.8f)
            )
            
            // Radio selector layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isCustomLimit = false }
                ) {
                    RadioButton(
                        selected = !isCustomLimit,
                        onClick = { isCustomLimit = false },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF7D928E))
                    )
                    Text("Presets", fontSize = 14.sp, color = PastelPalettes.defaultText)
                }

                Spacer(modifier = Modifier.width(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isCustomLimit = true }
                ) {
                    RadioButton(
                        selected = isCustomLimit,
                        onClick = { isCustomLimit = true },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF7D928E))
                    )
                    Text("Custom", fontSize = 14.sp, color = PastelPalettes.defaultText)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!isCustomLimit) {
                // Preset Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(33, 100, 300, 1000).forEach { limit ->
                        val isSelected = selectedPresetLimit == limit
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFFB8C6C3) else Color(0xFFF8FAFC))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF7D928E) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPresetLimit = limit }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$limit",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = PastelPalettes.defaultText
                            )
                        }
                    }
                }
            } else {
                // Custom numeric limit input
                OutlinedTextField(
                    value = customLimitStr,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            customLimitStr = it
                        }
                    },
                    placeholder = { Text("e.g. 500 (Leave blank for continuous counting)", color = PastelPalettes.defaultText.copy(alpha = 0.4f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFB8C6C3),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedTextColor = PastelPalettes.defaultText,
                        unfocusedTextColor = PastelPalettes.defaultText
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lap Limit
            Text(
                text = "Lap Reset Trigger Threshold",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPalettes.defaultText.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = "The counter automatically resets to zero and increments a Lap once this value is reached.",
                fontSize = 11.sp,
                color = PastelPalettes.defaultText.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = lapLimitStr,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        lapLimitStr = it
                    }
                },
                placeholder = { Text("e.g. 33 (Defaults to 33, 0 disables)", color = PastelPalettes.defaultText.copy(alpha = 0.4f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lap_limit_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB8C6C3),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedTextColor = PastelPalettes.defaultText,
                    unfocusedTextColor = PastelPalettes.defaultText
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Theme selection swatches
            Text(
                text = "Pastel Dial Accent Theme",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPalettes.defaultText.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PastelPalettes.themes.forEach { theme ->
                    val isSelected = selectedThemeHex.equals(theme.primaryHex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(theme.primaryColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) theme.accentColor else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedThemeHex = theme.primaryHex }
                            .shadow(elevation = if (isSelected) 2.dp else 0.dp, shape = CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Create Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@Button
                    }
                    val startCount = initialCountStr.toIntOrNull() ?: 0
                    val limit = if (isCustomLimit) {
                        customLimitStr.toIntOrNull() ?: 0
                    } else {
                        selectedPresetLimit
                    }
                    val lapLimit = lapLimitStr.toIntOrNull() ?: 33 // default 33

                    viewModel.createCounter(
                        title = title,
                        initialCount = startCount,
                        targetLimit = limit,
                        lapLimit = lapLimit,
                        themeColorHex = selectedThemeHex
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("create_counter_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB8C6C3),
                    contentColor = PastelPalettes.defaultText
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Create Counter",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
