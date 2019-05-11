@file:Suppress("DEPRECATION")

package pl.idappstudio.jakdobrzesieznacie.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.util.AdMobUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil
import java.util.*
import kotlin.concurrent.schedule


class IntroActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        MobileAds.initialize(this, resources.getString(R.string.adMob_id))

        AdMobUtil(this@IntroActivity, resources.getString(R.string.adMob_login_ad_id))

        if (FirebaseAuth.getInstance().currentUser == null) {

            Timer("StartActivity", false).schedule(1000) {

                startActivity<LoginMenuActivity>()
                finish()

            }

        } else {

            UserUtil.initialize {

                if(it != "") {

                    UserUtil.initializeUser {

                        startActivity<MenuActivity>()
                        finish()

                    }

                } else {

                    startActivity<LoginMenuActivity>()
                    finish()

                }

            }

        }

    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        hideSystemUI()
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    override fun onBackPressed() {
        hideSystemUI()
        super.onBackPressed()
    }

    private fun hideSystemUI(){

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    }

    override fun onWindowFocusChanged(hasFocus:Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

}
