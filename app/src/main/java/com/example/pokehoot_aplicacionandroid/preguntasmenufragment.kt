package com.example.pokehoot_aplicacionandroid

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.pokehoot_aplicacionandroid.Managers.SocketManager
import com.example.pokehoot_aplicacionandroid.databinding.PreguntasmenuBinding
import io.socket.client.Socket
import org.json.JSONObject

class preguntasmenufragment : Fragment() {
    private var _binding: PreguntasmenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var socket: Socket
    private var codigoSala: String = ""
    private var modo: String = "multijugador" // "multijugador" o "solitario"
    private var puntuacion: Int = 0
    private var rondaActual: Int = 0
    private var totalRondas: Int = 10
    private var timerActual: CountDownTimer? = null
    private var yaRespondio: Boolean = false
    private var preguntasCorrectas: Int = 0
    private var rachaActual: Int = 0
    private var preguntasIncorrectas: Int = 0

    private var listenerPreguntaMulti: io.socket.emitter.Emitter.Listener? = null
    private lateinit var opciones: List<CardView>
    private lateinit var textoOpciones: List<android.widget.TextView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PreguntasmenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codigoSala = arguments?.getString("codigoSala") ?: ""
        modo = arguments?.getString("modo") ?: "multijugador"

        opciones = listOf(binding.optionYellow, binding.optionRed, binding.optionBlue, binding.optionGreen)
        textoOpciones = opciones.map { it.getChildAt(0) as android.widget.TextView }
        deshabilitarBotones()
        socket = SocketManager.conectar()
        configurarEventosSocket()
        configurarBotonesOpciones()

