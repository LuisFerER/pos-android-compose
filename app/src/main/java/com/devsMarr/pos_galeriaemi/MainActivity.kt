package com.devsMarr.pos_galeriaemi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.devsMarr.pos_galeriaemi.ui.navigation.PosNavigation
import com.devsMarr.pos_galeriaemi.ui.presentation.category_form.CategoryFormScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.inventory.ProductListScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.pos.PosScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.product_form.ProductFormScreen
import com.devsMarr.pos_galeriaemi.ui.theme.PosGaleriaEmiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PosGaleriaEmiTheme {
                //PosScreen()

//                ProductListScreen(
//                    onNavigateToAddProduct = {
//                        Toast.makeText(this, "Navegar a Agregar", Toast.LENGTH_SHORT).show()
//                    },
//                    onNavigateToEditProduct = { id ->
//                        Toast.makeText(this, "Editar producto $id", Toast.LENGTH_SHORT).show()
//                    }
//                )

//                // Cambiamos temporalmente la pantalla principal para probar
//                CategoryFormScreen(
//                    onNavigateBack = {
//                        // Cuando se guarde con éxito, se ejecutará esto
//                        Toast.makeText(this, "¡Categoría guardada! (Navegando atrás...)", Toast.LENGTH_SHORT).show()
//                    }
//                )

                // Cambiamos temporalmente para probar el formulario de Productos
//                ProductFormScreen(
//                    onNavigateBack = {
//                        // Cuando se guarde con éxito, se ejecutará esto
//                        Toast.makeText(this, "¡Producto guardado! (Regresando...)", Toast.LENGTH_SHORT).show()
//                    }
//                )

                PosNavigation()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PosGaleriaEmiTheme {
        Greeting("Android")
    }
}