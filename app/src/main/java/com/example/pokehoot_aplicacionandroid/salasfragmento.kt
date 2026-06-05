package com.example.pokehoot_aplicacionandroid

import com.example.pokehoot_aplicacionandroid.Managers.SocketManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokehoot_aplicacionandroid.DataClass.Salas
import com.example.pokehoot_aplicacionandroid.data.RetrofitClient
import com.example.pokehoot_aplicacionandroid.databinding.SalasfragmentoBinding
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject

class salasfragmento : Fragment() {
    private var _binding: SalasfragmentoBinding? = null
    private val binding get() = _binding!!
    private var codigoSala: String = ""

    private lateinit var socket: Socket
    private lateinit var adapter: recycleviewsalasfragmento
    private val listaSalas = mutableListOf<Salas>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SalasfragmentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val nombreJugador = prefs.getString("username", "Entrenador") ?: "Entrenador"

        adapter = recycleviewsalasfragmento(listaSalas) { codigo -> unirseASala(codigo, nombreJugador) }
        binding.recyclerSalas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSalas.adapter = adapter

        socket = SocketManager.conectar()
        configurarEventosSocket(nombreJugador)

        cargarSalas()
        binding.btnBuscarSala.setOnClickListener {
            val codigo = binding.etCodigoSala.text.toString().trim().uppercase()
            if (codigo.isEmpty()) {
                Toast.makeText(requireContext(), "Introduce un código de sala", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            unirseASala(codigo, nombreJugador)
        }
    }


    private fun cargarSalas() {
        Log.d("SALAS", "Iniciando carga de salas...")
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getSalas()
                Log.d("SALAS", "Salas recibidas: ${response.salas?.size ?: 0}")
                if (response.success) {
                    val salas = response.salas ?: emptyList()
                    adapter.actualizarSalas(salas)
                    binding.tvSalasOnline.text = "${salas.size} salas activas"
                }
            } catch (e: Exception) {
                Log.e("SALAS", "Error cargando salas: ${e.message}")
            }
        }
    }

    private fun configurarEventosSocket(nombreJugador: String) {
        socket.on(Socket.EVENT_CONNECT) {
            Log.d("SALAS", "Socket conectado, cargando salas...")
            activity?.runOnUiThread {
                if (isAdded && _binding != null) cargarSalas()
            }
        }

        socket.on("resultado_unirse") { args ->
            val data = args[0] as JSONObject
            if (data.getBoolean("success")) {
                activity?.runOnUiThread {
                    if (!isAdded || _binding == null) return@runOnUiThread
                    val bundle = Bundle().apply {
                        putString("codigoSala", codigoSala)
                        putBoolean("esHost", false)
                        // Pasar jugadores iniciales
                        if (data.has("jugadores")) {
                            putString("jugadoresIniciales", data.getJSONArray("jugadores").toString())
                        }
                    }
                    findNavController().navigate(R.id.dentrosalafragmento, bundle)
                }
            }
        }

        socket.on("salas_actualizadas") {
            activity?.runOnUiThread {
                if (isAdded && _binding != null) cargarSalas()
            }
        }
    }

    private fun unirseASala(codigo: String, nombreJugador: String) {
        codigoSala = codigo
        Log.d("SALAS", "Emitiendo unirse_sala | Código: $codigo | Jugador: $nombreJugador")
        socket.emit("unirse_sala", JSONObject()
            .put("codigoSala", codigo)
            .put("nombreJugador", nombreJugador))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        socket.off("resultado_unirse")
        socket.off("jugador_unido")
        socket.off(Socket.EVENT_CONNECT)
        socket.off("salas_actualizadas")
        _binding = null
    }
}