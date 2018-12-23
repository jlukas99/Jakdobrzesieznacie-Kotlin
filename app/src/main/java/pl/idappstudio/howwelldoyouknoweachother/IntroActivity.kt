@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.concurrent.schedule


class IntroActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

    }

    override fun onResume() {
        super.onResume()
        if(FirebaseAuth.getInstance().currentUser == null) {

            val intent = Intent(this, LoginMenuActivity::class.java)

            Timer("StartIntent", false).schedule(4500) {
                startActivity(intent)
                finish()
            }

        } else {

            val intent = Intent(this, LoginActivity::class.java)

            Timer("StartIntent", false).schedule(4500) {
                startActivity(intent)
                finish()
            }

        }
    }
}
