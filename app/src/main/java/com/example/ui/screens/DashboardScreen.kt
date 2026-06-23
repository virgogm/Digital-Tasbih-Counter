package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TasbihCounter
import com.example.ui.SortOption
import com.example.ui.TasbihViewModel
import com.example.ui.components.IslamicPatternBackdrop
import com.example.ui.theme.PastelPalettes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TasbihViewModel,
    onNavigateToWorkspace: () -> Unit,
    onShowAddCounterModal: () -> Unit
) {
    val counters by viewModel.counters.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf<TasbihCounter?>(null) }
    var showResetConfirmDialog by remember { mutableStateOf<TasbihCounter?>(null) }
    var showInfoModal by remember { mutableStateOf<TasbihCounter?>(null) }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Digital Tasbih",
                        fontWeight = FontWeight.Bold,
                        color = PastelPalettes.defaultText,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowAddCounterModal,
                containerColor = Color(0xFFB8C6C3),
                contentColor = PastelPalettes.defaultText,
                shape = CircleShape,
                modifier = Modifier
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .testTag("add_counter_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create New Counter",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = Color(0xFFF4F7F6) // Soft Mint Grey Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Low-opacity canvas pattern backdrop
            IslamicPatternBackdrop(
                patternColor = Color(0xFF2C3E43).copy(alpha = 0.03f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search your counters...", color = PastelPalettes.defaultText.copy(alpha = 0.5f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PastelPalettes.defaultText.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_bar"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFFB8C6C3),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedTextColor = PastelPalettes.defaultText,
                        unfocusedTextColor = PastelPalettes.defaultText
                    ),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sort Options Layout (Horizontal Scroll/Chips)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort Counters By:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PastelPalettes.defaultText.copy(alpha = 0.7f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SortChip(
                            label = "A-Z",
                            selected = sortBy == SortOption.A_Z,
                            onClick = { viewModel.setSortBy(SortOption.A_Z) }
                        )
                        SortChip(
                            label = "Recent",
                            selected = sortBy == SortOption.RECENT_ACTIVITY,
                            onClick = { viewModel.setSortBy(SortOption.RECENT_ACTIVITY) }
                        )
                        SortChip(
                            label = "Top Count",
                            selected = sortBy == SortOption.HIGHEST_COUNT,
                            onClick = { viewModel.setSortBy(SortOption.HIGHEST_COUNT) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (counters.isEmpty()) {
                    // Beautiful empty state placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Empty State Icon",
                                tint = Color(0xFFB8C6C3),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Your Dhikr Journey Begins Here",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PastelPalettes.defaultText,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create your first digital tasbih counter by tapping the plush pastel button below.",
                                fontSize = 14.sp,
                                color = PastelPalettes.defaultText.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = counters,
                            key = { it.id }
                        ) { counter ->
                            CounterCard(
                                counter = counter,
                                onClick = {
                                    viewModel.selectActiveCounter(counter)
                                    onNavigateToWorkspace()
                                },
                                onReset = { showResetConfirmDialog = counter },
                                onDelete = { showDeleteConfirmDialog = counter },
                                onShowInfo = { showInfoModal = counter }
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation & Metadata Modals
    showDeleteConfirmDialog?.let { counter ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCounter(counter)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFB37E7E))
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = PastelPalettes.defaultText)
                ) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Counter?", fontWeight = FontWeight.Bold, color = PastelPalettes.defaultText) },
            text = { Text("Are you sure you want to permanently delete \"${counter.title}\"? This action cannot be undone.", color = PastelPalettes.defaultText.copy(alpha = 0.8f)) },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    showResetConfirmDialog?.let { counter ->
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.quickResetCounter(counter)
                        showResetConfirmDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = PastelPalettes.defaultText)
                ) {
                    Text("Reset", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetConfirmDialog = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = PastelPalettes.defaultText)
                ) {
                    Text("Cancel")
                }
            },
            title = { Text("Reset Count Progress?", fontWeight = FontWeight.Bold, color = PastelPalettes.defaultText) },
            text = { Text("This will reset the current count and lap records of \"${counter.title}\" back to zero. Do you wish to proceed?", color = PastelPalettes.defaultText.copy(alpha = 0.8f)) },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    showInfoModal?.let { counter ->
        val formattedDate = DateFormat.format("MMM dd, yyyy - hh:mm a", counter.createdAt).toString()
        val formattedUpdate = DateFormat.format("MMM dd, yyyy - hh:mm a", counter.lastUpdatedAt).toString()
        val customTheme = PastelPalettes.getThemeByPrimaryHex(counter.themeColorHex)

        AlertDialog(
            onDismissRequest = { showInfoModal = null },
            confirmButton = {
                Button(
                    onClick = { showInfoModal = null },
                    colors = ButtonDefaults.buttonColors(containerColor = customTheme.primaryColor, contentColor = PastelPalettes.defaultText),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = customTheme.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Counter Information",
                        fontWeight = FontWeight.Bold,
                        color = PastelPalettes.defaultText
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoRow(label = "Title", value = counter.title)
                    InfoRow(label = "Current Count", value = "${counter.currentCount}")
                    InfoRow(label = "Lap Records", value = "${counter.lapCount} Laps")
                    InfoRow(label = "Lap Limit Trigger", value = if (counter.lapLimit > 0) "${counter.lapLimit} counts" else "Infinite")
                    InfoRow(label = "Target/Limit Preset", value = if (counter.targetLimit > 0) "${counter.targetLimit} counts" else "None")
                    InfoRow(label = "Historical Total", value = "${counter.totalAccumulated} total taps")
                    InfoRow(label = "Creation Date", value = formattedDate)
                    InfoRow(label = "Last Tapped", value = formattedUpdate)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFFB8C6C3) else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) PastelPalettes.defaultText else PastelPalettes.defaultText.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun CounterCard(
    counter: TasbihCounter,
    onClick: () -> Unit,
    onReset: () -> Unit,
    onDelete: () -> Unit,
    onShowInfo: () -> Unit
) {
    val theme = PastelPalettes.getThemeByPrimaryHex(counter.themeColorHex)
    val progressFraction = if (counter.targetLimit > 0) {
        (counter.currentCount.toFloat() / counter.targetLimit.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .animateContentSize(animationSpec = spring()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Card theme accent indicator on left border
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(110.dp)
                    .background(theme.primaryColor)
                    .align(Alignment.CenterStart)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = counter.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PastelPalettes.defaultText,
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${counter.currentCount}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = theme.accentColor
                        )
                        if (counter.targetLimit > 0) {
                            Text(
                                text = " / ${counter.targetLimit}",
                                fontSize = 14.sp,
                                color = PastelPalettes.defaultText.copy(alpha = 0.4f),
                                modifier = Modifier.padding(start = 2.dp, top = 6.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        if (counter.lapCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.backgroundColor)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${counter.lapCount} Laps",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.accentColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    if (counter.targetLimit > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(theme.primaryColor.copy(alpha = 0.15f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressFraction)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(theme.accentColor)
                            )
                        }
                    } else {
                        // Infinite progress tag
                        Text(
                            text = "Continuous Counting",
                            fontSize = 11.sp,
                            color = PastelPalettes.defaultText.copy(alpha = 0.4f)
                        )
                    }
                }

                // Quick Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onReset,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = theme.backgroundColor.copy(alpha = 0.5f)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Count",
                            tint = theme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onShowInfo,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = theme.backgroundColor.copy(alpha = 0.5f)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Stats & Metadata",
                            tint = theme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFFFECEC)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Counter",
                            tint = Color(0xFFB37E7E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = PastelPalettes.defaultText.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = PastelPalettes.defaultText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}
