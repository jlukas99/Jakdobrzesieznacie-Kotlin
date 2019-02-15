package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.LoginMenuActivity
import pl.idappstudio.howwelldoyouknoweachother.activity.SettingsActivity
import pl.idappstudio.howwelldoyouknoweachother.util.GlideUtil
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GameUtil

class ProfileFragment : Fragment() {

    private lateinit var name: TextView
    private lateinit var image: CircleImageView

    private lateinit var loading: SpinKitView

    private lateinit var logout: Button
    private lateinit var settings: Button
    private lateinit var share: Button

    private val currentUser = FirestoreUtil.currentUser

    private val glide = GlideUtil()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_profile, container, false)

        name = rootView.findViewById(R.id.profile_name)
        image = rootView.findViewById(R.id.profile_image)

        loading = rootView.findViewById(R.id.profile_loading_image)

        logout = rootView.findViewById(R.id.profile_logout_btn)
        settings = rootView.findViewById(R.id.profile_settings_button)
        share = rootView.findViewById(R.id.profile_share_button)

        logout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            startActivity(intentFor<LoginMenuActivity>().newTask().clearTask())

        }

        settings.setOnClickListener {

            startActivity<SettingsActivity>()
            onStop()

        }

        share.setOnClickListener {

//            val sendIntent = Intent()
//            sendIntent.action = Intent.ACTION_SEND
//            sendIntent.putExtra(Intent.EXTRA_TEXT, "Cześć, pobierz aplikacje i zagraj ze mną\n\nhttps://howwelldoyouknoweachother.page.link/download_game")
//            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Jak Dobrze Się Znacie?")
//            sendIntent.type = "text/plain"
//            startActivity(Intent.createChooser(sendIntent, FirestoreUtil.currentUser.name)
//
//            )

        }

        return rootView
    }

    fun setInformation(){

        loading.visibility = View.VISIBLE

        name.text = currentUser.name

        FirestoreUtil.getStats {

            val a: Float = it.canswer.toFloat()
            val b: Float = it.banswer.toFloat()
            val c: Int = it.games

            friends_profile_stats_canswer.text = a.toInt().toString()
            friends_profile_stats_banswer.text = b.toInt().toString()
            friends_profile_stats_games.text = c.toString()
            profile_stats_friend_precent.text = "${GameUtil.getPrecent(it)}%"

        }

        glide.setImage(currentUser.fb, currentUser.image, this.context!!, image){

            loading.visibility = View.GONE

        }

    }

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

    override fun onResume() {
        super.onResume()

        FirestoreUtil.initialize()
        setInformation()

    }


}
