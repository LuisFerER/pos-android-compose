package com.devsMarr.pos_galeriaemi.ui.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Storefront
// import com.devsMarr.pos_galeriaemi.R // Tu import de recursos (R)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit // Navegar al Punto de Venta cuando entre correctamente
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Observar si el login fue exitoso para cambiar de pantalla
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (!uiState.hasUsers) {
                // MODO A: Crear el primer administrador
                CreateMasterAdminForm(uiState, viewModel)
            } else {
                // MODO B: El Login normal con ComboBox
                LoginForm(uiState, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(uiState: LoginUiState, viewModel: LoginViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // --- EL LOGO DEL NEGOCIO (Placeholder) ---
        // Por ahora usamos el ícono por defecto de la app de Android.
        // Más adelante esto leerá la imagen guardada en Configuración.
        Icon(
            imageVector = Icons.Default.Storefront,
            contentDescription = "Logo del Negocio",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .size(140.dp) // <-- Subimos de 100 a 140
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(28.dp) // <-- Ajustamos el padding para que el icono se vea bien
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- LA TARJETA DE LOGIN ---
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedUser?.firstName ?: "Selecciona un usuario",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Usuario") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        uiState.usersList.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.firstName) },
                                onClick = {
                                    viewModel.onUserSelected(user)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.passwordInput,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.errorMessage != null) {
                    Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Entrar", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun CreateMasterAdminForm(uiState: LoginUiState, viewModel: LoginViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // --- EL LOGO DEL NEGOCIO (Placeholder) ---
        Icon(
            imageVector = Icons.Default.Storefront,
            contentDescription = "Logo del Negocio",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .size(140.dp) // <-- Subimos de 100 a 140
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(28.dp) // <-- Ajustamos el padding para que el icono se vea bien
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("¡Bienvenido!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Vamos a crear la cuenta de Administrador.")

                OutlinedTextField(
                    value = uiState.newFirstName,
                    onValueChange = { viewModel.onNewAdminDataChange(it, uiState.newLastName, uiState.newPhone, uiState.newAdminPassword) },
                    label = { Text("Nombre(s)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.newLastName,
                    onValueChange = { viewModel.onNewAdminDataChange(uiState.newFirstName, it, uiState.newPhone, uiState.newAdminPassword) },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.newAdminPassword,
                    onValueChange = { viewModel.onNewAdminDataChange(uiState.newFirstName, uiState.newLastName, uiState.newPhone, it) },
                    label = { Text("Contraseña Maestra") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.errorMessage != null) {
                    Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = { viewModel.createMasterAdmin() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Crear Administrador")
                }
            }
        }
    }
}