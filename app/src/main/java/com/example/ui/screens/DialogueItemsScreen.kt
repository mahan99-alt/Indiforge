package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun DialogueItemsScreen(
    project: GameProject,
    onUpsertDialogueNode: (DialogueNode) -> Unit,
    onDeleteDialogueNode: (String) -> Unit,
    onUpsertGameItem: (GameItem) -> Unit,
    onDeleteGameItem: (String) -> Unit
) {
    var activeSubTab by remember { mutableStateOf("DIALOGUE") } // "DIALOGUE" or "ITEMS"
    var editingDialogue by remember { mutableStateOf<DialogueNode?>(null) }
    var showCreateDialogue by remember { mutableStateOf(false) }

    var editingItem by remember { mutableStateOf<GameItem?>(null) }
    var showCreateItem by remember { mutableStateOf(false) }

    var previewSelectedChoiceOutcome by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("dialogue_items_screen")
    ) {
        // Header + Sub-Tab Switcher
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = activeSubTab == "DIALOGUE",
                    onClick = { activeSubTab = "DIALOGUE" },
                    label = { Text("Dialogue Editor (${project.dialogueNodes.size})") },
                    leadingIcon = { Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_dialogue_editor")
                )
                FilterChip(
                    selected = activeSubTab == "ITEMS",
                    onClick = { activeSubTab = "ITEMS" },
                    label = { Text("Item Descriptions (${project.items.size})") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_items_editor")
                )
            }

            Button(
                onClick = {
                    if (activeSubTab == "DIALOGUE") showCreateDialogue = true
                    else showCreateItem = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("add_narrative_item_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (activeSubTab == "DIALOGUE") "New Node" else "New Item",
                    color = ObsidianBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (activeSubTab == "DIALOGUE") {
            if (previewSelectedChoiceOutcome != null) {
                Surface(
                    color = CyanContainer,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CyberCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DIALOGUE SIMULATOR BRANCH RESULT", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                            Text(previewSelectedChoiceOutcome!!, style = MaterialTheme.typography.bodySmall, color = CrispAlabaster)
                        }
                        IconButton(onClick = { previewSelectedChoiceOutcome = null }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss outcome", tint = OnCyanContainer, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(project.dialogueNodes, key = { it.id }) { node ->
                    DialogueNodePreviewCard(
                        node = node,
                        onTestChoice = { choice ->
                            previewSelectedChoiceOutcome = "Transition → [${choice.nextNodeCode}] • Effect: ${choice.outcomeEffect}"
                        },
                        onEdit = { editingDialogue = node },
                        onDelete = { onDeleteDialogueNode(node.id) }
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(project.items, key = { it.id }) { item ->
                    GameItemTooltipCard(
                        item = item,
                        onEdit = { editingItem = item },
                        onDelete = { onDeleteGameItem(item.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialogue || editingDialogue != null) {
        DialogueNodeEditorDialog(
            initial = editingDialogue,
            onDismiss = {
                showCreateDialogue = false
                editingDialogue = null
            },
            onSave = { saved ->
                onUpsertDialogueNode(saved)
                showCreateDialogue = false
                editingDialogue = null
            }
        )
    }

    if (showCreateItem || editingItem != null) {
        GameItemEditorDialog(
            initial = editingItem,
            onDismiss = {
                showCreateItem = false
                editingItem = null
            },
            onSave = { saved ->
                onUpsertGameItem(saved)
                showCreateItem = false
                editingItem = null
            }
        )
    }
}

@Composable
private fun DialogueNodePreviewCard(
    node: DialogueNode,
    onTestChoice: (DialogueChoice) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, HairlineBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Node Code & Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = VioletContainer,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = node.nodeCode,
                            style = MaterialTheme.typography.labelMedium,
                            color = OnVioletContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        color = ObsidianBackground,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, HairlineBorder)
                    ) {
                        Text(
                            text = node.speakerEmotion,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Dialogue Node", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Dialogue Node", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Context: ${node.sceneContext}",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSteel
            )

            Spacer(modifier = Modifier.height(10.dp))

            // In-Game Dialogue Box Simulator Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianBackground)
                    .border(1.dp, CyberCyan.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = node.speakerName.uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "LIVE IN-GAME HUD PREVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateDim
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${node.spokenLine}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CrispAlabaster
                    )
                    if (node.voiceDirectionNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Voice / Audio Note: ${node.voiceDirectionNotes}",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MutedSteel
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "PLAYER RESPONSE BRANCHES (TAP TO SIMULATE):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSteel
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    node.choices.forEachIndexed { index, choice ->
                        Surface(
                            color = ElevatedSlatePanel,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onTestChoice(choice) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${index + 1}. ${choice.playerPrompt}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CrispAlabaster,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (choice.conditionOrSkillCheck.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = choice.conditionOrSkillCheck,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyberCyan
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "→ ${choice.nextNodeCode} • ${choice.outcomeEffect}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedSteel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameItemTooltipCard(
    item: GameItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val rarityColor: Color = when (item.rarity) {
        ItemRarity.COMMON -> MutedSteel
        ItemRarity.UNCOMMON -> EmeraldSuccess
        ItemRarity.RARE -> CyberCyan
        ItemRarity.EXOTIC -> ElectricViolet
        ItemRarity.MYTHIC -> MythicGold
    }

    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.55f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            rarityColor.copy(alpha = 0.10f),
                            CardSurface,
                            CardSurface
                        )
                    )
                )
                .padding(16.dp)
        ) {
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
                        color = rarityColor.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = item.rarity.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = rarityColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = "${item.category} • ${item.codeId}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MutedSteel
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.valueCredits} Cr",
                        style = MaterialTheme.typography.labelLarge,
                        color = MythicGold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Item", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Item", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleLarge,
                color = CrispAlabaster
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.uiTooltipDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = CrispAlabaster
            )

            if (item.statModifiers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    item.statModifiers.forEach { mod ->
                        Surface(
                            color = ObsidianBackground,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = mod,
                                style = MaterialTheme.typography.labelSmall,
                                color = rarityColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (item.flavorLore.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = HairlineBorder)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.flavorLore,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MutedSteel
                )
            }
        }
    }
}

@Composable
private fun DialogueNodeEditorDialog(
    initial: DialogueNode?,
    onDismiss: () -> Unit,
    onSave: (DialogueNode) -> Unit
) {
    var nodeCode by remember { mutableStateOf(initial?.nodeCode ?: "DLG_SECTOR_03") }
    var speakerName by remember { mutableStateOf(initial?.speakerName ?: "Dockmaster Korr") }
    var speakerEmotion by remember { mutableStateOf(initial?.speakerEmotion ?: "Suspicious / Gruff") }
    var sceneContext by remember { mutableStateOf(initial?.sceneContext ?: "Airlock Bay 4 after docking with unverified cargo.") }
    var spokenLine by remember { mutableStateOf(initial?.spokenLine ?: "") }
    var voiceNotes by remember { mutableStateOf(initial?.voiceDirectionNotes ?: "Low radio compression, metallic hangar echo.") }
    var choice1Prompt by remember {
        mutableStateOf(initial?.choices?.getOrNull(0)?.playerPrompt ?: "\"Here's the encrypted blackbox telemetry. Check the timestamp yourself.\"")
    }
    var choice1Outcome by remember {
        mutableStateOf(initial?.choices?.getOrNull(0)?.outcomeEffect ?: "+150 Iron Drift Rep; unlocks Hangar Repair Bay")
    }
    var choice2Prompt by remember {
        mutableStateOf(initial?.choices?.getOrNull(1)?.playerPrompt ?: "\"My cargo manifest is private, Korr. Name your clearance fee.\"")
    }
    var choice2Outcome by remember {
        mutableStateOf(initial?.choices?.getOrNull(1)?.outcomeEffect ?: "-400 Credits; bypasses Syndicate inspection")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ElevatedSlatePanel,
        titleContentColor = CrispAlabaster,
        textContentColor = CrispAlabaster,
        title = { Text(if (initial == null) "New Dialogue Node" else "Edit Dialogue Node") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nodeCode,
                        onValueChange = { nodeCode = it },
                        label = { Text("Node ID") },
                        singleLine = true,
                        modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                        value = speakerName,
                        onValueChange = { speakerName = it },
                        label = { Text("Speaker") },
                        singleLine = true,
                        modifier = Modifier.weight(1.1f)
                    )
                }
                OutlinedTextField(
                    value = speakerEmotion,
                    onValueChange = { speakerEmotion = it },
                    label = { Text("Emotion / Portrait State") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sceneContext,
                    onValueChange = { sceneContext = it },
                    label = { Text("Trigger / Scene Context") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = spokenLine,
                    onValueChange = { spokenLine = it },
                    label = { Text("Spoken Dialogue Line") },
                    placeholder = { Text("What the character says in the dialogue box…") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialogue_spoken_line_input")
                )
                OutlinedTextField(
                    value = choice1Prompt,
                    onValueChange = { choice1Prompt = it },
                    label = { Text("Branch 1: Player Response") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = choice1Outcome,
                    onValueChange = { choice1Outcome = it },
                    label = { Text("Branch 1: Consequence / Outcome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = choice2Prompt,
                    onValueChange = { choice2Prompt = it },
                    label = { Text("Branch 2: Player Response") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val saved = DialogueNode(
                        id = initial?.id ?: ("dlg_" + UUID.randomUUID().toString().take(6)),
                        nodeCode = nodeCode.ifBlank { "DLG_NODE" },
                        speakerName = speakerName.ifBlank { "Unknown Speaker" },
                        speakerEmotion = speakerEmotion,
                        sceneContext = sceneContext,
                        spokenLine = spokenLine.ifBlank { "Your transponder signal looks ghosted, Navigator. State your business." },
                        voiceDirectionNotes = voiceNotes,
                        choices = listOf(
                            DialogueChoice("c1", choice1Prompt, "Verify Telemetry", "${nodeCode}_A", choice1Outcome),
                            DialogueChoice("c2", choice2Prompt, "[Bribe / Persuade]", "${nodeCode}_B", choice2Outcome)
                        )
                    )
                    onSave(saved)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("save_dialogue_node_button")
            ) {
                Text("Save Node", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun GameItemEditorDialog(
    initial: GameItem?,
    onDismiss: () -> Unit,
    onSave: (GameItem) -> Unit
) {
    var codeId by remember { mutableStateOf(initial?.codeId ?: "MOD_PHASE_DRIVE") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "Ship Module") }
    var rarity by remember { mutableStateOf(initial?.rarity ?: ItemRarity.EXOTIC) }
    var valueCreditsText by remember { mutableStateOf((initial?.valueCredits ?: 2400).toString()) }
    var statsText by remember {
        mutableStateOf(initial?.statModifiers?.joinToString(", ") ?: "+30% Thrust Impulse, -15% Reactor Heat")
    }
    var tooltipDesc by remember { mutableStateOf(initial?.uiTooltipDescription ?: "") }
    var flavorLore by remember { mutableStateOf(initial?.flavorLore ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ElevatedSlatePanel,
        titleContentColor = CrispAlabaster,
        textContentColor = CrispAlabaster,
        title = { Text(if (initial == null) "New Item Specification" else "Edit Item Description") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    ItemRarity.entries.forEach { r ->
                        FilterChip(
                            selected = rarity == r,
                            onClick = { rarity = r },
                            label = { Text(r.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    placeholder = { Text("e.g., Null-Point Graviton Anchor") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_name_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = valueCreditsText,
                        onValueChange = { valueCreditsText = it.filter { c -> c.isDigit() } },
                        label = { Text("Value (Cr)") },
                        singleLine = true,
                        modifier = Modifier.weight(0.7f)
                    )
                }
                OutlinedTextField(
                    value = statsText,
                    onValueChange = { statsText = it },
                    label = { Text("Stat Modifiers (comma-separated)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tooltipDesc,
                    onValueChange = { tooltipDesc = it },
                    label = { Text("UI Tooltip Functional Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = flavorLore,
                    onValueChange = { flavorLore = it },
                    label = { Text("Flavor Lore Quote") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val saved = GameItem(
                        id = initial?.id ?: ("itm_" + UUID.randomUUID().toString().take(6)),
                        codeId = codeId.ifBlank { "ITM_CUSTOM" },
                        name = name.ifBlank { "Prototype Relic Module" },
                        category = category.ifBlank { "Equipment" },
                        rarity = rarity,
                        valueCredits = valueCreditsText.toIntOrNull() ?: 1000,
                        statModifiers = statsText.split(",").map { it.trim() }.filter { it.isNotBlank() },
                        uiTooltipDescription = tooltipDesc.ifBlank { "Experimental module salvaged from a deep-space anomaly." },
                        flavorLore = flavorLore.ifBlank { "\"Handle with insulated gloves—it still hums in zero gravity.\"" }
                    )
                    onSave(saved)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("save_item_button")
            ) {
                Text("Save Item", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
