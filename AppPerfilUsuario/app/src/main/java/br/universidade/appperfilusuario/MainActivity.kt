package br.universidade.appperfilusuario

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.universidade.appperfilusuario.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val prefs = getSharedPreferences("dados_login_prefs", Context.MODE_PRIVATE)

        val currentUser = auth.currentUser
        val nome = currentUser?.displayName?.takeIf { it.isNotBlank() }
            ?: prefs.getString("sessao_nome", "Usuário")
        val email = currentUser?.email?.takeIf { it.isNotBlank() }
            ?: prefs.getString("sessao_email", "usuario@email.com")

        binding.txtValorNome.text = nome
        binding.txtValorEmail.text = email

        binding.btnEncerrarSessao.setOnClickListener {
            prefs.edit()
                .remove("sessao_email")
                .remove("sessao_nome")
                .apply()

            auth.signOut()

            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    Toast.makeText(this, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
        }
    }
}
