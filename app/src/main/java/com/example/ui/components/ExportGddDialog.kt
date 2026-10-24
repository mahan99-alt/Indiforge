package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ProjectJsonSerializer
import com.example.model.GameProject
import com.example.ui.theme.*

@Composable
fun ExportGddDialog(
    project: GameProject,
    onDismiss: () -> Unit
) {
    var selectedFormat by remember { mutableStateOf("MARKDOWN") } // MARKDOWN or JSON
    var copiedBanner by remember { mutableStateOf(false) }

    val markdownOutput = remember(project) {
        ProjectJsonSerializer.toMarkdownDocument(project)
    }
    val jsonOutput = remember(project) {
        ProjectJsonSerializer.toJsonString(project, indentSpaces = 2)
    }

    val currentContent = if (selectedFormat == "MARKDOWN") markdownOutput else jsonOutput
    val lineCount = remember(currentContent) { currentContent.lines().size }
    val charCount = remember(currentContent) { currentContent.length }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ElevatedSlatePanel,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, HairlineBorder),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.86f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = CyberCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Export Game Design Document",
                                style = MaterialTheme.typography.titleLarge,
                                color = CrispAlabaster
                            )
                            Text(
                                text = "${project.title} • Full Spec Compiler",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSteel
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Export Dialog", tint = MutedSteel)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Format Switcher + Telemetry
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedFormat == "MARKDOWN",
                            onClick = {
                                selectedFormat = "MARKDOWN"
                                copiedBanner = false
                            },
                            label = { Text("Markdown (.md)") },
                            leadingIcon = {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.testTag("export_format_markdown_chip")
                        )
                        FilterChip(
                            selected = selectedFormat == "JSON",
                            onClick = {
                                selectedFormat = "JSON"
                                copiedBanner = false
                            },
                            label = { Text("JSON Schema (.json)") },
                            leadingIcon = {
                                Icon(Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.testTag("export_format_json_chip")
                        )
                    }

                    Text(
                        text = "$lineCount lines • ${charCount / 1024} KB",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberCyan
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Code Preview Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianBackground)
                        .border(1.dp, HairlineBorder, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = currentContent,
                        fontFamily = JetBrainsMonoFamily,
                        style = MaterialTheme.typography.bodySmall,
                        color = CrispAlabaster,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .testTag("export_preview_text")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (copiedBanner) {
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Copied full ${if (selectedFormat == "MARKDOWN") "Markdown (.md)" else "JSON (.json)"} document to clipboard!",
                                style = MaterialTheme.typography.bodySmall,
                                color = CrispAlabaster
                            )
                        }
                    }
                }

                // Bottom Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(currentContent))
                            copiedBanner = true
                        },
                        border = BorderStroke(1.dp, CyberCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_export_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy ${if (selectedFormat == "MARKDOWN") ".MD" else ".JSON"}", color = CyberCyan)
                    }

                    Button(
                        onClick = {
                            val ext = if (selectedFormat == "MARKDOWN") "md" else "json"
                            val mime = if (selectedFormat == "MARKDOWN") "text/markdown" else "application/json"
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = mime
                                putExtra(Intent.EXTRA_SUBJECT, "${project.title}_GDD.$ext")
                                putExtra(Intent.EXTRA_TEXT, currentContent)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Export GDD ($ext)"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_export_button")
                    ) {
                        Icon(Icons.Default.IosShare, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share / Save File", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
