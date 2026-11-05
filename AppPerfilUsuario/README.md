# AppPerfilUsuario - Gerenciamento de Login e Perfil

Aplicativo Android desenvolvido em Kotlin utilizando o modelo de autenticação do FirebaseUI (`FirebaseAuthUIActivityResultContract`, `AuthUI` e `FirebaseAuth`).

## Telas do Projeto
- **Tela de Login (`LoginActivity`)**: Permite que o usuário entre na sua conta ou realize o cadastro de um novo usuário informando Nome, E-mail e Senha, além de integrar o fluxo `AuthUI.getInstance().createSignInIntentBuilder()`.
- **Tela de Perfil (`MainActivity`)**: Exibe o Nome e o E-mail do usuário conectado e disponibiliza o botão **Sair e Retornar ao Login**, que chama `AuthUI.getInstance().signOut(this)` e redireciona novamente para a `LoginActivity`.
