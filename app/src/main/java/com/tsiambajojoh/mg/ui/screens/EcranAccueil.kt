package com.tsiambajojoh.mg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tsiambajojoh.mg.data.Langue
import com.tsiambajojoh.mg.data.MotDePasse
import com.tsiambajojoh.mg.data.MotDePasseRepository
import com.tsiambajojoh.mg.data.Preferences
import com.tsiambajojoh.mg.data.Sites
import com.tsiambajojoh.mg.data.Textes
import com.tsiambajojoh.mg.data.ThemeApp
import com.tsiambajojoh.mg.ui.theme.BleuPrimaire
import com.tsiambajojoh.mg.ui.theme.SurfaceBlanche
import com.tsiambajojoh.mg.ui.theme.TexteSecondaire

@Composable
fun EcranAccueil(
    repository: MotDePasseRepository,
    preferences: Preferences,
    langue: Langue,
    onOuvrir: (Long) -> Unit,
    onAjouter: () -> Unit,
    onParametres: () -> Unit
) {
    val t = { cle: String -> Textes.t(cle, langue) }
    var modeRecherche by remember { mutableStateOf(false) }
    var requete by remember { mutableStateOf("") }
    var menuTriOuvert by remember { mutableStateOf(false) }
    var tri by remember { mutableIntStateOf(0) }
    
    val systemeEnSombre = isSystemInDarkTheme()
    val modeActuelEstSombre = when (preferences.theme.value) {
        ThemeApp.CLAIR -> false
        ThemeApp.SOMBRE -> true
        ThemeApp.SYSTEME -> systemeEnSombre
    }
    val iconTheme = if (modeActuelEstSombre) "light" else "dark"

    val listeAffichee = remember(repository.liste.size, requete, tri) {
        val base = ArrayList<MotDePasse>(repository.liste.size + 1)
        for (i in repository.liste.indices) {
            val m = repository.liste[i]
            if (requete.isNotEmpty()) {
                val r = requete.lowercase()
                if (!m.nom.lowercase().contains(r) &&
                    !m.siteNom.lowercase().contains(r) &&
                    !m.identifiant.lowercase().contains(r)
                ) continue
            }
            base.add(m)
        }
        when (tri) {
            0 -> base.sortBy { it.siteNom.lowercase() }
            1 -> base.sortByDescending { it.siteNom.lowercase() }
            2 -> base.sortByDescending { it.horodatage }
            3 -> base.sortBy { it.horodatage }
        }
        base
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    IconButton(onClick = { preferences.basculerTheme() }) {
                        IconeDrawableFallback(nom = iconTheme, modifier = Modifier.size(24.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { modeRecherche = !modeRecherche; if (!modeRecherche) requete = "" }) {
                        IconeDrawableFallback(nom = "search", modifier = Modifier.size(24.dp))
                    }
                    Box {
                        IconButton(onClick = { menuTriOuvert = true }) {
                            IconeDrawableFallback(nom = "sort", modifier = Modifier.size(24.dp))
                        }
                        DropdownMenu(
                            expanded = menuTriOuvert,
                            onDismissRequest = { menuTriOuvert = false }
                        ) {
                            DropdownMenuItem(text = { Text(t("tri_az")) }, onClick = { tri = 0; menuTriOuvert = false })
                            DropdownMenuItem(text = { Text(t("tri_za")) }, onClick = { tri = 1; menuTriOuvert = false })
                            DropdownMenuItem(text = { Text(t("tri_recent")) }, onClick = { tri = 2; menuTriOuvert = false })
                            DropdownMenuItem(text = { Text(t("tri_ancien")) }, onClick = { tri = 3; menuTriOuvert = false })
                        }
                    }
                    IconButton(onClick = onParametres) {
                        IconeDrawableFallback(nom = "settings", modifier = Modifier.size(24.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(BleuPrimaire)
                    .clickable { onAjouter() },
                contentAlignment = Alignment.Center
            ) {
                IconeDrawableFallback(nom = "plus", modifier = Modifier.size(36.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (modeRecherche) {
                OutlinedTextField(
                    value = requete,
                    onValueChange = { requete = it },
                    placeholder = { Text(t("rechercher")) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (requete.isNotEmpty()) {
                            IconButton(onClick = { requete = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            if (listeAffichee.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (requete.isNotEmpty()) {
                        Text(
                            text = t("aucun_resultat"),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TexteSecondaire
                        )
                    } else {
                        IconeDrawableFallback(
                            nom = "vide",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    items(items = listeAffichee, key = { it.id }) { mdp ->
                        CarteMotDePasse(mdp = mdp, onOuvrir = onOuvrir)
                    }
                    item { Spacer(modifier = Modifier.height(88.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CarteMotDePasse(
    mdp: MotDePasse,
    onOuvrir: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onOuvrir(mdp.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val site = remember(mdp.siteCle) { Sites.parCle(mdp.siteCle) }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(BleuPrimaire),
                contentAlignment = Alignment.Center
            ) {
                if (site.drawable.isNotEmpty()) {
                    IconeDrawableFallback(nom = site.drawable, modifier = Modifier.size(44.dp))
                } else {
                    val initiale = if (mdp.siteNom.isNotEmpty()) mdp.siteNom[0].uppercaseChar().toString() else "?"
                    Text(text = initiale, style = MaterialTheme.typography.titleMedium, color = SurfaceBlanche)
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (mdp.nom.isNotEmpty()) mdp.nom else mdp.siteNom,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = mdp.identifiant,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TexteSecondaire,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
