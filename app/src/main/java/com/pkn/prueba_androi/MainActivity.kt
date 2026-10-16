package com.pkn.prueba_androi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG = "Email Pasword"

    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!) } catch (e:Exeption)
        {Log.w(TAG,"Googlesinginfailed",e)
            setOf(Toast.makeText(this, "Error en Google:${e.message}", Toast.LENGTH_SHORT))
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        mAuth=firebaseAuth.getInstance() tittle="Inicio de sesion"
        val gso = oGoogleSignInOptions.Builder(GoogleSingInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.String.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient= GoogleSignIn.getClient(this,gso)
        val txtCoreo = findViewById<EditText>(R.id.txtCorreo)
        val txtPass = findViewById<EditText>(R.id.txtPass)
        findViewById<Button>(R.id.btnIngresar).setOnClickListener {
            val Correo = txtCoreo?.text?.toString()?.trim()?:""
            val Pass = txtPass?.text?.toString()?.trim()?:""
            ingresar(correo, pass)
        }

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            startActivity(Intent(this,Registro::class.java))
        }

        findViewById<Button>(R.id.btnGoogle).setOnClickListener {
            googleLauncher.launch(googleSignInClient.signInIntent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun ingresar(email:String,password:String) {
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Ingrese correo y contraseña",Toast.LENGTH_SHORT).show()
        return}

        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) {task ->
                if(task.isSuccessful){
                    startActivity(Intent(this, Principal::class.java))
                    finish()
                }else{
                    Toast.makeText(this,"Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential= GoogleAuthProvider.getCredential(idToken,null)
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                Toast.makeText(this, "Bienvenido con Google", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Principal::class.java))
            finish()}
            .addOnSuccessListener { e ->
                Toast.makeText(this,"Error en Google:${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
