package com.example.lab10

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lab10.data.SerieApiService
import com.example.lab10.data.SerieModel

//val LightBlue = Color(0xFF80D8FF)
//val MediumBlue = Color(0xFF40C4FF)
//val DarkBlue = Color(0xFF01579B)
//val DarkerBlue = Color(0xFF003C8F)
//val BlueGrey = Color(0xFF607D8B)

@Composable
fun ContenidoSeriesListado(navController: NavHostController, servicio: SerieApiService) {
    var listaSeries: SnapshotStateList<SerieModel> = remember { mutableStateListOf() }
    LaunchedEffect(Unit) {
        val listado = servicio.selectSeries()
        listaSeries.addAll(listado)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        Button(
            onClick = { navController.navigate("crearSerie") },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0xFF01579B))
        ) {
            Text("Crear Nueva Serie", color = Color.White)
        }

        LazyColumn {
            item {
                Row(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SERIE", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text("CATEGORÍA", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text("ACCIÓN", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                }
            }

            items(listaSeries) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.White)
                        .padding(12.dp)
                        .shadow(1.dp, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item.name, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Text(text = item.category, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { navController.navigate("serieVer/${item.id}") },
                        Modifier.weight(0.2f)
                    ) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Ver", tint = Color(0xFF00796B))
                    }
                    IconButton(
                        onClick = { navController.navigate("serieDel/${item.id}") },
                        Modifier.weight(0.2f)
                    ) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Eliminar", tint = Color(0xFFB71C1C))
                    }
                }
            }
        }
    }
}

@Composable
fun ContenidoSerieEditar(navController: NavHostController, servicio: SerieApiService, pid: Int = 0) {
    var id by remember { mutableIntStateOf(pid) }
    var name by remember { mutableStateOf("") }
    var release_date by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var grabar by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        if (id != 0) {
            val objSerie = servicio.selectSerie(id.toString())
            objSerie.body()?.let {
                name = it.name ?: ""
                release_date = it.release_date ?: ""
                rating = it.rating?.toString() ?: ""
                category = it.category ?: ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        listOf("ID (solo lectura)", "Name", "Release Date", "Rating", "Category").forEach { label ->
            TextField(
                value = when (label) {
                    "ID (solo lectura)" -> id.toString()
                    "Name" -> name
                    "Release Date" -> release_date
                    "Rating" -> rating
                    "Category" -> category
                    else -> ""
                },
                onValueChange = {
                    when (label) {
                        "Name" -> name = it
                        "Release Date" -> release_date = it
                        "Rating" -> rating = it
                        "Category" -> category = it
                    }
                },
                label = { Text(label) },
                singleLine = true,
                readOnly = label == "ID (solo lectura)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            )
        }

        Button(
            onClick = { grabar = true },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0xFF01579B))
        ) {
            Text("Guardar Cambios", color = Color.White)
        }
    }

    if (grabar) {
        val objSerie = SerieModel(id, name, release_date, rating.toInt(), category)
        LaunchedEffect(grabar) {
            try {
                if (id == 0) {
                    servicio.insertSerie(objSerie)
                } else {
                    servicio.updateSerie(id.toString(), objSerie)
                }
                grabar = false
                navController.navigate("series")
            } catch (e: Exception) {
                Log.e("Error", "Error al actualizar la serie: ${e.message}")
            }
        }
    }
}

@Composable
fun ContenidoSerieEliminar(navController: NavHostController, servicio: SerieApiService, id: Int) {
    var showDialog by remember { mutableStateOf(true) }
    var borrar by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            { showDialog = false }, {
                Button(
                    onClick = {
                        showDialog = false
                        borrar = true
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFFB71C1C))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            },
            title = { Text(text = "Confirmar Eliminación", color = Color(0xFF01579B), fontWeight = FontWeight.Bold) },
            text = { Text("¿Está seguro de eliminar la Serie?") },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("series")
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF757575))
                ) {
                    Text("Cancelar", color = Color.White)
                }
            },
        )
    }

    if (borrar) {
        LaunchedEffect(Unit) {
            servicio.deleteSerie(id.toString())
            borrar = false
            navController.navigate("series")
        }
    }
}

@Composable
fun ContenidoSerieCrear(navController: NavHostController, servicio: SerieApiService) {
    var name by remember { mutableStateOf("") }
    var release_date by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var crear by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        listOf("Nombre", "Fecha de Lanzamiento (AAAA-MM-DD)", "Rating", "Categoría").forEach { label ->
            TextField(
                value = when (label) {
                    "Nombre" -> name
                    "Fecha de Lanzamiento (AAAA-MM-DD)" -> release_date
                    "Rating" -> rating
                    "Categoría" -> category
                    else -> ""
                },
                onValueChange = {
                    when (label) {
                        "Nombre" -> name = it
                        "Fecha de Lanzamiento (AAAA-MM-DD)" -> release_date = it
                        "Rating" -> rating = it
                        "Categoría" -> category = it
                    }
                },
                label = { Text(label) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            )
        }

        Button(
            onClick = { crear = true },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0xFF01579B))
        ) {
            Text("Crear Serie", color = Color.White)
        }
    }

    if (crear) {
        val nuevaSerie = SerieModel(0, name, release_date, rating.toInt(), category)
        LaunchedEffect(crear) {
            try {
                servicio.insertSerie(nuevaSerie)
                crear = false
                navController.navigate("series")
            } catch (e: Exception) {
                Log.e("Error", "Error al crear la serie: ${e.message}")
            }
        }
    }
}
