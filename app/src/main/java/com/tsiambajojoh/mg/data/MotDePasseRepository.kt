package com.tsiambajojoh.mg.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MotDePasseRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val portee = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val liste: SnapshotStateList<MotDePasse> = mutableStateListOf()

    private var prochainId: Long = 1L

    init {
        charger()
    }

    private fun charger() {
        liste.clear()
        val chiffre = prefs.getString(CLE_DONNEES, null) ?: return
        val brut = Chiffrement.dechiffrer(chiffre) ?: return
        var maxId = 0L
        try {
            val arr = JSONArray(brut)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val id = o.optLong("id", 0L)
                if (id <= 0L) continue
                if (id > maxId) maxId = id
                liste.add(
                    MotDePasse(
                        id = id,
                        nom = o.optString("nom", ""),
                        siteCle = o.optString("siteCle", ""),
                        siteNom = o.optString("siteNom", ""),
                        identifiant = o.optString("identifiant", ""),
                        secret = o.optString("secret", ""),
                        note = o.optString("note", ""),
                        horodatage = o.optLong("horodatage", 0L)
                    )
                )
            }
        } catch (_: Exception) {
        }
        prochainId = maxId + 1
    }

    private fun sauvegarder() {
        val arr = JSONArray()
        for (i in liste.indices) {
            val m = liste[i]
            val o = JSONObject()
            o.put("id", m.id)
            o.put("nom", m.nom)
            o.put("siteCle", m.siteCle)
            o.put("siteNom", m.siteNom)
            o.put("identifiant", m.identifiant)
            o.put("secret", m.secret)
            o.put("note", m.note)
            o.put("horodatage", m.horodatage)
            arr.put(o)
        }
        val json = arr.toString()
        portee.launch {
            val chiffre = Chiffrement.chiffrer(json)
            prefs.edit().putString(CLE_DONNEES, chiffre).apply()
        }
    }

    fun ajouter(
        nom: String,
        siteCle: String,
        siteNom: String,
        identifiant: String,
        secret: String,
        note: String
    ): Long {
        val id = prochainId++
        liste.add(
            MotDePasse(
                id = id,
                nom = nom,
                siteCle = siteCle,
                siteNom = siteNom,
                identifiant = identifiant,
                secret = secret,
                note = note,
                horodatage = System.currentTimeMillis()
            )
        )
        sauvegarder()
        return id
    }

    fun modifier(
        id: Long,
        nom: String,
        siteCle: String,
        siteNom: String,
        identifiant: String,
        secret: String,
        note: String
    ) {
        val idx = indexDe(id)
        if (idx < 0) return
        val ancien = liste[idx]
        liste[idx] = MotDePasse(
            id = id,
            nom = nom,
            siteCle = siteCle,
            siteNom = siteNom,
            identifiant = identifiant,
            secret = secret,
            note = note,
            horodatage = ancien.horodatage
        )
        sauvegarder()
    }

    fun supprimer(id: Long) {
        val idx = indexDe(id)
        if (idx < 0) return
        liste.removeAt(idx)
        sauvegarder()
    }

    fun trouver(id: Long): MotDePasse? {
        val idx = indexDe(id)
        return if (idx < 0) null else liste[idx]
    }

    fun exporterJsonChiffre(): String {
        val arr = JSONArray()
        for (i in liste.indices) {
            val m = liste[i]
            val o = JSONObject()
            o.put("id", m.id)
            o.put("nom", m.nom)
            o.put("siteCle", m.siteCle)
            o.put("siteNom", m.siteNom)
            o.put("identifiant", m.identifiant)
            o.put("secret", m.secret)
            o.put("note", m.note)
            o.put("horodatage", m.horodatage)
            arr.put(o)
        }
        return Chiffrement.chiffrer(arr.toString())
    }

    fun importerJsonChiffre(contenu: String): Boolean {
        val brut = Chiffrement.dechiffrer(contenu.trim()) ?: return false
        return try {
            val arr = JSONArray(brut)
            val nouvelles = ArrayList<MotDePasse>(arr.length())
            var maxId = prochainId - 1
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val id = o.optLong("id", 0L)
                if (id <= 0L) continue
                if (id > maxId) maxId = id
                nouvelles.add(
                    MotDePasse(
                        id = id,
                        nom = o.optString("nom", ""),
                        siteCle = o.optString("siteCle", ""),
                        siteNom = o.optString("siteNom", ""),
                        identifiant = o.optString("identifiant", ""),
                        secret = o.optString("secret", ""),
                        note = o.optString("note", ""),
                        horodatage = o.optLong("horodatage", 0L)
                    )
                )
            }
            val parId = HashMap<Long, MotDePasse>(liste.size + nouvelles.size + 1)
            for (i in liste.indices) parId[liste[i].id] = liste[i]
            for (i in nouvelles.indices) parId[nouvelles[i].id] = nouvelles[i]
            liste.clear()
            for (m in parId.values) liste.add(m)
            prochainId = maxId + 1
            sauvegarder()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun libererCache() {
    }

    private fun indexDe(id: Long): Int {
        for (i in liste.indices) {
            if (liste[i].id == id) return i
        }
        return -1
    }

    companion object {
        private const val PREFS = "tsiamba_prefs"
        private const val CLE_DONNEES = "mots_de_passe"
    }
}
