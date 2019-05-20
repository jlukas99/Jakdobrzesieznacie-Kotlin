@file:Suppress("DEPRECATION")

package pl.idappstudio.jakdobrzesieznacie.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_menu.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.enums.StatusMessage
import pl.idappstudio.jakdobrzesieznacie.fragments.*
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class MenuActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_USER_ITEM = "user"
        const val EXTRA_USER_IMAGE_TRANSITION_NAME = "image"
        const val EXTRA_USER_NAME_TRANSITION_NAME = "name"
        const val EXTRA_USER_BTN_CHAT_TRANSITION_NAME = "chat"
        const val EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME = "favorite"
        const val EXTRA_USER_STATUS_GAME_TRANSITION_NAME = "game-status"
        const val EXTRA_USER_IMAGE_GAME_TRANSITION_NAME = "game-image"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.adMob_menu_ad_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {

                mInterstitialAd.show()

                setContentView(R.layout.activity_menu)

                setupViewPager(viewPager)

            }

            override fun onAdFailedToLoad(errorCode: Int) {

                setContentView(R.layout.activity_menu)

                setupViewPager(viewPager)

            }

            override fun onAdOpened() {

            }

            override fun onAdLeftApplication() {

                setContentView(R.layout.activity_menu)

                setupViewPager(viewPager)

            }

            override fun onAdClosed() {

            }
        }

    }

    private fun setupViewPager(viewPager: ViewPager) {

        fab.setOnClickListener {

            if(!navigation.menu.getItem(2).isChecked || !navigation2.menu.getItem(2).isChecked) {

                viewPager.currentItem = 2
                navigation.menu.getItem(2).isChecked = true
                navigation2.menu.getItem(2).isChecked = true

            }

        }

        navigation.menu.getItem(2).isVisible = false
        navigation2.menu.getItem(2).isVisible = false

        navigation2.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.navigation_add_friends -> {

                    viewPager.currentItem = 3
                    navigation.menu.getItem(2).isChecked = true

                    true
                }

                R.id.navigation_profile -> {

                    viewPager.currentItem = 4
                    navigation.menu.getItem(2).isChecked = true

                    true
                }
                else -> false
            }
        }

        navigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.navigation_pack -> {

                    viewPager.currentItem = 0
                    navigation2.menu.getItem(2).isChecked = true

                    true
                }

                R.id.navigation_states -> {

                    viewPager.currentItem = 1
                    navigation2.menu.getItem(2).isChecked = true

                    true
                }

                else -> false
            }
        }

        navigation.menu.getItem(2).isEnabled = false
        navigation2.menu.getItem(2).isEnabled = false

        val adapter = ViewPagerAdapter(supportFragmentManager)

        val packFragment = PackFragment()
        val achivmentsFragment = AchivmentsFragment()
        val friendsFragment = FriendsFragment()
        val invitesFragment = InvitesFragment()
        val profileFragment = ProfileFragment()

        adapter.addFragment(packFragment, "pack")
        adapter.addFragment(achivmentsFragment, "achivment")
        adapter.addFragment(friendsFragment, "friends")
        adapter.addFragment(invitesFragment, "invites")
        adapter.addFragment(profileFragment, "profile")

        viewPager.offscreenPageLimit = 4

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

                when(position) {

                    0 -> {
                        navigation.menu.getItem(0).isChecked = true
                        navigation2.menu.getItem(2).isChecked = true

                    }

                    1 -> {
                        navigation.menu.getItem(1).isChecked = true
                        navigation2.menu.getItem(2).isChecked = true

                    }

                    2 -> {
                        navigation.menu.getItem(2).isChecked = true
                        navigation2.menu.getItem(2).isChecked = true
                    }

                    3 -> {
                        navigation.menu.getItem(2).isChecked = true
                        navigation2.menu.getItem(0).isChecked = true

                    }

                    4 ->  {
                        navigation.menu.getItem(2).isChecked = true
                        navigation2.menu.getItem(1).isChecked = true

                    }

                }

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        viewPager.adapter = adapter

        viewPager.currentItem = 2

    }

    override fun onResume() {
        super.onResume()
        Timer("status", false).schedule(700) {
            UserUtil.updateStatus(resources.getString(StatusMessage.online)) {}
        }
        UserUtil.getUser {}
        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        UserUtil.updateStatus(resources.getString(StatusMessage.offline)) {}
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

    override fun onDestroy() {
        super.onDestroy()
        UserUtil.stopListener()
    }

    internal inner class ViewPagerAdapter(manager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(manager) {

        private val mFragmentList: ArrayList<androidx.fragment.app.Fragment> = ArrayList()
        private val mFragmentTitleList: ArrayList<String> = ArrayList()

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            return mFragmentList[position]
        }

        fun addFragment(fragment: androidx.fragment.app.Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return ""
        }

    }

}
