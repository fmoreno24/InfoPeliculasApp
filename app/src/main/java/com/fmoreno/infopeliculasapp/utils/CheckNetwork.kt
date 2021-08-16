package com.fmoreno.infopeliculasapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.fmoreno.infopeliculasapp.ui.MainActivity.Companion.isNetworkConnected
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class CheckNetwork(context: Context) {
    val mContext = context

    /**
     * Metodo utilizado para escuchar si hay algun cambio de conexión
     */
    // Network Check
    @RequiresApi(Build.VERSION_CODES.N)
    fun registerNetworkCallback() {
        try {

            val connectivityManager =
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isNetworkConnected = true // Global Static Variable
                }

                override fun onLost(network: Network) {
                    isNetworkConnected = false // Global Static Variable
                }
            }
            )
            isNetworkConnected = false
        } catch (e: Exception) {
            isNetworkConnected = false
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}