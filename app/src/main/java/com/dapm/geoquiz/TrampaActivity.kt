package com.dapm.geoquiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.dapm.geoquiz.databinding.ActivityTrampaBinding

class TrampaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrampaBinding
    private var rptaCorrecta = false

    companion object {
        const val EXTRA_RPTA_CORRECTA = "com.dapm.geoquiz.rpta_correcta"
        const val EXTRA_RPTA_MOSTRADA = "com.dapm.geoquiz.rpta_mostrada"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrampaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restaurar estado si es necesario
        if (savedInstanceState != null) {
            rptaCorrecta = savedInstanceState.getBoolean(EXTRA_RPTA_CORRECTA)
        } else {
            // Obtener la respuesta correcta
            rptaCorrecta = intent.getBooleanExtra(EXTRA_RPTA_CORRECTA, false)
        }

        // Mostrar la respuesta correcta en la vista
        val textoRespuesta = if (rptaCorrecta) {
            getString(R.string.toast_correcto)
        } else {
            getString(R.string.toast_incorrecto)
        }
        binding.textoTrampa.text = textoRespuesta

        // Al hacer trampa, retornamos el dato al Activity principal
        binding.botonHacerTrampa.setOnClickListener {
            val data = Intent()
            data.putExtra(EXTRA_RPTA_MOSTRADA, rptaCorrecta)
            setResult(RESULT_OK, data)
            finish()
        }
    }

    // Guardar el estado antes de que la actividad sea destruida
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean(EXTRA_RPTA_CORRECTA, rptaCorrecta)
    }

    // Crear un Intent para lanzar el TrampaActivity
    fun nuevoIntent(packageContext: Context, rptaCorrecta: Boolean): Intent {
        val intent = Intent(packageContext, TrampaActivity::class.java)
        intent.putExtra(EXTRA_RPTA_CORRECTA, rptaCorrecta)
        return intent
    }
}