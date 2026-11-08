package com.tsiambajojoh.mg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.tsiambajojoh.mg.data.Langue
import com.tsiambajojoh.mg.data.MotDePasseRepository
import com.tsiambajojoh.mg.data.Sites
import com.tsiambajojoh.mg.data.Textes
import com.tsiambajojoh.mg.ui.theme.SurfaceBlanche

@Composable
fun EcranAjout(
    repository: MotDePasseRepository,
    langue: Langue,
    idEdition: Long?,
    onTermine: () -> Unit,
    onAnnuler: () -> Unit
) {
    val t = { cle: String -> Textes.t(cle, langue) }
    val existant = remember(idEdition) {
        if (idEdition == null) null else repository.trouver(idEdition)
    }
    val listeSites = remember { Sites.liste }

    var nom by remember { mutableStateOf(existant?.nom ?: "") }
    var siteSelectionne by remember { mutableStateOf(Sites.parCle(existant?.siteCle ?: "facebook")) }
    var siteNomManuel by remember { mutableStateOf(existant?.siteNom ?: "") }
    var identifiant by remember { mutableStateOf(existant?.identifiant ?: "") }
    var secret by remember { mutableStateOf(existant?.secret ?: "") }
    var note by remember { mutableStateOf(existant?.note ?: "") }
    var secretVisible by remember { mutableStateOf(false) }
    var menuSiteOuvert by remember { mutableStateOf(false) }

    val titre = if (existant == null) t("nouveau_mdp") else t("modifier")
    val siteEstAutre = siteSelectionne.cle == "autre"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = titre) },
                navigationIcon = {
                    IconButton(onClick = onAnnuler) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("annuler"))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text(t("nom")) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = t("choisir_site"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { menuSiteOuvert = true }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (siteSelectionne.drawable.isNotEmpty()) {
                        IconeDrawableFallback(
                            nom = siteSelectionne.drawable,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                    }
                    Text(
                        text = siteSelectionne.nom,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = menuSiteOuvert,
                    onDismissRequest = { menuSiteOuvert = false },
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    for (i in listeSites.indices) {
                        val s = listeSites[i]
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (s.drawable.isNotEmpty()) {
                                        IconeDrawableFallback(
                                            nom = s.drawable,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                    Text(text = s.nom)
                                }
                            },
                            onClick = {
                                siteSelectionne = s
                                menuSiteOuvert = false
                            }
                        )
                    }
                }
            }

            if (siteEstAutre) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = siteNomManuel,
                    onValueChange = { siteNomManuel = it },
                    label = { Text(t("nom_site")) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = identifiant,
                onValueChange = { identifiant = it },
                label = { Text(t("identifiant")) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = secret,
                onValueChange = { secret = it },
                label = { Text(t("mot_de_passe")) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                visualTransformation = if (secretVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { secretVisible = !secretVisible }) {
                        IconeDrawableFallback(
                            nom = if (secretVisible) "hide" else "show",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(t("note")) },
                minLines = 3,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            val nomFinal = if (siteEstAutre) siteNomManuel.trim() else siteSelectionne.nom
            Button(
                onClick = {
                    if (nomFinal.isBlank()) return@Button
                    val cle = if (siteEstAutre) "autre" else siteSelectionne.cle
                    if (existant == null) {
                        repository.ajouter(nom.trim(), cle, nomFinal, identifiant, secret, note)
                    } else {
                        repository.modifier(existant.id, nom.trim(), cle, nomFinal, identifiant, secret, note)
                    }
                    onTermine()
                },
                enabled = nomFinal.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = SurfaceBlanche
                )
            ) {
                Text(text = if (existant == null) t("enregistrer") else t("mettre_a_jour"))
            }
        }
    }
}
