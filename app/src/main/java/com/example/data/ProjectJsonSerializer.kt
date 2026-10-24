package com.example.data

import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ProjectJsonSerializer {

    fun toJsonString(project: GameProject, indentSpaces: Int = 0): String {
        val root = JSONObject()
        root.put("id", project.id)
        root.put("title", project.title)
        root.put("tagline", project.tagline)
        root.put("genre", project.genre)
        root.put("engine", project.engine)
        root.put("targetPlatforms", project.targetPlatforms)
        root.put("studioName", project.studioName)
        root.put("targetAudience", project.targetAudience)
        root.put("lastUpdatedEpochMillis", project.lastUpdatedEpochMillis)

        // GDD Sections
        val gddArr = JSONArray()
        project.gddSections.forEach { sec ->
            val obj = JSONObject()
            obj.put("pillar", sec.pillar.name)
            obj.put("designPillarStatement", sec.designPillarStatement)
            obj.put("contentMarkdown", sec.contentMarkdown)
            obj.put("keyMechanicsBullets", JSONArray(sec.keyMechanicsBullets))
            obj.put("status", sec.status.name)
            gddArr.put(obj)
        }
        root.put("gddSections", gddArr)

        // Codex Entries
        val codexArr = JSONArray()
        project.codexEntries.forEach { entry ->
            val obj = JSONObject()
            obj.put("id", entry.id)
            obj.put("category", entry.category.name)
            obj.put("name", entry.name)
            obj.put("roleOrType", entry.roleOrType)
            obj.put("affiliationOrBiome", entry.affiliationOrBiome)
            obj.put("threatOrTier", entry.threatOrTier)
            obj.put("summaryLore", entry.summaryLore)
            obj.put("gameplayBehavior", entry.gameplayBehavior)
            obj.put("statsOrAttributes", JSONArray(entry.statsOrAttributes))
            obj.put("tags", JSONArray(entry.tags))
            codexArr.put(obj)
        }
        root.put("codexEntries", codexArr)

        // Quests
        val questArr = JSONArray()
        project.quests.forEach { q ->
            val obj = JSONObject()
            obj.put("id", q.id)
            obj.put("title", q.title)
            obj.put("questType", q.questType.name)
            obj.put("giverOrTrigger", q.giverOrTrigger)
            obj.put("regionOrSector", q.regionOrSector)
            obj.put("recommendedTier", q.recommendedTier)
            obj.put("synopsis", q.synopsis)
            val objArr = JSONArray()
            q.objectives.forEach { o ->
                val oJson = JSONObject()
                oJson.put("id", o.id)
                oJson.put("description", o.description)
                oJson.put("isOptional", o.isOptional)
                oJson.put("isCompletedInPrototype", o.isCompletedInPrototype)
                objArr.put(oJson)
            }
            obj.put("objectives", objArr)
            obj.put("branchingOutcomes", q.branchingOutcomes)
            obj.put("rewards", JSONArray(q.rewards))
            questArr.put(obj)
        }
        root.put("quests", questArr)

        // Backlog Features
        val backlogArr = JSONArray()
        project.backlogFeatures.forEach { b ->
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("title", b.title)
            obj.put("categoryTag", b.categoryTag)
            obj.put("priority", b.priority.name)
            obj.put("status", b.status.name)
            obj.put("milestoneTag", b.milestoneTag)
            obj.put("effortPoints", b.effortPoints)
            obj.put("acceptanceCriteria", b.acceptanceCriteria)
            backlogArr.put(obj)
        }
        root.put("backlogFeatures", backlogArr)

        // Milestones
        val milestoneArr = JSONArray()
        project.milestones.forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("title", m.title)
            obj.put("targetDate", m.targetDate)
            obj.put("status", m.status.name)
            obj.put("summaryGoal", m.summaryGoal)
            obj.put("deliverables", JSONArray(m.deliverables))
            obj.put("progressPercent", m.progressPercent)
            milestoneArr.put(obj)
        }
        root.put("milestones", milestoneArr)

        // Dialogue Nodes
        val dialogueArr = JSONArray()
        project.dialogueNodes.forEach { d ->
            val obj = JSONObject()
            obj.put("id", d.id)
            obj.put("nodeCode", d.nodeCode)
            obj.put("speakerName", d.speakerName)
            obj.put("speakerEmotion", d.speakerEmotion)
            obj.put("sceneContext", d.sceneContext)
            obj.put("spokenLine", d.spokenLine)
            obj.put("voiceDirectionNotes", d.voiceDirectionNotes)
            val choiceArr = JSONArray()
            d.choices.forEach { c ->
                val cJson = JSONObject()
                cJson.put("id", c.id)
                cJson.put("playerPrompt", c.playerPrompt)
                cJson.put("conditionOrSkillCheck", c.conditionOrSkillCheck)
                cJson.put("nextNodeCode", c.nextNodeCode)
                cJson.put("outcomeEffect", c.outcomeEffect)
                choiceArr.put(cJson)
            }
            obj.put("choices", choiceArr)
            dialogueArr.put(obj)
        }
        root.put("dialogueNodes", dialogueArr)

        // Items
        val itemArr = JSONArray()
        project.items.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("codeId", item.codeId)
            obj.put("name", item.name)
            obj.put("category", item.category)
            obj.put("rarity", item.rarity.name)
            obj.put("valueCredits", item.valueCredits)
            obj.put("statModifiers", JSONArray(item.statModifiers))
            obj.put("uiTooltipDescription", item.uiTooltipDescription)
            obj.put("flavorLore", item.flavorLore)
            itemArr.put(obj)
        }
        root.put("items", itemArr)

        return if (indentSpaces > 0) root.toString(indentSpaces) else root.toString()
    }

    fun fromJsonString(jsonStr: String): GameProject {
        val root = JSONObject(jsonStr)
        val id = root.optString("id", "proj_default")
        val title = root.optString("title", "Untitled Game")
        val tagline = root.optString("tagline", "")
        val genre = root.optString("genre", "Action Adventure")
        val engine = root.optString("engine", "Godot 4.3")
        val targetPlatforms = root.optString("targetPlatforms", "PC / Steam Deck")
        val studioName = root.optString("studioName", "Indie Studio")
        val targetAudience = root.optString("targetAudience", "Core Indie Gamers")
        val lastUpdated = root.optLong("lastUpdatedEpochMillis", System.currentTimeMillis())

        val gddList = mutableListOf<GddSection>()
        val gddArr = root.optJSONArray("gddSections") ?: JSONArray()
        for (i in 0 until gddArr.length()) {
            val obj = gddArr.getJSONObject(i)
            val pillarName = obj.optString("pillar", GddPillarType.GAMEPLAY.name)
            val pillar = runCatching { GddPillarType.valueOf(pillarName) }.getOrDefault(GddPillarType.GAMEPLAY)
            val statusName = obj.optString("status", SectionStatus.IN_REVIEW.name)
            val status = runCatching { SectionStatus.valueOf(statusName) }.getOrDefault(SectionStatus.IN_REVIEW)
            gddList.add(
                GddSection(
                    pillar = pillar,
                    designPillarStatement = obj.optString("designPillarStatement", ""),
                    contentMarkdown = obj.optString("contentMarkdown", ""),
                    keyMechanicsBullets = jsonArrayToStringList(obj.optJSONArray("keyMechanicsBullets")),
                    status = status
                )
            )
        }

        val codexList = mutableListOf<CodexEntry>()
        val codexArr = root.optJSONArray("codexEntries") ?: JSONArray()
        for (i in 0 until codexArr.length()) {
            val obj = codexArr.getJSONObject(i)
            val catName = obj.optString("category", CodexCategory.CHARACTER.name)
            val category = runCatching { CodexCategory.valueOf(catName) }.getOrDefault(CodexCategory.CHARACTER)
            codexList.add(
                CodexEntry(
                    id = obj.optString("id", "codex_$i"),
                    category = category,
                    name = obj.optString("name", ""),
                    roleOrType = obj.optString("roleOrType", ""),
                    affiliationOrBiome = obj.optString("affiliationOrBiome", ""),
                    threatOrTier = obj.optString("threatOrTier", ""),
                    summaryLore = obj.optString("summaryLore", ""),
                    gameplayBehavior = obj.optString("gameplayBehavior", ""),
                    statsOrAttributes = jsonArrayToStringList(obj.optJSONArray("statsOrAttributes")),
                    tags = jsonArrayToStringList(obj.optJSONArray("tags"))
                )
            )
        }

        val questList = mutableListOf<QuestItem>()
        val questArr = root.optJSONArray("quests") ?: JSONArray()
        for (i in 0 until questArr.length()) {
            val obj = questArr.getJSONObject(i)
            val qType = runCatching { QuestType.valueOf(obj.optString("questType")) }.getOrDefault(QuestType.MAIN_ARC)
            val objArr = obj.optJSONArray("objectives") ?: JSONArray()
            val objectives = mutableListOf<QuestObjective>()
            for (j in 0 until objArr.length()) {
                val oJson = objArr.getJSONObject(j)
                objectives.add(
                    QuestObjective(
                        id = oJson.optString("id", "obj_${i}_$j"),
                        description = oJson.optString("description", ""),
                        isOptional = oJson.optBoolean("isOptional", false),
                        isCompletedInPrototype = oJson.optBoolean("isCompletedInPrototype", false)
                    )
                )
            }
            questList.add(
                QuestItem(
                    id = obj.optString("id", "quest_$i"),
                    title = obj.optString("title", ""),
                    questType = qType,
                    giverOrTrigger = obj.optString("giverOrTrigger", ""),
                    regionOrSector = obj.optString("regionOrSector", ""),
                    recommendedTier = obj.optString("recommendedTier", "Tier 1"),
                    synopsis = obj.optString("synopsis", ""),
                    objectives = objectives,
                    branchingOutcomes = obj.optString("branchingOutcomes", ""),
                    rewards = jsonArrayToStringList(obj.optJSONArray("rewards"))
                )
            )
        }

        val backlogList = mutableListOf<BacklogFeature>()
        val backlogArr = root.optJSONArray("backlogFeatures") ?: JSONArray()
        for (i in 0 until backlogArr.length()) {
            val obj = backlogArr.getJSONObject(i)
            val priority = runCatching { FeaturePriority.valueOf(obj.optString("priority")) }.getOrDefault(FeaturePriority.MEDIUM)
            val status = runCatching { FeatureStatus.valueOf(obj.optString("status")) }.getOrDefault(FeatureStatus.BACKLOG)
            backlogList.add(
                BacklogFeature(
                    id = obj.optString("id", "feat_$i"),
                    title = obj.optString("title", ""),
                    categoryTag = obj.optString("categoryTag", "Core"),
                    priority = priority,
                    status = status,
                    milestoneTag = obj.optString("milestoneTag", "Vertical Slice"),
                    effortPoints = obj.optInt("effortPoints", 3),
                    acceptanceCriteria = obj.optString("acceptanceCriteria", "")
                )
            )
        }

        val milestoneList = mutableListOf<MilestoneItem>()
        val milestoneArr = root.optJSONArray("milestones") ?: JSONArray()
        for (i in 0 until milestoneArr.length()) {
            val obj = milestoneArr.getJSONObject(i)
            val status = runCatching { MilestoneStatus.valueOf(obj.optString("status")) }.getOrDefault(MilestoneStatus.UPCOMING)
            milestoneList.add(
                MilestoneItem(
                    id = obj.optString("id", "ms_$i"),
                    title = obj.optString("title", ""),
                    targetDate = obj.optString("targetDate", ""),
                    status = status,
                    summaryGoal = obj.optString("summaryGoal", ""),
                    deliverables = jsonArrayToStringList(obj.optJSONArray("deliverables")),
                    progressPercent = obj.optInt("progressPercent", 0)
                )
            )
        }

        val dialogueList = mutableListOf<DialogueNode>()
        val dialogueArr = root.optJSONArray("dialogueNodes") ?: JSONArray()
        for (i in 0 until dialogueArr.length()) {
            val obj = dialogueArr.getJSONObject(i)
            val choiceArr = obj.optJSONArray("choices") ?: JSONArray()
            val choices = mutableListOf<DialogueChoice>()
            for (j in 0 until choiceArr.length()) {
                val cObj = choiceArr.getJSONObject(j)
                choices.add(
                    DialogueChoice(
                        id = cObj.optString("id", "choice_${i}_$j"),
                        playerPrompt = cObj.optString("playerPrompt", ""),
                        conditionOrSkillCheck = cObj.optString("conditionOrSkillCheck", ""),
                        nextNodeCode = cObj.optString("nextNodeCode", ""),
                        outcomeEffect = cObj.optString("outcomeEffect", "")
                    )
                )
            }
            dialogueList.add(
                DialogueNode(
                    id = obj.optString("id", "dlg_$i"),
                    nodeCode = obj.optString("nodeCode", "DLG_0$i"),
                    speakerName = obj.optString("speakerName", ""),
                    speakerEmotion = obj.optString("speakerEmotion", "Neutral"),
                    sceneContext = obj.optString("sceneContext", ""),
                    spokenLine = obj.optString("spokenLine", ""),
                    voiceDirectionNotes = obj.optString("voiceDirectionNotes", ""),
                    choices = choices
                )
            )
        }

        val itemList = mutableListOf<GameItem>()
        val itemArr = root.optJSONArray("items") ?: JSONArray()
        for (i in 0 until itemArr.length()) {
            val obj = itemArr.getJSONObject(i)
            val rarity = runCatching { ItemRarity.valueOf(obj.optString("rarity")) }.getOrDefault(ItemRarity.COMMON)
            itemList.add(
                GameItem(
                    id = obj.optString("id", "item_$i"),
                    codeId = obj.optString("codeId", "ITM_0$i"),
                    name = obj.optString("name", ""),
                    category = obj.optString("category", "Equipment"),
                    rarity = rarity,
                    valueCredits = obj.optInt("valueCredits", 100),
                    statModifiers = jsonArrayToStringList(obj.optJSONArray("statModifiers")),
                    uiTooltipDescription = obj.optString("uiTooltipDescription", ""),
                    flavorLore = obj.optString("flavorLore", "")
                )
            )
        }

        return GameProject(
            id = id,
            title = title,
            tagline = tagline,
            genre = genre,
            engine = engine,
            targetPlatforms = targetPlatforms,
            studioName = studioName,
            targetAudience = targetAudience,
            lastUpdatedEpochMillis = lastUpdated,
            gddSections = gddList,
            codexEntries = codexList,
            quests = questList,
            backlogFeatures = backlogList,
            milestones = milestoneList,
            dialogueNodes = dialogueList,
            items = itemList
        )
    }

    fun toMarkdownDocument(project: GameProject): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val formattedDate = dateFormat.format(Date(project.lastUpdatedEpochMillis))
        return buildString {
            appendLine("# ${project.title} — Game Design Document (GDD)")
            appendLine("> **${project.tagline}**")
            appendLine()
            appendLine("## 1. Project Overview & Metadata")
            appendLine("- **Studio:** ${project.studioName}")
            appendLine("- **Genre:** ${project.genre}")
            appendLine("- **Engine & Tech Stack:** ${project.engine}")
            appendLine("- **Target Platforms:** ${project.targetPlatforms}")
            appendLine("- **Target Audience:** ${project.targetAudience}")
            appendLine("- **GDD Readiness:** ${project.overallGddReadiness}% (${project.totalWordCount} words)")
            appendLine("- **Last Compiled:** $formattedDate")
            appendLine()
            appendLine("---")
            appendLine()
            appendLine("## 2. Core Game Design Pillars & Architecture")
            project.gddSections.forEach { sec ->
                appendLine("### 2.${sec.pillar.ordinal + 1} ${sec.pillar.title} `[${sec.status.label}]`")
                appendLine("**Design Pillar:** *\"${sec.designPillarStatement}\"*")
                appendLine()
                appendLine(sec.contentMarkdown)
                appendLine()
                if (sec.keyMechanicsBullets.isNotEmpty()) {
                    appendLine("**Key Specifications & Rules:**")
                    sec.keyMechanicsBullets.forEach { b ->
                        appendLine("- $b")
                    }
                    appendLine()
                }
            }
            appendLine("---")
            appendLine()
            appendLine("## 3. World Codex (Characters, Factions, Locations & Enemies)")
            CodexCategory.entries.forEach { cat ->
                val entries = project.codexEntries.filter { it.category == cat }
                if (entries.isNotEmpty()) {
                    appendLine("### ${cat.label}")
                    entries.forEach { e ->
                        appendLine("#### ${e.name} (${e.roleOrType})")
                        appendLine("- **Affiliation / Biome:** ${e.affiliationOrBiome} | **Tier / Threat:** ${e.threatOrTier}")
                        appendLine("- **Lore Summary:** ${e.summaryLore}")
                        appendLine("- **Gameplay / Mechanics:** ${e.gameplayBehavior}")
                        if (e.statsOrAttributes.isNotEmpty()) {
                            appendLine("- **Attributes:** `${e.statsOrAttributes.joinToString("`, `")}`")
                        }
                        appendLine()
                    }
                }
            }
            appendLine("---")
            appendLine()
            appendLine("## 4. Quests & Mission Design")
            project.quests.forEach { q ->
                appendLine("### [${q.questType.label}] ${q.title}")
                appendLine("- **Trigger / Giver:** ${q.giverOrTrigger} | **Sector:** ${q.regionOrSector} | **Tier:** ${q.recommendedTier}")
                appendLine("- **Synopsis:** ${q.synopsis}")
                appendLine("- **Objectives:**")
                q.objectives.forEach { o ->
                    val check = if (o.isCompletedInPrototype) "[x]" else "[ ]"
                    val opt = if (o.isOptional) " *(Optional)*" else ""
                    appendLine("  - $check ${o.description}$opt")
                }
                appendLine("- **Branching & Consequences:** ${q.branchingOutcomes}")
                appendLine("- **Rewards:** ${q.rewards.joinToString(", ")}")
                appendLine()
            }
            appendLine("---")
            appendLine()
            appendLine("## 5. Milestone Roadmap & Feature Backlog")
            appendLine("### Milestone Schedule")
            project.milestones.forEach { m ->
                appendLine("- **${m.title}** (${m.targetDate}) — `${m.status.label}` (${m.progressPercent}%): ${m.summaryGoal}")
            }
            appendLine()
            appendLine("### Feature Backlog (${project.completedBacklogCount}/${project.backlogFeatures.size} Done)")
            project.backlogFeatures.forEach { f ->
                appendLine("- `[${f.status.label}]` **[${f.priority.label}] ${f.title}** (${f.categoryTag}, ${f.effortPoints} pts, ${f.milestoneTag}) — ${f.acceptanceCriteria}")
            }
            appendLine()
            appendLine("---")
            appendLine()
            appendLine("## 6. Narrative Dialogue & Item Database")
            appendLine("### Sample Dialogue Nodes")
            project.dialogueNodes.forEach { d ->
                appendLine("#### `${d.nodeCode}` — ${d.speakerName} (${d.speakerEmotion})")
                appendLine("*Context: ${d.sceneContext}*")
                appendLine("> \"${d.spokenLine}\"")
                d.choices.forEach { c ->
                    appendLine("- **Player:** \"${c.playerPrompt}\" `[${c.conditionOrSkillCheck}]` → `${c.nextNodeCode}` (${c.outcomeEffect})")
                }
                appendLine()
            }
            appendLine("### Item & Equipment Specifications")
            project.items.forEach { item ->
                appendLine("- **`${item.codeId}` ${item.name}** `[${item.rarity.label} ${item.category}]` (${item.valueCredits} Cr): ${item.uiTooltipDescription} | Stats: `${item.statModifiers.joinToString(", ")}`")
            }
        }
    }

    private fun jsonArrayToStringList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            list.add(arr.optString(i))
        }
        return list
    }
}
