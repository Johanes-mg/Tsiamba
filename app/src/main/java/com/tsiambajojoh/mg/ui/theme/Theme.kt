package com.tsiambajojoh.mg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.tsiambajojoh.mg.data.ThemeApp

private val SchemaClair = lightColorScheme(
    primary = BleuPrimaire,
    onPrimary = SurfaceBlanche,
    primaryContainer = BleuDoux,
    onPrimaryContainer = BleuFonce,
    secondary = AccentVert,
    onSecondary = SurfaceBlanche,
    background = FondClair,
    onBackground = TextePrincipal,
    surface = SurfaceBlanche,
    onSurface = TextePrincipal,
    surfaceVariant = SurfaceDouce,
    onSurfaceVariant = TextePrincipal,
    outline = BordureDouce,
    error = RougeErreur
)

private val SchemaSombre = darkColorScheme(
    primary = BleuClair,
    onPrimary = BleuFonce,
    primaryContainer = BleuPrimaire,
    onPrimaryContainer = SurfaceBlanche,
    secondary = AccentVert,
    onSecondary = SurfaceBlanche,
    background = FondSombre,
    onBackground = SurfaceBlanche,
    surface = SurfaceSombre,
    onSurface = SurfaceBlanche,
    surfaceVariant = SurfaceSombre2,
    onSurfaceVariant = SurfaceBlanche,
    outline = BordureSombre,
    error = RougeErreur
)

@Composable
fun TsiambaTheme(theme: ThemeApp = ThemeApp.SYSTEME, content: @Composable () -> Unit) {
    val sombre = when (theme) {
        ThemeApp.CLAIR -> false
        ThemeApp.SOMBRE -> true
        ThemeApp.SYSTEME -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (sombre) SchemaSombre else SchemaClair,
        typography = Typographie,
        content = content
    )
}
