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
import pl.idappstudio.jakdobrzesieznacie.fragments.*
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil
import kotlin.collections.ArrayList

class MenuActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_USER_ITEM = "user"
        const val EXTRA_USER_IMAGE_TRANSITION_NAME = "image"
        const val EXTRA_USER_NAME_TRANSITION_NAME = "name"
        const val EXTRA_USER_BTN_CHAT_TRANSITION_NAME = "chat"
        const val EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME = "favorite"
        const val EXTRA_USER_STATUS_GAME_TRANSITION_NAME = "game-status"
        const val EXTRA_USER_IMAGE_GAME_TRANSITION_NAME = "game-image"

        lateinit var viewPager2: ViewPager

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

        val adapter = ViewPagerAdapter(supportFragmentManager)

        val inviteFragment = InvitesFragment()
        val friendsFragment = FriendsFragment()
        val profileFragment = ProfileFragment()

        adapter.addFragment(inviteFragment, "invite")
        adapter.addFragment(friendsFragment, "friends")
        adapter.addFragment(profileFragment, "profile")

        viewPager.offscreenPageLimit = 2

        viewPager.adapter = adapter

        viewPager.currentItem = 1

        viewPager2 = viewPager

    }

    override fun onResume() {
        super.onResume()
        UserUtil.getUser {}
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
