package com.example.data

import com.example.model.*
import java.util.UUID

object SeedProjects {

    fun createAetherisSpaceProject(): GameProject {
        val now = System.currentTimeMillis()
        return GameProject(
            id = "proj_aetheris_void",
            title = "Aetheris: Void Cartographer",
            tagline = "Chart unstable quantum star-lanes, salvage precursor relics, and survive sentient cosmic storms in a procedural deep-space sandbox.",
            genre = "Procedural Space Exploration / Survival Sim",
            engine = "Godot 4.3 (Forward+ / GDScript + GDExtension)",
            targetPlatforms = "PC (Steam / Deck Verified), Linux, macOS",
            studioName = "Nebula Anvil Interactive",
            targetAudience = "Fans of Outer Wilds, FTL, No Man's Sky, and Subnautica",
            lastUpdatedEpochMillis = now,
            gddSections = listOf(
                GddSection(
                    pillar = GddPillarType.GAMEPLAY,
                    designPillarStatement = "Knowledge and sensor triangulation are your primary weapons—every star system is a tactile astrophysical puzzle.",
                    contentMarkdown = """
                        ### Core Gameplay Loop
                        1. **Probe & Triangulate (3–5 mins):** Enter an uncharted sector via fold-jump. Deploy long-range acoustic and gravimetric probes to resolve hidden orbital signatures.
                        2. **Navigate & Eva-Salvage (10–15 mins):** Pilot the *S.S. Astrolabe* through ionized dust rings, solar flares, and derelict megastructures. Dock or perform zero-G tether walks to extract Precursor Blackboxes and Exotic Isotopes.
                        3. **Synthesize & Upgrade (5 mins):** Return to your ship's Cartography Deck. Decode star-charts, craft hull shielding or resonator modules, and sell telemetry data to rival guild beacons.
                        
                        ### Session Pacing & Failure States
                        Hull breaches do not cause instant game-over; instead, they vent atmosphere from specific ship compartments, forcing emergency bulkhead sealing and field repairs before executing an emergency jump.
                    """.trimIndent(),
                    keyMechanicsBullets = listOf(
                        "3-stage sensor sweep: Passive Thermal -> Active Lidar Ping -> Quantum Resonance Lock",
                        "Zero-G EVA tether physics with Newtonian momentum and oxygen/thruster fuel budgeting",
                        "Dynamic sector instability timer driven by pulsar flare cycles",
                        "Modular ship interior grid where damaged relays physically spark and require manual patching"
                    ),
                    status = SectionStatus.LOCKED
                ),
                GddSection(
                    pillar = GddPillarType.STORY,
                    designPillarStatement = "An archaeological mystery told through blackbox telemetry, ancient star-buoys, and competing survivor philosophies.",
                    contentMarkdown = """
                        ### Premise & Setting
                        Three centuries after the **Great Fold Collapse**, humanity's outer colonies were severed into isolated pocket nebulae known as the **Shattered Reach**. As a licensed **Void Cartographer** aboard a custom-rigged survey corvette, you discover a harmonic signal originating from the *Cradle Singularity*—the exact coordinates where the original hyper-gates vanished.
                        
                        ### Narrative Structure
                        - **Act I — The Drift:** Reconnect the three estranged outer habitats (Helios Spire, Iron Drift, and Chorus Reef) by charting stable hyper-corridors.
                        - **Act II — Echoes of the Architects:** Uncover why the Precursor Lattice shut down—not from war, but to quarantine a self-replicating gravitational anomaly called the **Null Tide**.
                        - **Act III — The Final Chart:** Choose whether to reignite the hyper-gate network (uniting humanity but waking the Null Tide) or collapse the Singularity forever.
                    """.trimIndent(),
                    keyMechanicsBullets = listOf(
                        "Non-linear clue graph stored on the ship's holographic Rumor Astrolabe",
                        "3 Faction reputation tracks that unlock exclusive Act III warp coordinates",
                        "Diegetic audio logs recovered from drifted escape pods with spectrogram hidden codes"
                    ),
                    status = SectionStatus.LOCKED
                ),
                GddSection(
                    pillar = GddPillarType.WORLD,
                    designPillarStatement = "Deterministic procedural generation seeded by real astrophysical phenomena and hand-authored point-of-interest prefabs.",
                    contentMarkdown = """
                        ### Procedural Sector Architecture
                        Each sector is generated from a 64-bit cosmic seed combining **Stellar Class** (White Dwarf, Magnetar, Binary Proto-Star, Rogue Brown Dwarf) with **Biome Modifiers**:
                        - **Shattered Silicate Rings:** Dense high-velocity asteroid fields rich in Titanium-48 and cryovolcanic geysers.
                        - **Ionized Kelp Nebulae:** Bioluminescent plasma clouds that blind radar but recharge capacitor banks passively.
                        - **Graveyard Orbits:** Lagrange points choked with pre-collapse dreadnought hulls and automated defense mines.
                        - **Glassed Exoplanets:** Tidally locked worlds with scorching day-sides and frozen dark-side vaults.
                    """.trimIndent(),
                    keyMechanicsBullets = listOf(
                        "Seed-based 3D Voronoi nebula density fields rendered with raymarched volumetric shaders",
                        "Keplerian orbital mechanics simplified for readable tactical navigation",
                        "Handcrafted 'Anchor POIs' injected into procedural sectors to guarantee narrative progression"
                    ),
                    status = SectionStatus.IN_REVIEW
                ),
                GddSection(
                    pillar = GddPillarType.CONTROLS,
                    designPillarStatement = "Tactile, analog flight instrument control—every toggle switch, thruster burst, and scanner dial feels grounded.",
                    contentMarkdown = """
                        ### Dual-Mode Control Scheme
                        - **Helm Flight Mode (6-DOF Assisted):**
                          - `Left Stick / WASD`: Translational thrusters (Surge, Sway, Heave)
                          - `Right Stick / Mouse`: Pitch & Yaw orientation; `Q / E` or Bumpers for Roll
                          - `Left Trigger / Shift`: Newtonian Flight Assist Toggle (Coupled vs. Decoupled drift)
                          - `Right Trigger / LMB`: Primary Mining Laser / Pulse Disruptor
                        - **Cartography & Systems Deck:**
                          - `Tab / Select`: Instant camera transition to the holographic Navigation Table
                          - `D-Pad / 1–4`: Quick power-routing presets (Engines, Shields, Sensors, Stealth)
                    """.trimIndent(),
                    keyMechanicsBullets = listOf(
                        "Full Steam Deck & DualSense controller support with adaptive trigger resistance on hull stress",
                        "One-button 'Match Target Velocity' assist to eliminate frustrating zero-G docking",
                        "Fully remappable bindings + high-contrast telemetry colorblind presets"
                    ),
                    status = SectionStatus.LOCKED
                ),
                GddSection(
                    pillar = GddPillarType.MECHANICS,
                    designPillarStatement = "Interlocking systemic simulation: power heat management, acoustic stealth, and procedural relic synthesis.",
                    contentMarkdown = """
                        ### Core Systemic Loops
                        1. **Reactor Thermal & EM Signature:** Every active module (Shields, High-Gain Radar, Warp Spool) emits Electromagnetic (EM) and Thermal signatures. Running hot attracts **Void Leviathans** and **Corsair Drones**; shutting down life support and coasting ballistic makes you invisible.
                        2. **Spectrographic Salvage:** Derelict hulls are cut apart along structural weld seams using a frequency-tuned plasma cutter. Hitting pressurized fuel lines causes chain detonations.
                        3. **Relic Grafting:** Precursor artifacts have randomized prefix/suffix modifiers that can be slotted into the ship's core bus, trading stability for rule-breaking abilities (e.g., *Phase-Blink Thrusters*).
                    """.trimIndent(),
                    keyMechanicsBullets = listOf(
                        "EM Signature Formula: BaseReactorEmit + (ActiveModules * 1.4) -NebulaOcclusionFactor",
                        "Hull Integrity divided into 4 directional quadrants + Internal Pressurization %",
                        "32 craftable ship schematics across Propulsion, Telemetry, Defense, and Exo-Rig categories"
                    ),
                    status = SectionStatus.IN_REVIEW
                ),
                GddSection(
                    pillar = GddPillarType.UI,
                    designPillarStatement = "Diegetic cockpit instrumentation first—minimal screen-edge clutter, crisp amber/cyan vector telemetry.",
                    contentMarkdown = """
                        ### HUD & Interface Philosophy
                        - **Cockpit Canopy Projection:** Velocity vectors, prograde/retrograde markers, and spectral waveforms are projected directly onto the helmet visor and canopy glass.
                        - **Modular MFD Consoles:** Left panel displays Reactor Heat & Power Allocation; Right panel displays Sector Radar & Probe Spectrogram.
                        - **Cartographer's Codex Drawer:** Clean slate-dark tabbed overlay (`#0E1118`) using monospace telemetry readouts for coordinates, mass, and threat indices.
                    """.trimIndent(),
                    keyMechanicsBullets = listOf(
                        "Zero-pause quick radial wheel for probe deployment and shield frequency tuning",
                        "Scalable UI text size (85% to 140%) for handheld Steam Deck 800p legibility",
                        "Diegetic warning klaxons paired with directional visual threat arcs"
                    ),
                    status = SectionStatus.IN_REVIEW
                ),
                GddSection(
                    pillar = GddPillarType.AUDIO,
                    designPillarStatement = "Vacuum silence contrasted against resonant hull vibrations, analog tape synths, and radio-telescope sonification.",
                    contentMarkdown = """
                        ### Sound Design & Adaptive Score
                        - **Vacuum vs. Hull Conduction:** Outside the ship, explosions are muffled low-pass thuds transmitted only through your tether or suit boots. Inside the hull, creaking bulkheads, cooling pumps, and geiger clicks create intimacy.
                        - **Generative Synth Score:** Composed in 4 vertical stems (*Ambient Pad, Arpeggiator Pulse, Tension Sub-Bass, Anomaly Choir*) driven by distance to stellar hazards and oxygen reserves.
                    """.trimIndent(),
                    keyMechanicsBullets = listOf(
                        "Real pulsar and magnetar radio conversions layered into scanner ping feedback",
                        "Dynamic low-pass filter when cabin atmosphere depressurizes below 30 kPa",
                        "Distinct musical leitmotifs for each of the 3 major factions"
                    ),
                    status = SectionStatus.DRAFT
                ),
                GddSection(
                    pillar = GddPillarType.MONETIZATION,
                    designPillarStatement = "Ethical single-purchase indie premium release—no microtransactions, no battle passes, built for long-tail community modding.",
                    contentMarkdown = """
                        ### Release & Commercial Strategy
                        - **Base Price:** $24.99 USD on Steam, GOG, and Humble Store (15% launch week discount).
                        - **Demo / Steam Next Fest:** Free standalone "Sector Zero" prologue featuring 2 star classes, 15 ship modules, and full seed sharing.
                        - **Post-Launch Expansion:** One major paid expansion (*"Echoes of the Deep Rim"*, $12.99) adding carrier-class vessels and binary black hole biomes 12 months post-launch, alongside free Workshop mod tools.
                    """.trimIndent(),
                    keyMechanicsBullets = listOf(
                        "Zero microtransactions or pay-to-skip timers",
                        "Built-in JSON/Markdown mod loader for community-authored quests and star sectors",
                        "Original Soundtrack + Digital Artbook & GDD Bundle ($7.99)"
                    ),
                    status = SectionStatus.LOCKED
                )
            ),
            codexEntries = listOf(
                CodexEntry(
                    id = "codex_char_1",
                    category = CodexCategory.CHARACTER,
                    name = "Kaelen Vance",
                    roleOrType = "Chief Void Cartographer (Player / Protagonist)",
                    affiliationOrBiome = "Independent Survey Corps",
                    threatOrTier = "Rank IV Navigator",
                    summaryLore = "Former astrophysicist on Helios Spire who salvaged an experimental Precursor astrolabe core after the orbital ring disaster. Obsessed with mapping the unchartable Null Zone.",
                    gameplayBehavior = "Starts with +20% passive probe scan radius and can decode Precursor dialect fragments at level 1.",
                    statsOrAttributes = listOf("O2 Capacity: 180s", "EVA Thrust: 12 m/s²", "Scan Precision: +25%"),
                    tags = listOf("Protagonist", "EVA Specialist", "Xenolinguist")
                ),
                CodexEntry(
                    id = "codex_char_2",
                    category = CodexCategory.CHARACTER,
                    name = "SORA-9 (Synthetic Orbital Relay)",
                    roleOrType = "Shipboard AI & Telemetry Officer",
                    affiliationOrBiome = "S.S. Astrolabe Mainframe",
                    threatOrTier = "Companion Core",
                    summaryLore = "An eccentric pre-collapse navigation heuristic unit with 14% corrupted memory sectors. Speaks in dry astronomical metaphors and fiercely protects the crew.",
                    gameplayBehavior = "Provides real-time hazard callouts, manages automated turret point-defense, and offers philosophical dialogue branches after major discoveries.",
                    statsOrAttributes = listOf("CPU Load: 64 Teraflops", "Auto-Repair: +1.5%/sec", "Decrypt Tier: III"),
                    tags = listOf("AI Companion", "Bridge Officer", "Lore Guide")
                ),
                CodexEntry(
                    id = "codex_fac_1",
                    category = CodexCategory.FACTION,
                    name = "The Astral Syndicate",
                    roleOrType = "Technocratic Cartography Guild",
                    affiliationOrBiome = "Helios Spire & Core Beacons",
                    threatOrTier = "Major Power (High Tech)",
                    summaryLore = "Controls the remaining hyper-lane toll beacons. They pay top credit for verified star-charts but ruthlessly confiscate unlicensed Precursor relics.",
                    gameplayBehavior = "Selling charts to the Syndicate unlocks military-grade plasma shields and high-grade fuel, but lowers reputation with the Free Drifters.",
                    statsOrAttributes = listOf("Fleet Strength: High", "Tech Tier: Plasma/Shields", "Bounty Threshold: Strict"),
                    tags = listOf("Guild", "Trade Hub", "Lawful Neutral")
                ),
                CodexEntry(
                    id = "codex_fac_2",
                    category = CodexCategory.FACTION,
                    name = "Children of the Horizon",
                    roleOrType = "Nomadic Void-Mystic Salvagers",
                    affiliationOrBiome = "Chorus Reef Nebulae",
                    threatOrTier = "Major Power (Bio-Acoustic Tech)",
                    summaryLore = "Generational fleet-dwellers who believe the Null Tide is a living cosmic organism. They graft resonant crystal harmonics onto their ship hulls.",
                    gameplayBehavior = "Provides stealth cloak modules and harmonic resonators that pacify Void Leviathans.",
                    statsOrAttributes = listOf("Fleet Strength: Guerilla", "Tech Tier: Stealth/Harmonics", "Sanctuary: Chorus Reef"),
                    tags = listOf("Nomads", "Stealth Tech", "Lore Keepers")
                ),
                CodexEntry(
                    id = "codex_loc_1",
                    category = CodexCategory.LOCATION,
                    name = "The Glass Choir (Sector K-09)",
                    roleOrType = "Crystalline Magnetar Graveyard",
                    affiliationOrBiome = "Outer Rim — High Radiation",
                    threatOrTier = "Hazard Tier IV",
                    summaryLore = "A shattered Dyson swarm orbiting a hyper-spinning magnetar. Every 90 seconds, magnetic pulses cause the silicate shards to sing across radio frequencies.",
                    gameplayBehavior = "During the 12-second Magnetar Pulse, unshielded ships suffer 40 EM overload damage unless parked inside the shadow cone of a mega-shard.",
                    statsOrAttributes = listOf("Pulse Interval: 90s", "Primary Loot: Crystalline Cores", "Visibility: 1.8 km"),
                    tags = listOf("Magnetar", "Dyson Ruin", "High Loot")
                ),
                CodexEntry(
                    id = "codex_enemy_1",
                    category = CodexCategory.ENEMY,
                    name = "Phase-Skimmer Leviathan",
                    roleOrType = "Apex Void-Borne Predator",
                    affiliationOrBiome = "Deep Ion Nebulae",
                    threatOrTier = "Boss / Apex Threat",
                    summaryLore = "A 120-meter silicon-based organism that feeds on reactor Cherenkov radiation. Hunts entirely by electromagnetic signature rather than sight.",
                    gameplayBehavior = "Stalks ships running above 60% EM output. Can phase-shift through asteroids and latch onto the hull to drain capacitor banks unless repelled by an Acoustic Decoy Buoy.",
                    statsOrAttributes = listOf("Hull HP: 4,200", "Detection: EM > 60%", "Weakness: Cryo / Sonic"),
                    tags = listOf("Leviathan", "Stealth Predator", "EMP Drain")
                ),
                CodexEntry(
                    id = "codex_enemy_2",
                    category = CodexCategory.ENEMY,
                    name = "Auton Sentinel Swarm (Mk.VII)",
                    roleOrType = "Corrupted Precursor Defense Drones",
                    affiliationOrBiome = "Graveyard Orbits & Vaults",
                    threatOrTier = "Medium Tactical Threat",
                    summaryLore = "Self-repairing geometric drones left behind to guard Precursor telemetry vaults. They communicate via laser mesh networks.",
                    gameplayBehavior = "Individually fragile, but if the Central Relay Drone isn't destroyed first, destroyed units reconstruct within 15 seconds.",
                    statsOrAttributes = listOf("Swarm Size: 6–12", "Weapon: Cutting Beam", "Core Drop: Logic Lattice"),
                    tags = listOf("Precursor", "Swarm AI", "Salvageable")
                )
            ),
            quests = listOf(
                QuestItem(
                    id = "quest_main_01",
                    title = "Signal in the Cinder Belt",
                    questType = QuestType.MAIN_ARC,
                    giverOrTrigger = "SORA-9 Automated Distress Intercept",
                    regionOrSector = "Sector A-01: Cinder Belt",
                    recommendedTier = "Tier 1 (Starter Corvette)",
                    synopsis = "Triangulate a repeating 412-Hz Precursor beacon buried inside the unstable core of a hollow silicate asteroid before an approaching solar storm strips the sector.",
                    objectives = listOf(
                        QuestObjective("obj_1", "Deploy 3 Acoustic Probes around the Cinder Belt perimeter", isOptional = false, isCompletedInPrototype = true),
                        QuestObjective("obj_2", "Navigate the hollow asteroid trench without exceeding 50% EM signature", isOptional = false, isCompletedInPrototype = true),
                        QuestObjective("obj_3", "Perform zero-G EVA walk to extract the Precursor Astrolabe Shard", isOptional = false, isCompletedInPrototype = false),
                        QuestObjective("obj_4", "Recover Captain Mirek's personal blackbox log from the wrecked tug", isOptional = true, isCompletedInPrototype = false)
                    ),
                    branchingOutcomes = "Deliver the Shard to the Astral Syndicate for 2,500 Credits + Mk.II Thermal ShieldSchematic, OR let SORA-9 decrypt it privately to unlock hidden Chorus Reef coordinates.",
                    rewards = listOf("2,500 Credits", "Astrolabe Shard (Key Item)", "+150 Syndicate or Free Drifter Rep")
                ),
                QuestItem(
                    id = "quest_anom_02",
                    title = "The Singing Magnetar",
                    questType = QuestType.EXPLORATION_ANOMALY,
                    giverOrTrigger = "Archivist Vaelen (Children of the Horizon)",
                    regionOrSector = "Sector K-09: The Glass Choir",
                    recommendedTier = "Tier 3 (Radiation-Shielded Hull)",
                    synopsis = "Record three harmonic resonance samples from the heart of the Glass Choir during peak magnetar emission without frying the ship's navigation computer.",
                    objectives = listOf(
                        QuestObjective("obj_2_1", "Equip Lead-Lined Faraday Plating or Harmonic Dampener", isOptional = false, isCompletedInPrototype = true),
                        QuestObjective("obj_2_2", "Hold position within 300m of Choir Spire Alpha during a pulse", isOptional = false, isCompletedInPrototype = false),
                        QuestObjective("obj_2_3", "Pacify or evade the resident Phase-Skimmer Leviathan", isOptional = true, isCompletedInPrototype = false)
                    ),
                    branchingOutcomes = "If the Leviathan is spared using an Acoustic Buoy, Archivist Vaelen grants the 'Void-Whisperer' hull harmonic.",
                    rewards = listOf("Harmonic Cloak Mk.III", "4,000 Credits", "Leviathan Bio-Telemetry Entry")
                ),
                QuestItem(
                    id = "quest_side_03",
                    title = "Ghost of the Iron Drift",
                    questType = QuestType.FACTION_CONTRACT,
                    giverOrTrigger = "Dockmaster Korr (Iron Drift Station)",
                    regionOrSector = "Sector D-04: Dreadnought Graveyard",
                    recommendedTier = "Tier 2",
                    synopsis = "Board the drifting pre-collapse carrier 'Vanguard-12', restore auxiliary gravity to Deck C, and salvage its intact cryo-medicine shipment.",
                    objectives = listOf(
                        QuestObjective("obj_3_1", "Cut through the jammed cargo airlock using the Plasma Torch", isOptional = false, isCompletedInPrototype = true),
                        QuestObjective("obj_3_2", "Reroute 3 junction boxes while avoiding Auton Sentinel patrols", isOptional = false, isCompletedInPrototype = false)
                    ),
                    branchingOutcomes = "Donate the cryo-medicine to the refugees on Iron Drift (+300 Rep, permanent repair discount) or sell on the black market (+4,800 Credits).",
                    rewards = listOf("Cryo-Injector Blueprint", "3,200 Credits", "Iron Drift Dock Pass")
                )
            ),
            backlogFeatures = listOf(
                BacklogFeature(
                    id = "feat_1",
                    title = "6-DOF Newtonian Flight Controller & Coupled Assist",
                    categoryTag = "Core Loop",
                    priority = FeaturePriority.CRITICAL,
                    status = FeatureStatus.DONE,
                    milestoneTag = "Prototype",
                    effortPoints = 8,
                    acceptanceCriteria = "Smooth thruster interpolation, instant toggle between Coupled and Decoupled drift, and zero-G velocity matching."
                ),
                BacklogFeature(
                    id = "feat_2",
                    title = "Triangulation Probe & Waveform Scanner Mini-Game",
                    categoryTag = "Mechanics",
                    priority = FeaturePriority.CRITICAL,
                    status = FeatureStatus.DONE,
                    milestoneTag = "Prototype",
                    effortPoints = 5,
                    acceptanceCriteria = "Player deploys 3 probes; overlapping signal rings resolve hidden POI coordinates on the 3D nav table."
                ),
                BacklogFeature(
                    id = "feat_3",
                    title = "Zero-G EVA Tether Physics & Airlock Transition",
                    categoryTag = "Core Loop",
                    priority = FeaturePriority.HIGH,
                    status = FeatureStatus.IN_PROGRESS,
                    milestoneTag = "Vertical Slice",
                    effortPoints = 8,
                    acceptanceCriteria = "Seamless first-person exit from ship airlock with dynamic Verlet physics tether and oxygen consumption HUD."
                ),
                BacklogFeature(
                    id = "feat_4",
                    title = "Reactor Thermal & EM Stealth Detection AI",
                    categoryTag = "Combat / AI",
                    priority = FeaturePriority.HIGH,
                    status = FeatureStatus.PLAYTESTING,
                    milestoneTag = "Vertical Slice",
                    effortPoints = 5,
                    acceptanceCriteria = "Void Leviathan and Auton Sentinels dynamically shift from Patrol -> Investigate -> Attack based on ship EM emission radius."
                ),
                BacklogFeature(
                    id = "feat_5",
                    title = "Procedural Voronoi Nebula & Asteroid Belt Generator",
                    categoryTag = "ProcGen",
                    priority = FeaturePriority.HIGH,
                    status = FeatureStatus.IN_PROGRESS,
                    milestoneTag = "Alpha",
                    effortPoints = 13,
                    acceptanceCriteria = "64-bit seed generates deterministic star class, asteroid ring density, and places 4–6 handcrafted POIs without overlap."
                ),
                BacklogFeature(
                    id = "feat_6",
                    title = "Diegetic Holographic Rumor Astrolabe UI",
                    categoryTag = "UI/UX",
                    priority = FeaturePriority.MEDIUM,
                    status = FeatureStatus.BACKLOG,
                    milestoneTag = "Alpha",
                    effortPoints = 5,
                    acceptanceCriteria = "Interactive node graph connecting discovered logs, factions, and mystery clues with filterable status."
                )
            ),
            milestones = listOf(
                MilestoneItem(
                    id = "ms_1",
                    title = "M1: Greybox Flight & Scanner Prototype",
                    targetDate = "Q1 — Completed",
                    status = MilestoneStatus.COMPLETED,
                    summaryGoal = "Validate 6-DOF flight feel, probe triangulation loop, and basic asteroid collision physics.",
                    deliverables = listOf("6-DOF Corvette Controller", "3-Probe Triangulation Loop", "1 Greybox Asteroid Sector"),
                    progressPercent = 100
                ),
                MilestoneItem(
                    id = "ms_2",
                    title = "M2: Playable Vertical Slice (Sector Zero)",
                    targetDate = "Q2 — Current Sprint",
                    status = MilestoneStatus.ACTIVE,
                    summaryGoal = "Complete one polished star sector ('Cinder Belt') with EVA salvage, EM stealth, SORA-9 dialogue, and Steam Deck 60fps target.",
                    deliverables = listOf("EVA Tether & Plasma Cutter", "Phase-Skimmer Leviathan AI", "Diegetic Cockpit HUD", "Full Audio Stem Integration"),
                    progressPercent = 68
                ),
                MilestoneItem(
                    id = "ms_3",
                    title = "M3: Procedural Galaxy & 3-Faction Alpha",
                    targetDate = "Q3 — Upcoming",
                    status = MilestoneStatus.UPCOMING,
                    summaryGoal = "Scale procedural generator to 4 stellar classes, integrate all 3 faction reputation arcs, and implement save/load serialization.",
                    deliverables = listOf("4 Biome Generators", "32 Ship Upgrade Schematics", "Act I–III Main Story Quests"),
                    progressPercent = 20
                ),
                MilestoneItem(
                    id = "ms_4",
                    title = "M4: Steam Next Fest Demo & 1.0 Launch",
                    targetDate = "Q4 — Target Release",
                    status = MilestoneStatus.UPCOMING,
                    summaryGoal = "Public demo telemetry polish, localization (8 languages), accessibility audit, and 1.0 master gold build.",
                    deliverables = listOf("Steam Deck Verified Badge", "JSON/Markdown Mod Loader", "60+ Achievement Triggers"),
                    progressPercent = 5
                )
            ),
            dialogueNodes = listOf(
                DialogueNode(
                    id = "dlg_01",
                    nodeCode = "SORA_CINDER_01",
                    speakerName = "SORA-9 (Ship AI)",
                    speakerEmotion = "Analytical / Urgent",
                    sceneContext = "Triggered upon entering Sector A-01 when the Precursor carrier wave spikes on the long-range spectrogram.",
                    spokenLine = "Navigator Vance, I am isolating a 412-Hertz harmonic beneath the silicate ring's thermal noise. That modulation pattern predates the Collapse by at least four millennia—and our reactor glow is currently broadcasting our position to every scavenger in the belt.",
                    voiceDirectionNotes = "Warm analog synth filter, subtle radio static tail on consonants; calm but cautionary cadence.",
                    choices = listOf(
                        DialogueChoice(
                            id = "ch_1",
                            playerPrompt = "\"Cut main thrusters and drop reactor to 20%. We coast in on cold gas.\"",
                            conditionOrSkillCheck = "Stealth Protocol",
                            nextNodeCode = "SORA_CINDER_STEALTH",
                            outcomeEffect = "EM Signature reduced by 65%; Cabin temperature drops."
                        ),
                        DialogueChoice(
                            id = "ch_2",
                            playerPrompt = "\"Overclock the active lidar array. Pinpoint that shard before the storm hits.\"",
                            conditionOrSkillCheck = "[Sensor Tech Lv.2]",
                            nextNodeCode = "SORA_CINDER_PING",
                            outcomeEffect = "Instantly reveals all 3 POI coordinates; attracts 2 Auton Sentinels."
                        ),
                        DialogueChoice(
                            id = "ch_3",
                            playerPrompt = "\"Cross-reference the 412-Hz signature with our Helios Spire disaster files.\"",
                            conditionOrSkillCheck = "[Xenolinguist Lore]",
                            nextNodeCode = "SORA_CINDER_LORE",
                            outcomeEffect = "Unlocks Rumor Node: 'The Architect's Quarantine'."
                        )
                    )
                ),
                DialogueNode(
                    id = "dlg_02",
                    nodeCode = "VAELEN_CHOIR_01",
                    speakerName = "Archivist Vaelen",
                    speakerEmotion = "Reverent / Cryptic",
                    sceneContext = "Hailed via tight-beam laser comm near the outer perimeter of the Glass Choir magnetar.",
                    spokenLine = "You carry Syndicate transponders on your hull, Cartographer, yet you fly with your weapons powered down. Have you come to strip the Choir for glass-ore, or have you come to listen to the star's warning?",
                    voiceDirectionNotes = "Layered breathy reverb, accompanied by low crystalline chime undertones.",
                    choices = listOf(
                        DialogueChoice(
                            id = "ch_2_1",
                            playerPrompt = "\"I'm charting safe passage through the Null Tide. Share your harmonic charts.\"",
                            conditionOrSkillCheck = "Diplomacy",
                            nextNodeCode = "VAELEN_ALLIANCE",
                            outcomeEffect = "+50 Children of the Horizon Rep; unlocks Quest 'The Singing Magnetar'."
                        ),
                        DialogueChoice(
                            id = "ch_2_2",
                            playerPrompt = "\"Transmit the Astrolabe Shard telemetry. Proof that the Quarantine is failing.\"",
                            conditionOrSkillCheck = "[Requires: Astrolabe Shard]",
                            nextNodeCode = "VAELEN_REVELATION",
                            outcomeEffect = "Grants 'Resonant Beacon' blueprint immediately."
                        )
                    )
                )
            ),
            items = listOf(
                GameItem(
                    id = "item_01",
                    codeId = "MOD_ASTRO_SHARD",
                    name = "Precursor Astrolabe Core",
                    category = "Relic Bus Module",
                    rarity = ItemRarity.MYTHIC,
                    valueCredits = 12500,
                    statModifiers = listOf("+45% Quantum Scan Range", "-20% Hyper-Fold Fuel Cost", "Unlocks Null-Tide Telemetry"),
                    uiTooltipDescription = "A suspended dodecahedron of non-baryonic glass that hums in sympathetic resonance with active fold-gates.",
                    flavorLore = "\"Before the Architects vanished into the Singularity, they etched every star's birth and death into a single lattice of folded light.\" — SORA-9 Archive"
                ),
                GameItem(
                    id = "item_02",
                    codeId = "EQP_CRYO_TORCH",
                    name = "Harmonic Plasma Cutter Mk.IV",
                    category = "EVA Tool / Weapon",
                    rarity = ItemRarity.RARE,
                    valueCredits = 1850,
                    statModifiers = listOf("Weld Cut Speed: 3.2 m/s", "Thermal Spread: -35%", "Bonus Salvage Yield: +20%"),
                    uiTooltipDescription = "Precision magnetic-pinch plasma torch engineered for zero-G hull breaching without igniting volatile fuel lines.",
                    flavorLore = "Standard issue for Iron Drift deep-wreck breakers who prefer returning home with all ten fingers."
                ),
                GameItem(
                    id = "item_03",
                    codeId = "SHD_FARADAY_WEAVE",
                    name = "Choir-Glass Faraday Plating",
                    category = "Hull Armor",
                    rarity = ItemRarity.EXOTIC,
                    valueCredits = 5400,
                    statModifiers = listOf("+65% EM Pulse Resistance", "-18% Passive Radar Signature", "Hull Mass: +4.2 Tons"),
                    uiTooltipDescription = "Laminated silicate-bismuth hull plates harvested from the Glass Choir that disperse magnetar radiation into harmless aurorae.",
                    flavorLore = "When struck by an ion flare, the hull rings like a cathedral bell."
                ),
                GameItem(
                    id = "item_04",
                    codeId = "CON_ACOUSTIC_BUOY",
                    name = "Sub-Harmonic Decoy Buoy",
                    category = "Tactical Consumable",
                    rarity = ItemRarity.UNCOMMON,
                    valueCredits = 320,
                    statModifiers = listOf("Lure Radius: 1,500m", "Duration: 25s", "EM Mimic Output: 140%"),
                    uiTooltipDescription = "Deployable micro-satellite that imitates the Cherenkov signature of a critically overheating reactor core.",
                    flavorLore = "A Void Cartographer's best friend when a 120-meter Phase-Skimmer decides your ship looks appetizing."
                )
            )
        )
    }

