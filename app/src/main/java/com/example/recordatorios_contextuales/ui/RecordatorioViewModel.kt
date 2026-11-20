package com.example.recordatorios_contextuales.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios_contextuales.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface RecordatoriosUiState {
    data object Cargando : RecordatoriosUiState
    data object Vacio : RecordatoriosUiState
    data class Datos(val recordatorios: List<Recordatorio>) : RecordatoriosUiState
    data class Error(val mensaje: String) : RecordatoriosUiState
}

class RecordatorioViewModel(
    private val repository: RecordatorioRepository = InMemoryRecordatorioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecordatoriosUiState>(RecordatoriosUiState.Cargando)
    val uiState: StateFlow<RecordatoriosUiState> = _uiState.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private var ultimoModoError = false
    private var ultimoModoVacio = false

    init {
        cargarRecordatorios()
    }

    fun cargarRecordatorios(simularError: Boolean = false, simularVacio: Boolean = false) {
        ultimoModoError = simularError
        ultimoModoVacio = simularVacio
        viewModelScope.launch {
            _uiState.value = RecordatoriosUiState.Cargando
            try {
                val resultado = repository.obtenerRecordatorios(
                    simularError = simularError,
                    simularVacio = simularVacio
                )
                _uiState.value = if (resultado.isEmpty()) {
                    RecordatoriosUiState.Vacio
                } else {
                    RecordatoriosUiState.Datos(resultado)
                }
            } catch (e: Exception) {
                _uiState.value = RecordatoriosUiState.Error(
                    e.message ?: "Ocurrió un error inesperado."
                )
            }
        }
    }

    fun reintentar() {
        cargarRecordatorios(simularError = false, simularVacio = false)
    }

    fun guardarRecordatorio(
        titulo: String,
        descripcion: String,
        categoria: Categoria,
        prioridad: Prioridad,
        ubicacion: String,
        aviso: String,
        onExito: () -> Unit
    ) {
        viewModelScope.launch {
            _guardando.value = true
            val nuevo = Recordatorio(
                id = UUID.randomUUID().toString(),
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                categoria = categoria,
                prioridad = prioridad,
                estado = EstadoRecordatorio.Pendiente,
                ubicacion = ubicacion.trim().ifEmpty { null },
                aviso = aviso.trim().ifEmpty { null }
            )
            repository.agregarRecordatorio(nuevo)
            _guardando.value = false
            cargarRecordatorios()
            onExito()
        }
    }

    fun obtenerRecordatorioPorId(id: String): Recordatorio? {
        val estadoActual = _uiState.value
        return if (estadoActual is RecordatoriosUiState.Datos) {
            estadoActual.recordatorios.find { it.id == id }
        } else {
            null
        }
    }

    fun alternarCompletado(id: String) {
        viewModelScope.launch {
            repository.alternarEstado(id)
            cargarRecordatorios(simularError = ultimoModoError, simularVacio = ultimoModoVacio)
        }
    }
}
