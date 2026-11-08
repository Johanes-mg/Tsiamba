package com.tsiambajojoh.mg.data

import android.os.Build
import android.util.Base64
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object Chiffrement {

    private const val ALIAS = "tsiamba_aes_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BITS = 128
    private const val IV_LENGTH = 12

    private fun obtenirCle(): SecretKey {
        check(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            "Le chiffrement AndroidKeyStore AES-GCM nécessite Android 6.0 ou supérieur."
        }

        val ks = KeyStore.getInstance("AndroidKeyStore")
        ks.load(null)

        val existante = ks.getKey(ALIAS, null) as? SecretKey
        if (existante != null) return existante

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        generator.init(
            KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )

        return generator.generateKey()
    }

    fun chiffrer(texte: String): String {
        val cle = obtenirCle()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, cle)

        val iv = cipher.iv
        require(iv.size == IV_LENGTH) {
            "Longueur IV inattendue pour AES-GCM."
        }

        val chiffre = cipher.doFinal(texte.toByteArray(Charsets.UTF_8))
        val combine = ByteArray(IV_LENGTH + chiffre.size)

        System.arraycopy(iv, 0, combine, 0, IV_LENGTH)
        System.arraycopy(chiffre, 0, combine, IV_LENGTH, chiffre.size)

        // android.util.Base64 est disponible dès les anciennes versions Android.
        // NO_WRAP conserve le format compact précédemment produit par java.util.Base64.
        return Base64.encodeToString(combine, Base64.NO_WRAP)
    }

    fun dechiffrer(texte: String): String? {
        return try {
            val combine = Base64.decode(texte.trim(), Base64.DEFAULT)
            if (combine.size <= IV_LENGTH) return null

            val iv = ByteArray(IV_LENGTH)
            System.arraycopy(combine, 0, iv, 0, IV_LENGTH)

            val chiffre = ByteArray(combine.size - IV_LENGTH)
            System.arraycopy(combine, IV_LENGTH, chiffre, 0, chiffre.size)

            val cle = obtenirCle()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                cle,
                GCMParameterSpec(TAG_LENGTH_BITS, iv)
            )

            val clair = cipher.doFinal(chiffre)
            String(clair, Charsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }
}
