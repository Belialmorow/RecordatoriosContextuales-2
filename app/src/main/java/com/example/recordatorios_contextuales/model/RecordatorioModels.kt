package com.example.recordatorios_contextuales.model

import androidx.compose.ui.graphics.Color

sealed class Categoria(val nombre: String) {
    data object Trabajo : Categoria("Trabajo")
    data object Estudio : Categoria("Estudio")
    data object Personal : Categoria("Personal")
    data object Salud : Categoria("Salud")
    data object Ocio : Categoria("Ocio")
}

sealed class Prioridad(val etiqueta: String) {
    data object Baja : Prioridad("Baja")
    data object Media : Prioridad("Media")
    data object Alta : Prioridad("Alta")
}

sealed class EstadoRecordatorio(val etiqueta: String) {
    data object Pendiente : EstadoRecordatorio("Pendiente")
    data object Completado : EstadoRecordatorio("Completado")
    data object Vencido : EstadoRecordatorio("Vencido")
}

data class Recordatorio(
    val id: String,
    val titulo: String,
    val descripcion: String = "",
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio,
    val ubicacion: String? = null,
    val aviso: String? = null
)

object RecordatorioConfig {
    val categoriasUnicas: Set<Categoria> = setOf(
        Categoria.Trabajo,
        Categoria.Estudio,
        Categoria.Personal,
        Categoria.Salud,
        Categoria.Ocio
    )

    val coloresPorCategoria: Map<Categoria, Color> = mapOf(
        Categoria.Trabajo to Color(0xFF1565C0),
        Categoria.Estudio to Color(0xFF6A1B9A),
        Categoria.Personal to Color(0xFF2E7D32),
        Categoria.Salud to Color(0xFFC62828),
        Categoria.Ocio to Color(0xFFEF6C00)
    )

    val recordatoriosSimulados: List<Recordatorio> = listOf(
        Recordatorio(
            id = "1",
            titulo = "Entregar Actividad 1 Mobile",
            descripcion = "Subir el PDF con las 3 capturas y el enlace al repositorio.",
            categoria = Categoria.Estudio,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Pendiente,
            aviso = "Hoy 23:59 hs"
        ),
        Recordatorio(
            id = "2",
            titulo = "Completar curso solicitado",
            descripcion = "Finalizar los módulos obligatorios de capacitación interna.",
            categoria = Categoria.Trabajo,
            prioridad = Prioridad.Media,
            estado = EstadoRecordatorio.Completado,
            ubicacion = "Sala virtual Zoom"
        ),
        Recordatorio(
            id = "3",
            titulo = "Hora con psicólogo",
            descripcion = "Sesión de seguimiento mensual.",
            categoria = Categoria.Salud,
            prioridad = Prioridad.Baja,
            estado = EstadoRecordatorio.Pendiente,
            ubicacion = "Centro Médico Integramédica",
            aviso = "Mañana 10:00 hs"
        ),
        Recordatorio(
            id = "4",
            titulo = "Pagar servicio de internet",
            descripcion = "Realizar transferencia antes del corte de facturación.",
            categoria = Categoria.Personal,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Vencido
        ),
        Recordatorio(
            id = "5",
            titulo = "Junta con amigos",
            descripcion = "Tarde de descanso y partida grupal.",
            categoria = Categoria.Ocio,
            prioridad = Prioridad.Baja,
            estado = EstadoRecordatorio.Pendiente,
            ubicacion = "Casa Miguel o Discord con juegos"
        )
    )

    fun validarTitulo(titulo: String): String? {
        val limpio = titulo.trim()
        return when {
            limpio.isEmpty() -> "El título es obligatorio."
            limpio.length < 4 -> "El título debe tener al menos 4 caracteres."
            else -> null
        }
    }

    fun validarDescripcion(descripcion: String): String? {
        return if (descripcion.length > 200) {
            "La descripción no puede superar los 200 caracteres."
        } else {
            null
        }
    }
}
