package pl.idappstudio.jakdobrzesieznacie.fragments.stages

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import kotlinx.android.synthetic.main.dialog_loading.view.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.friends
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.friendsStats
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.game
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.questionList
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.userStats
import pl.idappstudio.jakdobrzesieznacie.interfaces.nextFragment
import pl.idappstudio.jakdobrzesieznacie.model.InviteNotificationMessage
import pl.idappstudio.jakdobrzesieznacie.model.Message
import pl.idappstudio.jakdobrzesieznacie.model.NotificationType
import pl.idappstudio.jakdobrzesieznacie.util.FirestoreUtil
import pl.idappstudio.jakdobrzesieznacie.util.GameUtil
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

class StageThreeFragment(private val listener: nextFragment, private val packUrl: String) : androidx.fragment.app.Fragment(), View.OnClickListener {

    override fun onClick(v: View?) {

        if(v != null){

            lockButton()

            checkAnswer(v)

        }

    }

    private val glide = GlideUtil

    private lateinit var stageTitle: TextView

    private lateinit var questionText: TextView

    private lateinit var aAnswerText: TextView
    private lateinit var bAnswerText: TextView
    private lateinit var cAnswerText: TextView
    private lateinit var dAnswerText: TextView

    private lateinit var aAnswerUserImage: ImageView
    private lateinit var bAnswerUserImage: ImageView
    private lateinit var cAnswerUserImage: ImageView
    private lateinit var dAnswerUserImage: ImageView

    private lateinit var aAnswerButton: ConstraintLayout
    private lateinit var bAnswerButton: ConstraintLayout
    private lateinit var cAnswerButton: ConstraintLayout
    private lateinit var dAnswerButton: ConstraintLayout

    private lateinit var nextQuestion: Button
    private lateinit var changeQuestion: Button

    private var userAnswer: ArrayList<String> = ArrayList()

    private lateinit var rewardedVideoAd: RewardedVideoAd

    private var questionNumber = 1

    private var question = ""
    private var canswer = ""
    private var banswer = ""
    private var banswer2 = ""
    private var banswer3 = ""

