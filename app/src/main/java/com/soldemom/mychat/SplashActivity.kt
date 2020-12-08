package com.soldemom.mychat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    lateinit var remoteConfig: FirebaseRemoteConfig
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splash_title

        val animation = AnimationUtils.loadAnimation(this,R.anim.alpha)
        splash_title.startAnimation(animation)

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            this.minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)


        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                displayMessage()
            }


        animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        splash_title.animation = animation

    }

    fun displayMessage() {
        val splash_background = remoteConfig.getString("splash_background")
        val caps = remoteConfig.getBoolean("splash_message_caps")
        val splash_message = remoteConfig.getString("splash_message")

        if (caps) {
           AlertDialog.Builder(this)
               .setMessage(splash_message).setPositiveButton("확인") { _,_ ->
                   finish()
               }.create().show()
        }

        splash_constraint_layout.setBackgroundColor(Color.parseColor(splash_background))

    }
}