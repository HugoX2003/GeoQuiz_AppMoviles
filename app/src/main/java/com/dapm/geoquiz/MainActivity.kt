package com.dapm.geoquiz

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.dapm.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

import android.util.Log
import android.widget.Toast
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    // Claves para guardar/restaurar estado
    private companion object {
        const val KEY_INDICE = "indiceActual"
        const val KEY_RESPONDIDA = "respondida"
        const val KEY_TOTAL_CORRECTAS = "totalCorrectas"
        const val KEY_TOTAL_RESPONDIDAS = "totalRespondidas"
    }

    // Constante para el tag de los logs
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    // Lista de preguntas con su respectiva respuesta correcta
    private val preguntas = listOf(
        Pregunta(R.string.pregunta_australia, true),
        Pregunta(R.string.pregunta_oceanos, true),
        Pregunta(R.string.pregunta_medio_oriente, false),
        Pregunta(R.string.pregunta_africa, false),
        Pregunta(R.string.pregunta_america, true),
        Pregunta(R.string.pregunta_asia, true)
    )

    // Índice actual de la pregunta
    private var indiceActual: Int = 0

    // Estado por pregunta
    private val respondida = BooleanArray(preguntas.size) { false }
    private var totalCorrectas = 0
    private var totalRespondidas = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ---------- Restaurar estado si existe ----------
        if (savedInstanceState != null) {
            indiceActual = savedInstanceState.getInt(KEY_INDICE, 0)
            totalCorrectas = savedInstanceState.getInt(KEY_TOTAL_CORRECTAS, 0)
            totalRespondidas = savedInstanceState.getInt(KEY_TOTAL_RESPONDIDAS, 0)
            savedInstanceState.getBooleanArray(KEY_RESPONDIDA)?.let { arr ->
                // Copiamos con seguridad por si el tamaño difiere
                val n = min(arr.size, respondida.size)
                System.arraycopy(arr, 0, respondida, 0, n)
            }
        }

        // Mostrar la primera pregunta al iniciar
        mostrarPregunta()

        // Responder Verdadero
        binding.botonVerdadero.setOnClickListener {
            procesarRespuesta(usuarioDijo = true)
        }

        // Responder Falso
        binding.botonFalso.setOnClickListener {
            procesarRespuesta(usuarioDijo = false)
        }

        // Botón siguiente
        binding.botonSiguiente.setOnClickListener {
            indiceActual = (indiceActual + 1) % preguntas.size
            mostrarPregunta()
        }

        // Botón anterior
        binding.botonAnterior.setOnClickListener {
            indiceActual = if (indiceActual == 0) preguntas.size - 1 else indiceActual - 1
            mostrarPregunta()
        }
    }

    // Guardar estado ante cambios de configuración (rotación, etc.)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDICE, indiceActual)
        outState.putBooleanArray(KEY_RESPONDIDA, respondida)
        outState.putInt(KEY_TOTAL_CORRECTAS, totalCorrectas)
        outState.putInt(KEY_TOTAL_RESPONDIDAS, totalRespondidas)
    }

    // Mostrar la pregunta actual
    private fun mostrarPregunta() {
        val pregunta = preguntas[indiceActual]
        binding.textoPregunta.setText(pregunta.textoPregunta)
        actualizarHabilitadoRespuestas()
    }

    private fun actualizarHabilitadoRespuestas() {
        val yaRespondida = respondida[indiceActual]
        binding.botonVerdadero.isEnabled = !yaRespondida
        binding.botonFalso.isEnabled = !yaRespondida
    }

    private fun procesarRespuesta(usuarioDijo: Boolean) {
        // Evita doble click si ya fue respondida
        if (respondida[indiceActual]) return

        val correcta = preguntas[indiceActual].esVerdadera == usuarioDijo
        if (correcta) totalCorrectas++
        respondida[indiceActual] = true
        totalRespondidas++

        // Feedback inmediato
        showTopSnack(
            if(correcta) getString(R.string.toast_correcto) else getString(R.string.toast_incorrecto),
            if (correcta) Color.parseColor("#2E7D32") else Color.parseColor("#C62828")
        )

        // Deshabilita botones para esta pregunta
        actualizarHabilitadoRespuestas()

        // Si ya respondió todas, mostrar Toast con porcentaje
        if (totalRespondidas == preguntas.size) {
            val porcentaje = (totalCorrectas * 100) / preguntas.size
            Toast.makeText(
                this,
                "¡Completado! Aciertos: $totalCorrectas/${preguntas.size} ($porcentaje%)",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Función reutilizable para mostrar Snackbars arriba
    private fun showTopSnack(message: String, bgColor: Int) {
        val sb = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        val sbView = sb.view

        // Configuramos color de fondo y texto
        sb.setBackgroundTint(bgColor)
        sb.setTextColor(Color.WHITE)

        // Ajustamos los parámetros de layout del Snackbar para que se muestre en la parte superior
        val params = sbView.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        sbView.layoutParams = params

        sb.show()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart fue llamado: La actividad está visible pero no interactuable")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume fue llamado: La actividad está activa y lista para interactuar")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause fue llamado: La actividad pierde el foco, pero sigue visible")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop fue llamado: La actividad ya no es visible")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy fue llamado")
    }
}