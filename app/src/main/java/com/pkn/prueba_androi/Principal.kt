package com.pkn.prueba_androi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Principal : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth

    private lateinit var txtNombre: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var listView: ListView

    private val listaMostrar = ArrayList<String>()
    private val listaIds = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private var idSeleccionado = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        mAuth = FirebaseAuth.getInstance()
        title = "panel CRUD"

        txtNombre = findViewById(R.id.txtNombreItem)
        txtDescripcion = findViewById(R.id.txtDescripcionItem)
        listView = findViewById(R.id.listViewUsuarios)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaMostrar)
        listView.adapter = adapter

        cargarDatos()






        // 4. Botón Eliminar
        findViewById<Button>(R.id.btnEliminar).setOnClickListener {
            if (idSeleccionado.isNotEmpty()) {
                db.collection("Usuarios").document(idSeleccionado).delete().addOnSuccessListener {
                    Toast.makeText(this, "Eliminado con éxito", Toast.LENGTH_SHORT).show()
                    limpiar()
                    cargarDatos()
                }
            } else {
                Toast.makeText(this, "Selecciona un elemento de la lista primero", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Botón Cerrar Sesión (Firebase + Google Sign-In)
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            mAuth.signOut()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, gso)
            googleClient.signOut().addOnCompleteListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun cargarDatos() {
        db.collection("Usuarios").get().addOnSuccessListener { resultado ->
            listaMostrar.clear()
            listaIds.clear()
            for (doc in resultado) {
                val nom = doc.getString("nombre") ?: ""
                val desc = doc.getString("descripcion") ?: ""
                listaMostrar.add("$nom - $desc")
                listaIds.add(doc.id)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun limpiar() {
        txtNombre.text.clear()
        txtDescripcion.text.clear()
        idSeleccionado = ""
    }
}
