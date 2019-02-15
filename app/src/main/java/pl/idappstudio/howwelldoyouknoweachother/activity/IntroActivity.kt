@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.util.AdMobUtil
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import java.util.*
import kotlin.concurrent.schedule


class IntroActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        MobileAds.initialize(this, resources.getString(R.string.adMob_id))

        AdMobUtil(this@IntroActivity)

        if (FirebaseAuth.getInstance().currentUser == null) {
            Timer("StartActivity", false).schedule(1000) {
                startActivity<LoginMenuActivity>()
                finish()
            }
        } else {
            FirestoreUtil.getCurrentUser {

                startActivity<MenuActivity>()
                finish()

            }
        }

    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}
