package pl.idappstudio.jakdobrzesieznacie.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_friends_profile.*
import kotlinx.android.synthetic.main.activity_game.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.FriendsProfileActivity.Companion.EXTRA_FRIEND_IMAGE_TRANSITION
import pl.idappstudio.jakdobrzesieznacie.activity.FriendsProfileActivity.Companion.EXTRA_USER_IMAGE_TRANSITION
import pl.idappstudio.jakdobrzesieznacie.enums.StatusMessage
import pl.idappstudio.jakdobrzesieznacie.fragments.*
import pl.idappstudio.jakdobrzesieznacie.fragments.stages.StageOneFragment
import pl.idappstudio.jakdobrzesieznacie.fragments.stages.StageThreeFragment
import pl.idappstudio.jakdobrzesieznacie.fragments.stages.StageThreeOwnQuestionFragment
import pl.idappstudio.jakdobrzesieznacie.fragments.stages.StageTwoFragment
import pl.idappstudio.jakdobrzesieznacie.interfaces.nextFragment
import pl.idappstudio.jakdobrzesieznacie.model.*
import pl.idappstudio.jakdobrzesieznacie.util.GameUtil
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

class GameActivity : AppCompatActivity(), nextFragment {

    override fun showFragment() {

        if(!isFinishing){

            if(b) {

                hideLoading {

                    b = false
                    mContent = supportFragmentManager.fragments[0]

                }

            }

        }

    }

    override fun next() {

        showLoading()

        getInformation {

            if(it){

                setInterface {

                    setFragment()

                }

            } else {

                error()

            }

        }

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

        lateinit var friendsStats: StatsData
        lateinit var userStats: StatsData

        lateinit var uSet: UserSetData
        lateinit var fSet: UserSetData

        lateinit var game: GameData

        lateinit var questionList: QuestionData
        lateinit var answerList: AnswerData
        lateinit var yourAnswerList: AnswerData

        var canswer: Int = 0
        var banswer: Int = 0

    }

    lateinit var questionOne: TextView
    lateinit var questionTwo: TextView
    lateinit var questionThree: TextView

    lateinit var mContent: androidx.fragment.app.Fragment

    private val glide = GlideUtil

    private var b: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        supportPostponeEnterTransition()

        val back_btn = linearLayout1.findViewById<ImageButton>(R.id.back_btn)
        back_btn.setOnClickListener {

            onBackPressed()

        }

        val extras = intent.extras
        val imageUserTransitionName = extras.getString(EXTRA_USER_IMAGE_TRANSITION)
        val imageFriendTransitionName = extras.getString(EXTRA_FRIEND_IMAGE_TRANSITION)

        userProfileImage.transitionName = imageUserTransitionName
        friendProfileImage.transitionName = imageFriendTransitionName

        questionOne = findViewById(R.id.questionOneText)
        questionTwo = findViewById(R.id.questionTwoText)
        questionThree = findViewById(R.id.questionThreeText)

        userLoadingImage.visibility = View.VISIBLE
        friendLoadingImage.visibility = View.VISIBLE

        showLoading()

