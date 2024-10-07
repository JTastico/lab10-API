package com.example.lab10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab10.ui.theme.Lab10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab10Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") { PageInicio(navController) }
        composable("series") { ContenidoSeriesListado(navController) }
        composable("serieNuevo") { ContenidoSerieEditar(navController, 0) }
        composable("serieVer/{id}") { backStackEntry ->
            ContenidoSerieEditar(navController, backStackEntry.arguments?.getInt("id") ?: 0)
        }
        composable("serieDel/{id}") { backStackEntry ->
            ContenidoSerieEliminar(navController, backStackEntry.arguments?.getInt("id") ?: 0)
        }
        composable("seriesApp") { SeriesApp() }
    }
}

fun ContenidoSerieEliminar(navController: NavHostController, servicio: Int) {

}

@Composable
fun PageInicio(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("series") }) {
            Text(text = "Ir a Listado de Series")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("serieNuevo") }) {
            Text(text = "Agregar Nueva Serie")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("serieVer/1") }) { // Cambia "1" por el id deseado
            Text(text = "Ver Serie")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("serieDel/1") }) { // Cambia "1" por el id deseado
            Text(text = "Eliminar Serie")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Botón que redirige a SeriesApp
        Button(onClick = { navController.navigate("seriesApp") }) {
            Text(text = "Ir a Series App")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab10Theme {
        PageInicio(navController = rememberNavController())
    }
}
