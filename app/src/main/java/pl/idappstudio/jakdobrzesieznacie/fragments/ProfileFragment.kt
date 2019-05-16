package pl.idappstudio.jakdobrzesieznacie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private lateinit var premium: Button

    private lateinit var friendsProfileStatsCanswer: TextView
    private lateinit var friendsProfileStatsBanswer: TextView
    private lateinit var friendsProfileStatsGames: TextView
    private lateinit var friendsProfileStatsPrecent: TextView

    private val glide = GlideUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_profile, container, false)

        name = rootView.findViewById(R.id.profile_name)
        image = rootView.findViewById(R.id.profile_image)

        loading = rootView.findViewById(R.id.profile_loading_image)

        logout = rootView.findViewById(R.id.profile_logout_btn)
        settings = rootView.findViewById(R.id.profile_settings_button)
        share = rootView.findViewById(R.id.profile_share_button)
        premium = rootView.findViewById(R.id.friends_profile_set_btn)

        friendsProfileStatsCanswer = rootView.findViewById(R.id.friends_profile_stats_canswer)
        friendsProfileStatsBanswer = rootView.findViewById(R.id.friends_profile_stats_banswer)
        friendsProfileStatsGames = rootView.findViewById(R.id.friends_profile_stats_games)
        friendsProfileStatsPrecent = rootView.findViewById(R.id.profile_stats_friend_precent)
        logout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            startActivity(intentFor<LoginMenuActivity>().newTask().clearTask())

        }

        premium.setOnClickListener {

            SnackBarUtil.setActivitySnack(
                resources.getString(R.string.premium_disabled),
                ColorSnackBar.WARING,
                R.drawable.ic_corn,
                it
            ) { }

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

//            val shareHashTag = ShareHashtag.Builder().setHashtag("#Jakdobrzesieznacie").setHashtag("#iDappStudio").build()
//            val shareLinkContent = ShareLinkContent.Builder()
//                .setShareHashtag(shareHashTag)
//                .setQuote("Moje statystyki :D")
//                .setContentUrl(Uri.parse("https://www.facebook.com/plugins/post.php?href=https%3A%2F%2Fwww.facebook.com%2FJakdobrzesieznacie%2Fphotos%2Fa.562584780804838%2F612757935787522%2F%3Ftype%3D3&width=500"))
//                .build()
//
//            ShareDialog.show(this,shareLinkContent)

        }

        setInformation()

        return rootView
    }

    private fun setInformation() {

        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.context!!, image) {}

        name.text = UserUtil.user.name

        UserUtil.getProfileUserStats { i1, i2, i3 ->

            val a: Float = i1.toFloat()
            val b: Float = i2.toFloat()
            val c: Int = i3
            val precent = GameUtil.getPrecent(StatsData(i1, i2, i3))
            resources.getString(R.string.percent)
            friendsProfileStatsCanswer.text = a.toInt().toString()
            friendsProfileStatsBanswer.text = b.toInt().toString()
            friendsProfileStatsGames.text = c.toString()
            friendsProfileStatsPrecent.text = String.format("$precent%s", "%")

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        UserUtil.stopListener()
    }

}
