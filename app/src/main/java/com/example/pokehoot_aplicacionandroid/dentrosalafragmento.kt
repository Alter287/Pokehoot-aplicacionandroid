package com.example.pokehoot_aplicacionandroid

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokehoot_aplicacionandroid.DataClass.JugadorSala
import com.example.pokehoot_aplicacionandroid.Managers.SocketManager
import com.example.pokehoot_aplicacionandroid.databinding.DentrosalafragmentoBinding
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONObject

class dentrosalafragmento : Fragment(){
    private var _binding: DentrosalafragmentoBinding? = null
    private val binding get() = _binding!!

    private lateinit var socket: Socket
    private lateinit var adapter: recycleviewjugadoresfragmento
    private val listaJugadores = mutableListOf<JugadorSala>()
    private var codigoSala: String = ""
    private var esHost: Boolean = false
    private var countdownTimer: CountDownTimer? = null

    private var listenerPregunta: io.socket.emitter.Emitter.Listener? = null

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DentrosalafragmentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codigoSala = arguments?.getString("codigoSala") ?: ""
        esHost = arguments?.getBoolean("esHost") ?: false

        val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
        val nombreJugador = prefs.getString("username", "Entrenador") ?: "Entrenador"

        adapter = recycleviewjugadoresfragmento(listaJugadores)
        binding.recyclerJugadores.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerJugadores.adapter = adapter

        val jugadoresIniciales = arguments?.getString("jugadoresIniciales")
        if (!jugadoresIniciales.isNullOrEmpty()) {
            val jsonArray = JSONArray(jugadoresIniciales)
            val lista = parsearJugadores(jsonArray)
            adapter.actualizarJugadores(lista)
        }
        binding.btnEmpezar.visibility = if (esHost) View.VISIBLE else View.GONE

        if (codigoSala.isNotEmpty()) {
            binding.tvCodigoSala.text = codigoSala
        }

        socket = SocketManager.conectar()
        configurarEventosSocket(nombreJugador)

        binding.btnCopiarCodigo.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("codigoSala", codigoSala)
            clipboard.setPrimaryClip(clip)
        }

        binding.btnEmpezar.setOnClickListener {
            if (listaJugadores.size < 2) {
                Toast.makeText(requireContext(), "Necesitas al menos 2 jugadores", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            socket.emit("iniciar_partida", JSONObject().put("codigoSala", codigoSala))
        }

        binding.btnSalirSala.setOnClickListener {
            socket.emit("salir_sala", JSONObject().put("codigoSala", codigoSala))
            findNavController().navigate(R.id.menuprincipalfragment)
        }
    }

    private fun configurarEventosSocket(nombreJugador: String) {

        //Mira si la sala esta creada
        socket.on("sala_creada") { args ->
            val data = args[0] as JSONObject
            Log.d("SALA", "Respuesta sala_creada: $data")
            if (data.getBoolean("success")) {
                codigoSala = data.getString("codigoSala")
                esHost = true
                activity?.runOnUiThread {
                    binding.tvCodigoSala.text = codigoSala
                    binding.btnEmpezar.visibility = View.VISIBLE
                }
            }
        }

        //Se unio un jugador y actualiza la lista
        socket.on("jugador_unido") { args ->
            val data = args[0] as JSONObject
            val jugadores = data.getJSONArray("jugadores")
            val nuevaLista = parsearJugadores(jugadores)
            activity?.runOnUiThread {
                adapter.actualizarJugadores(nuevaLista)
            }
        }

        //Esto es porque habia un error de que cojia el socket de solitario, por lo cual lo llamo de una forma especifica para especificar cual uso cuando
        listenerPregunta = io.socket.emitter.Emitter.Listener { args ->
            val data = args[0] as JSONObject
            activity?.runOnUiThread {
                iniciarCuentaAtras {
                    socket.off("pregunta", listenerPregunta)
                    val bundle = Bundle().apply {
                        putString("codigoSala", codigoSala)
                        putString("modo", "multijugador")
                        putString("primeraPregunta", data.toString())
                    }
                    findNavController().navigate(R.id.preguntasmenufragment, bundle)
                }
            }
        }
        socket.on("pregunta", listenerPregunta)

        //Para crear la sala con el host
        if (esHost) {
            socket.emit("crear_sala", JSONObject().put("nombreJugador", nombreJugador))
        }

        //Para cerrar la sala
        socket.on("sala_cerrada") { args ->
            val data = args[0] as JSONObject
            activity?.runOnUiThread {
                if (!isAdded || _binding == null) return@runOnUiThread
                Toast.makeText(requireContext(), data.getString("mensaje"), Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.menuprincipalfragment)
            }
        }
    }

    private fun parsearJugadores(jsonArray: JSONArray): List<JugadorSala> {
        val lista = mutableListOf<JugadorSala>()
        for (i in 0 until jsonArray.length()) {
            val jugador = jsonArray.getJSONObject(i)
            lista.add(
                JugadorSala(
                    id = jugador.getString("id"),
                    nombre = jugador.getString("nombre"),
                    esHost = i == 0
                )
            )
        }
        return lista
    }

    private fun iniciarCuentaAtras(alTerminar: () -> Unit) {
        binding.countdownOverlay.visibility = View.VISIBLE
        countdownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvCountdown.text = (millisUntilFinished / 1000 + 1).toString()
            }
            override fun onFinish() {
                binding.countdownOverlay.visibility = View.GONE
                alTerminar()
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //apago todos los sockets
        socket.off("sala_creada")
        socket.off("jugador_unido")
        socket.off("pregunta", listenerPregunta)
        socket.off("sala_cerrada")
        countdownTimer?.cancel()
        _binding = null
    }
}