package com.dapm.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class PreguntaViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val preguntas = listOf(
        Pregunta(R.string.pregunta_australia, true),
        Pregunta(R.string.pregunta_oceanos, true),
        Pregunta(R.string.pregunta_medio_oriente, false),
        Pregunta(R.string.pregunta_africa, false),
        Pregunta(R.string.pregunta_america, true),
        Pregunta(R.string.pregunta_asia, true)
    )

    var indiceActual: Int = savedStateHandle.get<Int>(KEY_INDICE) ?: 0
    val respondida = BooleanArray(preguntas.size) { false }
    var totalCorrectas = 0
    var totalRespondidas = 0

    val preguntaActual: Pregunta
        get() = preguntas[indiceActual]

    val cantidadPreguntas: Int
        get() = preguntas.size

    fun moverAlSiguiente() {
        if (indiceActual < preguntas.size - 1) {
            indiceActual++
            savedStateHandle.set(KEY_INDICE, indiceActual)
        }
    }

    fun moverAlAnterior() {
        if (indiceActual > 0) {
            indiceActual--
            savedStateHandle.set(KEY_INDICE, indiceActual)
        }
    }

    fun marcarPreguntaRespondida() {
        respondida[indiceActual] = true
        totalRespondidas++
    }

    fun incrementarRespuestasCorrectas() {
        totalCorrectas++
    }

    companion object {
        const val KEY_INDICE = "indiceActual"
    }
}
