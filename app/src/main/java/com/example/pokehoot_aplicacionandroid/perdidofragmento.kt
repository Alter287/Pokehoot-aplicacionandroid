package com.example.pokehoot_aplicacionandroid

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pokehoot_aplicacionandroid.Managers.SocketManager
import com.example.pokehoot_aplicacionandroid.databinding.PerdidofragmentBinding

class perdidofragmento : Fragment() {
    private var _binding: PerdidofragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PerdidofragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val puntuacion = arguments?.getInt("puntuacion") ?: 0
        val racha = arguments?.getInt("racha") ?: 0
        val correctas = arguments?.getInt("correctas") ?: 0
        val totalPreguntas = correctas + 1

        val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val mejorRacha = prefs.getInt("mejorRacha", 0)

        if (racha > mejorRacha) {
            prefs.edit().putInt("mejorRacha", racha).apply()
            binding.tvMejorRachaComparacion.text = racha.toString()
        } else {
            binding.tvMejorRachaComparacion.text = mejorRacha.toString()
        }

        binding.tvPuntuacionFinal.text = puntuacion.toString()
        binding.tvRachaPerdida.text = racha.toString()
        binding.tvPreguntasRespondidas.text = totalPreguntas.toString()
        binding.tvCorrectas.text = correctas.toString()

        // Calcular precisión
        val precision = if (totalPreguntas > 0) {
            (correctas.toFloat() / totalPreguntas * 100).toInt()
        } else 0

        binding.tvPrecision.text = "$precision%"
        binding.progressPrecision.progress = precision

        // Botón salir al menú
        binding.btnSalirMenu.setOnClickListener {
            findNavController().navigate(R.id.menuprincipalfragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}