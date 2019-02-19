@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_menu.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.fragments.*
import pl.idappstudio.howwelldoyouknoweachother.util.AdMobUtil
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil

class MenuActivity : AppCompatActivity() {

    var b: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val adManager = AdMobUtil(this@MenuActivity)
        val ad:InterstitialAd = adManager.getAd()

        ad.adListener = object: AdListener() {
            override fun onAdLoaded() {
                ad.show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                adManager.createAd(this@MenuActivity)
            }

            override fun onAdOpened() {

            }

            override fun onAdLeftApplication() {

            }

            override fun onAdClosed() {
                adManager.createAd(this@MenuActivity)
            }
        }

        navigation.selectedItemId = R.id.navigation_play

        fab.setOnClickListener {

            if(!navigation.menu.getItem(2).isChecked) {
                openFragment(FriendsFragment())
                navigation.selectedItemId = R.id.navigation_play
            }

        }

        openFragment(FriendsFragment())

        navigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.navigation_pack -> {
                    if(!it.isChecked) {

                        openFragment(PackFragment())

                    }
                    true
                }
                R.id.navigation_states -> {
                    if(!it.isChecked) {

                        openFragment(AchivmentsFragment())

                    }
                    true
                }
                R.id.navigation_play -> {
                    true
                }
                R.id.navigation_add_friends -> {
                    if(!it.isChecked) {

                        openFragment(InvitesFragment())

                    }
                    true
                }
                R.id.navigation_profile -> {
                    if(!it.isChecked) {

                        openFragment(ProfileFragment())

                    }
                    true
                }
                else -> false
            }
        }

        navigation.menu.getItem(2).isEnabled = false

    }

    private fun openFragment(fragment: Fragment) {

            supportFragmentManager.beginTransaction()
                .disallowAddToBackStack()
                .replace(R.id.container, fragment)
                .commit()

    }

    override fun onResume() {
        super.onResume()
        FirestoreUtil.initialize()
        hideNavigationBar()
    }

    private fun hideNavigationBar() {

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.decorView.systemUiVisibility = flags

        val decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility = flags
            }
        }
    }
}
