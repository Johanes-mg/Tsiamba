package com.tsiambajojoh.mg.data

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent

object AuthentificationBiometrique {

    fun codeDisponible(contexte: Context): Boolean {
        val gestionnaire = contexte.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        return gestionnaire?.isKeyguardSecure == true
    }

    fun creerIntentVerrouillage(
        activite: Activity,
        titre: String,
        sousTitre: String
    ): Intent? {
        val gestionnaire = activite.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (gestionnaire == null || !gestionnaire.isKeyguardSecure) {
            return null
        }
        return gestionnaire.createConfirmDeviceCredentialIntent(titre, sousTitre)
    }
}
