package com.acecorp.sqlpractice

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureStorage {
    private const val PREFS_NAME = "secure_prefs"

    private fun getPrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(context: Context, email: String, password: String) {
        getPrefs(context).edit()
            .putString("email", email)
            .putString("password", password)
            .apply()
    }

    fun getCredentials(context: Context): Pair<String?, String?> {
        val prefs = getPrefs(context)
        return prefs.getString("email", null) to prefs.getString("password", null)
    }

    fun clearCredentials(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
