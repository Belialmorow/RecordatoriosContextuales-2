package com.example.recordatorios_contextuales.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recordatorios_contextuales.model.*

object RutasApp {
    const val LISTADO = "listado"
    const val CREACION = "creacion"
    const val DETALLE = "detalle/{recordatorioId}"
    fun crearRutaDetalle(id: String) = "detalle/$id"
}

@Composable
fun Actividad2App(viewModel: RecordatorioViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RutasApp.LISTADO
    ) {
        composable(RutasApp.LISTADO) {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ListadoRecordatoriosScreen(
                uiState = uiState,
                onCrearRecordatorio = { navController.navigate(RutasApp.CREACION) },
                onRecordatorioClick = { id ->
                    navController.navigate(RutasApp.crearRutaDetalle(id))
                },
                onAlternarEstado = { id -> viewModel.alternarCompletado(id) },
                onReintentar = { viewModel.reintentar() },
                onSimularDatos = { viewModel.cargarRecordatorios(simularError = false, simularVacio = false) },
                onSimularVacio = { viewModel.cargarRecordatorios(simularError = false, simularVacio = true) },
                onSimularError = { viewModel.cargarRecordatorios(simularError = true, simularVacio = false) }
            )
        }

        composable(RutasApp.CREACION) {
            val guardando by viewModel.guardando.collectAsStateWithLifecycle()

            CrearRecordatorioScreen(
                guardando = guardando,
                onGuardar = { titulo, desc, cat, prio, ubic, aviso ->
                    viewModel.guardarRecordatorio(
                        titulo = titulo,
                        descripcion = desc,
                        categoria = cat,
                        prioridad = prio,
                        ubicacion = ubic,
                        aviso = aviso,
                        onExito = { navController.popBackStack() }
                    )
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        composable(
            route = RutasApp.DETALLE,
            arguments = listOf(navArgument("recordatorioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("recordatorioId") ?: ""
            val recordatorio = viewModel.obtenerRecordatorioPorId(id)

            DetalleRecordatorioScreen(
                recordatorio = recordatorio,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoRecordatoriosScreen(
    uiState: RecordatoriosUiState,
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (String) -> Unit,
    onAlternarEstado: (String) -> Unit,
    onReintentar: () -> Unit,
    onSimularDatos: () -> Unit,
    onSimularVacio: () -> Unit,
    onSimularError: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recordatorios Contextuales") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearRecordatorio) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo recordatorio")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSimularDatos,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text("Datos", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = onSimularVacio,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text("Vacío", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = onSimularError,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text("Error", style = MaterialTheme.typography.labelSmall)
                }
            }

            when (uiState) {
                is RecordatoriosUiState.Cargando -> VistaCargando()
                is RecordatoriosUiState.Vacio -> VistaVacia(onCrearClick = onCrearRecordatorio)
                is RecordatoriosUiState.Error -> VistaError(
                    mensaje = uiState.mensaje,
                    onReintentar = onReintentar
                )
                is RecordatoriosUiState.Datos -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                    ) {
                        items(uiState.recordatorios, key = { it.id }) { item ->
                            TarjetaRecordatorio(
                                recordatorio = item,
                                onRecordatorioClick = onRecordatorioClick,
                                onAlternarEstadoClick = onAlternarEstado
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRecordatorioScreen(
    guardando: Boolean,
    onGuardar: (String, String, Categoria, Prioridad, String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria>(Categoria.Estudio) }
    var prioridadSeleccionada by remember { mutableStateOf<Prioridad>(Prioridad.Media) }
    var ubicacion by remember { mutableStateOf("") }
    var aviso by remember { mutableStateOf("") }
    var intentoEnvio by remember { mutableStateOf(false) }

    val errorTitulo = RecordatorioConfig.validarTitulo(titulo)
    val errorDescripcion = RecordatorioConfig.validarDescripcion(descripcion)
    val formularioValido = errorTitulo == null && errorDescripcion == null

    val categorias = RecordatorioConfig.categoriasUnicas.toList()
    val prioridades = listOf(Prioridad.Alta, Prioridad.Media, Prioridad.Baja)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Recordatorio") },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoFormularioControlado(
                valor = titulo,
                onValorChange = {
                    titulo = it
                    intentoEnvio = true
                },
                etiqueta = "Título *",
                errorMensaje = if (intentoEnvio || titulo.isNotEmpty()) errorTitulo else null,
                textoAyuda = "Mínimo 4 caracteres sin espacios extremos"
            )

            CampoFormularioControlado(
                valor = descripcion,
                onValorChange = { descripcion = it },
                etiqueta = "Descripción (opcional)",
                errorMensaje = errorDescripcion,
                maxLineas = 3,
                textoAyuda = "${descripcion.length}/200 caracteres"
            )

            Text("Categoría", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categorias) { cat ->
                    FilterChip(
                        selected = categoriaSeleccionada == cat,
                        onClick = { categoriaSeleccionada = cat },
                        label = { Text(cat.nombre) }
                    )
                }
            }
            Text("Prioridad", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                prioridades.forEach { prio ->
                    FilterChip(
                        selected = prioridadSeleccionada == prio,
                        onClick = { prioridadSeleccionada = prio },
                        label = { Text(prio.etiqueta) }
                    )
                }
            }
            CampoFormularioControlado(
                valor = ubicacion,
                onValorChange = { ubicacion = it },
                etiqueta = "Ubicación / Lugar (opcional)",
                errorMensaje = null
            )
            CampoFormularioControlado(
                valor = aviso,
                onValorChange = { aviso = it },
                etiqueta = "Aviso / Horario (opcional)",
                errorMensaje = null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f),
                    enabled = !guardando
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        intentoEnvio = true
                        if (formularioValido) {
                            onGuardar(
                                titulo,
                                descripcion,
                                categoriaSeleccionada,
                                prioridadSeleccionada,
                                ubicacion,
                                aviso
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = formularioValido && !guardando
                ) {
                    Text(if (guardando) "Guardando..." else "Guardar")
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecordatorioScreen(
    recordatorio: Recordatorio?,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Recordatorio") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (recordatorio == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontró el recordatorio solicitado.")
            }
        } else {
            val colorCat = RecordatorioConfig.coloresPorCategoria[recordatorio.categoria] ?: Color.Gray
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = recordatorio.categoria.nombre,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .background(colorCat, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                Text(
                    text = recordatorio.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                Text(
                    text = "Descripción:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = recordatorio.descripcion.ifEmpty { "Sin descripción adicional." },
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Prioridad: ${recordatorio.prioridad.etiqueta}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Estado actual: ${recordatorio.estado.etiqueta}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (recordatorio.ubicacion != null) {
                    Text(
                        text = "Ubicación: ${recordatorio.ubicacion}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (recordatorio.aviso != null) {
                    Text(
                        text = "Aviso: ${recordatorio.aviso}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al listado")
                }
            }
        }
    }
}
