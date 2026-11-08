package com.tsiambajojoh.mg.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DialogueConfirm(
    titre: String,
    message: String,
    texteConfirmer: String,
    texteAnnuler: String,
    couleurConfirmer: Color,
    onConfirmer: () -> Unit,
    onAnnuler: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onAnnuler,
        title = { Text(text = titre) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirmer) {
                Text(text = texteConfirmer, color = couleurConfirmer)
            }
        },
        dismissButton = {
            TextButton(onClick = onAnnuler) {
                Text(text = texteAnnuler)
            }
        }
    )
}
