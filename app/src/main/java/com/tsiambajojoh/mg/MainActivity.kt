package com.tsiambajojoh.mg

import android.content.ComponentCallbacks2
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tsiambajojoh.mg.data.MethodeVerrou
import com.tsiambajojoh.mg.data.MotDePasseRepository
import com.tsiambajojoh.mg.data.Preferences
import com.tsiambajojoh.mg.data.Sites
import com.tsiambajojoh.mg.ui.screens.EcranAccueil
import com.tsiambajojoh.mg.ui.screens.EcranAjout
import com.tsiambajojoh.mg.ui.screens.EcranDetail
import com.tsiambajojoh.mg.ui.screens.EcranParametres
import com.tsiambajojoh.mg.ui.screens.EcranVerrou
import com.tsiambajojoh.mg.ui.theme.TsiambaTheme

class MainActivity : ComponentActivity() {

    private lateinit var repository: MotDePasseRepository
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Sites.initialiserCache(applicationContext)
        repository = MotDePasseRepository(applicationContext)
        preferences = Preferences(applicationContext)
        setContent {
            TsiambaTheme(theme = preferences.theme.value) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val autoriserCaptures = preferences.autoriserCaptures.value
                    LaunchedEffect(autoriserCaptures) {
                        if (autoriserCaptures) {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        } else {
                            window.setFlags(
                                WindowManager.LayoutParams.FLAG_SECURE,
                                WindowManager.LayoutParams.FLAG_SECURE
                            )
                        }
                    }
                    ApplicationTsiamba(
                        activite = this,
                        repository = repository,
                        preferences = preferences
                    )
                }
            }
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            if (::repository.isInitialized) repository.libererCache()
        }
    }
}

private sealed interface Ecran {
    data object Verrou : Ecran
    data object Accueil : Ecran
    data class Detail(val id: Long) : Ecran
    data class Ajout(val idEdition: Long?) : Ecran
    data object Parametres : Ecran
}

@Composable
private fun ApplicationTsiamba(
    activite: ComponentActivity,
    repository: MotDePasseRepository,
    preferences: Preferences
) {
    var ecran by remember {
        mutableStateOf<Ecran>(
            if (preferences.methodeVerrou.value == MethodeVerrou.AUCUNE) Ecran.Accueil else Ecran.Verrou
        )
    }
    val langue = preferences.langue.value
    val methode = preferences.methodeVerrou.value

    when (val courant = ecran) {
        is Ecran.Verrou -> {
            EcranVerrou(
                activite = activite,
                methode = methode,
                langue = langue,
                onDeverrouille = { ecran = Ecran.Accueil }
            )
        }
        is Ecran.Accueil -> {
            EcranAccueil(
                repository = repository,
                preferences = preferences,
                langue = langue,
                onOuvrir = { id -> ecran = Ecran.Detail(id) },
                onAjouter = { ecran = Ecran.Ajout(null) },
                onParametres = { ecran = Ecran.Parametres }
            )
        }
        is Ecran.Detail -> {
            BackHandler { ecran = Ecran.Accueil }
            EcranDetail(
                activite = activite,
                repository = repository,
                langue = langue,
                id = courant.id,
                onRetour = { ecran = Ecran.Accueil },
                onModifier = { id -> ecran = Ecran.Ajout(id) },
                onSupprimer = { id ->
                    repository.supprimer(id)
                    ecran = Ecran.Accueil
                }
            )
        }
        is Ecran.Ajout -> {
            BackHandler { ecran = Ecran.Accueil }
            EcranAjout(
                repository = repository,
                langue = langue,
                idEdition = courant.idEdition,
                onTermine = { ecran = Ecran.Accueil },
                onAnnuler = { ecran = Ecran.Accueil }
            )
        }
        is Ecran.Parametres -> {
            BackHandler { ecran = Ecran.Accueil }
            EcranParametres(
                preferences = preferences,
                repository = repository,
                onRetour = { ecran = Ecran.Accueil }
            )
        }
    }
}
