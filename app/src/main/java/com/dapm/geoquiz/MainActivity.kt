package com.dapm.geoquiz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dapm.geoquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val preguntaViewModel: PreguntaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar la primera pregunta
        mostrarPregunta()

        // Botón Verdadero
        binding.botonVerdadero.setOnClickListener {
            procesarRespuesta(usuarioDijo = true)
        }

        // Botón Falso
        binding.botonFalso.setOnClickListener {
            procesarRespuesta(usuarioDijo = false)
        }

        // Botón Siguiente
        binding.botonSiguiente.setOnClickListener {
            if (preguntaViewModel.indiceActual < preguntaViewModel.cantidadPreguntas - 1) {
                preguntaViewModel.moverAlSiguiente()
                mostrarPregunta()
            } else {
                Toast.makeText(this, "Ya no hay más preguntas.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Anterior
        binding.botonAnterior.setOnClickListener {
            if (preguntaViewModel.indiceActual > 0) {
                preguntaViewModel.moverAlAnterior()
                mostrarPregunta()
            }
        }

        // Botón Trampa
        binding.botonTrampa.setOnClickListener {
            val rptaCorrecta = preguntaViewModel.preguntaActual.esVerdadera
            val intentTrampa = TrampaActivity().nuevoIntent(this, rptaCorrecta)
            startActivityForResult(intentTrampa, 1)
        }
    }

    private fun mostrarPregunta() {
        val pregunta = preguntaViewModel.preguntaActual
        binding.textoPregunta.setText(pregunta.textoPregunta)
        actualizarHabilitadoRespuestas()
    }

    private fun procesarRespuesta(usuarioDijo: Boolean) {
        // Comparar la respuesta seleccionada con la respuesta correcta
        val correcta = preguntaViewModel.preguntaActual.esVerdadera == usuarioDijo

        // Mostrar el resultado
        if (correcta) {
            preguntaViewModel.incrementarRespuestasCorrectas()
            Toast.makeText(this, "Respuesta correcta!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Respuesta incorrecta", Toast.LENGTH_SHORT).show()
        }

        // Marcar la pregunta como respondida y deshabilitar los botones
        preguntaViewModel.marcarPreguntaRespondida()
        actualizarHabilitadoRespuestas()

        // Si el usuario ya respondió todas las preguntas
        if (preguntaViewModel.totalRespondidas == preguntaViewModel.cantidadPreguntas) {
            // Calcular el porcentaje de respuestas correctas
            val porcentaje = (preguntaViewModel.totalCorrectas * 100) / preguntaViewModel.cantidadPreguntas

            // Mostrar el porcentaje
            Toast.makeText(
                this,
                "¡Completado! Aciertos: ${preguntaViewModel.totalCorrectas}/${preguntaViewModel.cantidadPreguntas} ($porcentaje%)",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun actualizarHabilitadoRespuestas() {
        // Deshabilitar los botones de respuesta si ya fue respondida
        val yaRespondida = preguntaViewModel.respondida[preguntaViewModel.indiceActual]
        binding.botonVerdadero.isEnabled = !yaRespondida
        binding.botonFalso.isEnabled = !yaRespondida
    }

    // Recupera los resultados del Activity Trampa
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val rptaMostrada = data?.getBooleanExtra(TrampaActivity.EXTRA_RPTA_MOSTRADA, false) ?: false
            // Usar el valor retornado para validar si el usuario hizo trampa
            if (rptaMostrada) {
                Toast.makeText(this, "El usuario hizo trampa", Toast.LENGTH_SHORT).show()
            }
        }
    }
}