package com.example.pokehoot_aplicacionandroid

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pokehoot_aplicacionandroid.databinding.RankingfragmentoBinding
import org.json.JSONArray

class rankingfragmento : Fragment() {
    private var _binding: RankingfragmentoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RankingfragmentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val usernameActual = prefs.getString("username", "") ?: ""

        val clasificacionJson = arguments?.getString("clasificacion") ?: "[]"
        val puntuacionActual = arguments?.getInt("puntuacion") ?: 0
        val rachaActual = arguments?.getInt("racha") ?: 0

        val clasificacion = JSONArray(clasificacionJson)

        binding.tuPuntuacion.text = puntuacionActual.toString()
        binding.tuRacha.text = rachaActual.toString()

        var tuPosicion = 0
        for (i in 0 until clasificacion.length()) {
            val jugador = clasificacion.getJSONObject(i)
            if (jugador.getString("nombre") == usernameActual) {
                tuPosicion = i + 1
                break
            }
        }
        binding.tuPosicion.text = "#$tuPosicion"

        if (clasificacion.length() >= 1) {
            val primero = clasificacion.getJSONObject(0)
            binding.primerNombre.text = primero.getString("nombre")
            binding.primerPuntuacion.text = primero.getInt("puntuacion").toString()
        }
        if (clasificacion.length() >= 2) {
            val segundo = clasificacion.getJSONObject(1)
            binding.segundoNombre.text = segundo.getString("nombre")
            binding.segundoPuntuacion.text = segundo.getInt("puntuacion").toString()
        }
        if (clasificacion.length() >= 3) {
            val tercero = clasificacion.getJSONObject(2)
            binding.tercerNombre.text = tercero.getString("nombre")
            binding.tercerPuntuacion.text = tercero.getInt("puntuacion").toString()
        }

        for (i in 0 until clasificacion.length()) {
            val jugador = clasificacion.getJSONObject(i)
            val nombre = jugador.getString("nombre")
            val puntos = jugador.getInt("puntuacion")
            val esActual = nombre == usernameActual

            val fila = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(24, 16, 24, 16)
                setBackgroundColor(
                    if (esActual) 0xFFE3F2FD.toInt()
                    else if (i % 2 == 0) 0xFFFFFFFF.toInt()
                    else 0xFFF5F5F5.toInt()
                )
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 2 }
            }

            val tvPosicion = TextView(requireContext()).apply {
                text = when (i) {
                    0 -> "🥇"
                    1 -> "🥈"
                    2 -> "🥉"
                    else -> "${i + 1}"
                }
                textSize = 14f
                width = 120
                gravity = android.view.Gravity.CENTER
                setTextColor(0xFF212121.toInt())
            }

            val tvNombre = TextView(requireContext()).apply {
                text = if (esActual) "$nombre ◀" else nombre
                textSize = 14f
                setTextColor(if (esActual) 0xFF2196F3.toInt() else 0xFF212121.toInt())
                if (esActual) setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(16, 0, 0, 0)
            }

            val tvPuntos = TextView(requireContext()).apply {
                text = puntos.toString()
                textSize = 14f
                setTextColor(0xFF212121.toInt())
                setTypeface(null, android.graphics.Typeface.BOLD)
                width = 160
                gravity = android.view.Gravity.END
            }

            fila.addView(tvPosicion)
            fila.addView(tvNombre)
            fila.addView(tvPuntos)
            binding.rankingContainer.addView(fila)
        }

        // Botón salir
        binding.btnSalir.setOnClickListener {
            findNavController().navigate(R.id.menuprincipalfragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}