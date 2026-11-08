package com.tsiambajojoh.mg.data

enum class Langue(val code: String, val libelle: String) {
    FRANCAIS("fr", "Français"),
    ENGLISH("en", "English"),
    MALAGASY("mg", "Malagasy");

    companion object {
        fun depuisCode(code: String?): Langue =
            values().firstOrNull { it.code == code } ?: FRANCAIS
    }
}
