package com.example.lab10

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lab10.data.SerieApiService
import com.example.lab10.data.SerieModel
import com.example.lab10.ui.theme.*

@Composable
fun ContenidoSeriesListado(navController: NavHostController, servicio: SerieApiService) {
    var listaSeries: SnapshotStateList<SerieModel> = remember { mutableStateListOf() }
    LaunchedEffect(Unit) {
        val listado = servicio.selectSeries()
        listado.forEach { listaSeries.add(it) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { navController.navigate("crearSerie") },
            modifier = Modifier
                .padding(16.dp)
                .background(LightBlue),
            colors = ButtonDefaults.buttonColors(MediumBlue)
        ) {
            Text("Crear Nueva Serie", color = Color.White)
        }

        LazyColumn {
            item {
                Row(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SERIE", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))
                    Text("Categoria", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))
                    Text("Accion", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
                }
            }

            items(listaSeries) { item ->
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                    Text(text = item.category, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))
                    IconButton(
                        onClick = { navController.navigate("serieVer/${item.id}") },
                        Modifier.weight(0.2f)
                    ) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Ver", tint = DarkBlue)
                    }
                    IconButton(
                        onClick = { navController.navigate("serieDel/${item.id}") },
                        Modifier.weight(0.3f)
                    ) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Eliminar", tint = DarkBlue)
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = id.toString(),
            onValueChange = {},
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.background(MediumBlue.copy(alpha = 0.1f))
        )
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        TextField(
            value = release_date,
            onValueChange = { release_date = it },
            label = { Text("Release Date") },
            singleLine = true,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        TextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Rating") },
            singleLine = true,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            singleLine = true,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(
            onClick = { grabar = true },
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(DarkBlue)
        ) {
            Text("Grabar", color = Color.White)
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
                    colors = ButtonDefaults.buttonColors(DarkBlue)
                ) {
                    Text("Aceptar", color = Color.White)
                }
            },
            title = {
                Text(
                    text = "Confirmar Eliminación",
                    fontWeight = FontWeight.Bold,
                    color = MediumBlue
                )
            },
            text = {
                Text(
                    "¿Está seguro de eliminar la Serie?",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("series")
                    },
                    colors = ButtonDefaults.buttonColors(Color.Gray)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = Color.White,
        )
    }

    if (borrar) {
        LaunchedEffect(Unit) {
            try {
                servicio.deleteSerie(id.toString())
                navController.navigate("series")
            } catch (e: Exception) {
                Log.e("Error", "Error al eliminar la serie: ${e.message}")
            } finally {
                borrar = false
            }
        }
    }
}
