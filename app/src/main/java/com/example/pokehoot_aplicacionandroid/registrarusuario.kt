package com.example.pokehoot_aplicacionandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pokehoot_aplicacionandroid.DataClass.RegistroRequest
import com.example.pokehoot_aplicacionandroid.data.RetrofitClient
import com.example.pokehoot_aplicacionandroid.databinding.RegistrarusuarioBinding
import kotlinx.coroutines.launch

class registrarusuario : Fragment(){
    private var _binding: RegistrarusuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegistrarusuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                usuario.isEmpty() || email.isEmpty() || password.isEmpty()->{
                    Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                }
                password.length < 4 -> {
                    Toast.makeText(requireContext(), "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show()
                }
                !email.matches(Regex("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) -> {
                    Toast.makeText(requireContext(), "Tienes que poner un correo correcto", Toast.LENGTH_SHORT).show()
                }
                else ->{
                    lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.instance.registro(
                                RegistroRequest(username = usuario, email = email, password = password)
                            )
                            if (response.success) {
                                Toast.makeText(requireContext(), "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.iniciarSesionFragmento)
                            } else {
                                Toast.makeText(requireContext(), response.error ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: retrofit2.HttpException) {
                            val errorBody = e.response()?.errorBody()?.string()
                            val mensaje = try {
                                org.json.JSONObject(errorBody ?: "").getString("error")
                            } catch (ex: Exception) {
                                "Error al registrarse"
                            }
                            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}