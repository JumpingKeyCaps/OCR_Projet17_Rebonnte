package com.openclassrooms.rebonnte

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
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

    //test injection of authservice
    @Inject
    lateinit var authService: FirebaseAuthService

    private val networkStatus = HashMap<String, Boolean>() // Pour suivre l'état de chaque réseau


    private lateinit var myBroadcastReceiver: MyBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
        setContent {
            //debug of auth service injection
            Log.d("authDebug", "Connected user: ${authService.getCurrentConnectedUser()!=null} ")



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

      //  startBroadcastReceiver()  //crazy loop
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





    private fun startMyBroadcast() {
        val intent = Intent("com.rebonnte.ACTION_UPDATE")
        sendBroadcast(intent)
        startBroadcastReceiver()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun startBroadcastReceiver() {
        myBroadcastReceiver = MyBroadcastReceiver()
        val filter = IntentFilter().apply {
            addAction("com.rebonnte.ACTION_UPDATE")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myBroadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(myBroadcastReceiver, filter)
        }

        Handler().postDelayed({
           // startMyBroadcast()
        }, 20)
    }


    class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(mainActivity, "Update reçu", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        lateinit var mainActivity: MainActivity
    }
}


