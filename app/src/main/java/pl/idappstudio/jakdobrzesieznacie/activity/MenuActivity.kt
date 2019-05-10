@file:Suppress("DEPRECATION")

package pl.idappstudio.jakdobrzesieznacie.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.viewpager.widget.ViewPager
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

        val adManager = AdMobUtil(this@MenuActivity, resources.getString(R.string.adMob_menu_ad_id))
        val ad:InterstitialAd = adManager.getAd()

        ad.adListener = object: AdListener() {
            override fun onAdLoaded() {

                ad.show()

                setContentView(R.layout.activity_menu)

                setupViewPager(viewPager)

            }

            override fun onAdFailedToLoad(errorCode: Int) {

                setContentView(R.layout.activity_menu)

                adManager.createAd(this@MenuActivity, resources.getString(R.string.adMob_menu_ad_id))

                setupViewPager(viewPager)

            }

            override fun onAdOpened() {

            }

            override fun onAdLeftApplication() {

                setContentView(R.layout.activity_menu)

                setupViewPager(viewPager)

            }

            override fun onAdClosed() {

                adManager.createAd(this@MenuActivity, resources.getString(R.string.adMob_menu_ad_id))

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
        UserUtil.getUser {}
        UserUtil.updateStatus(StatusMessage.inmenu)
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

    override fun onDestroy() {
        super.onDestroy()
        UserUtil.stopListener()
        UserUtil.updateStatus(StatusMessage.offline)
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
