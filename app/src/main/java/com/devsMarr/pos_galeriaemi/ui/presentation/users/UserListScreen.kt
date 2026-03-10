package com.devsMarr.pos_galeriaemi.ui.presentation.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devsMarr.pos_galeriaemi.domain.model.User
// Importa tus componentes recién creados
import com.devsMarr.pos_galeriaemi.ui.presentation.users.components.UserCard
import com.devsMarr.pos_galeriaemi.ui.presentation.users.components.DeleteUserDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    users: List<User>,
    onNavigateToAddUser: () -> Unit,
    onNavigateToEditUser: (Long) -> Unit,
    onDeactivateUser: (User) -> Unit,
    onBackClick: () -> Unit
) {
    // Estado del diálogo
    var userToDelete by remember { mutableStateOf<User?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrar Empleados", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddUser,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Empleado")
            }
        }
    ) { paddingValues ->

        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay empleados registrados.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users, key = { it.id }) { user ->
                    // Uso de nuestro componente limpio
                    UserCard(
                        user = user,
                        onEditClick = { onNavigateToEditUser(user.id) },
                        onDeleteClick = { userToDelete = user }
                    )
                }
            }
        }
    }

    // Renderizado condicional del diálogo limpio
    userToDelete?.let { user ->
        DeleteUserDialog(
            userName = user.fullName,
            onConfirm = {
                onDeactivateUser(user)
                userToDelete = null
            },
            onDismiss = { userToDelete = null }
        )
    }
}