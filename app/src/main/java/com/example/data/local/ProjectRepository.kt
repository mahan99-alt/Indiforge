package com.example.data.local

import com.example.data.ProjectJsonSerializer
import com.example.data.SeedProjects
import com.example.model.GameProject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjectsFlow: Flow<List<GameProject>> = projectDao.observeAllProjects().map { entities ->
        entities.mapNotNull { entity ->
            runCatching { ProjectJsonSerializer.fromJsonString(entity.payloadJson) }.getOrNull()
        }
    }

    suspend fun ensureSeedProjectsLoaded() {
        val count = projectDao.getProjectCount()
        if (count == 0) {
            val aetheris = SeedProjects.createAetherisSpaceProject()
            val chronos = SeedProjects.createChronosDungeonProject()
            saveProject(aetheris)
            saveProject(chronos)
        }
    }

    suspend fun saveProject(project: GameProject) {
        val entity = ProjectEntity(
            id = project.id,
            title = project.title,
            genre = project.genre,
            engine = project.engine,
            updatedAtMillis = project.lastUpdatedEpochMillis,
            payloadJson = ProjectJsonSerializer.toJsonString(project)
        )
        projectDao.upsertProject(entity)
    }

    suspend fun deleteProject(projectId: String) {
        projectDao.deleteProjectById(projectId)
    }
}
