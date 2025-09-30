package com.example.sqlpractice

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
    // Inicio de sesión (Email/Pass)
    // ---------------------------
    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
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
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
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
    // Recuperar contraseña (reset link)
    // ---------------------------
    fun sendPasswordResetEmail(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null) // Se envió el correo
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
                return "Problema de conexión. Verifica tu Internet e inténtalo de nuevo."
            is FirebaseTooManyRequestsException ->
                return "Demasiados intentos. Inténtalo nuevamente más tarde."
        }

        // Errores con códigos de FirebaseAuth
        val ex = throwable as? FirebaseAuthException
            ?: return "Error de autenticación: ${throwable.localizedMessage ?: "desconocido"}"

        return when (ex.errorCode) {
            // Credenciales / sesión
            "ERROR_INVALID_CREDENTIAL" ->
                "Las credenciales no son válidas o han expirado."
            "ERROR_USER_DISABLED" ->
                "La cuenta ha sido deshabilitada."
            "ERROR_USER_NOT_FOUND" ->
                "No existe una cuenta asociada a este correo."
            "ERROR_USER_TOKEN_EXPIRED",
            "ERROR_INVALID_USER_TOKEN" ->
                "La sesión ha expirado. Inicia sesión nuevamente."
            "ERROR_OPERATION_NOT_ALLOWED" ->
                "El método de autenticación no está habilitado en el proyecto."
            "ERROR_TOO_MANY_REQUESTS" ->
                "Demasiados intentos. Espera un momento e inténtalo de nuevo."
            "ERROR_NETWORK_REQUEST_FAILED" ->
                "Falla de red. Revisa tu conexión e inténtalo otra vez."

            // Email / Password
            "ERROR_INVALID_EMAIL" ->
                "El correo electrónico no es válido."
            "ERROR_WRONG_PASSWORD" ->
                "La contraseña es incorrecta."
            "ERROR_EMAIL_ALREADY_IN_USE" ->
                "Este correo ya está registrado."
            "ERROR_WEAK_PASSWORD" ->
                "La contraseña es demasiado débil (usa 6 o más caracteres)."

            // Proveedores y conflictos
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
                "Ya existe una cuenta con otro método de acceso para este correo."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" ->
                "Estas credenciales ya están en uso por otra cuenta."
            "ERROR_CUSTOM_TOKEN_MISMATCH",
            "ERROR_INVALID_CUSTOM_TOKEN" ->
                "Token personalizado inválido."
            "ERROR_USER_MISMATCH" ->
                "Las credenciales no corresponden al usuario actual."

            // Reautenticación requerida (cambios sensibles)
            "ERROR_REQUIRES_RECENT_LOGIN" ->
                "Por seguridad, vuelve a iniciar sesión para completar esta acción."

            else -> "Error de autenticación: ${ex.localizedMessage ?: ex.errorCode}"
        }
    }
}
