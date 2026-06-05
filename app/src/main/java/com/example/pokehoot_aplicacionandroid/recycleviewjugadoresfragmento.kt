package com.example.pokehoot_aplicacionandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokehoot_aplicacionandroid.DataClass.JugadorSala

class recycleviewjugadoresfragmento(private val listaJugadores: MutableList<JugadorSala>) :
    RecyclerView.Adapter<recycleviewjugadoresfragmento.JugadorViewHolder>() {

    class JugadorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvJugador)
        val estado: TextView = view.findViewById(R.id.tvEstado)
        val host: TextView = view.findViewById(R.id.tvHost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleviewjugadoresitem, parent, false)
        return JugadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: JugadorViewHolder, position: Int) {
        val jugador = listaJugadores[position]
        holder.nombre.text = jugador.nombre
        holder.estado.text = "Listo para jugar"
        holder.host.visibility = if (jugador.esHost) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = listaJugadores.size

    fun actualizarJugadores(nuevaLista: List<JugadorSala>) {
        listaJugadores.clear()
        listaJugadores.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}