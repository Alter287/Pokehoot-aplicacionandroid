package com.example.pokehoot_aplicacionandroid.Managers

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketManager {

    private const val SERVER_URL = "https://pokehoot-server-production.up.railway.app"

    private var socket: Socket? = null

    fun conectar(): Socket {
        if (socket == null || !socket!!.connected()) {
            try {
                val opciones = IO.Options().apply {
                    reconnection = true
                    reconnectionAttempts = 5
                    reconnectionDelay = 1000
                    transports = arrayOf("polling", "websocket")
                }
                socket = IO.socket(SERVER_URL, opciones)

                socket!!.on(Socket.EVENT_CONNECT) {
                    android.util.Log.d("nose", "Conectado")
                }
                socket!!.on(Socket.EVENT_DISCONNECT) { args ->
                    val motivo = args.getOrNull(0)?.toString() ?: ""
                    android.util.Log.d("nose", "Desconectado: $motivo")
                }
                socket!!.on(Socket.EVENT_CONNECT_ERROR) { args ->
                    android.util.Log.d("nose", "Error conexión: ${args.getOrNull(0)}")
                }

                socket!!.connect()
                android.util.Log.d("nose", "Intentando conectar...")
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        } else {
            android.util.Log.d("nose", "Socket ya conectado, reutilizando")
        }
        return socket!!
    }

    fun desconectar() {
        socket?.off()
        socket?.disconnect()
        socket = null
    }

    fun getSocket(): Socket? = socket
    fun estaConectado(): Boolean = socket?.connected() ?: false
}