        // Si es solitario iniciamos la partida directamente
        if (modo == "solitario") {
            val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
            val userId = prefs.getInt("userId", 0)
            socket.emit("solitario:iniciar", JSONObject().put("userId", userId))
            // En solitario no hay rondas fijas, ocultamos el contador
            binding.tvRonda.text = "∞"
        }else{
            val primeraPreguntaStr = arguments?.getString("primeraPregunta")
            if (!primeraPreguntaStr.isNullOrEmpty()) {
                mostrarPregunta(JSONObject(primeraPreguntaStr))
            }
        }
    }

    private fun configurarBotonesOpciones() {
        opciones.forEachIndexed { indice, opcion ->
            opcion.setOnClickListener {
                if (yaRespondio) return@setOnClickListener
                yaRespondio = true
                timerActual?.cancel()
                deshabilitarBotones()

                if (modo == "solitario") {
                    Log.d("nose", "Enviando índice: $indice")
                    socket.emit("solitario:responder", JSONObject()
                        .put("indiceRespuesta", indice))
                } else {
                    socket.emit("responder", JSONObject()
                        .put("codigoSala", codigoSala)
                        .put("indiceRespuesta", indice))
                }
            }
        }
    }

    private fun configurarEventosSocket() {
        if (modo == "solitario") {
            configurarEventosSolitario()
        } else {
            configurarEventosMultijugador()
        }
    }

    // ─── EVENTOS MULTIJUGADOR ────────────────────────────────────────────────

    private fun configurarEventosMultijugador() {
        listenerPreguntaMulti = io.socket.emitter.Emitter.Listener { args ->
            val data = args[0] as JSONObject
            Log.d("PREGUNTA", "Nueva pregunta recibida: $data")
            activity?.runOnUiThread { mostrarPregunta(data) }
        }
        socket.on("pregunta", listenerPreguntaMulti)

        socket.on("resultado_ronda") { args ->
            val data = args[0] as JSONObject
            activity?.runOnUiThread { mostrarResultadoRondaMulti(data) }
        }

        socket.on("fin_partida") { args ->
            val data = args[0] as JSONObject
            activity?.runOnUiThread { guardarEstadisticasYNavegar(data) }
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "Desconectado del servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ─── EVENTOS SOLITARIO ───────────────────────────────────────────────────

    private fun configurarEventosSolitario() {

        // Partida iniciada
        socket.on("solitario:iniciado") { _ ->
            activity?.runOnUiThread {
            }
        }

        // Recibir pregunta solitario
        socket.on("solitario:pregunta") { args ->
            val data = args[0] as JSONObject
            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                rondaActual = data.getInt("ronda")
                puntuacion = data.getInt("puntuacion")
                rachaActual = data.getInt("racha")
                binding.tvRonda.text = "Ronda $rondaActual"
                mostrarPregunta(data)
            }
        }

        // Resultado correcto en solitario
        socket.on("solitario:resultado") { args ->
            val data = args[0] as JSONObject
            Log.d("nose", "Solitario:resultado recibido: $data")
            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                val respuestaCorrecta = data.getInt("respuestaCorrecta")
                val puntos = data.getInt("puntos")
                val bonusRacha = data.getInt("bonusRacha")
                puntuacion = data.getInt("puntuacion")
                rachaActual = data.getInt("racha")
                preguntasCorrectas++

                // Colorear opciones
                opciones.forEachIndexed { index, opcion ->
                    opcion.setCardBackgroundColor(
                        if (index == respuestaCorrecta) 0xFF4CAF50.toInt()
                        else 0xFFF44336.toInt()
                    )
                }

                // Mostrar feedback
                binding.tvPuntuacion.text = puntuacion.toString()
                binding.tvFeedbackIcono.text = "✓"
                binding.tvFeedbackIcono.setBackgroundResource(R.drawable.bg_feedback_correcto)
                binding.tvFeedbackTexto.text = "¡Correcto!"
                binding.tvPuntosGanados.text = "+$puntos"
                binding.tvFeedbackRespuesta.text = "La respuesta era: ${textoOpciones[respuestaCorrecta].text}"

                if (rachaActual >= 3) {
                    binding.tvBonusRacha.visibility = View.VISIBLE
                    binding.tvBonusRacha.text = "🔥 ¡Racha de $rachaActual! +$bonusRacha bonus"
                } else {
                    binding.tvBonusRacha.visibility = View.GONE
                }

                binding.layoutFeedback.visibility = View.VISIBLE
            }
        }

        // Fin solitario — perdiste
        // Fin solitario — perdiste
        socket.on("solitario:fin") { args ->
            val data = args[0] as JSONObject
            Log.d("nose", "Solitario:fin recibido: $data")
            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread  // ✅ fragment ya destruido

                val respuestaCorrecta = data.getInt("respuestaCorrecta")

                opciones.forEachIndexed { index, opcion ->
                    opcion.setCardBackgroundColor(
                        if (index == respuestaCorrecta) 0xFF4CAF50.toInt()
                        else 0xFFF44336.toInt()
                    )
                }

                binding.tvFeedbackIcono.text = "✗"
                binding.tvFeedbackIcono.setBackgroundResource(R.drawable.bg_feedback_incorrecto)
                binding.tvFeedbackTexto.text = if (data.getString("motivo") == "tiempo_agotado")
                    "¡Tiempo agotado!" else "¡Incorrecto!"
                binding.tvPuntosGanados.text = "+0"
                binding.tvFeedbackRespuesta.text = "La respuesta era: ${textoOpciones[respuestaCorrecta].text}"
                binding.tvBonusRacha.visibility = View.GONE
                binding.layoutFeedback.visibility = View.VISIBLE

                // ✅ Guardamos los datos antes del delay
                val puntuacionFinal = data.getInt("puntuacion")
                val rachaFinal = data.getInt("racha")
                val correctasFinal = data.getInt("rondas") - 1

                view?.postDelayed({
                    if (_binding == null) return@postDelayed  // ✅ segunda comprobación tras el delay
                    val bundle = Bundle().apply {
                        putInt("puntuacion", puntuacionFinal)
                        putInt("racha", rachaFinal)
                        putInt("correctas", correctasFinal)
                    }
                    findNavController().navigate(R.id.perdidofragmento, bundle)
                }, 2000)
            }
        }

        socket.on("solitario:error") { args ->
            val data = args[0] as JSONObject
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), data.getString("mensaje"), Toast.LENGTH_SHORT).show()
            }
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "Desconectado del servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ─── FUNCIONES COMPARTIDAS ───────────────────────────────────────────────

    private fun mostrarPregunta(data: JSONObject) {
        yaRespondio = false
        timerActual?.cancel()
        habilitarBotones()

        val pregunta = data.getString("pregunta")
        val imagenUrl = data.getString("imagenUrl")
        val silueta = data.getBoolean("silueta")
        val tiempoLimite = data.getInt("tiempoLimite")
        val opcionesArray = data.getJSONArray("opciones")
        val tipo = data.getString("tipo")

        if (modo == "multijugador") {
            rondaActual = data.getInt("ronda")
            totalRondas = data.getInt("totalRondas")
            binding.tvRonda.text = "$rondaActual/$totalRondas"
        }

        binding.tvPregunta.text = pregunta
        binding.tvPuntuacion.text = puntuacion.toString()

        binding.tvTipoPregunta.text = when (tipo) {
            "nombre" -> "¿Qué Pokémon es?"
            "tipo" -> "¿De qué tipo es?"
            "numero" -> "¿Qué número de Pokédex tiene?"
            else -> "Pregunta"
        }

        if (silueta) {
            Glide.with(this).load(imagenUrl).into(binding.ivPokemon)
            val matriz = ColorMatrix().apply { setSaturation(0f) }
            binding.ivPokemon.colorFilter = ColorMatrixColorFilter(matriz)
        } else {
            binding.ivPokemon.colorFilter = null
            Glide.with(this).load(imagenUrl).into(binding.ivPokemon)
        }

        val coloresOriginales = listOf(0xFFFFC107.toInt(), 0xFFF44336.toInt(), 0xFF2196F3.toInt(), 0xFF4CAF50.toInt())
        for (i in 0 until opcionesArray.length()) {
            textoOpciones[i].text = opcionesArray.getString(i)
            opciones[i].setCardBackgroundColor(coloresOriginales[i])
            opciones[i].isClickable = true
        }

        binding.layoutFeedback.visibility = View.GONE
        arrancarTimer(tiempoLimite)
    }

    private fun arrancarTimer(segundos: Int) {
        binding.tvTimer.text = segundos.toString()
        binding.progressTimer.max = 100
        binding.progressTimer.progress = 100

        timerActual = object : CountDownTimer(segundos * 1000L, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val progreso = ((millisUntilFinished.toFloat() / (segundos * 1000f)) * 100).toInt()
                binding.progressTimer.progress = progreso
                binding.tvTimer.text = (millisUntilFinished / 1000 + 1).toString()
            }
            override fun onFinish() {
                binding.tvTimer.text = "0"
                binding.progressTimer.progress = 0
            }
        }.start()
    }

    private fun mostrarResultadoRondaMulti(data: JSONObject) {
        timerActual?.cancel()
        val respuestaCorrecta = data.getInt("respuestaCorrecta")
        val resultados = data.getJSONArray("resultados")
        val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val miNombre = prefs.getString("username", "") ?: ""

        var correcto = false
        var puntos = 0
        for (i in 0 until resultados.length()) {
            val resultado = resultados.getJSONObject(i)
            if (resultado.getString("nombre") == miNombre) {
                correcto = resultado.getBoolean("correcto")
                puntos = resultado.getInt("puntos")
                puntuacion = resultado.getInt("puntuacionTotal")
                break
            }
        }

        opciones.forEachIndexed { index, opcion ->
            opcion.setCardBackgroundColor(
                if (index == respuestaCorrecta) 0xFF4CAF50.toInt()
                else 0xFFF44336.toInt()
            )
        }

        if (correcto) {
            preguntasCorrectas++
            rachaActual++
        } else {
            rachaActual = 0
            preguntasIncorrectas++
        }

        binding.tvPuntuacion.text = puntuacion.toString()
        binding.tvFeedbackIcono.text = if (correcto) "✓" else "✗"
        binding.tvFeedbackIcono.setBackgroundResource(
            if (correcto) R.drawable.bg_feedback_correcto else R.drawable.bg_feedback_incorrecto
        )
        binding.tvFeedbackTexto.text = if (correcto) "Correcto" else "Incorrecto"
        binding.tvPuntosGanados.text = "+$puntos"
        binding.tvFeedbackRespuesta.text = "La respuesta era: ${textoOpciones[respuestaCorrecta].text}"

        if (rachaActual >= 3) {
            binding.tvBonusRacha.visibility = View.VISIBLE
            binding.tvBonusRacha.text = "🔥 ¡Racha de $rachaActual!"
        } else {
            binding.tvBonusRacha.visibility = View.GONE
        }

        binding.layoutFeedback.visibility = View.VISIBLE
    }

    private fun guardarEstadisticasYNavegar(data: JSONObject) {
        val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", 0)
        val clasificacion = data.getJSONArray("clasificacion")

        val gano = clasificacion.getJSONObject(0).getString("nombre") ==
                prefs.getString("username", "")

        socket.emit("guardar_estadisticas", JSONObject()
            .put("userId", userId)
            .put("gano", gano)
            .put("correctas", preguntasCorrectas)
            .put("puntos", puntuacion))

        val bundle = Bundle().apply {
            putString("clasificacion", clasificacion.toString())
            putInt("puntuacion", puntuacion)
            putInt("racha", rachaActual)
            putInt("correctas", preguntasCorrectas)
        }
        findNavController().navigate(R.id.rankingfragmento, bundle)
    }

    private fun deshabilitarBotones() {
        opciones.forEach {
            it.isClickable = false
            it.isEnabled = false
        }
    }

    private fun habilitarBotones() {
        opciones.forEach {
            it.isClickable = true
            it.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerActual?.cancel()
        socket.off("pregunta", listenerPreguntaMulti)
        socket.off("resultado_ronda")
        socket.off("fin_partida")
        socket.off("solitario:pregunta")
        socket.off("solitario:resultado")
        socket.off("solitario:fin")
        _binding = null
    }
}