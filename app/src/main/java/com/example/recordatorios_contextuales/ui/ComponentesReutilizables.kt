package com.example.recordatorios_contextuales.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recordatorios_contextuales.model.Recordatorio
import com.example.recordatorios_contextuales.model.RecordatorioConfig

@Composable
fun TarjetaRecordatorio(
    recordatorio: Recordatorio,
    onRecordatorioClick: (String) -> Unit,
    onAlternarEstadoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorCategoria = RecordatorioConfig.coloresPorCategoria[recordatorio.categoria] ?: Color.Gray

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onRecordatorioClick(recordatorio.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recordatorio.categoria.nombre,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .background(colorCategoria, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text(
                    text = "Prioridad: ${recordatorio.prioridad.etiqueta}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recordatorio.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado: ${recordatorio.estado.etiqueta}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                TextButton(
                    onClick = { onAlternarEstadoClick(recordatorio.id) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text("Cambiar estado", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun VistaCargando(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(12.dp))
            Text("Cargando recordatorios...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun VistaVacia(
    onCrearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No hay recordatorios registrados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Presiona el botón para agregar tu primer recordatorio contextual.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onCrearClick) {
                Text("Crear recordatorio")
            }
        }
    }
}

@Composable
fun VistaError(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Error al obtener datos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onReintentar) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
fun CampoFormularioControlado(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    errorMensaje: String?,
    modifier: Modifier = Modifier,
    maxLineas: Int = 1,
    textoAyuda: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            label = { Text(etiqueta) },
            isError = errorMensaje != null,
            maxLines = maxLineas,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (errorMensaje != null) {
                    Text(text = errorMensaje, color = MaterialTheme.colorScheme.error)
                } else if (textoAyuda != null) {
                    Text(text = textoAyuda)
                }
            }
        )
    }
}