    fun createChronosDungeonProject(): GameProject {
        val now = System.currentTimeMillis() - 86_400_000L
        return GameProject(
            id = "proj_chronos_bastion",
            title = "Chronos: Clockwork Bastion",
            tagline = "Rewind 6-second combat timelines, rewire brass automata, and ascend a shifting mechanical citadel.",
            genre = "Tactical Time-Loop Action Roguelite",
            engine = "Unity 6 (URP 2D/3D Hybrid)",
            targetPlatforms = "PC, Nintendo Switch, Android Tablet",
            studioName = "Gearwright Collective",
            targetAudience = "Fans of Hades, Into the Breach, and Dead Cells",
            lastUpdatedEpochMillis = now,
            gddSections = GddPillarType.entries.map { pillar ->
                when (pillar) {
                    GddPillarType.GAMEPLAY -> GddSection(
                        pillar = pillar,
                        designPillarStatement = "Every 6-second turn can be rewound and layered with your past phantom self to execute synchronized combos.",
                        contentMarkdown = "Players ascend the 4 districts of the Clockwork Bastion. Spending Chrono-Gears creates a temporal echo that replays your last 4 seconds of movement and attacks while you flank the enemy.",
                        keyMechanicsBullets = listOf(
                            "4-second Phantom Echo recording and dual-aggro flanking",
                            "Deterministic enemy telegraph lasers for zero-RNG parry timing",
                            "Gear-train weapon sockets that alter attack cadence"
                        ),
                        status = SectionStatus.LOCKED
                    )
                    GddPillarType.STORY -> GddSection(
                        pillar = pillar,
                        designPillarStatement = "A guild apprentice trapped in the final midnight chime of a dying mechanical metropolis.",
                        contentMarkdown = "The Great Mainspring of Oakhaven has fractured, freezing the city at 11:59 PM. Only Horologist Lyra carries the Anchor Pendulum capable of rewinding local rooms.",
                        keyMechanicsBullets = listOf("Hub progression in the Grand Belltower", "NPC memories persist across time-loops"),
                        status = SectionStatus.IN_REVIEW
                    )
                    else -> GddSection(
                        pillar = pillar,
                        designPillarStatement = "Crisp steampunk readability with high-contrast telegraphing and modular brass instrumentation.",
                        contentMarkdown = "Detailed specifications for ${pillar.title} tailored for fast 60fps roguelite room encounters and responsive controller input.",
                        keyMechanicsBullets = listOf("Sub-50ms input latency", "Modular room seed generator"),
                        status = SectionStatus.DRAFT
                    )
                }
            },
            codexEntries = listOf(
                CodexEntry(
                    id = "chr_lyra",
                    category = CodexCategory.CHARACTER,
                    name = "Lyra the Horologist",
                    roleOrType = "Time-Weaving Apprentice",
                    affiliationOrBiome = "Guild of the Mainspring",
                    threatOrTier = "Protagonist",
                    summaryLore = "Wields the Anchor Caliper and pocket chronometer.",
                    gameplayBehavior = "High mobility dash-cancels with 3 charges of Temporal Echo.",
                    statsOrAttributes = listOf("HP: 120", "Chrono Charges: 3", "Dash Cooldown: 0.4s"),
                    tags = listOf("Protagonist", "Melee/Ranged")
                ),
                CodexEntry(
                    id = "chr_warden",
                    category = CodexCategory.ENEMY,
                    name = "Escapement Warden",
                    roleOrType = "Heavy Pendulum Automaton",
                    affiliationOrBiome = "Bellfoundry Floor",
                    threatOrTier = "Elite Boss",
                    summaryLore = "A 4-meter brass sentinel powered by a white-hot Mainspring coil.",
                    gameplayBehavior = "Frontal shield blocks all damage; requires recording a Phantom Echo to bait its swing while attacking its rear winding key.",
                    statsOrAttributes = listOf("HP: 2,800", "Armor: Frontal Immune", "Drop: Master Spring"),
                    tags = listOf("Boss", "Directional Shield")
                )
            ),
            quests = listOf(
                QuestItem(
                    id = "q_chronos_1",
                    title = "Rewind the Foundry Heart",
                    questType = QuestType.MAIN_ARC,
                    giverOrTrigger = "Master Vane",
                    regionOrSector = "District 1: Brass Bellfoundry",
                    recommendedTier = "Loop 1+",
                    synopsis = "Defeat the Escapement Warden and recover the First Mainspring Escapement Gear.",
                    objectives = listOf(
                        QuestObjective("co_1", "Clear 8 procedural gear-rooms", isOptional = false, isCompletedInPrototype = true),
                        QuestObjective("co_2", "Shatter the Warden's rear winding key using a synchronized Echo strike", isOptional = false, isCompletedInPrototype = false)
                    ),
                    branchingOutcomes = "Unlocks the Pendulum Grapple hook for all future loops.",
                    rewards = listOf("Pendulum Grapple", "500 Aether Brass")
                )
            ),
            backlogFeatures = listOf(
                BacklogFeature(
                    id = "bf_c1",
                    title = "4-Second Ghost Transform Recorder",
                    categoryTag = "Core Loop",
                    priority = FeaturePriority.CRITICAL,
                    status = FeatureStatus.DONE,
                    milestoneTag = "Prototype",
                    effortPoints = 8,
                    acceptanceCriteria = "60Hz transform + hitbox replay with zero memory allocation spikes."
                ),
                BacklogFeature(
                    id = "bf_c2",
                    title = "Procedural Room Graph & Door Lock Triggers",
                    categoryTag = "ProcGen",
                    priority = FeaturePriority.HIGH,
                    status = FeatureStatus.IN_PROGRESS,
                    milestoneTag = "Vertical Slice",
                    effortPoints = 5,
                    acceptanceCriteria = "Generates 12-room floor layout with 1 shop, 1 relic vault, and 1 boss arena."
                )
            ),
            milestones = listOf(
                MilestoneItem(
                    id = "ms_c1",
                    title = "Combat & Echo Prototype",
                    targetDate = "Month 2",
                    status = MilestoneStatus.COMPLETED,
                    summaryGoal = "Prove the fun of dual-echo flanking combat.",
                    deliverables = listOf("Echo Recorder", "3 Enemy Archetypes"),
                    progressPercent = 100
                ),
                MilestoneItem(
                    id = "ms_c2",
                    title = "District 1 Vertical Slice",
                    targetDate = "Month 5",
                    status = MilestoneStatus.ACTIVE,
                    summaryGoal = "Full playable 20-minute roguelite loop with boss fight.",
                    deliverables = listOf("18 Gear Relics", "Boss Arena"),
                    progressPercent = 55
                )
            ),
            dialogueNodes = listOf(
                DialogueNode(
                    id = "dlg_c1",
                    nodeCode = "VANE_HUB_01",
                    speakerName = "Master Vane",
                    speakerEmotion = "Weary / Determined",
                    sceneContext = "Spoken in the Belltower Hub after the player's first time-loop reset.",
                    spokenLine = "The twelfth chime hasn't finished ringing, Lyra. Feel the brass beneath your boots—it's still warm from four minutes ago.",
                    voiceDirectionNotes = "Gravelly baritone with ticking pocket-watch foley.",
                    choices = listOf(
                        DialogueChoice("cc_1", "\"The Escapement Warden guarded the lower lift. I need a heavier gear-blade.\"", "Weapon Unlock", "VANE_ARSENAL", "Opens Arsenal Bench")
                    )
                )
            ),
            items = listOf(
                GameItem(
                    id = "itm_c1",
                    codeId = "WPN_CHRONO_CALIPER",
                    name = "Twin-Spring Caliper Blades",
                    category = "Primary Weapon",
                    rarity = ItemRarity.EXOTIC,
                    valueCredits = 900,
                    statModifiers = listOf("+30% Echo Attack Speed", "Crit on Backstab: 220%"),
                    uiTooltipDescription = "Dual brass daggers that store kinetic tension during dashes and release a sonic snap on the third strike.",
                    flavorLore = "Forged to measure the distance between two heartbeats."
                )
            )
        )
    }

