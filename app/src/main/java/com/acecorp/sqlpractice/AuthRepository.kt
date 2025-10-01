package com.acecorp.sqlpractice

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

object AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser() = auth.currentUser
    fun isLoggedIn() = auth.currentUser != null
    fun logout() = auth.signOut()

    // ---------------------------
    // Inicio de sesión (email/contraseña)
    // ---------------------------
    fun loginUser(
        rawEmail: String,
        rawPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val email = rawEmail.trim()
        val password = rawPassword // no trim a password

        if (email.isEmpty() || password.isEmpty()) {
            onResult(false, "Ingresa correo y contraseña.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, toSpanishAuthError(task.exception))
                }
            }
    }

    // ---------------------------
    // Registro de usuario
    // ---------------------------
    fun registerUser(
        rawEmail: String,
        rawPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val email = rawEmail.trim()
        val password = rawPassword

        if (email.isEmpty() || password.isEmpty()) {
            onResult(false, "Ingresa correo y contraseña.")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, toSpanishAuthError(task.exception))
                }
            }
    }

    // ---------------------------
    // Recuperar contraseña (reset por email)
    // *Sin* fetchSignInMethods: dejamos que Firebase valide y responda
    // ---------------------------
    fun sendPasswordReset(
        rawEmail: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val email = rawEmail.trim()
        if (email.isEmpty()) {
            onResult(false, "Ingresa un correo válido.")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Te enviamos un enlace para restablecer tu contraseña. Revisa tu bandeja de entrada y spam.")
                } else {
                    onResult(false, toSpanishAuthError(task.exception))
                }
            }
    }

    // -------------------------------------------------------------------
    // Traducción de errores comunes de FirebaseAuth -> español para UI
    // -------------------------------------------------------------------
    private fun toSpanishAuthError(throwable: Throwable?): String {
        if (throwable == null) return "Ha ocurrido un error desconocido."

        // Conectividad y límites
        when (throwable) {
            is FirebaseNetworkException ->
                return "Problema de conexión. Verifica tu Internet e inténtalo nuevamente."
            is FirebaseTooManyRequestsException ->
                return "Demasiados intentos. Inténtalo más tarde."
        }

        val ex = throwable as? FirebaseAuthException
            ?: return "Error de autenticación: ${throwable.localizedMessage ?: "desconocido"}"

        return when (ex.errorCode) {
            // Credenciales / sesión
            "ERROR_INVALID_CREDENTIAL" -> "Las credenciales no son válidas o han expirado."
            "ERROR_USER_DISABLED" -> "La cuenta ha sido deshabilitada."
            "ERROR_USER_NOT_FOUND" -> "No existe una cuenta asociada a este correo."
            "ERROR_USER_TOKEN_EXPIRED",
            "ERROR_INVALID_USER_TOKEN" -> "La sesión ha expirado. Inicia sesión nuevamente."
            "ERROR_OPERATION_NOT_ALLOWED" -> "El método de autenticación no está habilitado en el proyecto."
            "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Espera un momento e inténtalo de nuevo."
            "ERROR_NETWORK_REQUEST_FAILED" -> "Falla de red. Revisa tu conexión e inténtalo otra vez."

            // Email / Password
            "ERROR_INVALID_EMAIL" -> "El correo electrónico no es válido."
            "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya está registrado."
            "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado débil (usa 6 o más caracteres)."

            // Proveedores y conflictos
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
                "Ya existe una cuenta con otro método de acceso para este correo."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" ->
                "Estas credenciales ya están en uso por otra cuenta."
            "ERROR_CUSTOM_TOKEN_MISMATCH",
            "ERROR_INVALID_CUSTOM_TOKEN" -> "Token personalizado inválido."
            "ERROR_USER_MISMATCH" -> "Las credenciales no corresponden al usuario actual."

            // Reautenticación requerida
            "ERROR_REQUIRES_RECENT_LOGIN" ->
                "Por seguridad, vuelve a iniciar sesión para completar esta acción."

            else -> "Error de autenticación: ${ex.localizedMessage ?: ex.errorCode}"
        }
    }
}
