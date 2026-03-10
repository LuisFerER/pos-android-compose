package com.devsMarr.pos_galeriaemi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devsMarr.pos_galeriaemi.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Para la lista de administración de empleados
    @Query("SELECT * FROM users WHERE isActive = 1 ORDER BY firstName ASC")
    fun getAllActiveUsers(): Flow<List<UserEntity>>

    // PARA EL LOGIN: Busca a un usuario activo que tenga esta contraseña encriptada
    @Query("SELECT * FROM users WHERE password = :hashedPassword AND isActive = 1 LIMIT 1")
    suspend fun getUserByPassword(hashedPassword: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // Soft delete
    @Query("UPDATE users SET isActive = 0 WHERE id = :userId")
    suspend fun deactivateUser(userId: Long)

    // Útil para saber si la base de datos está completamente vacía (para crear el primer Admin)
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?
}