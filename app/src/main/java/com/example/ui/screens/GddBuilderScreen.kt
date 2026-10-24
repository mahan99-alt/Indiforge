package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SeedProjects
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun GddBuilderScreen(
    project: GameProject,
    selectedPillar: GddPillarType,
    onSelectPillar: (GddPillarType) -> Unit,
    onUpdateSection: (
        pillar: GddPillarType,
        designPillarStatement: String,
        contentMarkdown: String,
        keyMechanicsBullets: List<String>,
        status: SectionStatus
    ) -> Unit,
    onCycleStatus: (GddPillarType) -> Unit,
    onOpenAiCoPilotForPillar: (GddPillarType) -> Unit
) {
    val activeSection = remember(project, selectedPillar) {
        project.gddSections.find { it.pillar == selectedPillar }
            ?: GddSection(
                pillar = selectedPillar,
                designPillarStatement = "Core pillar statement for ${selectedPillar.title}",
                contentMarkdown = "",
                keyMechanicsBullets = emptyList()
            )
    }

    var pillarStatement by remember(project.id, selectedPillar) {
        mutableStateOf(activeSection.designPillarStatement)
    }
    var markdownContent by remember(project.id, selectedPillar) {
        mutableStateOf(activeSection.contentMarkdown)
    }
    var bullets by remember(project.id, selectedPillar) {
        mutableStateOf(activeSection.keyMechanicsBullets)
    }
    var newBulletText by remember { mutableStateOf("") }

    // Keep local fields in sync when Co-Pilot inserts text externally
    LaunchedEffect(activeSection.contentMarkdown, activeSection.designPillarStatement, activeSection.keyMechanicsBullets) {
        pillarStatement = activeSection.designPillarStatement
        markdownContent = activeSection.contentMarkdown
        bullets = activeSection.keyMechanicsBullets
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("gdd_builder_screen")
    ) {
        val isWideLayout = maxWidth >= 720.dp

        if (isWideLayout) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Pillar Tree Sidebar (240.dp)
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .width(240.dp)
                        .fillMaxHeight()
                        .background(ElevatedSlatePanel)
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "8 GDD PILLARS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MutedSteel,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                    GddPillarType.entries.forEach { pillar ->
                        val sec = project.gddSections.find { it.pillar == pillar }
                        val isSelected = pillar == selectedPillar
                        PillarSidebarItem(
                            pillar = pillar,
                            status = sec?.status ?: SectionStatus.DRAFT,
                            wordCount = sec?.wordCount ?: 0,
                            isSelected = isSelected,
                            onClick = { onSelectPillar(pillar) }
                        )
                    }
                }

                // Right Modular Editor Canvas
                GddSectionEditorPane(
                    section = activeSection,
                    pillarStatement = pillarStatement,
                    markdownContent = markdownContent,
                    bullets = bullets,
                    newBulletText = newBulletText,
                    onPillarStatementChange = {
                        pillarStatement = it
                        onUpdateSection(selectedPillar, it, markdownContent, bullets, activeSection.status)
                    },
                    onMarkdownChange = {
                        markdownContent = it
                        onUpdateSection(selectedPillar, pillarStatement, it, bullets, activeSection.status)
                    },
                    onAddBullet = {
                        if (newBulletText.isNotBlank()) {
                            val updated = bullets + newBulletText.trim()
                            bullets = updated
                            newBulletText = ""
                            onUpdateSection(selectedPillar, pillarStatement, markdownContent, updated, activeSection.status)
                        }
                    },
                    onRemoveBullet = { idx ->
                        val updated = bullets.filterIndexed { i, _ -> i != idx }
                        bullets = updated
                        onUpdateSection(selectedPillar, pillarStatement, markdownContent, updated, activeSection.status)
                    },
                    onNewBulletTextChange = { newBulletText = it },
                    onCycleStatus = { onCycleStatus(selectedPillar) },
                    onOpenAiCoPilot = { onOpenAiCoPilotForPillar(selectedPillar) },
                    onLoadQuickBlueprint = { tpl ->
                        val combinedMd = if (markdownContent.isBlank()) tpl.contentBody else "$markdownContent\n\n${tpl.contentBody}"
                        val combinedBullets = (bullets + tpl.bullets).distinct()
                        pillarStatement = tpl.pillarStatement
                        markdownContent = combinedMd
                        bullets = combinedBullets
                        onUpdateSection(selectedPillar, tpl.pillarStatement, combinedMd, combinedBullets, activeSection.status)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Horizontal 8-Pillar Selector Strip on Compact / Mobile
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ElevatedSlatePanel)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    GddPillarType.entries.forEach { pillar ->
                        val sec = project.gddSections.find { it.pillar == pillar }
                        val isSelected = pillar == selectedPillar
                        val dotColor = when (sec?.status) {
                            SectionStatus.LOCKED -> EmeraldSuccess
                            SectionStatus.IN_REVIEW -> CyberCyan
                            else -> AmberWarning
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectPillar(pillar) },
                            label = {
                                Text(
                                    text = pillar.title.substringBefore(" &"),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                            },
                            modifier = Modifier.testTag("gdd_pillar_chip_${pillar.id}")
                        )
                    }
                }

                GddSectionEditorPane(
                    section = activeSection,
                    pillarStatement = pillarStatement,
                    markdownContent = markdownContent,
                    bullets = bullets,
                    newBulletText = newBulletText,
                    onPillarStatementChange = {
                        pillarStatement = it
                        onUpdateSection(selectedPillar, it, markdownContent, bullets, activeSection.status)
                    },
                    onMarkdownChange = {
                        markdownContent = it
                        onUpdateSection(selectedPillar, pillarStatement, it, bullets, activeSection.status)
                    },
                    onAddBullet = {
                        if (newBulletText.isNotBlank()) {
                            val updated = bullets + newBulletText.trim()
                            bullets = updated
                            newBulletText = ""
                            onUpdateSection(selectedPillar, pillarStatement, markdownContent, updated, activeSection.status)
                        }
                    },
                    onRemoveBullet = { idx ->
                        val updated = bullets.filterIndexed { i, _ -> i != idx }
                        bullets = updated
                        onUpdateSection(selectedPillar, pillarStatement, markdownContent, updated, activeSection.status)
                    },
                    onNewBulletTextChange = { newBulletText = it },
                    onCycleStatus = { onCycleStatus(selectedPillar) },
                    onOpenAiCoPilot = { onOpenAiCoPilotForPillar(selectedPillar) },
                    onLoadQuickBlueprint = { tpl ->
                        val combinedMd = if (markdownContent.isBlank()) tpl.contentBody else "$markdownContent\n\n${tpl.contentBody}"
                        val combinedBullets = (bullets + tpl.bullets).distinct()
                        pillarStatement = tpl.pillarStatement
                        markdownContent = combinedMd
                        bullets = combinedBullets
                        onUpdateSection(selectedPillar, tpl.pillarStatement, combinedMd, combinedBullets, activeSection.status)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PillarSidebarItem(
    pillar: GddPillarType,
    status: SectionStatus,
    wordCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val statusColor = when (status) {
        SectionStatus.LOCKED -> EmeraldSuccess
        SectionStatus.IN_REVIEW -> CyberCyan
        SectionStatus.DRAFT -> AmberWarning
    }
    Surface(
        color = if (isSelected) VioletContainer else CardSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (isSelected) ElectricViolet else HairlineBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = pillar.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = CrispAlabaster
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${status.label} • $wordCount words",
                style = MaterialTheme.typography.labelSmall,
                color = MutedSteel
            )
        }
    }
}