    private lateinit var alertDialog: AlertDialog
    private lateinit var dialogText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_stage_three_question, container, false)

        stageTitle = rootView.findViewById(R.id.gameStageTitle)

        questionText = rootView.findViewById(R.id.questionEditText)

        aAnswerText = rootView.findViewById(R.id.aAnswerEditText)
        bAnswerText = rootView.findViewById(R.id.bAnswerEditText)
        cAnswerText = rootView.findViewById(R.id.cAnswerEditText)
        dAnswerText = rootView.findViewById(R.id.dAnswerEditText)

        aAnswerUserImage = rootView.findViewById(R.id.aAnswerUserProfile)
        bAnswerUserImage = rootView.findViewById(R.id.bAnswerUserProfile)
        cAnswerUserImage = rootView.findViewById(R.id.cAnswerUserProfile)
        dAnswerUserImage = rootView.findViewById(R.id.dAnswerUserProfile)

        aAnswerButton = rootView.findViewById(R.id.answer_btn_a)
        bAnswerButton = rootView.findViewById(R.id.answer_btn_b)
        cAnswerButton = rootView.findViewById(R.id.answer_btn_c)
        dAnswerButton = rootView.findViewById(R.id.answer_btn_d)

        aAnswerButton.setOnClickListener(this)
        bAnswerButton.setOnClickListener(this)
        cAnswerButton.setOnClickListener(this)
        dAnswerButton.setOnClickListener(this)

        nextQuestion = rootView.findViewById(R.id.nextQuestionBtn)
        changeQuestion = rootView.findViewById(R.id.loadingAds)

        val dialogBuilder = AlertDialog.Builder(this.context, R.style.Base_Theme_MaterialComponents_Dialog)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)
        dialogText = dialogView.findViewById(R.id.textView3)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTranspery)))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this.context)
        rewardedVideoAd.rewardedVideoAdListener = object : RewardedVideoAdListener {

            override fun onRewarded(p0: RewardItem?) {
                newQuestion()
            }

            override fun onRewardedVideoAdLeftApplication() {

                if(alertDialog.isShowing) {
                    alertDialog.dismiss()
                }

            }

            override fun onRewardedVideoAdClosed() {

                if(alertDialog.isShowing) {
                    alertDialog.dismiss()
                }

            }

            override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {

                if(alertDialog.isShowing) {
                    alertDialog.dismiss()
                }

            }

            override fun onRewardedVideoAdLoaded() { showAd() }

            override fun onRewardedVideoAdOpened() { }

            override fun onRewardedVideoStarted() { }

            override fun onRewardedVideoCompleted() { }


        }

        changeQuestion.setOnClickListener {

            dialogText.text = "Ładowanie..."
            alertDialog.show()
            loadRewardedVideoAd()

        }

        lockButton()

        setText()

        loadImage()

        changeQuestion.isEnabled = true
        changeQuestion.visibility = View.VISIBLE

        return rootView
    }

    private fun showAd(){

        if(rewardedVideoAd.isLoaded){

            rewardedVideoAd.show()

        }

    }

    private fun loadRewardedVideoAd() {
        rewardedVideoAd.loadAd(resources.getString(R.string.adMob_reward2_ad_id), AdRequest.Builder().build())
    }

    private fun newQuestion(){

        if(!alertDialog.isShowing) {
            dialogText.text = "Zmieniamy pytanie..."
            alertDialog.show()
        }

        GameUtil.getNewQuestionDataStageThree("set/${game.uSet.id}"){

            if(questionNumber == 1){

                questionList.question.question = it.question
                questionList.question.a = it.a
                questionList.question.b = it.b
                questionList.question.c = it.c
                questionList.question.d = it.d
                questionList.question.questionId = it.questionId

                setText()

            } else if(questionNumber == 2){

                questionList.question1.question = it.question
                questionList.question1.a = it.a
                questionList.question1.b = it.b
                questionList.question1.c = it.c
                questionList.question1.d = it.d
                questionList.question1.questionId = it.questionId

                setText()

            } else if(questionNumber == 3){

                questionList.question2.question = it.question
                questionList.question2.a = it.a
                questionList.question2.b = it.b
                questionList.question2.c = it.c
                questionList.question2.d = it.d
                questionList.question2.questionId = it.questionId

                setText()

            }

            changeQuestion.isEnabled = false
            changeQuestion.visibility = View.GONE

        }

    }

    private fun resetButton(){

        nextQuestion.text = "Odpowiedz na Pytanie"
        nextQuestion.background.setColorFilter(
            ContextCompat.getColor(
                this.context!!, R.color.colorBadAnswer
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        stageTitle.text = "Odpowiedz na pytania"

        aAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        bAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        cAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        dAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)

        aAnswerUserImage.visibility = View.INVISIBLE
        bAnswerUserImage.visibility = View.INVISIBLE
        cAnswerUserImage.visibility = View.INVISIBLE
        dAnswerUserImage.visibility = View.INVISIBLE

        aAnswerButton.visibility = View.INVISIBLE
        bAnswerButton.visibility = View.INVISIBLE
        cAnswerButton.visibility = View.INVISIBLE
        dAnswerButton.visibility = View.INVISIBLE

    }

    private fun nextQuestion() {

        nextQuestion.isEnabled = true

        if (questionNumber == 3) {

            nextQuestion.text = "zakończ swoją turę"
            nextQuestion.background.setColorFilter(
                ContextCompat.getColor(
                    this.context!!, R.color.colorCorrectAnswer
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

        } else {

            nextQuestion.text = "następne pytanie"
            nextQuestion.background.setColorFilter(
                ContextCompat.getColor(
                    this.context!!, R.color.colorPrimary
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

        }


        nextQuestion.setOnClickListener {

            lockButton()

            questionNumber++

            if (questionNumber != 4) {

                changeQuestion.isEnabled = true
                changeQuestion.visibility = View.VISIBLE

                setText()

            } else {

                GameUtil.sendAnswerStageThree(game, UserUtil.user, friends, userAnswer[0], userAnswer[1], userAnswer[2],
                    "$packUrl/${questionList.question.questionId}",
                    "$packUrl/${questionList.question1.questionId}",
                    "$packUrl/${questionList.question2.questionId}"){

                    if(userStats.games == 0 && friendsStats.games == 1){

                        GameUtil.notNewGame(game)

                    }

                    GameUtil.updateGame(userStats.games, UserUtil.user.uid, friends.uid)

                    val msg: Message = InviteNotificationMessage("Twoja kolej!", "${UserUtil.user.name} skończył swoją turę, czas na ciebie!", UserUtil.user.uid, friends.uid, UserUtil.user.name, NotificationType.GAME)
                    FirestoreUtil.sendMessage(msg, friends.uid)

                    activity?.onBackPressed()

                }

            }
        }

    }

    private fun checkAnswer(view: View){

        when (view) {
            aAnswerButton -> {

                userAnswer.add(aAnswerText.text.toString())

                aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                aAnswerUserImage.visibility = View.VISIBLE

            }
            bAnswerButton -> {

                userAnswer.add(bAnswerText.text.toString())

                bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                bAnswerUserImage.visibility = View.VISIBLE

            }
            cAnswerButton -> {

                userAnswer.add(cAnswerText.text.toString())

                cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                cAnswerUserImage.visibility = View.VISIBLE

            }
            dAnswerButton -> {

                userAnswer.add(dAnswerText.text.toString())

                dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                dAnswerUserImage.visibility = View.VISIBLE

            }
        }

        nextQuestion()

    }

    private fun lockButton(){

        aAnswerButton.isEnabled = false
        bAnswerButton.isEnabled = false
        cAnswerButton.isEnabled = false
        dAnswerButton.isEnabled = false

        nextQuestion.isEnabled = false
        changeQuestion.isEnabled = false
        changeQuestion.visibility = View.GONE

    }

    private fun setText(){

        resetButton()

        aAnswerText.text = ""
        bAnswerText.text = ""
        cAnswerText.text = ""
        dAnswerText.text = ""

        if(questionNumber == 1){

            question = questionList.question.question
            canswer = questionList.question.a
            banswer = questionList.question.b
            banswer2 = questionList.question.c
            banswer3 = questionList.question.d

        } else if(questionNumber == 2){

            question = questionList.question1.question
            canswer = questionList.question1.a
            banswer = questionList.question1.b
            banswer2 = questionList.question1.c
            banswer3 = questionList.question1.d

        } else if(questionNumber == 3){

            question = questionList.question2.question
            canswer = questionList.question2.a
            banswer = questionList.question2.b
            banswer2 = questionList.question2.c
            banswer3 = questionList.question2.d

        }

        questionText.text = question

        val array = ArrayList<String>()
        array.add(canswer)
        array.add(banswer)

        if(banswer2 != "" && banswer3 != ""){

            array.add(banswer2)
            array.add(banswer3)

        } else if(banswer2 != ""){

            array.add(banswer2)

        } else if(banswer3 != ""){

            array.add(banswer3)

        }

//        if(canswer.equals("tak", true) || canswer.equals("nie", true)){
//
//            aAnswerText.text = "Tak"
//
//            aAnswerButton.isEnabled = true
//            aAnswerButton.visibility = View.VISIBLE
//
//            bAnswerText.text = "Nie"
//
//            bAnswerButton.isEnabled = true
//            bAnswerButton.visibility = View.VISIBLE
//
//            array.removeAt(0)
//            array.removeAt(0)
//
//        }

        array.shuffle()

        if (aAnswerText.text == "") {

            for (a in array) {

                if (a != "") {

                    aAnswerText.text = a
                    array.remove(a)

                    aAnswerButton.isEnabled = true
                    aAnswerButton.visibility = View.VISIBLE

                    break

                }

            }

        }

        if (bAnswerText.text == "") {

            for (a in array) {

                if (a != "") {

                    bAnswerText.text = a
                    array.remove(a)

                    bAnswerButton.isEnabled = true
                    bAnswerButton.visibility = View.VISIBLE

                    break

                }

            }

        }

        if (cAnswerText.text == "") {

            for (a in array) {

                if (a != "") {

                    cAnswerText.text = a
                    array.remove(a)

                    cAnswerButton.isEnabled = true
                    cAnswerButton.visibility = View.VISIBLE

                    break

                }

            }

        }

        if (dAnswerText.text == "") {

            for (a in array) {

                if (a != "") {

                    dAnswerText.text = a
                    array.remove(a)

                    dAnswerButton.isEnabled = true
                    dAnswerButton.visibility = View.VISIBLE

                    break

                }

            }
        }

        if(alertDialog.isShowing) {
            alertDialog.dismiss()
        }

        listener.showFragment()

    }

    private fun loadImage(){

        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), aAnswerUserImage) {}
        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), bAnswerUserImage) {}
        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), cAnswerUserImage) {}
        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), dAnswerUserImage) {}

    }

}