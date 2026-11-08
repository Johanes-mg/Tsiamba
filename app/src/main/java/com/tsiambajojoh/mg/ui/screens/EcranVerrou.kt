package com.tsiambajojoh.mg.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tsiambajojoh.mg.data.AuthentificationBiometrique
import com.tsiambajojoh.mg.data.Langue
import com.tsiambajojoh.mg.data.MethodeVerrou
import com.tsiambajojoh.mg.data.Textes

@Composable
fun EcranVerrou(
    activite: Activity,
    methode: MethodeVerrou,
    langue: Langue,
    onDeverrouille: () -> Unit
) {
    val t = { cle: String -> Textes.t(cle, langue) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultat ->
        if (resultat.resultCode == Activity.RESULT_OK) {
            onDeverrouille()
        }
    }

    LaunchedEffect(methode) {
        if (methode == MethodeVerrou.AUCUNE) {
            onDeverrouille()
            return@LaunchedEffect
        }
        val intent = AuthentificationBiometrique.creerIntentVerrouillage(
            activite = activite,
            titre = t("verrou_titre"),
            sousTitre = t("verrou_sous_titre")
        )
        if (intent != null) {
            launcher.launch(intent)
        } else {
            onDeverrouille()
        }
    }

    if (methode == MethodeVerrou.AUCUNE) {
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            IconeDrawableFallback(
                nom = "logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = t("verrou_titre"),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = t("verrou_sous_titre"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val intent = AuthentificationBiometrique.creerIntentVerrouillage(
                        activite = activite,
                        titre = t("verrou_titre"),
                        sousTitre = t("verrou_sous_titre")
                    )
                    if (intent != null) launcher.launch(intent) else onDeverrouille()
                },
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = t("verrou_requis"))
            }
        }
    }
}
