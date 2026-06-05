package com.example.pokehoot_aplicacionandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokehoot_aplicacionandroid.DataClass.Salas

class recycleviewsalasfragmento(
    private val listaSalas: MutableList<Salas>,
    private val onEntrarSala: (String) -> Unit
) : RecyclerView.Adapter<recycleviewsalasfragmento.SalaViewHolder>() {

    class SalaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreSala)
        val codigo: TextView = view.findViewById(R.id.tvCodigoSala)
        val jugadores: TextView = view.findViewById(R.id.tvJugadores)
        val btnEntrar: Button = view.findViewById(R.id.btnEntrarSala)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleviewsalasitem, parent, false)
        return SalaViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalaViewHolder, position: Int) {
        val sala = listaSalas[position]
        holder.nombre.text = "Sala #${sala.codigo}"
        holder.codigo.text = "Código: ${sala.codigo}"
        holder.jugadores.text = "${sala.jugadoresActuales}/${sala.jugadoresMaximos} jugadores"
        holder.btnEntrar.setOnClickListener {
            onEntrarSala(sala.codigo)
        }
    }

    override fun getItemCount(): Int = listaSalas.size

    fun actualizarSalas(nuevaLista: List<Salas>) {
        listaSalas.clear()
        listaSalas.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}