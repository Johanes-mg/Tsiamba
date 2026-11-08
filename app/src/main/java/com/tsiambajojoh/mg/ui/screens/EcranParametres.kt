package com.tsiambajojoh.mg.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tsiambajojoh.mg.data.Langue
import com.tsiambajojoh.mg.data.MethodeVerrou
import com.tsiambajojoh.mg.data.MotDePasseRepository
import com.tsiambajojoh.mg.data.Preferences
import com.tsiambajojoh.mg.data.Textes
import java.io.InputStream
import java.io.OutputStream

@Composable
fun EcranParametres(
    preferences: Preferences,
    repository: MotDePasseRepository,
    onRetour: () -> Unit
) {
    val langue = preferences.langue.value
    val methode = preferences.methodeVerrou.value
    val t = { cle: String -> Textes.t(cle, langue) }
    val contexte = LocalContext.current
    var message by remember { mutableStateOf<String?>(null) }

    val launcherExport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            val os: OutputStream? = contexte.contentResolver.openOutputStream(uri)
            if (os != null) {
                os.use { it.write(repository.exporterJsonChiffre().toByteArray(Charsets.UTF_8)) }
                message = t("export_reussi")
            }
        } catch (_: Exception) {
            message = t("import_erreur")
        }
    }

    val launcherImport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            val ins: InputStream? = contexte.contentResolver.openInputStream(uri)
            if (ins != null) {
                val contenu = ins.use { it.readBytes().toString(Charsets.UTF_8) }
                val ok = repository.importerJsonChiffre(contenu)
                message = if (ok) t("import_reussi") else t("import_erreur")
            }
        } catch (_: Exception) {
            message = t("import_erreur")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = t("parametres")) },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("retour"))
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = t("langue"), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (l in Langue.values()) {
                            FilterChip(
                                selected = langue == l,
                                onClick = { preferences.changerLangue(l) },
                                label = { Text(text = l.libelle) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = t("methode_verrou"), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (m in MethodeVerrou.values()) {
                            val label = when (m) {
                                MethodeVerrou.BIOMETRIE -> t("verrou_bio")
                                MethodeVerrou.CODE -> t("verrou_code")
                                MethodeVerrou.AUCUNE -> t("verrou_aucune")
                            }
                            FilterChip(
                                selected = methode == m,
                                onClick = { preferences.changerMethodeVerrou(m) },
                                label = { Text(text = label) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = t("donnees"), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { launcherExport.launch("tsiamba-export.json") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            IconeDrawableFallback(nom = "export_json", modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = t("exporter_json"),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { launcherImport.launch(arrayOf("application/json", "text/plain", "*/*")) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            IconeDrawableFallback(nom = "import_json", modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = t("importer_json"),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            if (message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
