package com.example.pokehoot_aplicacionandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pokehoot_aplicacionandroid.DataClass.IntentarLogear
import com.example.pokehoot_aplicacionandroid.data.RetrofitClient
import com.example.pokehoot_aplicacionandroid.databinding.PantallainiciosesionBinding
import kotlinx.coroutines.launch
import androidx.core.content.edit

class IniciarSesionFragmento : Fragment() {
    private var _binding: PantallainiciosesionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PantallainiciosesionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (VerificarSesion.estaSesionActiva(requireContext())) {
            findNavController().navigate(R.id.menuprincipalfragment)
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.instance.login(IntentarLogear(email, password))
                        if (response.success) {
                            val prefs = requireContext().getSharedPreferences("pokehoot", Context.MODE_PRIVATE)
                            prefs.edit {
                                putString("token", response.token)
                                putString("username", response.username)
                                putInt("userId", response.userId ?: 0)
                            }
                            findNavController().navigate(R.id.menuprincipalfragment)
                        } else {
                            Toast.makeText(requireContext(), response.error ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: retrofit2.HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        val mensaje = try {
                            org.json.JSONObject(errorBody ?: "").getString("error")
                        } catch (ex: Exception) {
                            "Correo o contraseña incorrectos"
                        }
                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.tvRegistro.setOnClickListener {
            findNavController().navigate(R.id.registrarusuario)
        }
    }
}