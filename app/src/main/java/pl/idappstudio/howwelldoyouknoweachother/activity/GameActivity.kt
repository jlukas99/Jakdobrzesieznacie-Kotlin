package pl.idappstudio.howwelldoyouknoweachother.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_game.*
import org.jetbrains.anko.activityManager
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.fragments.*
import pl.idappstudio.howwelldoyouknoweachother.interfaces.nextFragment
import pl.idappstudio.howwelldoyouknoweachother.model.*
import pl.idappstudio.howwelldoyouknoweachother.util.GameUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GlideUtil

class GameActivity : AppCompatActivity(), nextFragment {

    override fun next() {

        supportFragmentManager.beginTransaction().replace(R.id.fragment, ClearFragment()).commit()
        showLoading()

        getFriendInformation()

    }

    override fun updateNumber(i: Int, b: Boolean){

        if(i == 1){
            if(b){
                questionOne.setBackgroundResource(R.drawable.number_correct_overlay)
                return
            } else {
                questionOne.setBackgroundResource(R.drawable.number_bad_overlay)
                return
            }
        }

        if(i == 2){
            if(b){
                questionTwo.setBackgroundResource(R.drawable.number_correct_overlay)
                return
            } else {
                questionTwo.setBackgroundResource(R.drawable.number_bad_overlay)
                return
            }
        }

        if(i == 3){
            if(b){
                questionThree.setBackgroundResource(R.drawable.number_correct_overlay)
                return
            } else {
                questionThree.setBackgroundResource(R.drawable.number_bad_overlay)
                return
            }
        }

    }

    companion object {

        lateinit var friends: UserData
        lateinit var stats: StatsData

        lateinit var information: FriendInfoData
        lateinit var game: GameData

        lateinit var user: UserData
        lateinit var userStats: StatsData

        lateinit var questionList: QuestionData
        lateinit var answerList: AnswerData

        var canswer: Int = 0
        var banswer: Int = 0

        fun updateStats(onComplete: () -> Unit){

            GameUtil.updateStats(user.uid, friends.uid, GameActivity.userStats.canswer + canswer, GameActivity.userStats.banswer + banswer, GameActivity.userStats.games){

                onComplete()

            }

        }

    }

    lateinit var questionOne: TextView
    lateinit var questionTwo: TextView
    lateinit var questionThree: TextView

    lateinit var mContent: Fragment

    private val glide = GlideUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        questionOne = findViewById(R.id.questionOneText)
        questionTwo = findViewById(R.id.questionTwoText)
        questionThree = findViewById(R.id.questionThreeText)

        userLoadingImage.visibility = View.VISIBLE
        friendLoadingImage.visibility = View.VISIBLE

        showLoading()

    }

    private fun showLoading(){

        gameIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorLigth), android.graphics.PorterDuff.Mode.SRC_IN)

        gameIcon.visibility = View.VISIBLE
        gameText.visibility = View.VISIBLE
        gameLoading.visibility = View.VISIBLE

    }

    private fun hideLoading(){

        gameIcon.visibility = View.GONE
        gameText.visibility = View.GONE
        gameLoading.visibility = View.GONE

        questionOne.background

    }

    private fun setFragment(){

        questionOne.setBackgroundResource(R.drawable.game_number_overlay)
        questionTwo.setBackgroundResource(R.drawable.game_number_overlay)
        questionThree.setBackgroundResource(R.drawable.game_number_overlay)

        if(game.uStage == 3 || game.uStage == 0){

            if(game.uSet.category == "own_question") {

                if(!isFinishing) {
                    supportFragmentManager.beginTransaction()
                        .disallowAddToBackStack()
                        .replace(R.id.fragment, StageThreeOwnQuestionFragment(this)).commit()
                    mContent = supportFragmentManager.fragments.get(0)
                    hideLoading()
                }

            } else {

                GameUtil.getQuestionDataStageThree("set/${game.uSet.id}/${user.gender}") {

                    questionList = it

                    if(!isFinishing) {
                        supportFragmentManager.beginTransaction()
                            .disallowAddToBackStack()
                            .replace(R.id.fragment, StageThreeFragment(this)).commit()
                        mContent = supportFragmentManager.fragments.get(0)
                        hideLoading()
                    }

                }

            }

        } else if(game.uStage == 2){

            GameUtil.getQuestionDataStageTwo("games/${game.gameID}/${friends.uid}/1", "games/${game.gameID}/${user.uid}/3") { a, q ->

                questionList = q
                answerList = a

                if(!isFinishing) {
                    supportFragmentManager.beginTransaction()
                        .disallowAddToBackStack()
                        .replace(R.id.fragment, StageTwoFragment(this)).commit()
                    mContent = supportFragmentManager.fragments.get(0)
                    hideLoading()
                }

            }

        } else if(game.uStage == 1){

            GameUtil.getQuestionData("games/${game.gameID}/${friends.uid}/3") {



                questionList = it
                if(!isFinishing) {
                    supportFragmentManager.beginTransaction()
                        .disallowAddToBackStack()
                        .replace(R.id.fragment, StageOneFragment(this)).commit()
                    mContent = supportFragmentManager.fragments.get(0)
                    hideLoading()
                }

            }

        } else {

            finish()

        }

    }

    private fun setInterface(onComplete: () -> Unit){

        setImage {

            onComplete()

        }

        userNameText.text = user.name
        friendNameText.text = friends.name

        if(game.uStage == 0){

            etapText.text = "3 z 3"

        } else {

            etapText.text = "${game.uStage} z 3"

        }

    }

    private fun setImage(onComplete: () -> Unit){

        glide.setImage(user.fb, user.image, this, userProfileImage){

            userLoadingImage.visibility = View.GONE

        }

        glide.setImage(friends.fb, friends.image, this, friendProfileImage){

            friendLoadingImage.visibility = View.GONE

        }

        onComplete()

    }

    private fun getFriendInformation(){
        GameUtil.getUserData(FirebaseAuth.getInstance().currentUser?.uid.toString(), intent?.extras?.get("id").toString()) { e ->

            friends = e.friendsData
            user = e.userData

            game = e.game
            information = e.finfo

            stats = e.fstats
            userStats = e.ustats

            setInterface {

                setFragment()

            }

        }
    }

    override fun onResume() {
        super.onResume()

        hideNavigationBar()

        getFriendInformation()

    }

    private fun hideNavigationBar() {

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

            window.decorView.systemUiVisibility = flags

            val decorView = window.decorView
            decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    decorView.systemUiVisibility = flags
                }
            }
    }

}
