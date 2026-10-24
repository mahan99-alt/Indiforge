package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GddPillarType
import com.example.model.WorkspaceModule
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            IndieForgeTheme {
                Surface(
                    color = ObsidianBackground,
                    modifier = Modifier.fillMaxSize()
                ) {
                    IndieForgeWorkspaceApp()
                }
            }
        }
    }
}

@Composable
fun IndieForgeWorkspaceApp(
    viewModel: WorkspaceViewModel = viewModel()
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()
    val activeModule by viewModel.activeModule.collectAsStateWithLifecycle()
    val selectedGddPillar by viewModel.selectedGddPillar.collectAsStateWithLifecycle()
    val selectedCodexCategory by viewModel.selectedCodexCategory.collectAsStateWithLifecycle()
    val saveStatus by viewModel.saveStatus.collectAsStateWithLifecycle()
    val lastSavedTime by viewModel.lastSavedTimeFormatted.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()

    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showAiCoPilotSheet by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Handle system Back button when on a sub-module or when drawer is open
    BackHandler(enabled = drawerState.isOpen || activeModule != WorkspaceModule.DASHBOARD) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            viewModel.selectModule(WorkspaceModule.DASHBOARD)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isDesktopOrTabletWide = maxWidth >= 840.dp

        if (isDesktopOrTabletWide) {
            // Desktop / Tablet Split-Pane Studio Layout with Permanent Sidebar
            Row(modifier = Modifier.fillMaxSize()) {
                ProjectSidebarContent(
                    projects = projects,
                    activeProjectId = activeProject?.id,
                    activeModule = activeModule,
                    onSelectProject = { viewModel.selectProject(it) },
                    onSelectModule = { viewModel.selectModule(it) },
                    onOpenNewProjectDialog = { showNewProjectDialog = true },
                    onDuplicateActiveProject = { viewModel.duplicateCurrentProject() },
                    onDeleteProject = { viewModel.deleteProject(it) },
                    modifier = Modifier
                        .width(290.dp)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .navigationBarsPadding()
                ) {
                    IndieForgeTopBar(
                        activeProject = activeProject,
                        saveStatus = saveStatus,
                        lastSavedTime = lastSavedTime,
                        showDrawerButton = false,
                        onOpenDrawer = {},
                        onOpenAiCoPilot = { showAiCoPilotSheet = true },
                        onOpenExportDialog = { showExportDialog = true }
                    )

                    WorkspaceModuleCanvas(
                        activeProject = activeProject,
                        activeModule = activeModule,
                        selectedGddPillar = selectedGddPillar,
                        selectedCodexCategory = selectedCodexCategory,
                        viewModel = viewModel,
                        onOpenAiCoPilotForPillar = { pillar ->
                            viewModel.selectGddPillar(pillar)
                            showAiCoPilotSheet = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Compact / Mobile Layout with Modal Project Sidebar Drawer + Bottom Navigation Bar
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = ElevatedSlatePanel,
                        modifier = Modifier.width(305.dp)
                    ) {
                        ProjectSidebarContent(
                            projects = projects,
                            activeProjectId = activeProject?.id,
                            activeModule = activeModule,
                            onSelectProject = {
                                viewModel.selectProject(it)
                                coroutineScope.launch { drawerState.close() }
                            },
                            onSelectModule = {
                                viewModel.selectModule(it)
                                coroutineScope.launch { drawerState.close() }
                            },
                            onOpenNewProjectDialog = {
                                coroutineScope.launch { drawerState.close() }
                                showNewProjectDialog = true
                            },
                            onDuplicateActiveProject = {
                                viewModel.duplicateCurrentProject()
                                coroutineScope.launch { drawerState.close() }
                            },
                            onDeleteProject = { viewModel.deleteProject(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            ) {
                Scaffold(
                    containerColor = ObsidianBackground,
                    topBar = {
                        IndieForgeTopBar(
                            activeProject = activeProject,
                            saveStatus = saveStatus,
                            lastSavedTime = lastSavedTime,
                            showDrawerButton = true,
                            onOpenDrawer = {
                                coroutineScope.launch { drawerState.open() }
                            },
                            onOpenAiCoPilot = { showAiCoPilotSheet = true },
                            onOpenExportDialog = { showExportDialog = true }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = ElevatedSlatePanel,
                            tonalElevation = 4.dp
                        ) {
                            WorkspaceModule.entries.forEach { module ->
                                val selected = activeModule == module
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.selectModule(module) },
                                    icon = {
                                        Icon(
                                            imageVector = module.icon(),
                                            contentDescription = module.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = module.shortLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberCyan,
                                        selectedTextColor = CyberCyan,
                                        indicatorColor = VioletContainer,
                                        unselectedIconColor = MutedSteel,
                                        unselectedTextColor = MutedSteel
                                    ),
                                    modifier = Modifier.testTag("bottom_nav_${module.name}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    WorkspaceModuleCanvas(
                        activeProject = activeProject,
                        activeModule = activeModule,
                        selectedGddPillar = selectedGddPillar,
                        selectedCodexCategory = selectedCodexCategory,
                        viewModel = viewModel,
                        onOpenAiCoPilotForPillar = { pillar ->
                            viewModel.selectGddPillar(pillar)
                            showAiCoPilotSheet = true
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onCreateProject = { title, tagline, genre, engine, platforms, studioName, useStarterTemplate ->
                viewModel.createNewProject(
                    title = title,
                    tagline = tagline,
                    genre = genre,
                    engine = engine,
                    platforms = platforms,
                    studioName = studioName,
                    useStarterTemplate = useStarterTemplate
                )
            }
        )
    }

    val currentProj = activeProject
    if (showExportDialog && currentProj != null) {
        ExportGddDialog(
            project = currentProj,
            onDismiss = { showExportDialog = false }
        )
    }

    if (showAiCoPilotSheet && currentProj != null) {
        AiForgeCoPilotSheet(
            project = currentProj,
            activePillar = selectedGddPillar,
            aiState = aiState,
            onGenerateWithGemini = { targetLabel, prompt, fallback ->
                viewModel.generateWithGeminiOrFallback(targetLabel, prompt, fallback)
            },
            onLoadTemplateDirectly = { text, title ->
                viewModel.loadEditableTemplateDirectly(text, title)
            },
            onApplyToActiveGddPillar = { pillar, textToInsert ->
                val existing = currentProj.gddSections.find { it.pillar == pillar }
                if (existing != null) {
                    val combinedMd = if (existing.contentMarkdown.isBlank()) {
                        textToInsert
                    } else {
                        "${existing.contentMarkdown}\n\n$textToInsert"
                    }
                    viewModel.updateGddSection(
                        pillar = pillar,
                        designPillarStatement = existing.designPillarStatement,
                        contentMarkdown = combinedMd,
                        keyMechanicsBullets = existing.keyMechanicsBullets,
                        status = existing.status
                    )
                }
            },
            onDismiss = { showAiCoPilotSheet = false }
        )
    }
}

@Composable
private fun WorkspaceModuleCanvas(
    activeProject: com.example.model.GameProject?,
    activeModule: WorkspaceModule,
    selectedGddPillar: GddPillarType,
    selectedCodexCategory: com.example.model.CodexCategory?,
    viewModel: WorkspaceViewModel,
    onOpenAiCoPilotForPillar: (GddPillarType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        if (activeProject == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(color = CyberCyan)
            }
        } else {
            when (activeModule) {
                WorkspaceModule.DASHBOARD -> DashboardScreen(
                    project = activeProject,
                    onNavigateToModule = { viewModel.selectModule(it) },
                    onSelectGddPillar = { viewModel.selectGddPillar(it) },
                    onUpdateMetadata = { title, tagline, genre, engine, platforms, studio, audience ->
                        viewModel.updateProjectMetadata(title, tagline, genre, engine, platforms, studio, audience)
                    },
                    onUpsertMilestone = { viewModel.upsertMilestone(it) },
                    onDeleteMilestone = { viewModel.deleteMilestone(it) }
                )

                WorkspaceModule.GDD_BUILDER -> GddBuilderScreen(
                    project = activeProject,
                    selectedPillar = selectedGddPillar,
                    onSelectPillar = { viewModel.selectGddPillar(it) },
                    onUpdateSection = { pillar, statement, markdown, bullets, status ->
                        viewModel.updateGddSection(pillar, statement, markdown, bullets, status)
                    },
                    onCycleStatus = { viewModel.cycleGddSectionStatus(it) },
                    onOpenAiCoPilotForPillar = onOpenAiCoPilotForPillar
                )

                WorkspaceModule.WORLD_CODEX -> WorldCodexScreen(
                    project = activeProject,
                    selectedCategory = selectedCodexCategory,
                    onSelectCategory = { viewModel.selectCodexCategory(it) },
                    onUpsertEntry = { viewModel.upsertCodexEntry(it) },
                    onDeleteEntry = { viewModel.deleteCodexEntry(it) }
                )

                WorkspaceModule.QUEST_DESIGNER -> QuestDesignerScreen(
                    project = activeProject,
                    onUpsertQuest = { viewModel.upsertQuest(it) },
                    onToggleObjective = { qId, objId -> viewModel.toggleQuestObjective(qId, objId) },
                    onDeleteQuest = { viewModel.deleteQuest(it) }
                )

                WorkspaceModule.BACKLOG -> BacklogRoadmapScreen(
                    project = activeProject,
                    onUpsertFeature = { viewModel.upsertBacklogFeature(it) },
                    onCycleStatus = { viewModel.cycleFeatureStatus(it) },
                    onDeleteFeature = { viewModel.deleteBacklogFeature(it) }
                )

                WorkspaceModule.DIALOGUE_ITEMS -> DialogueItemsScreen(
                    project = activeProject,
                    onUpsertDialogueNode = { viewModel.upsertDialogueNode(it) },
                    onDeleteDialogueNode = { viewModel.deleteDialogueNode(it) },
                    onUpsertGameItem = { viewModel.upsertGameItem(it) },
                    onDeleteGameItem = { viewModel.deleteGameItem(it) }
                )
            }
        }
    }
}
