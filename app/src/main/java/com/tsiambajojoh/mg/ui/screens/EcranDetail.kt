package com.tsiambajojoh.mg.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.tsiambajojoh.mg.data.AuthentificationBiometrique
import com.tsiambajojoh.mg.data.Langue
import com.tsiambajojoh.mg.data.MotDePasseRepository
import com.tsiambajojoh.mg.data.Textes
import com.tsiambajojoh.mg.ui.theme.RougeErreur
import com.tsiambajojoh.mg.ui.theme.TexteSecondaire

@Composable
fun EcranDetail(
    activite: Activity,
    repository: MotDePasseRepository,
    langue: Langue,
    id: Long,
    onRetour: () -> Unit,
    onModifier: (Long) -> Unit,
    onSupprimer: (Long) -> Unit
) {
    val t = { cle: String -> Textes.t(cle, langue) }
    val mdp = remember(id) { repository.trouver(id) }
    var secretVisible by remember { mutableStateOf(false) }
    var confirmation by remember { mutableStateOf(false) }
    var deverrouille by remember { mutableStateOf(false) }
    var introuvable by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultat ->
        if (resultat.resultCode == Activity.RESULT_OK) {
            deverrouille = true
        } else {
            onRetour()
        }
    }

    LaunchedEffect(id) {
        if (mdp == null) {
            introuvable = true
        } else {
            val intent = AuthentificationBiometrique.creerIntentVerrouillage(
                activite = activite,
                titre = mdp.siteNom,
                sousTitre = mdp.identifiant
            )
            if (intent != null) {
                launcher.launch(intent)
            } else {
                deverrouille = true
            }
        }
    }

    LaunchedEffect(introuvable) {
        if (introuvable) onRetour()
    }

    if (mdp == null) return

    if (!deverrouille) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (mdp.nom.isNotEmpty()) mdp.nom else mdp.siteNom) },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("retour"))
                    }
                },
                actions = {
                    IconButton(onClick = { onModifier(id) }) {
                        IconeDrawableFallback(nom = "edit", modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = { confirmation = true }) {
                        IconeDrawableFallback(nom = "delete", modifier = Modifier.size(24.dp))
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
        ) {
            if (mdp.nom.isNotEmpty()) {
                ChampDetail(label = t("nom"), valeur = mdp.nom)
                Spacer(modifier = Modifier.height(12.dp))
            }
            ChampDetail(label = t("site"), valeur = mdp.siteNom)
            Spacer(modifier = Modifier.height(12.dp))
            ChampDetail(label = t("identifiant"), valeur = mdp.identifiant)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = t("mot_de_passe"),
                        style = MaterialTheme.typography.labelSmall,
                        color = TexteSecondaire
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (secretVisible) mdp.secret else "\u2022".repeat(mdp.secret.length.coerceAtLeast(6)),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { secretVisible = !secretVisible }) {
                            IconeDrawableFallback(
                                nom = if (secretVisible) "hide" else "show",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            if (mdp.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                ChampDetail(label = t("note"), valeur = mdp.note)
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = { onModifier(id) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = t("modifier_ce_mdp"))
            }
        }
    }

    if (confirmation) {
        DialogueConfirm(
            titre = t("supprimer_titre"),
            message = t("supprimer_message"),
            texteConfirmer = t("supprimer"),
            texteAnnuler = t("annuler"),
            couleurConfirmer = RougeErreur,
            onConfirmer = {
                confirmation = false
                onSupprimer(id)
            },
            onAnnuler = { confirmation = false }
        )
    }
}

@Composable
private fun ChampDetail(label: String, valeur: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TexteSecondaire
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valeur,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
