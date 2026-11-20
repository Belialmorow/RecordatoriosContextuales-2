package com.example.recordatorios_contextuales.model

import kotlinx.coroutines.delay

interface RecordatorioRepository {
    suspend fun obtenerRecordatorios(simularError: Boolean = false, simularVacio: Boolean = false): List<Recordatorio>
    suspend fun obtenerPorId(id: String): Recordatorio?
    suspend fun agregarRecordatorio(nuevo: Recordatorio)
    suspend fun alternarEstado(id: String)
}

class InMemoryRecordatorioRepository : RecordatorioRepository {
    private val listaInterna = RecordatorioConfig.recordatoriosSimulados.toMutableList()

    override suspend fun obtenerRecordatorios(
        simularError: Boolean,
        simularVacio: Boolean
    ): List<Recordatorio> {
        delay(600)
        if (simularError) {
            throw Exception("No se pudieron cargar los recordatorios. Verifica tu conexión.")
        }
        if (simularVacio) {
            return emptyList()
        }
        return listaInterna.toList()
    }

    override suspend fun obtenerPorId(id: String): Recordatorio? {
        delay(200)
        return listaInterna.find { it.id == id }
    }

    override suspend fun agregarRecordatorio(nuevo: Recordatorio) {
        delay(400)
        listaInterna.add(0, nuevo)
    }

    override suspend fun alternarEstado(id: String) {
        val index = listaInterna.indexOfFirst { it.id == id }
        if (index != -1) {
            val actual = listaInterna[index]
            val nuevoEstado = if (actual.estado == EstadoRecordatorio.Completado) {
                EstadoRecordatorio.Pendiente
            } else {
                EstadoRecordatorio.Completado
            }
            listaInterna[index] = actual.copy(estado = nuevoEstado)
        }
    }
}
