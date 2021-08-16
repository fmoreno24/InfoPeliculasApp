package com.fmoreno.infopeliculasapp.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.fmoreno.infopeliculasapp.R
import com.fmoreno.infopeliculasapp.utils.CheckNetwork
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.Exception

class SplashActivity : AppCompatActivity() {

    val segundos = 4
    val milisegundos = segundos * 1000
    val delay = 2

    var atg: Animation? = null
    var right_in: Animation? = null
    var zoom_forward_in: Animation? = null

    var mImageLogo: ImageView? = null
    var mTxtFooter: TextView? = null
    var pbprogreso: ProgressBar? = null


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        atg = AnimationUtils.loadAnimation(this, R.anim.atg)
        right_in = AnimationUtils.loadAnimation(this, R.anim.right_in)
        zoom_forward_in = AnimationUtils.loadAnimation(this, R.anim.zoom_forward_in)

        mImageLogo = findViewById<View>(R.id.img_logo) as ImageView
        pbprogreso = findViewById<View>(R.id.pbprogreso) as ProgressBar
        mTxtFooter = findViewById<View>(R.id.txtFooterInfo) as TextView

        // Register Callback - Call this in your app start!
        val network = CheckNetwork(applicationContext)
        network.registerNetworkCallback()

        startAnimation()
        pbprogreso!!.max = maximumProgress()

        nextActivity()
    }

    /**
     * metodo para animación de splash
     */
    private fun startAnimation() {
        try {
            mImageLogo!!.startAnimation(atg)
            mTxtFooter!!.startAnimation(right_in)
            pbprogreso!!.startAnimation(zoom_forward_in)
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    fun maximumProgress(): Int {
        return segundos - delay
    }

    /**
     * Metodo para ir al mainactiviy
     */
    private fun nextActivity() {
        try {
            object : CountDownTimer(milisegundos.toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    val mainActivity = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(mainActivity)
                    overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out)
                    finish()
                }
            }.start()
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }
}