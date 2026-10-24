package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun DashboardScreen(
    project: GameProject,
    onNavigateToModule: (WorkspaceModule) -> Unit,
    onSelectGddPillar: (GddPillarType) -> Unit,
    onUpdateMetadata: (
        title: String,
        tagline: String,
        genre: String,
        engine: String,
        platforms: String,
        studioName: String,
        targetAudience: String
    ) -> Unit,
    onUpsertMilestone: (MilestoneItem) -> Unit,
    onDeleteMilestone: (String) -> Unit
) {
    var showEditMetadataDialog by remember { mutableStateOf(false) }
    var editingMilestone by remember { mutableStateOf<MilestoneItem?>(null) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
    ) {
        // 1. Project Hero Header Card
        item {
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    VioletContainer.copy(alpha = 0.7f),
                                    CardSurface,
                                    CyanContainer.copy(alpha = 0.4f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    color = CyberCyan.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = project.genre.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    color = ObsidianBackground.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, HairlineBorder)
                                ) {
                                    Text(
                                        text = project. studioName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MutedSteel,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showEditMetadataDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("edit_project_metadata_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Edit Project Details",
                                    tint = CrispAlabaster
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = CrispAlabaster
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = project.tagline,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnVioletContainer
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = HairlineBorder.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MetadataPill(
                                icon = Icons.Default.Memory,
                                label = "Engine",
                                value = project.engine
                            )
                            MetadataPill(
                                icon = Icons.Default.Devices,
                                label = "Targets",
                                value = project.targetPlatforms
                            )
                        }
                    }
                }
            }
        }

        // 2. 4-KPI Studio Telemetry Strip (2x2 adaptive grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TelemetryKpiCard(
                        title = "GDD Readiness",
                        primaryValue = "${project.overallGddReadiness}%",
                        subtitle = "${project.totalWordCount} words across 8 pillars",
                        accentColor = ElectricViolet,
                        icon = Icons.Default.MenuBook,
                        onClick = { onNavigateToModule(WorkspaceModule.GDD_BUILDER) },
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryKpiCard(
                        title = "World Codex",
                        primaryValue = "${project.codexEntries.size} Entries",
                        subtitle = "Chars, Factions, Worlds, Foes",
                        accentColor = CyberCyan,
                        icon = Icons.Default.Public,
                        onClick = { onNavigateToModule(WorkspaceModule.WORLD_CODEX) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TelemetryKpiCard(
                        title = "Sprint Backlog",
                        primaryValue = "${project.completedBacklogCount}/${project.backlogFeatures.size} Done",
                        subtitle = "${project.backlogVelocityPercent}% feature completion",
                        accentColor = EmeraldSuccess,
                        icon = Icons.Default.ViewKanban,
                        onClick = { onNavigateToModule(WorkspaceModule.BACKLOG) },
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryKpiCard(
                        title = "Quests & Items",
                        primaryValue = "${project.quests.size} Quests",
                        subtitle = "${project.dialogueNodes.size} Dialogues • ${project.items.size} Items",
                        accentColor = AmberWarning,
                        icon = Icons.Default.Explore,
                        onClick = { onNavigateToModule(WorkspaceModule.QUEST_DESIGNER) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. GDD 8-Pillar Readiness Matrix
        item {
            Surface(
                color = ElevatedSlatePanel,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, HairlineBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "GDD ARCHITECTURE PILLARS",
                                style = MaterialTheme.typography.labelMedium,
                                color = CyberCyan
                            )
                            Text(
                                text = "Tap any section to jump directly into the modular GDD editor",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSteel
                            )
                        }
                        TextButton(onClick = { onNavigateToModule(WorkspaceModule.GDD_BUILDER) }) {
                            Text("Open Builder", color = ElectricViolet)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    project.gddSections.chunked(2).forEach { pair ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            pair.forEach { sec ->
                                val statusColor = when (sec.status) {
                                    SectionStatus.LOCKED -> EmeraldSuccess
                                    SectionStatus.IN_REVIEW -> CyberCyan
                                    SectionStatus.DRAFT -> AmberWarning
                                }
                                Surface(
                                    color = CardSurface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, HairlineBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            onSelectGddPillar(sec.pillar)
                                            onNavigateToModule(WorkspaceModule.GDD_BUILDER)
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = sec.pillar.title.substringBefore(" &"),
                                                style = MaterialTheme.typography.titleSmall,
                                                color = CrispAlabaster,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(statusColor)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${sec.status.label} • ${sec.wordCount}w",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MutedSteel
                                        )
                                    }
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // 4. Milestone Roadmap Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "MILESTONE ROADMAP & PROGRESS",
                        style = MaterialTheme.typography.titleMedium,
                        color = CrispAlabaster
                    )
                    Text(
                        text = "Production phases from greybox prototype to 1.0 launch",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSteel
                    )
                }
                FilledTonalButton(
                    onClick = { showAddMilestoneDialog = true },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = VioletContainer,
                        contentColor = OnVioletContainer
                    ),
                    modifier = Modifier.testTag("add_milestone_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Phase", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        // 5. Milestone Cards
        items(project.milestones, key = { it.id }) { milestone ->
            MilestoneRoadmapCard(
                milestone = milestone,
                onEdit = { editingMilestone = milestone },
                onQuickProgressChange = { newProgress ->
                    val newStatus = when {
                        newProgress >= 100 -> MilestoneStatus.COMPLETED
                        newProgress > 10 -> MilestoneStatus.ACTIVE
                        else -> MilestoneStatus.UPCOMING
                    }
                    onUpsertMilestone(
                        milestone.copy(progressPercent = newProgress, status = newStatus)
                    )
                },
                onDelete = { onDeleteMilestone(milestone.id) }
            )
        }
    }

    if (showEditMetadataDialog) {
        EditProjectMetadataDialog(
            project = project,
            onDismiss = { showEditMetadataDialog = false },
            onSave = { title, tagline, genre, engine, platforms, studio, audience ->
                onUpdateMetadata(title, tagline, genre, engine, platforms, studio, audience)
                showEditMetadataDialog = false
            }
        )
    }

    if (showAddMilestoneDialog || editingMilestone != null) {
        MilestoneEditorDialog(
            initial = editingMilestone,
            onDismiss = {
                showAddMilestoneDialog = false
                editingMilestone = null
            },
            onSave = { saved ->
                onUpsertMilestone(saved)
                showAddMilestoneDialog = false
                editingMilestone = null
            }
        )
    }
}

@Composable
private fun MetadataPill(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CyberCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MutedSteel)
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = CrispAlabaster,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TelemetryKpiCard(
    title: String,
    primaryValue: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, HairlineBorder),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedSteel
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = primaryValue,
                style = MaterialTheme.typography.headlineSmall,
                color = CrispAlabaster,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MutedSteel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MilestoneRoadmapCard(
    milestone: MilestoneItem,
    onEdit: () -> Unit,
    onQuickProgressChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    val badgeColor = when (milestone.status) {
        MilestoneStatus.COMPLETED -> EmeraldSuccess
        MilestoneStatus.ACTIVE -> CyberCyan
        MilestoneStatus.UPCOMING -> ElectricViolet
    }

    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (milestone.status == MilestoneStatus.ACTIVE) CyberCyan.copy(alpha = 0.6f) else HairlineBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = milestone.status.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = milestone.targetDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = MutedSteel
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Milestone", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Milestone", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = milestone.title,
                style = MaterialTheme.typography.titleMedium,
                color = CrispAlabaster
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = milestone.summaryGoal,
                style = MaterialTheme.typography.bodySmall,
                color = MutedSteel
            )

            if (milestone.deliverables.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    milestone.deliverables.take(3).forEach { deliv ->
                        Surface(
                            color = ObsidianBackground,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, HairlineBorder)
                        ) {
                            Text(
                                text = "✓ $deliv",
                                style = MaterialTheme.typography.labelSmall,
                                color = CrispAlabaster,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Milestone Completion: ${milestone.progressPercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0, 50, 100).forEach { preset ->
                        Text(
                            text = "$preset%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (milestone.progressPercent == preset) CyberCyan else MutedSteel,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onQuickProgressChange(preset) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { milestone.progressPercent / 100f },
                color = badgeColor,
                trackColor = ObsidianBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
private fun EditProjectMetadataDialog(
    project: GameProject,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(project.title) }
    var tagline by remember { mutableStateOf(project.tagline) }
    var genre by remember { mutableStateOf(project.genre) }
    var engine by remember { mutableStateOf(project.engine) }
    var platforms by remember { mutableStateOf(project.targetPlatforms) }
    var studio by remember { mutableStateOf(project.studioName) }
    var audience by remember { mutableStateOf(project.targetAudience) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ElevatedSlatePanel,
        titleContentColor = CrispAlabaster,
        textContentColor = CrispAlabaster,
        title = { Text("Edit Game Project Metadata") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = tagline, onValueChange = { tagline = it }, label = { Text("Elevator Pitch") }, maxLines = 3, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = genre, onValueChange = { genre = it }, label = { Text("Genre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = engine, onValueChange = { engine = it }, label = { Text("Engine") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = platforms, onValueChange = { platforms = it }, label = { Text("Target Platforms") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = studio, onValueChange = { studio = it }, label = { Text("Studio Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = audience, onValueChange = { audience = it }, label = { Text("Target Audience") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, tagline, genre, engine, platforms, studio, audience) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text("Save Metadata", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun MilestoneEditorDialog(
    initial: MilestoneItem?,
    onDismiss: () -> Unit,
    onSave: (MilestoneItem) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var targetDate by remember { mutableStateOf(initial?.targetDate ?: "Q3 — Target") }
    var summaryGoal by remember { mutableStateOf(initial?.summaryGoal ?: "") }
    var deliverablesText by remember { mutableStateOf(initial?.deliverables?.joinToString(", ") ?: "Playable Build, Boss Fight, UI Polish") }
    var progress by remember { mutableFloatStateOf((initial?.progressPercent ?: 25).toFloat()) }
    var status by remember { mutableStateOf(initial?.status ?: MilestoneStatus.UPCOMING) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ElevatedSlatePanel,
        titleContentColor = CrispAlabaster,
        textContentColor = CrispAlabaster,
        title = { Text(if (initial == null) "Add Roadmap Milestone" else "Edit Milestone") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Milestone Title") },
                    placeholder = { Text("e.g., M5: Steam Next Fest Demo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Window / Date") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MilestoneStatus.entries.forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                OutlinedTextField(
                    value = summaryGoal,
                    onValueChange = { summaryGoal = it },
                    label = { Text("Primary Goal") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deliverablesText,
                    onValueChange = { deliverablesText = it },
                    label = { Text("Key Deliverables (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Progress: ${progress.toInt()}%", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    valueRange = 0f..100f
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val saved = MilestoneItem(
                        id = initial?.id ?: ("ms_" + UUID.randomUUID().toString().take(6)),
                        title = title.ifBlank { "New Milestone Phase" },
                        targetDate = targetDate,
                        status = status,
                        summaryGoal = summaryGoal.ifBlank { "Deliver playable milestone objectives." },
                        deliverables = deliverablesText.split(",").map { it.trim() }.filter { it.isNotBlank() },
                        progressPercent = progress.toInt()
                    )
                    onSave(saved)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text("Save Milestone", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
