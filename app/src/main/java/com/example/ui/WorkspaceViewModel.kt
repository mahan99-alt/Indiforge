package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SeedProjects
import com.example.data.local.IndieForgeDatabase
import com.example.data.local.ProjectRepository
import com.example.model.*
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val TAG = "WorkspaceViewModel"

sealed interface AiCoPilotState {
    data object Idle : AiCoPilotState
    data object Loading : AiCoPilotState
    data class Streaming(val partialText: String) : AiCoPilotState
    data class Success(val generatedText: String, val sourceLabel: String) : AiCoPilotState
    data class Error(val message: String, val fallbackSampleText: String) : AiCoPilotState
}

class WorkspaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository = ProjectRepository(
        IndieForgeDatabase.getInstance(application).projectDao()
    )

    // Lazy initialization per firebase-ai-logic-android skill
    private val generativeModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    val projects: StateFlow<List<GameProject>> = repository.allProjectsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedProjectId = MutableStateFlow<String?>(null)
    val selectedProjectId: StateFlow<String?> = _selectedProjectId.asStateFlow()

    val activeProject: StateFlow<GameProject?> = combine(projects, _selectedProjectId) { list, selId ->
        if (list.isEmpty()) null
        else list.find { it.id == selId } ?: list.first()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _activeModule = MutableStateFlow(WorkspaceModule.DASHBOARD)
    val activeModule: StateFlow<WorkspaceModule> = _activeModule.asStateFlow()

    private val _selectedGddPillar = MutableStateFlow(GddPillarType.GAMEPLAY)
    val selectedGddPillar: StateFlow<GddPillarType> = _selectedGddPillar.asStateFlow()

    private val _selectedCodexCategory = MutableStateFlow<CodexCategory?>(null)
    val selectedCodexCategory: StateFlow<CodexCategory?> = _selectedCodexCategory.asStateFlow()

    private val _saveStatus = MutableStateFlow(SaveStatusState.SAVED)
    val saveStatus: StateFlow<SaveStatusState> = _saveStatus.asStateFlow()

    private val _lastSavedTimeFormatted = MutableStateFlow("Just now")
    val lastSavedTimeFormatted: StateFlow<String> = _lastSavedTimeFormatted.asStateFlow()

    private val _aiState = MutableStateFlow<AiCoPilotState>(AiCoPilotState.Idle)
    val aiState: StateFlow<AiCoPilotState> = _aiState.asStateFlow()

    private var autosaveJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeedProjectsLoaded()
            updateTimestampLabel(System.currentTimeMillis())
        }
    }

    fun selectProject(projectId: String) {
        _selectedProjectId.value = projectId
    }

    fun selectModule(module: WorkspaceModule) {
        _activeModule.value = module
    }

    fun selectGddPillar(pillar: GddPillarType) {
        _selectedGddPillar.value = pillar
    }

    fun selectCodexCategory(category: CodexCategory?) {
        _selectedCodexCategory.value = category
    }

    private fun triggerAutosave(updatedProject: GameProject) {
        _saveStatus.value = SaveStatusState.SAVING
        autosaveJob?.cancel()
        autosaveJob = viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val stamped = updatedProject.copy(lastUpdatedEpochMillis = timestamp)
            repository.saveProject(stamped)
            delay(280L)
            updateTimestampLabel(timestamp)
            _saveStatus.value = SaveStatusState.SAVED
        }
    }

    private fun updateTimestampLabel(epochMillis: Long) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
        _lastSavedTimeFormatted.value = sdf.format(Date(epochMillis))
    }

    // 1. Project Management
    fun createNewProject(
        title: String,
        tagline: String,
        genre: String,
        engine: String,
        platforms: String,
        studioName: String,
        useStarterTemplate: Boolean
    ) {
        viewModelScope.launch {
            val newProj = SeedProjects.createNewCustomProject(
                title = title,
                tagline = tagline,
                genre = genre,
                engine = engine,
                platforms = platforms,
                studioName = studioName,
                useStarterTemplate = useStarterTemplate
            )
            _saveStatus.value = SaveStatusState.SAVING
            repository.saveProject(newProj)
            _selectedProjectId.value = newProj.id
            updateTimestampLabel(newProj.lastUpdatedEpochMillis)
            _saveStatus.value = SaveStatusState.SAVED
        }
    }

    fun duplicateCurrentProject() {
        val current = activeProject.value ?: return
        viewModelScope.launch {
            val copyId = "proj_" + UUID.randomUUID().toString().take(8)
            val now = System.currentTimeMillis()
            val duplicated = current.copy(
                id = copyId,
                title = "${current.title} (Copy)",
                lastUpdatedEpochMillis = now
            )
            repository.saveProject(duplicated)
            _selectedProjectId.value = copyId
            updateTimestampLabel(now)
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            val currentList = projects.value
            if (currentList.size <= 1) {
                // Always keep at least one project; re-seed Aetheris if deleting the last one
                repository.deleteProject(projectId)
                repository.ensureSeedProjectsLoaded()
            } else {
                repository.deleteProject(projectId)
                if (_selectedProjectId.value == projectId) {
                    _selectedProjectId.value = currentList.firstOrNull { it.id != projectId }?.id
                }
            }
        }
    }

    fun updateProjectMetadata(
        title: String,
        tagline: String,
        genre: String,
        engine: String,
        platforms: String,
        studioName: String,
        targetAudience: String
    ) {
        val current = activeProject.value ?: return
        triggerAutosave(
            current.copy(
                title = title,
                tagline = tagline,
                genre = genre,
                engine = engine,
                targetPlatforms = platforms,
                studioName = studioName,
                targetAudience = targetAudience
            )
        )
    }

    // 2. GDD Builder Updates
    fun updateGddSection(
        pillar: GddPillarType,
        designPillarStatement: String,
        contentMarkdown: String,
        keyMechanicsBullets: List<String>,
        status: SectionStatus
    ) {
        val current = activeProject.value ?: return
        val updatedSections = current.gddSections.map { sec ->
            if (sec.pillar == pillar) {
                sec.copy(
                    designPillarStatement = designPillarStatement,
                    contentMarkdown = contentMarkdown,
                    keyMechanicsBullets = keyMechanicsBullets,
                    status = status
                )
            } else sec
        }
        triggerAutosave(current.copy(gddSections = updatedSections))
    }

    fun cycleGddSectionStatus(pillar: GddPillarType) {
        val current = activeProject.value ?: return
        val updatedSections = current.gddSections.map { sec ->
            if (sec.pillar == pillar) {
                val next = when (sec.status) {
                    SectionStatus.DRAFT -> SectionStatus.IN_REVIEW
                    SectionStatus.IN_REVIEW -> SectionStatus.LOCKED
                    SectionStatus.LOCKED -> SectionStatus.DRAFT
                }
                sec.copy(status = next)
            } else sec
        }
        triggerAutosave(current.copy(gddSections = updatedSections))
    }

    // 3. World Codex Updates (Character, Faction, Location, Enemy)
    fun upsertCodexEntry(entry: CodexEntry) {
        val current = activeProject.value ?: return
        val exists = current.codexEntries.any { it.id == entry.id }
        val updatedList = if (exists) {
            current.codexEntries.map { if (it.id == entry.id) entry else it }
        } else {
            listOf(entry) + current.codexEntries
        }
        triggerAutosave(current.copy(codexEntries = updatedList))
    }

    fun deleteCodexEntry(entryId: String) {
        val current = activeProject.value ?: return
        triggerAutosave(
            current.copy(codexEntries = current.codexEntries.filterNot { it.id == entryId })
        )
    }

    // 4. Quest & Mission Designer Updates
    fun upsertQuest(quest: QuestItem) {
        val current = activeProject.value ?: return
        val exists = current.quests.any { it.id == quest.id }
        val updatedList = if (exists) {
            current.quests.map { if (it.id == quest.id) quest else it }
        } else {
            listOf(quest) + current.quests
        }
        triggerAutosave(current.copy(quests = updatedList))
    }

    fun toggleQuestObjective(questId: String, objectiveId: String) {
        val current = activeProject.value ?: return
        val updatedQuests = current.quests.map { q ->
            if (q.id == questId) {
                q.copy(
                    objectives = q.objectives.map { obj ->
                        if (obj.id == objectiveId) {
                            obj.copy(isCompletedInPrototype = !obj.isCompletedInPrototype)
                        } else obj
                    }
                )
            } else q
        }
        triggerAutosave(current.copy(quests = updatedQuests))
    }

    fun deleteQuest(questId: String) {
        val current = activeProject.value ?: return
        triggerAutosave(current.copy(quests = current.quests.filterNot { it.id == questId }))
    }

    // 5. Feature Backlog Updates
    fun upsertBacklogFeature(feature: BacklogFeature) {
        val current = activeProject.value ?: return
        val exists = current.backlogFeatures.any { it.id == feature.id }
        val updatedList = if (exists) {
            current.backlogFeatures.map { if (it.id == feature.id) feature else it }
        } else {
            listOf(feature) + current.backlogFeatures
        }
        triggerAutosave(current.copy(backlogFeatures = updatedList))
    }

    fun cycleFeatureStatus(featureId: String) {
        val current = activeProject.value ?: return
        val updated = current.backlogFeatures.map { f ->
            if (f.id == featureId) {
                val next = when (f.status) {
                    FeatureStatus.BACKLOG -> FeatureStatus.IN_PROGRESS
                    FeatureStatus.IN_PROGRESS -> FeatureStatus.PLAYTESTING
                    FeatureStatus.PLAYTESTING -> FeatureStatus.DONE
                    FeatureStatus.DONE -> FeatureStatus.BACKLOG
                }
                f.copy(status = next)
            } else f
        }
        triggerAutosave(current.copy(backlogFeatures = updated))
    }

    fun deleteBacklogFeature(featureId: String) {
        val current = activeProject.value ?: return
        triggerAutosave(current.copy(backlogFeatures = current.backlogFeatures.filterNot { it.id == featureId }))
    }

    // 6. Milestone Roadmap Updates
    fun upsertMilestone(milestone: MilestoneItem) {
        val current = activeProject.value ?: return
        val exists = current.milestones.any { it.id == milestone.id }
        val updated = if (exists) {
            current.milestones.map { if (it.id == milestone.id) milestone else it }
        } else {
            current.milestones + milestone
        }
        triggerAutosave(current.copy(milestones = updated))
    }

    fun deleteMilestone(milestoneId: String) {
        val current = activeProject.value ?: return
        triggerAutosave(current.copy(milestones = current.milestones.filterNot { it.id == milestoneId }))
    }

    // 7. Dialogue & Item Updates
    fun upsertDialogueNode(node: DialogueNode) {
        val current = activeProject.value ?: return
        val exists = current.dialogueNodes.any { it.id == node.id }
        val updated = if (exists) {
            current.dialogueNodes.map { if (it.id == node.id) node else it }
        } else {
            listOf(node) + current.dialogueNodes
        }
        triggerAutosave(current.copy(dialogueNodes = updated))
    }

    fun deleteDialogueNode(nodeId: String) {
        val current = activeProject.value ?: return
        triggerAutosave(current.copy(dialogueNodes = current.dialogueNodes.filterNot { it.id == nodeId }))
    }

    fun upsertGameItem(item: GameItem) {
        val current = activeProject.value ?: return
        val exists = current.items.any { it.id == item.id }
        val updated = if (exists) {
            current.items.map { if (it.id == item.id) item else it }
        } else {
            listOf(item) + current.items
        }
        triggerAutosave(current.copy(items = updated))
    }

    fun deleteGameItem(itemId: String) {
        val current = activeProject.value ?: return
        triggerAutosave(current.copy(items = current.items.filterNot { it.id == itemId }))
    }

    // 8. Optional AI Integration Point + Editable Blueprint Templates Fallback
    fun generateWithGeminiOrFallback(
        targetContextLabel: String,
        userPrompt: String,
        fallbackSampleOutput: String
    ) {
        val project = activeProject.value
        val fullPrompt = buildString {
            appendLine("You are a veteran Indie Game Design Director working inside IndieForge.")
            if (project != null) {
                appendLine("Game Title: ${project.title}")
                appendLine("Genre: ${project.genre} | Engine: ${project.engine}")
                appendLine("Elevator Pitch: ${project.tagline}")
            }
            appendLine("Target Design Module: $targetContextLabel")
            appendLine("Task: $userPrompt")
            appendLine("Format your output in clean, concrete, production-ready Markdown with specific mechanics, numbers, and evocative lore. Keep it concise (150-250 words).")
        }

        viewModelScope.launch {
            _aiState.value = AiCoPilotState.Loading
            val accumulated = StringBuilder()
            try {
                withTimeout(60_000L) {
                    generativeModel.generateContentStream(fullPrompt).collect { chunk ->
                        val text = chunk.text.orEmpty()
                        accumulated.append(text)
                        _aiState.value = AiCoPilotState.Streaming(accumulated.toString())
                    }
                }
                val finalOutput = accumulated.toString().trim()
                if (finalOutput.isNotBlank()) {
                    _aiState.value = AiCoPilotState.Success(
                        generatedText = finalOutput,
                        sourceLabel = "Gemini 3.8 Flash (Live AI)"
                    )
                } else {
                    _aiState.value = AiCoPilotState.Error(
                        message = "Model returned an empty response. Loaded editable Studio Sample Template below.",
                        fallbackSampleText = fallbackSampleOutput
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini generation timed out after 60s", e)
                _aiState.value = AiCoPilotState.Error(
                    message = "Generation timed out after 60s. You can use or customize the editable Studio Blueprint below.",
                    fallbackSampleText = fallbackSampleOutput
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI generation failed", e)
                _aiState.value = AiCoPilotState.Error(
                    message = "Live AI unavailable (${e.localizedMessage ?: "Unconfigured or offline"}). Using editable Studio Blueprint Template instead.",
                    fallbackSampleText = fallbackSampleOutput
                )
            }
        }
    }

    fun loadEditableTemplateDirectly(templateText: String, templateTitle: String) {
        _aiState.value = AiCoPilotState.Success(
            generatedText = templateText,
            sourceLabel = "Studio Blueprint: $templateTitle"
        )
    }

    fun resetAiState() {
        _aiState.value = AiCoPilotState.Idle
    }
}
