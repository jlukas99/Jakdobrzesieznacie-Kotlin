@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_menu.*


class MenuActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {

            R.id.navigation_pack -> {
                val packFragment = PackFragment.newInstance()
                openFragment(packFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_states -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_play -> {
                val friendsFragment = FriendsFragment.newInstance()
                openFragment(friendsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_add_friends -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        fab_play.fabIcon = resources.getDrawable(R.drawable.ic_play_icon)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val friendsFragment = FriendsFragment.newInstance()
        openFragment(friendsFragment)

    }

    fun openFragment(fragment: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.disallowAddToBackStack()
        transaction.replace(R.id.container, fragment)
        transaction.commit()

    }
}
