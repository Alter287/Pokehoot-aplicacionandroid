package com.example.pokehoot_aplicacionandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokehoot_aplicacionandroid.DataClass.respuestalogeo

class recycleviewjugadoresfragmento(private val listaJugadores: MutableList<respuestalogeo>) :
    RecyclerView.Adapter<recycleviewjugadoresfragmento.recycleviewjugadoresfragmento>() {

    class recycleviewjugadoresfragmento(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvJugador)
        val estado:  TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recycleviewjugadoresfragmento {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleviewjugadoresitem, parent, false)

        return recycleviewjugadoresfragmento(view)
    }

    override fun onBindViewHolder(holder: recycleviewjugadoresfragmento, position: Int) {

    }

    override fun getItemCount(): Int {
        return listaJugadores.size
    }
}