    fun createNewCustomProject(
        title: String,
        tagline: String,
        genre: String,
        engine: String,
        platforms: String,
        studioName: String,
        useStarterTemplate: Boolean
    ): GameProject {
        val id = "proj_" + UUID.randomUUID().toString().take(8)
        val now = System.currentTimeMillis()
        val sections = GddPillarType.entries.map { pillar ->
            GddSection(
                pillar = pillar,
                designPillarStatement = if (useStarterTemplate) {
                    "Define a clear, memorable player promise for ${pillar.title} in $title."
                } else {
                    "Core design pillar for ${pillar.title}."
                },
                contentMarkdown = if (useStarterTemplate) {
                    """
                        ### ${pillar.title} Overview
                        Describe how **$title** ($genre) delivers its core experience in **${pillar.title}**.
                        
                        - **Primary Player Goal:** What keeps the player engaged minute-to-minute?
                        - **Risk vs. Reward:** What meaningful trade-offs must the player weigh?
                        - **Scope Guardrail:** How do we keep this achievable for an indie team using $engine?
                    """.trimIndent()
                } else {
                    "Draft specifications for ${pillar.title}."
                },
                keyMechanicsBullets = if (useStarterTemplate) {
                    listOf(
                        "Core mechanic specification #1 for ${pillar.title}",
                        "Player feedback & telemetry loop",
                        "Vertical slice scope boundary"
                    )
                } else emptyList(),
                status = SectionStatus.DRAFT
            )
        }

        return GameProject(
            id = id,
            title = title.ifBlank { "Untitled Indie Game" },
            tagline = tagline.ifBlank { "An original $genre experience built in $engine." },
            genre = genre.ifBlank { "Action / Adventure" },
            engine = engine.ifBlank { "Godot 4.3" },
            targetPlatforms = platforms.ifBlank { "PC / Steam Deck" },
            studioName = studioName.ifBlank { "IndieForge Studio" },
            targetAudience = "Fans of $genre and systemic indie games",
            lastUpdatedEpochMillis = now,
            gddSections = sections,
            codexEntries = if (useStarterTemplate) {
                listOf(
                    CodexEntry(
                        id = "cdx_${UUID.randomUUID().toString().take(6)}",
                        category = CodexCategory.CHARACTER,
                        name = "Lead Protagonist",
                        roleOrType = "Player Avatar",
                        affiliationOrBiome = "Starting Region",
                        threatOrTier = "Tier 1",
                        summaryLore = "Central character of $title whose personal goal drives the core loop.",
                        gameplayBehavior = "Responsive core movement and signature toolkit.",
                        statsOrAttributes = listOf("Base Speed: 100%", "Stamina: 100"),
                        tags = listOf("Player", "Starter")
                    )
                )
            } else emptyList(),
            quests = if (useStarterTemplate) {
                listOf(
                    QuestItem(
                        id = "qst_${UUID.randomUUID().toString().take(6)}",
                        title = "Prologue: The First Trial",
                        questType = QuestType.MAIN_ARC,
                        giverOrTrigger = "Opening Cutscene / Tutorial Trigger",
                        regionOrSector = "Zone 01 — Starting Biome",
                        recommendedTier = "Tier 1",
                        synopsis = "Introduce the player to core movement, primary interaction, and the central mystery of $title.",
                        objectives = listOf(
                            QuestObjective("o1", "Master basic movement and traversal controls", false, true),
                            QuestObjective("o2", "Acquire the primary tool/weapon and complete the first encounter", false, false)
                        ),
                        branchingOutcomes = "Completing the optional discovery unlocks an early shortcut.",
                        rewards = listOf("Starter Equipment", "100 XP / Currency")
                    )
                )
            } else emptyList(),
            backlogFeatures = listOf(
                BacklogFeature(
                    id = "bf_${UUID.randomUUID().toString().take(6)}",
                    title = "Core Player Movement & Camera Controller",
                    categoryTag = "Core Loop",
                    priority = FeaturePriority.CRITICAL,
                    status = FeatureStatus.IN_PROGRESS,
                    milestoneTag = "Prototype",
                    effortPoints = 5,
                    acceptanceCriteria = "Responsive controls at 60fps with tuned acceleration and camera damping."
                ),
                BacklogFeature(
                    id = "bf_${UUID.randomUUID().toString().take(6)}",
                    title = "Playable Greybox Arena & Save System",
                    categoryTag = "Systems",
                    priority = FeaturePriority.HIGH,
                    status = FeatureStatus.BACKLOG,
                    milestoneTag = "Vertical Slice",
                    effortPoints = 8,
                    acceptanceCriteria = "End-to-end 10-minute playable loop with state persistence."
                )
            ),
            milestones = listOf(
                MilestoneItem(
                    id = "ms_${UUID.randomUUID().toString().take(6)}",
                    title = "M1: Core Loop Greybox Prototype",
                    targetDate = "Month 1",
                    status = MilestoneStatus.ACTIVE,
                    summaryGoal = "Validate the 30-second core gameplay loop in greybox.",
                    deliverables = listOf("Player Controller", "1 Test Arena", "Core Interaction"),
                    progressPercent = 25
                ),
                MilestoneItem(
                    id = "ms_${UUID.randomUUID().toString().take(6)}",
                    title = "M2: Vertical Slice Demo",
                    targetDate = "Month 3",
                    status = MilestoneStatus.UPCOMING,
                    summaryGoal = "Polished 15-minute slice representing final art, audio, and UI quality.",
                    deliverables = listOf("1 Complete Biome", "Boss Encounter", "Polished HUD"),
                    progressPercent = 0
                )
            ),
            dialogueNodes = emptyList(),
            items = emptyList()
        )
    }

