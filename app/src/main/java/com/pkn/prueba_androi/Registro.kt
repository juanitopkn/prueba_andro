package com.pkn.prueba_androi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Registro : AppCompatActivity() {

    private lateinit var oFirebaseAnalytics: FirebaseAnalytics
    private lateinit var oFirebaseAuth: FirebaseAuth
    private val TAG="EmailPassword"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        oFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle().apply { putString("Mensaje","Entro_al_registro") }
        oFirebaseAnalytics.logEvent("formulario_registro",bundle)
        title="FormularioRegistro"
        oFirebaseAuth= FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btnCrearRegistro).setOnClickListener {
            crearRegistro()
        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun crearRegistro() {
        val txtCorreo = findViewById<EditText>(R.id.txtCorreoR)
        val txtPass = findViewById<EditText>(R.id.txtPass1)

        val correo = txtCorreo?.text?.toString()?.trim() ?: ""
        val pass = txtPass?.text?.toString()?.trim() ?: ""

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (
            pass.length < 6) {
            Toast.makeText(this, "La contraseña debe tener mas de 6 digitos", Toast.LENGTH_SHORT)
                .show()
            return
        }
        oFirebaseAuth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "CreateUserWitchEmail:sucess")
                    Toast.makeText(this, "usuario ha sido creado", Toast.LENGTH_SHORT).show()
                    oFirebaseAuth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "CreateUserWithEmail:failure", task.exception)
                    val error = task.exception?.localizedMessage ?: "Fallo la autenticacion"
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user:FirebaseUser?){}
    private fun reload(user: FirebaseUser){}
    }
