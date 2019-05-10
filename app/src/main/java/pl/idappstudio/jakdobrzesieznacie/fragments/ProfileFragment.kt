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
import pl.idappstudio.jakdobrzesieznacie.model.UserData
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

    private lateinit var friends_profile_stats_canswer: TextView
    private lateinit var friends_profile_stats_banswer: TextView
    private lateinit var friends_profile_stats_games: TextView
    private lateinit var profile_stats_friend_precent: TextView

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

        friends_profile_stats_canswer = rootView.findViewById(R.id.friends_profile_stats_canswer)
        friends_profile_stats_banswer = rootView.findViewById(R.id.friends_profile_stats_banswer)
        friends_profile_stats_games = rootView.findViewById(R.id.friends_profile_stats_games)
        profile_stats_friend_precent = rootView.findViewById(R.id.profile_stats_friend_precent)
        logout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            startActivity(intentFor<LoginMenuActivity>().newTask().clearTask())

        }

        premium.setOnClickListener {

            SnackBarUtil.setActivitySnack("Opcja na czas testów, została wyłączona", ColorSnackBar.WARING, R.drawable.ic_corn, it){ }

        }

        settings.setOnClickListener {

            startActivity<SettingsActivity>()

        }

        share.setOnClickListener {

//            val sendIntent = Intent()
//            sendIntent.action = Intent.ACTION_SEND
//            sendIntent.putExtra(Intent.EXTRA_TEXT, "Cześć, pobierz aplikacje i zagraj ze mną\n\nhttps://jakdobrzesieznacie.page.link/download_game")
//            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Jak Dobrze Się Znacie?")
//            sendIntent.type = "text/plain"
//            startActivity(Intent.createChooser(sendIntent, FirestoreUtil.currentUser.name)
//
//            )

        }

        return rootView
    }

    fun setInformation(){

        glide.setActivityImage(UserUtil.user.fb, UserUtil.user.image, this.context!!, image) {}

        name.text = UserUtil.user.name

        UserUtil.getProfileUserStats { i1, i2, i3 ->

            val a: Float = i1.toFloat()
            val b: Float = i2.toFloat()
            val c: Int = i3

            friends_profile_stats_canswer.text = a.toInt().toString()
            friends_profile_stats_banswer.text = b.toInt().toString()
            friends_profile_stats_games.text = c.toString()
            profile_stats_friend_precent.text = "${GameUtil.getPrecent(StatsData(i1, i2, i3))}%"

        }

    }

    override fun onStart() {
        super.onStart()
        setInformation()
    }

    override fun onDestroy() {
        super.onDestroy()
        UserUtil.stopListener()
    }

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

}