    fun getBlueprintTemplatesForPillar(pillar: GddPillarType): List<BlueprintTemplate> {
        return when (pillar) {
            GddPillarType.GAMEPLAY -> listOf(
                BlueprintTemplate(
                    id = "tpl_gp_1",
                    category = "Exploration / Survival Loop",
                    title = "Tension-Release Expedition Loop",
                    description = "Ideal for procedural space, survival, or dungeon crawlers with safe hubs and high-stakes expeditions.",
                    pillarStatement = "Every expedition is a calculated push into the unknown where greed and curiosity battle diminishing life-support reserves.",
                    contentBody = """
                        ### 3-Phase Core Loop
                        1. **Preparation (Safe Hub — 3 mins):** Analyze sector anomalies, tune ship/character loadout, and select a contract matching current risk tolerance.
                        2. **Foray & Escalation (Active Zone — 12 mins):** Harvest high-tier telemetry and salvage while environmental hazards escalate on a visible waveform meter.
                        3. **Extraction & Synthesis (Return — 4 mins):** Execute a tense extraction sequence to bank discovered blueprints and permanently expand the hub's tech tree.
                    """.trimIndent(),
                    bullets = listOf(
                        "Banked knowledge persists even if physical cargo is lost on failed extraction",
                        "Dynamic hazard escalation prevents passive farming in one zone",
                        "3 distinct player playstyles: Stealth Cartographer, Heavy Salvager, Speed Runner"
                    )
                ),
                BlueprintTemplate(
                    id = "tpl_gp_2",
                    category = "Tactical Action / Roguelite",
                    title = "High-Readability Combat Rhythm",
                    description = "Focused on deterministic enemy telegraphs, stamina/heat management, and synergetic build crafting.",
                    pillarStatement = "Zero cheap hits—every enemy attack is clearly telegraphed, turning combat into a high-speed spatial puzzle.",
                    contentBody = """
                        ### Combat Cadence
                        - **Observe (0.5s):** Crisp visual and audio telegraph reveals attack vector and parry window.
                        - **Reposition / Counter (1.0s):** Use directional dash or shield-deflect to convert incoming kinetic energy into Overcharge.
                        - **Punish (2.0s):** Spend Overcharge on modular weapon skills that trigger elemental chain reactions.
                    """.trimIndent(),
                    bullets = listOf(
                        "100% deterministic attack patterns with color-coded threat indicators",
                        "Hit-stop frames (40ms) and screen-shake slider for tactile impact",
                        "Sub-20 minute run length designed for 'one more run' replayability"
                    )
                )
            )
            GddPillarType.STORY -> listOf(
                BlueprintTemplate(
                    id = "tpl_st_1",
                    category = "Archaeological Sci-Fi",
                    title = "Environmental & Telemetry Mystery",
                    description = "Non-linear storytelling inspired by Outer Wilds and Subnautica where clues assemble on a rumor board.",
                    pillarStatement = "No unskippable exposition dumps—story is discovered organically by decoding logs, ruins, and faction transmissions.",
                    contentBody = """
                        ### Narrative Architecture
                        - **The Hook:** A seemingly routine survey uncovers an impossible signal originating from a quarantined star sector.
                        - **The Investigation:** Players piece together three conflicting accounts from ancient precursors, corporate syndicates, and void-nomads.
                        - **The Climax:** The player's accumulated knowledge—not a arbitrary level gate—unlocks the final sequence and determines the sector's fate.
                    """.trimIndent(),
                    bullets = listOf(
                        "All dialogue lines kept under 35 words per node for crisp pacing",
                        "Interactive Rumor Graph automatically links related clues",
                        "3 philosophical endings tied to faction reputation and discovered secrets"
                    )
                )
            )
            GddPillarType.WORLD -> listOf(
                BlueprintTemplate(
                    id = "tpl_wd_1",
                    category = "Procedural Sandbox",
                    title = "Seeded Biome + Handcrafted Anchor POIs",
                    description = "Combines infinite replayability of procedural generation with authored narrative landmarks.",
                    pillarStatement = "Procedural generation provides the canvas; handcrafted Points of Interest provide the soul.",
                    contentBody = """
                        ### World Generation Pipeline
                        1. **Macro Graph:** Generate a 16-node sector map with guaranteed progression gates and hazard gradients.
                        2. **Biome Dressing:** Populate environmental hazards (ion storms, asteroid rings, radiation pockets) using Simplex noise fields.
                        3. **Anchor Injection:** Stamp authored derelict stations, faction outposts, and boss arenas into valid Lagrange sockets.
                    """.trimIndent(),
                    bullets = listOf(
                        "Shareable 8-character alphanumeric seeds for community challenges",
                        "Distinct color palette and ambient audio drone per biome class",
                        "Environmental hazards serve as both obstacles and tactical tools against enemies"
                    )
                )
            )
            else -> listOf(
                BlueprintTemplate(
                    id = "tpl_gen_${pillar.name}",
                    category = "Indie Production Standard",
                    title = "${pillar.title} — Vertical Slice Blueprint",
                    description = "Structured indie production specification for ${pillar.title} with clear scope boundaries.",
                    pillarStatement = "Deliver maximum clarity, tactile feedback, and maintainable indie scope across ${pillar.title}.",
                    contentBody = """
                        ### Core Specification for ${pillar.title}
                        - **Primary Design Goal:** Ensure every interaction within **${pillar.title}** reinforces the game's core atmosphere and player agency.
                        - **Implementation Architecture:** Modular data-driven configuration using ScriptableObjects / Resource files for rapid iteration without recompiling code.
                        - **Accessibility & Polish:** Built-in remapping, readable typography scaling, and clear audiovisual cues.
                    """.trimIndent(),
                    bullets = listOf(
                        "Data-driven configuration for rapid designer iteration",
                        "Verified 60fps performance budget on handheld & desktop hardware",
                        "Complete vertical slice feature lock before scaling content"
                    )
                )
            )
        }
    }
}
