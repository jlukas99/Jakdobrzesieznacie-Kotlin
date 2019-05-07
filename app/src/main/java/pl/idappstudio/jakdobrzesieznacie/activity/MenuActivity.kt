@file:Suppress("DEPRECATION")

package pl.idappstudio.jakdobrzesieznacie.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_menu.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.enums.StatusMessage
import pl.idappstudio.jakdobrzesieznacie.fragments.*
import pl.idappstudio.jakdobrzesieznacie.util.AdMobUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

class MenuActivity : AppCompatActivity() {

    companion object {

        val EXTRA_USER_ITEM = "user"
        val EXTRA_USER_IMAGE_TRANSITION_NAME = "image"
        val EXTRA_USER_NAME_TRANSITION_NAME = "name"
        val EXTRA_USER_BTN_CHAT_TRANSITION_NAME = "chat"
        val EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME = "favorite"
        val EXTRA_USER_STATUS_GAME_TRANSITION_NAME = "game-status"
        val EXTRA_USER_IMAGE_GAME_TRANSITION_NAME = "game-image"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val adManager = AdMobUtil(this@MenuActivity, resources.getString(R.string.adMob_menu_ad_id))
        val ad:InterstitialAd = adManager.getAd()

        ad.adListener = object: AdListener() {
            override fun onAdLoaded() {
                ad.show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                adManager.createAd(this@MenuActivity, resources.getString(R.string.adMob_menu_ad_id))
            }

            override fun onAdOpened() {

            }

            override fun onAdLeftApplication() {

            }

            override fun onAdClosed() {
                adManager.createAd(this@MenuActivity, resources.getString(R.string.adMob_menu_ad_id))
            }
        }

        fab.setOnClickListener {

            if(!navigation.menu.getItem(2).isChecked || !navigation2.menu.getItem(2).isChecked) {
                openFragment(FriendsFragment(), "friends")
                navigation.menu.getItem(2).isChecked = true
                navigation2.menu.getItem(2).isChecked = true
            }

        }

        navigation.menu.getItem(2).isVisible = false
        navigation2.menu.getItem(2).isVisible = false

        openFragment(FriendsFragment(), "friends")

        navigation2.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.navigation_add_friends -> {

                    if(!it.isChecked) {

                        openFragment(InvitesFragment(), "invites")
                        navigation.menu.getItem(2).isChecked = true

                    }
                    true
                }

                R.id.navigation_profile -> {
                    if(!it.isChecked) {

                        openFragment(ProfileFragment(), "profile")
                        navigation.menu.getItem(2).isChecked = true

                    }
                    true
                }
                else -> false
            }
        }

        navigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.navigation_pack -> {
                    if(!it.isChecked) {

                        openFragment(PackFragment(), "pack")
                        navigation2.menu.getItem(2).isChecked = true

                    }
                    true
                }

                R.id.navigation_states -> {
                    if(!it.isChecked) {

                        openFragment(AchivmentsFragment(), "achivment")
                        navigation2.menu.getItem(2).isChecked = true

                    }
                    true
                }

                else -> false
            }
        }

        navigation.menu.getItem(2).isChecked = true
        navigation2.menu.getItem(2).isChecked = true

        navigation.menu.getItem(2).isEnabled = false
        navigation2.menu.getItem(2).isEnabled = false

    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment, tag: String) {

        val fManager = supportFragmentManager
        val fTransaction = fManager.beginTransaction()

        val tag2 = fManager.findFragmentByTag(tag)

        if (tag2 == null) {

            fTransaction.add(R.id.container, fragment, tag).commit()

        } else {

            fTransaction.disallowAddToBackStack().replace(R.id.container, tag2, tag).commit()

        }

    }

    override fun onResume() {
        super.onResume()
        hideNavigationBar()
        UserUtil.getUser {}
        UserUtil.updateStatus(StatusMessage.inmenu)
    }

    override fun onDestroy() {
        super.onDestroy()
        UserUtil.stopListener()
        UserUtil.updateStatus(StatusMessage.offline)
    }

    private fun hideNavigationBar() {

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.decorView.systemUiVisibility = flags

    }
}
