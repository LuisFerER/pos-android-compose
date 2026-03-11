package com.devsMarr.pos_galeriaemi.ui.presentation.user_form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devsMarr.pos_galeriaemi.ui.presentation.user_form.PasswordFormField
import com.devsMarr.pos_galeriaemi.ui.presentation.user_form.RoleSelectorGroup
import com.devsMarr.pos_galeriaemi.ui.presentation.user_form.UserFormField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    viewModel: UserFormViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Observamos si se guardó con éxito para cerrar la pantalla
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.id == null) "Nuevo Empleado" else "Editar Empleado",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- MENSAJE DE ERROR ---
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            UserFormField(
                value = uiState.firstName,
                onValueChange = { viewModel.onFirstNameChange(it) },
                label = "Nombre(s) *"
            )

            UserFormField(
                value = uiState.lastName,
                onValueChange = { viewModel.onLastNameChange(it) },
                label = "Apellidos *"
            )

            UserFormField(
                value = uiState.phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = "Teléfono",
                keyboardType = KeyboardType.Phone,
                capitalization = KeyboardCapitalization.None
            )

            PasswordFormField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = if (uiState.id == null) "Contraseña *" else "Nueva Contraseña (Opcional)"
            )

            RoleSelectorGroup(
                selectedRole = uiState.role,
                onRoleSelected = { viewModel.onRoleChange(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- BOTÓN GUARDAR ---
            Button(
                onClick = { viewModel.saveUser() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Empleado", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
            }
        }
    }
}