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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.CodexCategory
import com.example.model.CodexEntry
import com.example.model.GameProject
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun WorldCodexScreen(
    project: GameProject,
    selectedCategory: CodexCategory?,
    onSelectCategory: (CodexCategory?) -> Unit,
    onUpsertEntry: (CodexEntry) -> Unit,
    onDeleteEntry: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var editingEntry by remember { mutableStateOf<CodexEntry?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredEntries = remember(project.codexEntries, selectedCategory, searchQuery) {
        project.codexEntries.filter { entry ->
            val matchesCat = selectedCategory == null || entry.category == selectedCategory
            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isEmpty() ||
                entry.name.lowercase().contains(q) ||
                entry.roleOrType.lowercase().contains(q) ||
                entry.summaryLore.lowercase().contains(q) ||
                entry.affiliationOrBiome.lowercase().contains(q) ||
                entry.tags.any { it.lowercase().contains(q) }
            matchesCat && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("world_codex_screen")
    ) {
        // Header + Create Entry Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "WORLD CODEX & ENTITY DATABASE",
                    style = MaterialTheme.typography.titleLarge,
                    color = CrispAlabaster
                )
                Text(
                    text = "Characters, Factions, Locations, and Enemies (${project.codexEntries.size} total)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSteel
                )
            }
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("add_codex_entry_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Entry", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search characters, factions, biomes, enemies, or tags…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("codex_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onSelectCategory(null) },
                label = { Text("All (${project.codexEntries.size})") }
            )
            CodexCategory.entries.forEach { cat ->
                val count = project.codexEntries.count { it.category == cat }
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onSelectCategory(cat) },
                    label = { Text("${cat.label} ($count)") },
                    modifier = Modifier.testTag("codex_filter_${cat.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredEntries.isEmpty()) {
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, HairlineBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(28.dp)
                ) {
                    Icon(Icons.Default.Public, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No Codex Entries Match Filter", style = MaterialTheme.typography.titleMedium, color = CrispAlabaster)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Create a new Character, Faction, Location, or Enemy entry to expand your world bible.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSteel
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredEntries, key = { it.id }) { entry ->
                    CodexEntryCard(
                        entry = entry,
                        onEdit = { editingEntry = entry },
                        onDelete = { onDeleteEntry(entry.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog || editingEntry != null) {
        CodexEntryEditorDialog(
            initial = editingEntry,
            defaultCategory = selectedCategory ?: CodexCategory.CHARACTER,
            onDismiss = {
                showCreateDialog = false
                editingEntry = null
            },
            onSave = { saved ->
                onUpsertEntry(saved)
                showCreateDialog = false
                editingEntry = null
            }
        )
    }
}

@Composable
private fun CodexEntryCard(
    entry: CodexEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val catColor: Color = when (entry.category) {
        CodexCategory.CHARACTER -> CyberCyan
        CodexCategory.FACTION -> ElectricViolet
        CodexCategory.LOCATION -> EmeraldSuccess
        CodexCategory.ENEMY -> RoseDanger
    }

    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, HairlineBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
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
                        color = catColor.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, catColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = entry.category.singular.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = catColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        color = ObsidianBackground,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, HairlineBorder)
                    ) {
                        Text(
                            text = entry.threatOrTier,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedSteel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit ${entry.name}", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete ${entry.name}", tint = MutedSteel, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.name,
                style = MaterialTheme.typography.titleLarge,
                color = CrispAlabaster
            )
            Text(
                text = "${entry.roleOrType} • ${entry.affiliationOrBiome}",
                style = MaterialTheme.typography.labelLarge,
                color = catColor
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.summaryLore,
                style = MaterialTheme.typography.bodyMedium,
                color = CrispAlabaster
            )

            if (entry.gameplayBehavior.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = ObsidianBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, HairlineBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "GAMEPLAY & SYSTEMIC BEHAVIOR",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = entry.gameplayBehavior,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSteel
                        )
                    }
                }
            }

            if (entry.statsOrAttributes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    entry.statsOrAttributes.forEach { stat ->
                        Surface(
                            color = ElevatedSlatePanel,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, catColor.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = stat,
                                style = MaterialTheme.typography.labelSmall,
                                color = CrispAlabaster,
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
private fun CodexEntryEditorDialog(
    initial: CodexEntry?,
    defaultCategory: CodexCategory,
    onDismiss: () -> Unit,
    onSave: (CodexEntry) -> Unit
) {
    var category by remember { mutableStateOf(initial?.category ?: defaultCategory) }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var roleOrType by remember { mutableStateOf(initial?.roleOrType ?: "") }
    var affiliationOrBiome by remember { mutableStateOf(initial?.affiliationOrBiome ?: "") }
    var threatOrTier by remember { mutableStateOf(initial?.threatOrTier ?: "Tier II") }
    var summaryLore by remember { mutableStateOf(initial?.summaryLore ?: "") }
    var gameplayBehavior by remember { mutableStateOf(initial?.gameplayBehavior ?: "") }
    var statsText by remember {
        mutableStateOf(initial?.statsOrAttributes?.joinToString(", ") ?: "HP: 500, Speed: Fast, Drop: Relic Shard")
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
                Text(if (initial == null) "New World Codex Entry" else "Edit Codex Entry")
                TextButton(
                    onClick = {
                        when (category) {
                            CodexCategory.CHARACTER -> {
                                name = "Dr. Elara Voss"
                                roleOrType = "Xenobiologist & Signal Specialist"
                                affiliationOrBiome = "Deep Rim Survey Outpost"
                                threatOrTier = "Key NPC Ally"
                                summaryLore = "Survived three months inside a bioluminescent plasma reef by calibrating her suit's harmonic emitters."
                                gameplayBehavior = "Trades rare bio-polymers and decodes organic anomaly samples."
                                statsText = "Trust Ranks: 1-5, Specialty: Bio-Scanners"
                            }
                            CodexCategory.FACTION -> {
                                name = "The Iron Horizons"
                                roleOrType = "Deep-Space Wreck Breakers Union"
                                affiliationOrBiome = "Sector D-04 Graveyard"
                                threatOrTier = "Industrial Guild"
                                summaryLore = "Independent salvagers who live inside hollowed-out carrier engines."
                                gameplayBehavior = "Provides hull armor upgrades and heavy cutting lasers at discounted rates."
                                statsText = "Fleet: Heavy Tugs, Tech: Hull Armor, Disposition: Neutral"
                            }
                            CodexCategory.LOCATION -> {
                                name = "Cryo-Vault Erebos"
                                roleOrType = "Sub-Glacial Precursor Observatory"
                                affiliationOrBiome = "Tidally Locked Rogue Moon"
                                threatOrTier = "Hazard Tier III"
                                summaryLore = "Buried beneath 4 kilometers of nitrogen ice; accessible only via thermal geyser shafts."
                                gameplayBehavior = "Extreme cold drains ship battery 2x faster unless engines remain above 40% thrust."
                                statsText = "Temp: -210C, Loot: Quantum Lenses, Depth: 4.2 km"
                            }
                            CodexCategory.ENEMY -> {
                                name = "Ion-Wraith Stalker"
                                roleOrType = "Cloaked Ambush Interceptor"
                                affiliationOrBiome = "Nebula Trade Lanes"
                                threatOrTier = "Elite Threat"
                                summaryLore = "Automated pirate interceptor retrofitted with illegal Precursor phase-coils."
                                gameplayBehavior = "Remains cloaked until within 400m; decloaks to fire EMP harpoons that disable lateral thrusters."
                                statsText = "HP: 1,450, Shield: 600 EM, Drop: Phase-Coil"
                            }
                        }
                    }
                ) {
                    Text("Fill Sample", color = CyberCyan, style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    CodexCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.singular, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("codex_name_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = roleOrType,
                        onValueChange = { roleOrType = it },
                        label = { Text("Role / Archetype") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = threatOrTier,
                        onValueChange = { threatOrTier = it },
                        label = { Text("Tier / Threat") },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }
                OutlinedTextField(
                    value = affiliationOrBiome,
                    onValueChange = { affiliationOrBiome = it },
                    label = { Text("Affiliation / Biome / Region") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = summaryLore,
                    onValueChange = { summaryLore = it },
                    label = { Text("Lore & Background Summary") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = gameplayBehavior,
                    onValueChange = { gameplayBehavior = it },
                    label = { Text("Gameplay Mechanics / Combat Behavior") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = statsText,
                    onValueChange = { statsText = it },
                    label = { Text("Stats / Attributes (comma-separated)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val saved = CodexEntry(
                        id = initial?.id ?: ("cdx_" + UUID.randomUUID().toString().take(6)),
                        category = category,
                        name = name.ifBlank { "Unnamed ${category.singular}" },
                        roleOrType = roleOrType.ifBlank { category.singular },
                        affiliationOrBiome = affiliationOrBiome.ifBlank { "Uncharted Sector" },
                        threatOrTier = threatOrTier.ifBlank { "Tier I" },
                        summaryLore = summaryLore.ifBlank { "Lore entry pending detail." },
                        gameplayBehavior = gameplayBehavior,
                        statsOrAttributes = statsText.split(",").map { it.trim() }.filter { it.isNotBlank() },
                        tags = listOf(category.singular)
                    )
                    onSave(saved)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("save_codex_entry_button")
            ) {
                Text("Save Entry", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
