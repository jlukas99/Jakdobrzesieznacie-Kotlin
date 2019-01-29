package pl.idappstudio.howwelldoyouknoweachother.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.model.FriendsData
import pl.idappstudio.howwelldoyouknoweachother.model.GameData
import pl.idappstudio.howwelldoyouknoweachother.model.StatsData
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil

class GameActivity : AppCompatActivity() {

    private lateinit var friends: FriendsData
    private lateinit var game: GameData
    private lateinit var stats: StatsData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }

    private fun getFriendInformation(){
        FirestoreUtil.getFriendsUser(intent?.extras?.get("id").toString()) { e ->

            friends = e
            game = e.game
            stats = e.stats

        }
    }

    override fun onResume() {
        super.onResume()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        getFriendInformation()

    }
}
