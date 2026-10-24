package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM game_projects ORDER BY updatedAtMillis DESC")
    fun observeAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT COUNT(*) FROM game_projects")
    suspend fun getProjectCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProject(entity: ProjectEntity)

    @Query("DELETE FROM game_projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)
}
