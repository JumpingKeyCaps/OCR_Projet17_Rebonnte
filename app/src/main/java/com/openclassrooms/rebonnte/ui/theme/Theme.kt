package com.openclassrooms.rebonnte.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color.Black, // Pour une couleur de fond sombre
    surface = Rebonnte_black, // Couleur de surface sombre
    onPrimary = Color.White, // Couleur du texte sur les éléments primaires
    onSurface = Color.White, // Couleur du texte sur la surface sombre
    onSurfaceVariant = Color.Gray // Couleur de texte pour les variantes de surface

)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White, // Fond clair
    surface = Color(0xFFF5F5F5), // Surface claire
    onPrimary = Color.Black, // Texte sombre sur élément primaire
    onSurface = Color.Black, // Texte sombre sur surface
    onSurfaceVariant = Color.DarkGray // Variante de texte sombre
)

@Composable
fun RebonnteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> DarkColorScheme
    }
    LaunchedEffect(darkTheme) {
        systemUiController.setSystemBarsColor(
            color = DarkColorScheme.background
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}