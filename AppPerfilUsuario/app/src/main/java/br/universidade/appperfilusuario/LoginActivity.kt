package br.universidade.appperfilusuario

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.universidade.appperfilusuario.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private val authLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        processarRetornoAuthUi(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val prefs = getSharedPreferences("dados_login_prefs", Context.MODE_PRIVATE)
        val sessaoEmail = prefs.getString("sessao_email", null)
        if (auth.currentUser != null || sessaoEmail != null) {
            navegarParaPerfil()
            return
        }

        binding.btnFazerLogin.setOnClickListener {
            val email = binding.inputEmailUsuario.text.toString().trim()
            val senha = binding.inputSenhaUsuario.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Digite o e-mail e a senha para entrar!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        prefs.edit()
                            .putString("sessao_email", user?.email ?: email)
                            .putString("sessao_nome", user?.displayName ?: prefs.getString("name_$email", "Usuário"))
                            .apply()
                        Toast.makeText(this, "Bem-vindo(a)!", Toast.LENGTH_SHORT).show()
                        navegarParaPerfil()
                    } else {
                        val senhaCadastrada = prefs.getString("pwd_$email", null)
                        if (senhaCadastrada != null && senhaCadastrada == senha) {
                            val nomeCadastrado = prefs.getString("name_$email", "Usuário")
                            prefs.edit()
                                .putString("sessao_email", email)
                                .putString("sessao_nome", nomeCadastrado)
                                .apply()
                            Toast.makeText(this, "Bem-vindo(a)!", Toast.LENGTH_SHORT).show()
                            navegarParaPerfil()
                        } else {
                            Toast.makeText(this, "Conta não encontrada ou senha incorreta!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        binding.btnNovoCadastro.setOnClickListener {
            val nome = binding.inputNomeUsuario.text.toString().trim()
            val email = binding.inputEmailUsuario.text.toString().trim()
            val senha = binding.inputSenhaUsuario.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Informe e-mail e senha para se cadastrar!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                binding.inputSenhaUsuario.error = "Digite no mínimo 6 caracteres"
                return@setOnClickListener
            }

            val nomeSalvo = if (nome.isNotEmpty()) nome else email.substringBefore("@")

            prefs.edit()
                .putString("pwd_$email", senha)
                .putString("name_$email", nomeSalvo)
                .putString("sessao_email", email)
                .putString("sessao_nome", nomeSalvo)
                .apply()

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val atualizacaoPerfil = UserProfileChangeRequest.Builder()
                            .setDisplayName(nomeSalvo)
                            .build()
                        auth.currentUser?.updateProfile(atualizacaoPerfil)
                    }
                    Toast.makeText(this, "Usuário cadastrado!", Toast.LENGTH_SHORT).show()
                    navegarParaPerfil()
                }
        }

        binding.btnAbrirFirebaseUi.setOnClickListener {
            chamarTelaFirebaseUi()
        }
    }

    private fun chamarTelaFirebaseUi() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        authLauncher.launch(signInIntent)
    }

    private fun processarRetornoAuthUi(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            val prefs = getSharedPreferences("dados_login_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("sessao_email", user?.email ?: "usuario@email.com")
                .putString("sessao_nome", user?.displayName ?: "Usuário")
                .apply()
            navegarParaPerfil()
        } else {
            Toast.makeText(this, "Não foi possível autenticar pelo FirebaseUI.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navegarParaPerfil() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
