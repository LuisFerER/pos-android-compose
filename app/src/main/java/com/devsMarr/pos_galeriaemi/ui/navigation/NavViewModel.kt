package com.devsMarr.pos_galeriaemi.ui.navigation

import androidx.lifecycle.ViewModel
import com.devsMarr.pos_galeriaemi.domain.manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    // Observamos al usuario actual para dibujar su nombre y su rol en el menú lateral
    val currentUser = sessionManager.currentUser

    fun logout() {
        sessionManager.logout()
    }
}