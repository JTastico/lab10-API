package com.example.lab10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab10.data.RetrofitInstance // Importa el objeto RetrofitInstance
import com.example.lab10.data.SerieApiService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtén la instancia de SerieApiService desde RetrofitInstance
        val servicio = RetrofitInstance.api


        setContent {
            MainScreen(servicio)
        }
    }
}

@Composable
fun MainScreen(servicio: SerieApiService) {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = "series"
        ) {
            // MainActivity.kt
            composable("series") {
                ContenidoSeriesListado(navController = navController, servicio = servicio) // Mantén este si `ContenidoSeriesListado` usa `servicio`
            }
            composable(
                "serieVer/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                ContenidoSerieEditar(navController = navController, servicio = servicio, pid = id)
            }
            composable(
                "serieDel/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                ContenidoSerieEliminar(navController = navController, servicio = servicio, id = id)
            }
            composable("crearSerie") {
                ContenidoSerieCrear(navController = navController, servicio = servicio)
            }
        }
    }
}
