package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SeedProjects
import com.example.model.BlueprintTemplate
import com.example.model.GameProject
import com.example.model.GddPillarType
import com.example.ui.AiCoPilotState
import com.example.ui.theme.*

@Composable
fun AiForgeCoPilotSheet(
    project: GameProject,
    activePillar: GddPillarType,
    aiState: AiCoPilotState,
    onGenerateWithGemini: (targetLabel: String, prompt: String, fallbackSample: String) -> Unit,
    onLoadTemplateDirectly: (templateText: String, title: String) -> Unit,
    onApplyToActiveGddPillar: (GddPillarType, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMode by remember { mutableStateOf("TEMPLATES") } // "TEMPLATES" or "GEMINI_AI"
    var targetPillar by remember { mutableStateOf(activePillar) }
    var customPrompt by remember(targetPillar) {
        mutableStateOf("Expand the ${targetPillar.title} section for ${project.title} with 3 concrete systemic mechanics, risk/reward balance numbers, and player feedback cues.")
    }

    val templatesForPillar = remember(targetPillar) {
        SeedProjects.getBlueprintTemplatesForPillar(targetPillar)
    }

    var editableScratchpad by remember {
        mutableStateOf(
            templatesForPillar.firstOrNull()?.contentBody ?: ""
        )
    }
    var activeSourceBadge by remember {
        mutableStateOf("Editable Studio Blueprint (Offline Ready)")
    }
    var appliedSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Sync AI state updates into the editable scratchpad so the user can always edit before applying
    LaunchedEffect(aiState) {
        when (aiState) {
            is AiCoPilotState.Streaming -> {
                editableScratchpad = aiState.partialText
                activeSourceBadge = "Streaming Gemini 3.8 Flash…"
            }
            is AiCoPilotState.Success -> {
                editableScratchpad = aiState.generatedText
                activeSourceBadge = aiState.sourceLabel
            }
            is AiCoPilotState.Error -> {
                editableScratchpad = aiState.fallbackSampleText
                activeSourceBadge = "Fallback Blueprint Sample (Editable)"
            }
            else -> {}
        }
    }

    val clipboardManager = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ElevatedSlatePanel,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyberCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Forge Design Co-Pilot & Blueprints",
                                style = MaterialTheme.typography.titleLarge,
                                color = CrispAlabaster
                            )
                            Text(
                                text = "Editable Studio Templates + Optional Live Gemini 3.8 AI",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSteel
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Co-Pilot", tint = MutedSteel)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Toggle: Editable Studio Blueprints vs Live Gemini AI
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedMode == "TEMPLATES",
                        onClick = { selectedMode = "TEMPLATES" },
                        label = { Text("Editable Blueprints & Samples") },
                        leadingIcon = {
                            Icon(Icons.Default.Architecture, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.testTag("copilot_mode_templates")
                    )
                    FilterChip(
                        selected = selectedMode == "GEMINI_AI",
                        onClick = { selectedMode = "GEMINI_AI" },
                        label = { Text("Live Gemini AI Generator") },
                        leadingIcon = {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.testTag("copilot_mode_gemini")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Target GDD Pillar Selector
                Text(
                    text = "TARGET GDD SECTION:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedSteel
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    GddPillarType.entries.forEach { pillar ->
                        FilterChip(
                            selected = targetPillar == pillar,
                            onClick = {
                                targetPillar = pillar
                                val tpl = SeedProjects.getBlueprintTemplatesForPillar(pillar).firstOrNull()
                                if (tpl != null && selectedMode == "TEMPLATES") {
                                    editableScratchpad = tpl.contentBody
                                    activeSourceBadge = "Studio Blueprint: ${tpl.title}"
                                }
                            },
                            label = { Text(pillar.title.substringBefore(" &"), style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (selectedMode == "TEMPLATES") {
                    Text(
                        text = "CURATED DESIGN BLUEPRINTS (TAP TO LOAD INTO SCRATCHPAD):",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        templatesForPillar.forEach { tpl ->
                            TemplateCardChip(
                                template = tpl,
                                onSelect = {
                                    editableScratchpad = buildString {
                                        appendLine("**Design Pillar:** *\"${tpl.pillarStatement}\"*")
                                        appendLine()
                                        appendLine(tpl.contentBody)
                                        appendLine()
                                        appendLine("### Key Mechanics & Rules")
                                        tpl.bullets.forEach { b -> appendLine("- $b") }
                                    }.trim()
                                    activeSourceBadge = "Blueprint: ${tpl.title}"
                                    onLoadTemplateDirectly(editableScratchpad, tpl.title)
                                }
                            )
                        }
                    }
                } else {
                    // Live Gemini AI Mode
                    OutlinedTextField(
                        value = customPrompt,
                        onValueChange = { customPrompt = it },
                        label = { Text("Design Prompt for Gemini 3.8 Flash") },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_prompt_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            AssistChip(
                                onClick = {
                                    customPrompt = "Generate 3 high-risk/high-reward gameplay mechanics for ${project.title} (${targetPillar.title}) with exact cooldowns and telemetry rules."
                                },
                                label = { Text("+3 Mechanics", style = MaterialTheme.typography.labelSmall) }
                            )
                            AssistChip(
                                onClick = {
                                    customPrompt = "Write an evocative anomaly encounter and environmental storytelling breakdown for ${project.title}."
                                },
                                label = { Text("+Anomaly Event", style = MaterialTheme.typography.labelSmall) }
                            )
                            AssistChip(
                                onClick = {
                                    customPrompt = "Tighten scope for a solo/small indie team building in ${project.engine}—list 3 things to cut and 3 core pillars to polish."
                                },
                                label = { Text("Scope Audit", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val fallback = templatesForPillar.firstOrNull()?.contentBody
                                    ?: "### ${targetPillar.title} Blueprint\n- Core Loop Integration\n- Telemetry & Player Feedback"
                                onGenerateWithGemini(targetPillar.title, customPrompt, fallback)
                            },
                            enabled = aiState !is AiCoPilotState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                            modifier = Modifier.testTag("run_gemini_generate_button")
                        ) {
                            if (aiState is AiCoPilotState.Loading) {
                                CircularProgressIndicator(
                                    color = ObsidianBackground,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generating…", color = ObsidianBackground)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (aiState is AiCoPilotState.Error) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = AmberWarning.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = aiState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = AmberWarning,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Editable Output Scratchpad Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "EDITABLE DESIGN SCRATCHPAD",
                        style = MaterialTheme.typography.labelMedium,
                        color = MutedSteel
                    )
                    Surface(
                        color = CyanContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = activeSourceBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnCyanContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = editableScratchpad,
                    onValueChange = { editableScratchpad = it },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMonoFamily),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("copilot_scratchpad_editor")
                )

                if (appliedSuccessMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = appliedSuccessMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldSuccess
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(editableScratchpad))
                            appliedSuccessMessage = "Copied scratchpad content to clipboard."
                        },
                        border = BorderStroke(1.dp, HairlineBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Text")
                    }

                    Button(
                        onClick = {
                            onApplyToActiveGddPillar(targetPillar, editableScratchpad)
                            appliedSuccessMessage = "Appended to '${targetPillar.title}' & autosaved locally!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("apply_to_gdd_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Insert into ${targetPillar.title.substringBefore(" &")}",
                            color = ObsidianBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateCardChip(
    template: BlueprintTemplate,
    onSelect: () -> Unit
) {
    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, HairlineBorder),
        modifier = Modifier
            .width(250.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onSelect)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = template.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = template.title,
                style = MaterialTheme.typography.titleSmall,
                color = CrispAlabaster
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                color = MutedSteel,
                maxLines = 2
            )
        }
    }
}
