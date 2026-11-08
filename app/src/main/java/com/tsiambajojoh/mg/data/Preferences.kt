package com.tsiambajojoh.mg.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf

enum class MethodeVerrou(val code: String) {
    BIOMETRIE("bio"),
    CODE("code"),
    AUCUNE("aucune");

    companion object {
        fun depuisCode(code: String?): MethodeVerrou =
            values().firstOrNull { it.code == code } ?: AUCUNE
    }
}

enum class ThemeApp(val code: String) {
    CLAIR("clair"),
    SOMBRE("sombre"),
    SYSTEME("systeme")
}

class Preferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    val langue = mutableStateOf(Langue.depuisCode(prefs.getString(CLE_LANGUE, null)))
    val methodeVerrou = mutableStateOf(MethodeVerrou.depuisCode(prefs.getString(CLE_VERROU, "aucune")))
    val theme = mutableStateOf(
        when (prefs.getString(CLE_THEME, "systeme")) {
            "clair" -> ThemeApp.CLAIR
            "sombre" -> ThemeApp.SOMBRE
            else -> ThemeApp.SYSTEME
        }
    )

    fun changerLangue(nouvelle: Langue) {
        langue.value = nouvelle
        prefs.edit().putString(CLE_LANGUE, nouvelle.code).apply()
    }

    fun changerMethodeVerrou(nouvelle: MethodeVerrou) {
        methodeVerrou.value = nouvelle
        prefs.edit().putString(CLE_VERROU, nouvelle.code).apply()
    }

    fun basculerTheme() {
        val nouveau = when (theme.value) {
            ThemeApp.CLAIR -> ThemeApp.SOMBRE
            ThemeApp.SOMBRE -> ThemeApp.CLAIR
            ThemeApp.SYSTEME -> ThemeApp.SOMBRE
        }
        theme.value = nouveau
        prefs.edit().putString(CLE_THEME, nouveau.code).apply()
    }

    companion object {
        private const val PREFS = "tsiamba_prefs_ui"
        private const val CLE_LANGUE = "langue"
        private const val CLE_VERROU = "methode_verrou"
        private const val CLE_THEME = "theme"
    }
}
