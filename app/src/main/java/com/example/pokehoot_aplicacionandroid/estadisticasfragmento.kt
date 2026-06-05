package com.example.pokehoot_aplicacionandroid

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pokehoot_aplicacionandroid.data.RetrofitClient
import com.example.pokehoot_aplicacionandroid.databinding.EstadisticasfragmentoBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class estadisticasfragmento : Fragment() {
    private var _binding: EstadisticasfragmentoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EstadisticasfragmentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", 0)
        val username = prefs.getString("username", "Entrenador") ?: "Entrenador"
        val mejorRachaLocal = prefs.getInt("mejorRacha", 0)

        binding.tvNombreEntrenador.text = username
        binding.tvMejorRacha.text = mejorRachaLocal.toString()
        binding.tvRachaActual.text = prefs.getInt("rachaActual", 0).toString()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getPerfil(userId)
                if (response.success) {
                    val perfil = response.perfil
                    val stats = perfil?.estadisticas

                    binding.tvNombreEntrenador.text = perfil?.username ?: username
                    binding.tvCorreoEntrenador.text = perfil?.email ?: ""

                    binding.tvPartidasJugadas.text = stats?.partidasJugadas?.toString() ?: "0"
                    binding.tvPartidasGanadas.text = stats?.partidasGanadas?.toString() ?: "0"
                    binding.tvPreguntasCorrectas.text = stats?.preguntasCorrectas?.toString() ?: "0"
                    binding.tvPuntuacionTotal.text = stats?.puntuacionTotal?.toString() ?: "0"
                    binding.tvMejorPuntuacion.text = stats?.mejorPuntuacion?.toString() ?: "0"
                    binding.tvMejorRacha.text = stats?.mejorRacha?.toString() ?: mejorRachaLocal.toString()
                    binding.tvRachaActual.text = stats?.rachaActual?.toString() ?: "0"

                    val partidas = stats?.partidasJugadas ?: 0
                    val victorias = stats?.partidasGanadas ?: 0
                    val winRate = if (partidas > 0) {
                        (victorias.toFloat() / partidas * 100).toInt()
                    } else 0
                    binding.tvWinRate.text = "$winRate% victorias"

                    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    binding.tvActualizadoEn.text = "Actualizado: ${formato.format(Date())}"

                } else {
                    Toast.makeText(requireContext(), "Error al cargar estadísticas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}