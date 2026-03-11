package com.devsMarr.pos_galeriaemi.ui.presentation.pos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devsMarr.pos_galeriaemi.domain.model.User
import com.devsMarr.pos_galeriaemi.domain.model.UserRole
import com.devsMarr.pos_galeriaemi.ui.navigation.Screen

@Composable
fun PosDrawer(
    currentUser: User,
    currentRoute: String?,
    onNavigateToPos: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
        // --- CABECERA DEL MENÚ ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User",
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = currentUser.fullName,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentUser.role == UserRole.ADMIN) "Administrador" else "Cajero",
                color = MaterialTheme.colorScheme.primaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTONES ---
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.PointOfSale, contentDescription = null) },
            label = { Text("Punto de Venta") },
            selected = currentRoute == Screen.Pos.route,
            onClick = onNavigateToPos,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )

        if (currentUser.role == UserRole.ADMIN) {
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                label = { Text("Panel de Administración") },
                selected = currentRoute == Screen.AdminDashboard.route,
                onClick = onNavigateToAdmin,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
            selected = false,
            onClick = onLogoutClick,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
        )
    }
}