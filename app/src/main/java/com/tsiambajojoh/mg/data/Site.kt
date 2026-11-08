package com.tsiambajojoh.mg.data

import android.content.Context

data class Site(
    val cle: String,
    val nom: String,
    val drawable: String
)

object Sites {

    val liste: List<Site> = listOf(
        Site("2fa", "2FA", "deux_fa"),
        Site("airtel", "Airtel", "airtel"),
        Site("amazon", "Amazon", "amazon"),
        Site("android", "Android", "android"),
        Site("bitcoin", "Bitcoin", "bitcoin"),
        Site("canva", "Canva", "canva"),
        Site("card_assurance", "Carte Assurance", "card_assurance"),
        Site("card_bank", "Carte Bancaire", "card_bank"),
        Site("card_credit", "Carte Crédit", "card_credit"),
        Site("discord", "Discord", "discord"),
        Site("facebook", "Facebook", "facebook"),
        Site("formspree", "Formspree", "formspree"),
        Site("github", "GitHub", "github"),
        Site("gitlab", "GitLab", "gitlab"),
        Site("google", "Google", "google"),
        Site("google_drive", "Google Drive", "google_drive"),
        Site("google_gmail", "Gmail", "google_gmail"),
        Site("infomaniak", "Infomaniak", "infomaniak"),
        Site("instagram", "Instagram", "instagram"),
        Site("iphone", "iPhone", "iphone"),
        Site("line", "Line", "line"),
        Site("linkedin", "LinkedIn", "linkedin"),
        Site("linux", "Linux", "linux"),
        Site("macbook", "MacBook", "macbook"),
        Site("mastodon", "Mastodon", "mastodon"),
        Site("mega", "MEGA", "mega"),
        Site("microsoft", "Microsoft", "microsoft"),
        Site("microsoft_outlook", "Outlook", "microsoft_outlook"),
        Site("netflix", "Netflix", "netflix"),
        Site("orange", "Orange", "orange"),
        Site("paramount", "Paramount+", "paramount"),
        Site("paypal", "PayPal", "paypal"),
        Site("pinterest", "Pinterest", "pinterest"),
        Site("proton_mail", "ProtonMail", "proton_mail"),
        Site("reddit", "Reddit", "reddit"),
        Site("snapchat", "Snapchat", "snapchat"),
        Site("spotify", "Spotify", "spotify"),
        Site("steam", "Steam", "steam"),
        Site("telegram", "Telegram", "telegram"),
        Site("tiktok", "TikTok", "tiktok"),
        Site("twitch", "Twitch", "twitch"),
        Site("twitter", "Twitter / X", "twitter"),
        Site("wattpad", "Wattpad", "wattpad"),
        Site("whatsapp", "WhatsApp", "whatsapp"),
        Site("windows", "Windows", "windows"),
        Site("yahoo", "Yahoo", "yahoo"),
        Site("yas", "Yas", "yas"),
        Site("youtube", "YouTube", "youtube"),
        Site("autre", "Autre", "")
    )

    private val indexParCle: HashMap<String, Site> =
        HashMap<String, Site>(liste.size + 1).apply {
            for (i in liste.indices) put(liste[i].cle, liste[i])
        }

    private val cacheResId: HashMap<String, Int> =
        HashMap<String, Int>(liste.size + 25)

    private var initialise = false

    fun initialiserCache(contexte: Context) {
        if (initialise) return
        initialise = true
        val ressources = contexte.resources
        val paquet = contexte.packageName
        for (i in liste.indices) {
            val d = liste[i].drawable
            if (d.isEmpty()) continue
            cacheResId[d] = ressources.getIdentifier(d, "drawable", paquet)
        }
        val extras = arrayOf(
            "plus", "search", "sort", "settings",
            "show", "hide", "edit", "delete",
            "export_json", "import_json", "logo",
            "light", "dark", "vide"
        )
        for (i in extras.indices) {
            cacheResId[extras[i]] = ressources.getIdentifier(extras[i], "drawable", paquet)
        }
    }

    fun resId(nom: String): Int = cacheResId[nom] ?: 0

    fun parCle(cle: String): Site = indexParCle[cle] ?: liste[liste.size - 1]

    fun parNom(nom: String): Site? {
        for (i in liste.indices) {
            if (liste[i].nom == nom) return liste[i]
        }
        return null
    }
}
