package com.example.model

enum class WorkspaceModule(val label: String, val shortLabel: String) {
    DASHBOARD("Overview & Roadmap", "Overview"),
    GDD_BUILDER("GDD Builder", "GDD"),
    WORLD_CODEX("World Codex", "Codex"),
    QUEST_DESIGNER("Quest Designer", "Quests"),
    BACKLOG("Feature Backlog", "Backlog"),
    DIALOGUE_ITEMS("Dialogue & Items", "Narrative")
}

enum class GddPillarType(val id: String, val title: String, val subtitle: String) {
    GAMEPLAY("gameplay", "Gameplay & Core Loop", "Session rhythm, win/loss states, and minute-to-minute feel"),
    STORY("story", "Story & Narrative", "Plot arcs, central themes, protagonist motivation, and pacing"),
    WORLD("world", "World & Lore", "Cosmology, biome rules, environmental storytelling, and history"),
    CONTROLS("controls", "Controls & Input", "Controller/KB&M mapping, ergonomics, haptics, and accessibility"),
    MECHANICS("mechanics", "Systems & Mechanics", "Physics, progression, crafting, proc-gen, and combat formulas"),
    UI("ui", "UI & HUD Architecture", "Diegetic vs. non-diegetic HUD, menus, telemetry, and readability"),
    AUDIO("audio", "Audio & Soundscape", "Adaptive music stems, spatial SFX, leitmotifs, and voice direction"),
    MONETIZATION("monetization", "Monetization & Release", "Pricing model, DLC roadmap, platform storefronts, and community")
}

enum class SectionStatus(val label: String) {
    DRAFT("Draft"),
    IN_REVIEW("In Review"),
    LOCKED("Locked")
}

data class GddSection(
    val pillar: GddPillarType,
    val designPillarStatement: String,
    val contentMarkdown: String,
    val keyMechanicsBullets: List<String>,
    val status: SectionStatus = SectionStatus.IN_REVIEW
) {
    val wordCount: Int
        get() = (designPillarStatement + " " + contentMarkdown + " " + keyMechanicsBullets.joinToString(" "))
            .trim()
            .split(Regex("\\s+"))
            .count { it.isNotBlank() }

    val completionPercent: Int
        get() = when (status) {
            SectionStatus.LOCKED -> 100
            SectionStatus.IN_REVIEW -> (wordCount * 100 / 120).coerceIn(35, 92)
            SectionStatus.DRAFT -> (wordCount * 100 / 150).coerceIn(10, 65)
        }
}

enum class CodexCategory(val label: String, val singular: String) {
    CHARACTER("Characters", "Character"),
    FACTION("Factions", "Faction"),
    LOCATION("Locations", "Location"),
    ENEMY("Enemies", "Enemy")
}

data class CodexEntry(
    val id: String,
    val category: CodexCategory,
    val name: String,
    val roleOrType: String,
    val affiliationOrBiome: String,
    val threatOrTier: String,
    val summaryLore: String,
    val gameplayBehavior: String,
    val statsOrAttributes: List<String>,
    val tags: List<String>
)

enum class QuestType(val label: String) {
    MAIN_ARC("Main Story Arc"),
    FACTION_CONTRACT("Faction Contract"),
    EXPLORATION_ANOMALY("Anomaly Survey"),
    SIDE_MISSION("Side Mission")
}

data class QuestObjective(
    val id: String,
    val description: String,
    val isOptional: Boolean = false,
    val isCompletedInPrototype: Boolean = false
)

data class QuestItem(
    val id: String,
    val title: String,
    val questType: QuestType,
    val giverOrTrigger: String,
    val regionOrSector: String,
    val recommendedTier: String,
    val synopsis: String,
    val objectives: List<QuestObjective>,
    val branchingOutcomes: String,
    val rewards: List<String>
)

enum class FeaturePriority(val label: String, val weight: Int) {
    CRITICAL("Critical", 4),
    HIGH("High", 3),
    MEDIUM("Medium", 2),
    LOW("Low", 1)
}

enum class FeatureStatus(val label: String) {
    BACKLOG("Backlog"),
    IN_PROGRESS("In Progress"),
    PLAYTESTING("Playtesting"),
    DONE("Done")
}

data class BacklogFeature(
    val id: String,
    val title: String,
    val categoryTag: String,
    val priority: FeaturePriority,
    val status: FeatureStatus,
    val milestoneTag: String,
    val effortPoints: Int,
    val acceptanceCriteria: String
)

enum class MilestoneStatus(val label: String) {
    COMPLETED("Completed"),
    ACTIVE("Active Sprint"),
    UPCOMING("Upcoming")
}

data class MilestoneItem(
    val id: String,
    val title: String,
    val targetDate: String,
    val status: MilestoneStatus,
    val summaryGoal: String,
    val deliverables: List<String>,
    val progressPercent: Int
)

data class DialogueChoice(
    val id: String,
    val playerPrompt: String,
    val conditionOrSkillCheck: String,
    val nextNodeCode: String,
    val outcomeEffect: String
)

data class DialogueNode(
    val id: String,
    val nodeCode: String,
    val speakerName: String,
    val speakerEmotion: String,
    val sceneContext: String,
    val spokenLine: String,
    val voiceDirectionNotes: String,
    val choices: List<DialogueChoice>
)

enum class ItemRarity(val label: String) {
    COMMON("Common"),
    UNCOMMON("Uncommon"),
    RARE("Rare"),
    EXOTIC("Exotic"),
    MYTHIC("Mythic")
}

data class GameItem(
    val id: String,
    val codeId: String,
    val name: String,
    val category: String,
    val rarity: ItemRarity,
    val valueCredits: Int,
    val statModifiers: List<String>,
    val uiTooltipDescription: String,
    val flavorLore: String
)

data class GameProject(
    val id: String,
    val title: String,
    val tagline: String,
    val genre: String,
    val engine: String,
    val targetPlatforms: String,
    val studioName: String,
    val targetAudience: String,
    val lastUpdatedEpochMillis: Long,
    val gddSections: List<GddSection>,
    val codexEntries: List<CodexEntry>,
    val quests: List<QuestItem>,
    val backlogFeatures: List<BacklogFeature>,
    val milestones: List<MilestoneItem>,
    val dialogueNodes: List<DialogueNode>,
    val items: List<GameItem>
) {
    val overallGddReadiness: Int
        get() = if (gddSections.isEmpty()) 0 else gddSections.sumOf { it.completionPercent } / gddSections.size

    val totalWordCount: Int
        get() = gddSections.sumOf { it.wordCount }

    val completedBacklogCount: Int
        get() = backlogFeatures.count { it.status == FeatureStatus.DONE }

    val backlogVelocityPercent: Int
        get() = if (backlogFeatures.isEmpty()) 0 else (completedBacklogCount * 100) / backlogFeatures.size
}

enum class SaveStatusState {
    SAVED,
    SAVING,
    MODIFIED
}

data class BlueprintTemplate(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val pillarStatement: String,
    val contentBody: String,
    val bullets: List<String>
)