        getInformation {

            if(it){

                getStats()

                setInterface {

                    setFragment()

                }

            } else {

                error()

            }

        }

    }

    private fun showLoading(){

        supportFragmentManager.beginTransaction().replace(R.id.fragment, ClearFragment()).commit()

        gameIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorLigth), android.graphics.PorterDuff.Mode.SRC_IN)

        gameIcon.visibility = View.VISIBLE
        gameText.visibility = View.VISIBLE
        gameLoading.visibility = View.VISIBLE

    }

    private fun hideLoading(onComplete: () -> Unit){

        gameIcon.visibility = View.GONE
        gameText.visibility = View.GONE
        gameLoading.visibility = View.GONE

        onComplete()

    }

    private fun setFragment(){

        questionOne.setBackgroundResource(R.drawable.game_number_overlay)
        questionTwo.setBackgroundResource(R.drawable.game_number_overlay)
        questionThree.setBackgroundResource(R.drawable.game_number_overlay)

        if(game.uStage == 3 || game.fStage == 0){

            if(uSet.category == "own_question") {

                if(!isFinishing) {
                    b = true
                    supportFragmentManager.beginTransaction().disallowAddToBackStack().replace(R.id.fragment, StageThreeOwnQuestionFragment(this)).commit()
                }

            } else {

                GameUtil.getQuestionDataStageThree("set/${game.uSet.id}") {it, it2 ->

                    questionList = it

                    if(!isFinishing) {
                        b = true
                        supportFragmentManager.beginTransaction().disallowAddToBackStack().replace(R.id.fragment, StageThreeFragment(this, it2)).commit()
                    }

                }

            }

        } else if(game.uStage == 2){

            GameUtil.getQuestionDataStageTwo("games/${game.gameID}/${friends.uid}/1", "games/${game.gameID}/${UserUtil.user.uid}/3") { a, q, y ->

                questionList = q
                answerList = a
                yourAnswerList = y

                if(!isFinishing) {
                    b = true
                    supportFragmentManager.beginTransaction().disallowAddToBackStack().replace(R.id.fragment, StageTwoFragment(this)).commit()
                }

            }

        } else if(game.uStage == 1){

            GameUtil.getQuestionData("games/${game.gameID}/${friends.uid}/3") {

                questionList = it

                if(it.question.questionId != "" && it.question1.questionId != "" && it.question2.questionId != "") {

                    if (!isFinishing) {
                        b = true
                        supportFragmentManager.beginTransaction().disallowAddToBackStack().replace(R.id.fragment, StageOneFragment(this)).commit()
                    }

                } else {

                    gameText.text = "Nie udało się załadować pytań\nczy chcesz zrestartować grę?"

                }

            }

        } else {

            finish()

        }

    }

    private fun setInterface(onComplete: () -> Unit){

        setImage { onComplete() }

        userNameText.text = UserUtil.user.name
        friendNameText.text = friends.name

        if(game.uStage == 0){

            etapText.text = "3 z 3"

        } else {

            etapText.text = "${game.uStage} z 3"

        }

    }

    private fun setImage(onComplete: () -> Unit){

        glide.setActivityImage(UserUtil.user.fb, UserUtil.user.image, this, userProfileImage){

            userLoadingImage.visibility = View.GONE

        }

        glide.setActivityImage(friends.fb, friends.image, this, friendProfileImage){

            friendLoadingImage.visibility = View.GONE
            supportStartPostponedEnterTransition()

        }

        onComplete()

    }

    private fun error(){

        gameText.text = "Nie udało się załadować pytań\nczy chcesz zrestartować grę?"

    }

    private fun getStats(){

        GameUtil.getFriendStats(intent?.extras?.getString("uid")!!){ca, ba, ga ->

            friendsStats = StatsData(ca, ba, ga)

        }

        GameUtil.getUserStats(intent?.extras?.getString("uid")!!){ca, ba, ga ->

            userStats = StatsData(ca, ba, ga)
            canswer = userStats.canswer
            banswer = userStats.banswer

        }

    }

    private fun getInformation(onComplete: (Boolean) -> Unit){

        UserUtil.getFriendData(intent?.extras?.getString("uid")!!){data, b ->

            if(b){

                friends = data

                GameUtil.getSetData(intent?.extras?.getString("uSet")!!) { data1, b1 ->

                    if(b1){

                        uSet = data1

                        GameUtil.getSetData(intent?.extras?.getString("fSet")!!) { data2, b2 ->

                            if(b2){

                                fSet = data2

                                GameUtil.getGameData(intent?.extras?.getString("gameId")!!, friends.uid){ data3, b3 ->

                                    if(b3){

                                        game = GameData(data3.yturn, data3.fturn, data3.ystage, data3.fstage, uSet, fSet, data3.gamemode, data3.gameId, data3.newGame, data3.yid, data3.fid)

                                        onComplete(true)

                                    } else {

                                        onComplete(false)

                                    }

                                }

                            } else {

                                onComplete(false)

                            }

                        }

                    } else {

                        onComplete(false)

                    }

                }

            } else {

                onComplete(false)

            }

        }

    }

    override fun onResume() {
        super.onResume()
        UserUtil.updateStatus(StatusMessage.ingame)
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
        GameUtil.removeListenerStats()
    }

}
