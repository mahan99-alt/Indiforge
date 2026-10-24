package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun QuestDesignerScreen(
    project: GameProject,
    onUpsertQuest: (QuestItem) -> Unit,
    onToggleObjective: (questId: String, objectiveId: String) -> Unit,
    onDeleteQuest: (String) -> Unit
) {
    var selectedTypeFilter by remember { mutableStateOf<QuestType?>(null) }
    var editingQuest by remember { mutableStateOf<QuestItem?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredQuests = remember(project.quests, selectedTypeFilter) {
        if (selectedTypeFilter == null) project.quests
        else project.quests.filter { it.questType == selectedTypeFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("quest_designer_screen")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "QUEST & MISSION DESIGNER",
                    style = MaterialTheme.typography.titleLarge,
                    color = CrispAlabaster
                )
                Text(
                    text = "Story arcs, anomaly surveys, objectives, and branching outcomes (${project.quests.size} missions)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSteel
                )
            }
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("add_quest_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Quest", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quest Type Filter Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            FilterChip(
                selected = selectedTypeFilter == null,
                onClick = { selectedTypeFilter = null },
                label = { Text("All Quests (${project.quests.size})") }
            )
            QuestType.entries.forEach { qType ->
                val count = project.quests.count { it.questType == qType }
                FilterChip(
                    selected = selectedTypeFilter == qType,
                    onClick = { selectedTypeFilter = qType },
                    label = { Text("${qType.label} ($count)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredQuests, key = { it.id }) { quest ->
                QuestDesignerCard(
                    quest = quest,
                    onToggleObjective = { objId -> onToggleObjective(quest.id, objId) },
                    onEdit = { editingQuest = quest },
                    onDelete = { onDeleteQuest(quest.id) }
                )
            }
        }
    }

    if (showCreateDialog || editingQuest != null) {
        QuestEditorDialog(
            initial = editingQuest,
            onDismiss = {
                showCreateDialog = false
                editingQuest = null
            },
            onSave = { saved ->
                onUpsertQuest(saved)
                showCreateDialog = false
                editingQuest = null
            }
        )
    }
}

@Composable
private fun QuestDesignerCard(
    quest: QuestItem,
    onToggleObjective: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val badgeColor: Color = when (quest.questType) {
        QuestType.MAIN_ARC -> ElectricViolet
        QuestType.EXPLORATION_ANOMALY -> CyberCyan
        QuestType.FACTION_CONTRACT -> AmberWarning
        QuestType.SIDE_MISSION -> EmeraldSuccess
    }

    Surface(
        color = CardSurface,
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
                            text = quest.questType.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = quest.recommendedTier,
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSteel
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Quest", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Quest", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = quest.title,
                style = MaterialTheme.typography.titleLarge,
                color = CrispAlabaster
            )
            Text(
                text = "Giver: ${quest.giverOrTrigger} • Sector: ${quest.regionOrSector}",
                style = MaterialTheme.typography.labelLarge,
                color = CyberCyan
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quest.synopsis,
                style = MaterialTheme.typography.bodyMedium,
                color = CrispAlabaster
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "MISSION OBJECTIVES (TAP TO TOGGLE PROTOTYPE STATE)",
                style = MaterialTheme.typography.labelSmall,
                color = MutedSteel
            )
            Spacer(modifier = Modifier.height(6.dp))

            quest.objectives.forEach { obj ->
                Surface(
                    color = ObsidianBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        if (obj.isCompletedInPrototype) EmeraldSuccess.copy(alpha = 0.45f) else HairlineBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleObjective(obj.id) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (obj.isCompletedInPrototype) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = null,
                            tint = if (obj.isCompletedInPrototype) EmeraldSuccess else MutedSteel,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = obj.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (obj.isCompletedInPrototype) MutedSteel else CrispAlabaster,
                            textDecoration = if (obj.isCompletedInPrototype) TextDecoration.LineThrough else null,
                            modifier = Modifier.weight(1f)
                        )
                        if (obj.isOptional) {
                            Surface(
                                color = VioletContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "OPTIONAL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnVioletContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (quest.branchingOutcomes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = VioletContainer.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "BRANCHING CHOICES & CONSEQUENCES",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnVioletContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = quest.branchingOutcomes,
                            style = MaterialTheme.typography.bodySmall,
                            color = CrispAlabaster
                        )
                    }
                }
            }

            if (quest.rewards.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    quest.rewards.forEach { reward ->
                        Surface(
                            color = CyanContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "★ $reward",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnCyanContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestEditorDialog(
    initial: QuestItem?,
    onDismiss: () -> Unit,
    onSave: (QuestItem) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var questType by remember { mutableStateOf(initial?.questType ?: QuestType.EXPLORATION_ANOMALY) }
    var giver by remember { mutableStateOf(initial?.giverOrTrigger ?: "SORA-9 Deep Scan") }
    var sector by remember { mutableStateOf(initial?.regionOrSector ?: "Sector E-07") }
    var tier by remember { mutableStateOf(initial?.recommendedTier ?: "Tier 2") }
    var synopsis by remember { mutableStateOf(initial?.synopsis ?: "") }
    var objectivesLines by remember {
        mutableStateOf(
            initial?.objectives?.joinToString("\n") { it.description }
                ?: "Triangulate the derelict signal source\nBreach the sealed cargo vault in zero-G\nExtract the intact memory core before flare impact"
        )
    }
    var branching by remember { mutableStateOf(initial?.branchingOutcomes ?: "") }
    var rewardsText by remember {
        mutableStateOf(initial?.rewards?.joinToString(", ") ?: "3,000 Credits, Warp Coil Schematic, +100 Guild Rep")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ElevatedSlatePanel,
        titleContentColor = CrispAlabaster,
        textContentColor = CrispAlabaster,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (initial == null) "Design New Quest" else "Edit Quest")
                TextButton(
                    onClick = {
                        title = "Echoes of the Fold-Gate"
                        questType = QuestType.EXPLORATION_ANOMALY
                        giver = "Beacon 14 Automated Distress"
                        sector = "Sector X-12: Event Horizon Rim"
                        tier = "Tier 3 (Gravity Shield Required)"
                        synopsis = "Investigate a pre-collapse hyper-gate that is intermittently firing ghost telemetry from a star system lost 300 years ago."
                        objectivesLines = "Align 3 quantum relay mirrors with the gate aperture\nDefend the calibration array from Auton Swarm waves\nDownload the Precursor jump-table before the wormhole collapses"
                        branching = "Lock the gate open to create a permanent trade shortcut (+Syndicate Rep) OR siphon its zero-point core to upgrade your ship's fold drive."
                        rewardsText = "Zero-Point Fold Core, 5,500 Credits, Horizon Achievement"
                    }
                ) {
                    Text("Fill Sample", color = CyberCyan)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    QuestType.entries.forEach { qt ->
                        FilterChip(
                            selected = questType == qt,
                            onClick = { questType = qt },
                            label = { Text(qt.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Quest Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quest_title_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = giver,
                        onValueChange = { giver = it },
                        label = { Text("Quest Giver / Trigger") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sector,
                        onValueChange = { sector = it },
                        label = { Text("Sector / Region") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = synopsis,
                    onValueChange = { synopsis = it },
                    label = { Text("Mission Synopsis") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = objectivesLines,
                    onValueChange = { objectivesLines = it },
                    label = { Text("Objectives (one per line)") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = branching,
                    onValueChange = { branching = it },
                    label = { Text("Branching Outcomes & Consequences") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rewardsText,
                    onValueChange = { rewardsText = it },
                    label = { Text("Rewards (comma-separated)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedObjectives = objectivesLines.lines()
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .mapIndexed { idx, line ->
                            QuestObjective(
                                id = "obj_${idx}_${UUID.randomUUID().toString().take(4)}",
                                description = line,
                                isOptional = line.lowercase().contains("optional"),
                                isCompletedInPrototype = false
                            )
                        }
                    val saved = QuestItem(
                        id = initial?.id ?: ("qst_" + UUID.randomUUID().toString().take(6)),
                        title = title.ifBlank { "Uncharted Anomaly Mission" },
                        questType = questType,
                        giverOrTrigger = giver,
                        regionOrSector = sector,
                        recommendedTier = tier,
                        synopsis = synopsis.ifBlank { "Explore the target sector and complete primary survey objectives." },
                        objectives = parsedObjectives,
                        branchingOutcomes = branching,
                        rewards = rewardsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    )
                    onSave(saved)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("save_quest_button")
            ) {
                Text("Save Quest", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
