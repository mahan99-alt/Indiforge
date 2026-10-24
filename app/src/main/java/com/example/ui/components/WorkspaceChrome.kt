package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
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
import com.example.model.GameProject
import com.example.model.SaveStatusState
import com.example.model.WorkspaceModule
import com.example.ui.theme.*

fun WorkspaceModule.icon(): ImageVector = when (this) {
    WorkspaceModule.DASHBOARD -> Icons.Default.SpaceDashboard
    WorkspaceModule.GDD_BUILDER -> Icons.AutoMirrored.Filled.LibraryBooks
    WorkspaceModule.WORLD_CODEX -> Icons.Default.Public
    WorkspaceModule.QUEST_DESIGNER -> Icons.Default.Explore
    WorkspaceModule.BACKLOG -> Icons.Default.ViewKanban
    WorkspaceModule.DIALOGUE_ITEMS -> Icons.Default.Forum
}

@Composable
fun IndieForgeTopBar(
    activeProject: GameProject?,
    saveStatus: SaveStatusState,
    lastSavedTime: String,
    showDrawerButton: Boolean,
    onOpenDrawer: () -> Unit,
    onOpenAiCoPilot: () -> Unit,
    onOpenExportDialog: () -> Unit
) {
    Surface(
        color = ElevatedSlatePanel,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = HairlineBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (showDrawerButton) {
                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("open_project_sidebar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Project Sidebar",
                                tint = CrispAlabaster
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(ElectricViolet, CyberCyan)
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesomeMosaic,
                                contentDescription = null,
                                tint = ObsidianBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Column(
                        modifier = Modifier
                            .clickable(enabled = showDrawerButton, onClick = onOpenDrawer)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeProject?.title ?: "IndieForge",
                                style = MaterialTheme.typography.titleMedium,
                                color = CrispAlabaster,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = VioletContainer,
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = activeProject?.engine?.substringBefore(" ") ?: "Godot",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnVioletContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        AutosaveStatusIndicator(
                            saveStatus = saveStatus,
                            lastSavedTime = lastSavedTime
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenAiCoPilot,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = VioletContainer.copy(alpha = 0.65f),
                            contentColor = OnVioletContainer
                        ),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 40.dp)
                            .testTag("open_ai_copilot_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Forge Co-Pilot",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Co-Pilot",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    FilledTonalButton(
                        onClick = onOpenExportDialog,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = CyanContainer,
                            contentColor = OnCyanContainer
                        ),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 40.dp)
                            .testTag("export_gdd_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.IosShare,
                            contentDescription = "Export GDD to Markdown or JSON",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Export",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AutosaveStatusIndicator(
    saveStatus: SaveStatusState,
    lastSavedTime: String
) {
    val dotColor by animateColorAsState(
        targetValue = when (saveStatus) {
            SaveStatusState.SAVED -> EmeraldSuccess
            SaveStatusState.SAVING -> AmberWarning
            SaveStatusState.MODIFIED -> SkyInfo
        },
        label = "autosave_dot"
    )
    val statusText = when (saveStatus) {
        SaveStatusState.SAVED -> "Saved locally • $lastSavedTime"
        SaveStatusState.SAVING -> "Autosaving to local DB…"
        SaveStatusState.MODIFIED -> "Unsaved changes"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.testTag("autosave_status_badge")
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            color = MutedSteel
        )
    }
}

@Composable
fun ProjectSidebarContent(
    projects: List<GameProject>,
    activeProjectId: String?,
    activeModule: WorkspaceModule,
    onSelectProject: (String) -> Unit,
    onSelectModule: (WorkspaceModule) -> Unit,
    onOpenNewProjectDialog: () -> Unit,
    onDuplicateActiveProject: () -> Unit,
    onDeleteProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(ElevatedSlatePanel)
            .border(width = 1.dp, color = HairlineBorder)
            .padding(16.dp)
    ) {
        // Brand Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(listOf(ElectricViolet, CyberCyan)))
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesomeMosaic,
                        contentDescription = null,
                        tint = ObsidianBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "INDIEFORGE",
                        style = MaterialTheme.typography.titleMedium,
                        color = CrispAlabaster,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "GAME DESIGN STUDIO",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = HairlineBorder)
        Spacer(modifier = Modifier.height(12.dp))

        // Section: Game Projects
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "GAME PROJECTS (${projects.size})",
                style = MaterialTheme.typography.labelMedium,
                color = MutedSteel
            )
            TextButton(
                onClick = onOpenNewProjectDialog,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.testTag("new_project_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Project",
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "New",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f, fill = false)
                .heightIn(max = 260.dp)
        ) {
            items(projects, key = { it.id }) { project ->
                val isSelected = project.id == activeProjectId
                Surface(
                    color = if (isSelected) VioletContainer else CardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) ElectricViolet else HairlineBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectProject(project.id) }
                        .testTag("project_item_${project.id}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = project.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = CrispAlabaster,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (projects.size > 1) {
                                IconButton(
                                    onClick = { onDeleteProject(project.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete ${project.title}",
                                        tint = MutedSteel,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${project.genre} • ${project.engine.substringBefore(" ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSteel,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "GDD Readiness",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedSteel
                            )
                            Text(
                                text = "${project.overallGddReadiness}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) CyberCyan else CrispAlabaster
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { project.overallGddReadiness / 100f },
                            color = if (isSelected) CyberCyan else ElectricViolet,
                            trackColor = ObsidianBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onOpenNewProjectDialog,
                border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create", style = MaterialTheme.typography.labelLarge)
            }
            OutlinedButton(
                onClick = onDuplicateActiveProject,
                border = BorderStroke(1.dp, HairlineBorder),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clone", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = HairlineBorder)
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "WORKSPACE MODULES",
            style = MaterialTheme.typography.labelMedium,
            color = MutedSteel
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            WorkspaceModule.entries.forEach { module ->
                val selected = module == activeModule
                Surface(
                    color = if (selected) InteractiveSurface else Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    border = if (selected) BorderStroke(1.dp, CyberCyan.copy(alpha = 0.45f)) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectModule(module) }
                        .testTag("sidebar_module_${module.name}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = module.icon(),
                            contentDescription = module.label,
                            tint = if (selected) CyberCyan else MutedSteel,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = module.label,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selected) CrispAlabaster else MutedSteel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreateProject: (
        title: String,
        tagline: String,
        genre: String,
        engine: String,
        platforms: String,
        studioName: String,
        useStarterTemplate: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var tagline by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Procedural Space Roguelite") }
    var engine by remember { mutableStateOf("Godot 4.3") }
    var platforms by remember { mutableStateOf("PC (Steam / Deck), Console") }
    var studioName by remember { mutableStateOf("Starlight Forge") }
    var useStarterTemplate by remember { mutableStateOf(true) }

    val presetGenres = listOf(
        "Procedural Space Roguelite",
        "2D Metroidvania",
        "Cozy Colony Sim",
        "Tactical Deckbuilder",
        "Survival Horror"
    )
    val presetEngines = listOf("Godot 4.3", "Unity 6", "Unreal 5.5", "Bevy / Rust", "GameMaker")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ElevatedSlatePanel,
        titleContentColor = CrispAlabaster,
        textContentColor = CrispAlabaster,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = CyberCyan)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Create New Game Project", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Game Title") },
                    placeholder = { Text("e.g., Starfall: Orbital Syndicate") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_project_title_input")
                )
                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("Elevator Pitch / Tagline") },
                    placeholder = { Text("One-sentence hook explaining what makes it special") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Quick Genre Presets", style = MaterialTheme.typography.labelMedium, color = MutedSteel)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    presetGenres.forEach { g ->
                        FilterChip(
                            selected = genre == g,
                            onClick = { genre = g },
                            label = { Text(g, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Engine", style = MaterialTheme.typography.labelMedium, color = MutedSteel)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    presetEngines.forEach { eng ->
                        FilterChip(
                            selected = engine == eng,
                            onClick = { engine = eng },
                            label = { Text(eng, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = studioName,
                        onValueChange = { studioName = it },
                        label = { Text("Studio") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = platforms,
                        onValueChange = { platforms = it },
                        label = { Text("Platforms") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pre-fill Starter GDD & Roadmap", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Seeds all 8 GDD pillars, starter quest, and milestones",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSteel
                        )
                    }
                    Switch(
                        checked = useStarterTemplate,
                        onCheckedChange = { useStarterTemplate = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateProject(
                        title.ifBlank { "Untitled Space Odyssey" },
                        tagline,
                        genre,
                        engine,
                        platforms,
                        studioName,
                        useStarterTemplate
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("confirm_create_project_button")
            ) {
                Text("Initialize Workspace", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedSteel)
            }
        }
    )
}
