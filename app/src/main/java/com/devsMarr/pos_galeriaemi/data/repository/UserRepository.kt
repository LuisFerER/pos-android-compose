package com.devsMarr.pos_galeriaemi.data.repository

import com.devsMarr.pos_galeriaemi.data.local.dao.UserDao
import com.devsMarr.pos_galeriaemi.data.mapper.toDomain
import com.devsMarr.pos_galeriaemi.data.mapper.toEntity
import com.devsMarr.pos_galeriaemi.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    // Obtener todos los empleados
    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllActiveUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // LOG IN: Recibe la contraseña cruda que tecleó el usuario, la encripta y busca si existe
    suspend fun loginWithPassword(rawPassword: String): User? {
        val hashedPassword = hashPassword(rawPassword)
        val userEntity = userDao.getUserByPassword(hashedPassword)
        return userEntity?.toDomain()
    }

    // Crear o actualizar un usuario
    suspend fun saveUser(user: User, isNewPassword: Boolean = false) {
        // Si es un usuario nuevo o le están cambiando la contraseña, encriptamos la nueva
        val finalPassword = if (isNewPassword) hashPassword(user.password) else user.password

        val entityToSave = user.copy(password = finalPassword).toEntity()

        if (entityToSave.id == 0L) {
            userDao.insertUser(entityToSave)
        } else {
            userDao.updateUser(entityToSave)
        }
    }

    suspend fun deleteUser(userId: Long) {
        userDao.deactivateUser(userId)
    }

    suspend fun hasAnyUser(): Boolean {
        return userDao.getUsersCount() > 0
    }

    // --- MAGIA DE ENCRIPTACIÓN ---
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}