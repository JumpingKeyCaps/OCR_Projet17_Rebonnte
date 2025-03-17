package com.openclassrooms.rebonnte


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.data.service.authentication.FirebaseAuthService
import com.openclassrooms.rebonnte.navigation.RebonnteNavHost
import com.openclassrooms.rebonnte.navigation.ScreensNav
import com.openclassrooms.rebonnte.ui.noInternet.NoInternetScreen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authService: FirebaseAuthService

    private val networkStatus = HashMap<String, Boolean>()

    /**
     * OnCreate method of the activity.
     * @param savedInstanceState The saved instance state of the activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
        setContent {
            //Internet connection checker
            val isInternetConnected = remember { mutableStateOf(isInternetAvailable(this)) }
            DisposableEffect(Unit) {
                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        networkStatus[network.toString()] = true // Marquer ce réseau comme connecté
                        val hasConnection = networkStatus.containsValue(true) // Vérifie si au moins un réseau est connecté
                        isInternetConnected.value = hasConnection
                    }
                    override fun onLost(network: Network) {
                        networkStatus.remove(network.toString()) // Marquer ce réseau comme déconnecté
                        val hasConnection = networkStatus.containsValue(true) // Vérifie si au moins un réseau est connecté
                        isInternetConnected.value = hasConnection
                    }
                    override fun onUnavailable() {
                        isInternetConnected.value = false // Aucune connexion disponible
                    }
                }
                val networkRequest = NetworkRequest.Builder().build()
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
                onDispose {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                }
            }

            //Main navigation
            val navController = rememberNavController()
            RebonnteTheme {
                if(isInternetConnected.value){ // -> internet available !
                    RebonnteNavHost(
                        navHostController = navController,
                        startDestination = if(FirebaseAuth.getInstance().currentUser != null) ScreensNav.Main.route else ScreensNav.SignIn.route,
                        onLogOutAction = { authService.signOutUser()   }// log out
                    )
                }else{
                    NoInternetScreen()
                }
            }
        }
    }

    /**
     * Utility function to check is an internet connection is available.
     * @param context The context of the activity.
     * @return True if an internet connection is available, false otherwise.
     */
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false
    }

    /**
     * Companion object to store the main activity instance.
     */
    companion object {
        lateinit var mainActivity: MainActivity
    }
}


