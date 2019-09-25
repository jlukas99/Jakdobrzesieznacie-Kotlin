package pl.idappstudio.jakdobrzesieznacie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.LoginMenuActivity
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.viewPager2
import pl.idappstudio.jakdobrzesieznacie.activity.SettingsActivity
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.model.StatsData
import pl.idappstudio.jakdobrzesieznacie.util.GameUtil
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

class ProfileFragment : androidx.fragment.app.Fragment() {

    private lateinit var name: TextView
    private lateinit var image: CircleImageView

    private lateinit var loading: SpinKitView

    private lateinit var logout: Button
    private lateinit var settings: Button
    private lateinit var share: Button

    private lateinit var back: ImageButton

    private lateinit var friendsProfileStatsCanswer: TextView
    private lateinit var friendsProfileStatsBanswer: TextView
    private lateinit var friendsProfileStatsGames: TextView
    private lateinit var friendsProfileStatsPrecent: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_profile, container, false)

        name = rootView.findViewById(R.id.profile_name)
        image = rootView.findViewById(R.id.profile_image)
        back = rootView.findViewById(R.id.back)

        loading = rootView.findViewById(R.id.profile_loading_image)

        logout = rootView.findViewById(R.id.profile_logout_btn)
        settings = rootView.findViewById(R.id.profile_settings_button)
        share = rootView.findViewById(R.id.profile_share_button)

        friendsProfileStatsCanswer = rootView.findViewById(R.id.friends_profile_stats_canswer)
        friendsProfileStatsBanswer = rootView.findViewById(R.id.friends_profile_stats_banswer)
        friendsProfileStatsGames = rootView.findViewById(R.id.friends_profile_stats_games)
        friendsProfileStatsPrecent = rootView.findViewById(R.id.profile_stats_friend_precent)
        logout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            startActivity(intentFor<LoginMenuActivity>().newTask().clearTask())

        }

        back.setOnClickListener {

            viewPager2.setCurrentItem(1, true)

        }

        settings.setOnClickListener {

            startActivity<SettingsActivity>()

        }

        share.setOnClickListener {
            SnackBarUtil.setActivitySnack(
                resources.getString(R.string.statistics_disabled),
                ColorSnackBar.WARING,
                R.drawable.ic_share,
                it
            ) { }

            SnackBarUtil.setActivitySnack(
                "Pracujemy nad udostępnianiem statystyk profilu",
                ColorSnackBar.WARING,
                R.drawable.ic_share,
                it
            ) { }

        }

        setInformation()

        return rootView
    }

    private fun setInformation() {

        if (isAdded) {

            GlideUtil.setImage(UserUtil.user.fb, UserUtil.user.image, this.context!!, image) {}

            name.text = UserUtil.user.name

            UserUtil.getProfileUserStats { i1, i2, i3 ->

                val a: Float = i1.toFloat()
                val b: Float = i2.toFloat()
                val c: Int = i3
                val percent = GameUtil.getPrecent(StatsData(i1, i2, i3))
                friendsProfileStatsCanswer.text = a.toInt().toString()
                friendsProfileStatsBanswer.text = b.toInt().toString()
                friendsProfileStatsGames.text = c.toString()
                friendsProfileStatsPrecent.text = String.format("$percent%s", "%")

            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        UserUtil.stopListener()
    }

}