@Composable
private fun GddSectionEditorPane(
    section: GddSection,
    pillarStatement: String,
    markdownContent: String,
    bullets: List<String>,
    newBulletText: String,
    onPillarStatementChange: (String) -> Unit,
    onMarkdownChange: (String) -> Unit,
    onAddBullet: () -> Unit,
    onRemoveBullet: (Int) -> Unit,
    onNewBulletTextChange: (String) -> Unit,
    onCycleStatus: () -> Unit,
    onOpenAiCoPilot: () -> Unit,
    onLoadQuickBlueprint: (BlueprintTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (section.status) {
        SectionStatus.LOCKED -> EmeraldSuccess
        SectionStatus.IN_REVIEW -> CyberCyan
        SectionStatus.DRAFT -> AmberWarning
    }
    val templates = remember(section.pillar) {
        SeedProjects.getBlueprintTemplatesForPillar(section.pillar)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Section Header Card
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GDD SECTION 0${section.pillar.ordinal + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                        Text(
                            text = section.pillar.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = CrispAlabaster
                        )
                        Text(
                            text = section.pillar.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSteel
                        )
                    }

                    Surface(
                        color = statusColor.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onCycleStatus)
                            .testTag("cycle_gdd_status_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = section.status.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick AI & Blueprint Toolbar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilledTonalButton(
                        onClick = onOpenAiCoPilot,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = VioletContainer,
                            contentColor = OnVioletContainer
                        )
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI / Blueprint Co-Pilot", style = MaterialTheme.typography.labelLarge)
                    }

                    templates.firstOrNull()?.let { firstTpl ->
                        OutlinedButton(
                            onClick = { onLoadQuickBlueprint(firstTpl) },
                            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.PostAdd, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Append '${firstTpl.title.take(18)}…'", style = MaterialTheme.typography.labelLarge, color = CyberCyan)
                        }
                    }
                }
            }
        }

        // 1. Design Pillar Statement Callout Editor
        Surface(
            color = VioletContainer.copy(alpha = 0.35f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CORE DESIGN PILLAR PROMISE",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnVioletContainer
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = pillarStatement,
                    onValueChange = onPillarStatementChange,
                    placeholder = { Text("One-sentence razor that guides every design decision in this section…") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gdd_pillar_statement_input")
                )
            }
        }

        // 2. Main Markdown Specification Editor
        Surface(
            color = ElevatedSlatePanel,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, HairlineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "DETAILED SPECIFICATION & PROSE (MARKDOWN)",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberCyan
                    )
                    Text(
                        text = "${section.wordCount} words • ${section.completionPercent}% ready",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSteel
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = markdownContent,
                    onValueChange = onMarkdownChange,
                    minLines = 9,
                    maxLines = 22,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = { Text("Write detailed gameplay loops, formulas, narrative arcs, or control schemes…") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gdd_markdown_content_input")
                )
            }
        }

        // 3. Key Mechanics & Rules Checklist
        Surface(
            color = CardSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, HairlineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "KEY SYSTEMIC RULES & MECHANIC BULLETS (${bullets.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = CrispAlabaster
                )
                Spacer(modifier = Modifier.height(8.dp))

                bullets.forEachIndexed { idx, bullet ->
                    Surface(
                        color = ObsidianBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, HairlineBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "0${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberCyan
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = bullet,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CrispAlabaster
                                )
                            }
                            IconButton(
                                onClick = { onRemoveBullet(idx) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove rule",
                                    tint = MutedSteel,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newBulletText,
                        onValueChange = onNewBulletTextChange,
                        placeholder = { Text("Add a concrete rule, formula, or design constraint…") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("gdd_new_bullet_input")
                    )
                    Button(
                        onClick = onAddBullet,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        modifier = Modifier.testTag("gdd_add_bullet_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Rule", tint = ObsidianBackground)
                    }
                }
            }
        }
    }
}
