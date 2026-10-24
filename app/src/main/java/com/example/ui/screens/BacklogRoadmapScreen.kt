package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun BacklogRoadmapScreen(
    project: GameProject,
    onUpsertFeature: (BacklogFeature) -> Unit,
    onCycleStatus: (String) -> Unit,
    onDeleteFeature: (String) -> Unit
) {
    var selectedStatusFilter by remember { mutableStateOf<FeatureStatus?>(null) }
    var editingFeature by remember { mutableStateOf<BacklogFeature?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredFeatures = remember(project.backlogFeatures, selectedStatusFilter) {
        val list = if (selectedStatusFilter == null) project.backlogFeatures
        else project.backlogFeatures.filter { it.status == selectedStatusFilter }
        list.sortedByDescending { it.priority.weight }
    }

    val totalPoints = remember(project.backlogFeatures) {
        project.backlogFeatures.sumOf { it.effortPoints }
    }
    val completedPoints = remember(project.backlogFeatures) {
        project.backlogFeatures.filter { it.status == FeatureStatus.DONE }.sumOf { it.effortPoints }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("backlog_screen")
    ) {
        // Header + Add Feature Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FEATURE BACKLOG & SPRINT BOARD",
                    style = MaterialTheme.typography.titleLarge,
                    color = CrispAlabaster
                )
                Text(
                    text = "Story Points: $completedPoints / $totalPoints pts completed (${project.backlogVelocityPercent}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberCyan
                )
            }
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("add_backlog_feature_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Feature", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filter Strip
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            FilterChip(
                selected = selectedStatusFilter == null,
                onClick = { selectedStatusFilter = null },
                label = { Text("All (${project.backlogFeatures.size})") }
            )
            FeatureStatus.entries.forEach { st ->
                val count = project.backlogFeatures.count { it.status == st }
                FilterChip(
                    selected = selectedStatusFilter == st,
                    onClick = { selectedStatusFilter = st },
                    label = { Text("${st.label} ($count)") },
                    modifier = Modifier.testTag("backlog_filter_${st.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredFeatures, key = { it.id }) { feature ->
                BacklogFeatureCard(
                    feature = feature,
                    onCycleStatus = { onCycleStatus(feature.id) },
                    onEdit = { editingFeature = feature },
                    onDelete = { onDeleteFeature(feature.id) }
                )
            }
        }
    }

    if (showCreateDialog || editingFeature != null) {
        BacklogFeatureEditorDialog(
            initial = editingFeature,
            onDismiss = {
                showCreateDialog = false
                editingFeature = null
            },
            onSave = { saved ->
                onUpsertFeature(saved)
                showCreateDialog = false
                editingFeature = null
            }
        )
    }
}

@Composable
private fun BacklogFeatureCard(
    feature: BacklogFeature,
    onCycleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor: Color = when (feature.priority) {
        FeaturePriority.CRITICAL -> RoseDanger
        FeaturePriority.HIGH -> AmberWarning
        FeaturePriority.MEDIUM -> CyberCyan
        FeaturePriority.LOW -> MutedSteel
    }

    val statusColor: Color = when (feature.status) {
        FeatureStatus.DONE -> EmeraldSuccess
        FeatureStatus.PLAYTESTING -> ElectricViolet
        FeatureStatus.IN_PROGRESS -> CyberCyan
        FeatureStatus.BACKLOG -> MutedSteel
    }

    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, HairlineBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Priority Badge
                    Surface(
                        color = priorityColor.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, priorityColor.copy(alpha = 0.55f))
                    ) {
                        Text(
                            text = feature.priority.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Category Badge
                    Surface(
                        color = ObsidianBackground,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, HairlineBorder)
                    ) {
                        Text(
                            text = feature.categoryTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = CrispAlabaster,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Effort Points
                    Text(
                        text = "${feature.effortPoints} pts • ${feature.milestoneTag}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSteel
                    )
                }

                // 1-Tap Interactive Status Cycle Pill
                Surface(
                    color = statusColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onCycleStatus)
                        .testTag("cycle_feature_status_${feature.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Cycle Status",
                            tint = statusColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = feature.status.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = CrispAlabaster,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Feature", tint = MutedSteel, modifier = Modifier.size(16.dp))
                }
            }

            if (feature.acceptanceCriteria.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Spec: ${feature.acceptanceCriteria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSteel
                )
            }
        }
    }
}

@Composable
private fun BacklogFeatureEditorDialog(
    initial: BacklogFeature?,
    onDismiss: () -> Unit,
    onSave: (BacklogFeature) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var categoryTag by remember { mutableStateOf(initial?.categoryTag ?: "Core Loop") }
    var priority by remember { mutableStateOf(initial?.priority ?: FeaturePriority.HIGH) }
    var status by remember { mutableStateOf(initial?.status ?: FeatureStatus.BACKLOG) }
    var milestoneTag by remember { mutableStateOf(initial?.milestoneTag ?: "Vertical Slice") }
    var effortPointsText by remember { mutableStateOf((initial?.effortPoints ?: 5).toString()) }
    var acceptanceCriteria by remember { mutableStateOf(initial?.acceptanceCriteria ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ElevatedSlatePanel,
        titleContentColor = CrispAlabaster,
        textContentColor = CrispAlabaster,
        title = { Text(if (initial == null) "Add Backlog Feature" else "Edit Backlog Feature") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Feature Title") },
                    placeholder = { Text("e.g., Spectrographic Salvage Cutter Shader") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("backlog_title_input")
                )

                Text("Priority", style = MaterialTheme.typography.labelSmall, color = MutedSteel)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    FeaturePriority.entries.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Text("Development Status", style = MaterialTheme.typography.labelSmall, color = MutedSteel)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    FeatureStatus.entries.forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = categoryTag,
                        onValueChange = { categoryTag = it },
                        label = { Text("System Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = effortPointsText,
                        onValueChange = { effortPointsText = it.filter { c -> c.isDigit() } },
                        label = { Text("Story Pts") },
                        singleLine = true,
                        modifier = Modifier.weight(0.6f)
                    )
                }

                OutlinedTextField(
                    value = milestoneTag,
                    onValueChange = { milestoneTag = it },
                    label = { Text("Target Milestone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = acceptanceCriteria,
                    onValueChange = { acceptanceCriteria = it },
                    label = { Text("Acceptance Criteria / Definition of Done") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val saved = BacklogFeature(
                        id = initial?.id ?: ("feat_" + UUID.randomUUID().toString().take(6)),
                        title = title.ifBlank { "New Gameplay Feature" },
                        categoryTag = categoryTag.ifBlank { "Systems" },
                        priority = priority,
                        status = status,
                        milestoneTag = milestoneTag.ifBlank { "Vertical Slice" },
                        effortPoints = effortPointsText.toIntOrNull()?.coerceIn(1, 34) ?: 5,
                        acceptanceCriteria = acceptanceCriteria.ifBlank { "Verified at 60fps in vertical slice build." }
                    )
                    onSave(saved)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("save_backlog_feature_button")
            ) {
                Text("Save Feature", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
