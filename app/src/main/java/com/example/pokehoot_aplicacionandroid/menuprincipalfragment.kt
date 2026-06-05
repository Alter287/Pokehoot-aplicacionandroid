package com.example.pokehoot_aplicacionandroid

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pokehoot_aplicacionandroid.databinding.MenuprincipalfragmentoBinding
import androidx.core.content.edit

class menuprincipalfragment : Fragment(){
    private var _binding: MenuprincipalfragmentoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuprincipalfragmentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Entrenador")

        binding.tvNombreJugador.text = username

        binding.tvJugadoresOnline.text = "0 online"

        binding.btnCerrarSesion.setOnClickListener {
            prefs.edit { clear() }
            findNavController().navigate(R.id.iniciarSesionFragmento)
        }

        binding.btnCrearSala.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("esHost", true)
            }
            findNavController().navigate(R.id.dentrosalafragmento, bundle)
        }

        binding.btnUnirSala.setOnClickListener {
            findNavController().navigate(R.id.salasfragmento)
        }

        binding.btnSolitario.setOnClickListener {
            val bundle = Bundle().apply {
                putString("modo", "solitario")
            }
            findNavController().navigate(R.id.preguntasmenufragment, bundle)
        }

        binding.btnEstadisticas.setOnClickListener {
            findNavController().navigate(R.id.estadisticasfragmento)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}