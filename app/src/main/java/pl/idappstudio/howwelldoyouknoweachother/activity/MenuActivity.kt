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
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil


class MenuActivity : AppCompatActivity() {

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

        fab_play.fabIcon = iconNav()

        openFragment(FriendsFragment())

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_pack -> {
                    openFragment(PackFragment())
                    true
                }
                R.id.navigation_states -> {
                    openFragment(StatesFragment())
                    true
                }
                R.id.navigation_play -> {
                    openFragment(FriendsFragment())
                    true
                }
                R.id.navigation_add_friends -> {
                    openFragment(InvitesFragment())
                    true
                }
                R.id.navigation_profile -> {
                    openFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        navigation.menu.getItem(2).isChecked = true

    }

    private fun iconNav() : Drawable {

        val dr = resources.getDrawable(R.mipmap.games_icon)
        val bitmap = (dr as BitmapDrawable).bitmap

        return BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 100, 100, true))

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
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}
