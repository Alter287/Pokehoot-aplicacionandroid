package com.example.pokehoot_aplicacionandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pokehoot_aplicacionandroid.databinding.PantallainiciosesionBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pokehoot_aplicacionandroid.databinding.MenuprincipalfragmentoBinding

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
        binding.btnCrearSala.setOnClickListener {
            findNavController().navigate(R.id.dentrosalafragmento)
        }
        binding.btnSolitario.setOnClickListener {
            findNavController().navigate(R.id.preguntasmenufragment)
        }
        binding.btnEstadisticas.setOnClickListener {
            findNavController().navigate(R.id.estadisticasfragmento)
        }
        binding.btnUnirSala.setOnClickListener {
            findNavController().navigate(R.id.salasfragmento)
        }
    }
}