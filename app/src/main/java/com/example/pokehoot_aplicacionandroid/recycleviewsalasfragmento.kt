package com.example.pokehoot_aplicacionandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokehoot_aplicacionandroid.DataClass.Salas

class recycleviewsalasfragmento(private val listaSalas: MutableList<Salas>) :
    RecyclerView.Adapter<recycleviewsalasfragmento.recycleviewsalasfragmento>() {
    class recycleviewsalasfragmento(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvJugador)
        val estado:  TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recycleviewsalasfragmento {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleviewsalasitem, parent, false)

        return recycleviewsalasfragmento(view)
    }

    override fun onBindViewHolder(holder: recycleviewsalasfragmento, position: Int) {

    }

    override fun getItemCount(): Int {
        return listaSalas.size
    }
}