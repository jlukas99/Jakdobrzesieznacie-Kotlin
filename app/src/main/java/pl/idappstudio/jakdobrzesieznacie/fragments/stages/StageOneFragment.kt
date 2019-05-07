package pl.idappstudio.jakdobrzesieznacie.fragments.stages

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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

import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.friends
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.game
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.questionList
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.userStats
import pl.idappstudio.jakdobrzesieznacie.interfaces.nextFragment
import pl.idappstudio.jakdobrzesieznacie.util.GameUtil
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

class StageOneFragment(private val listener: nextFragment) : androidx.fragment.app.Fragment(), View.OnClickListener {

    override fun onClick(v: View?) {

        if(v != null){

            lockButton()

            checkAnswer(v, a)

        }

    }

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

    private lateinit var aAnswerFriendImage: ImageView
    private lateinit var bAnswerFriendImage: ImageView
    private lateinit var cAnswerFriendImage: ImageView
    private lateinit var dAnswerFriendImage: ImageView

    private lateinit var aAnswerButton: ConstraintLayout
    private lateinit var bAnswerButton: ConstraintLayout
    private lateinit var cAnswerButton: ConstraintLayout
    private lateinit var dAnswerButton: ConstraintLayout

    private lateinit var nextQuestion: Button
    private lateinit var rejectBadAnswer: Button

    private lateinit var rewardedVideoAd: RewardedVideoAd

    private val glide = GlideUtil

    private var badAnswer: ArrayList<String> = ArrayList()
    private var userAnswer: ArrayList<String> = ArrayList()

    private var questionNumber = 1

    private var question = ""
    private var a = ""
    private var b = ""
    private var c = ""
    private var d = ""

    private var enable = true

    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_stage_one, container, false)

        MobileAds.initialize(this.context, resources.getString(R.string.adMob_id))

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

        aAnswerFriendImage = rootView.findViewById(R.id.aAnswerFriendProfile)
        bAnswerFriendImage = rootView.findViewById(R.id.bAnswerFriendProfile)
        cAnswerFriendImage = rootView.findViewById(R.id.cAnswerFriendProfile)
        dAnswerFriendImage = rootView.findViewById(R.id.dAnswerFriendProfile)

        aAnswerButton = rootView.findViewById(R.id.answer_btn_a)
        bAnswerButton = rootView.findViewById(R.id.answer_btn_b)
        cAnswerButton = rootView.findViewById(R.id.answer_btn_c)
        dAnswerButton = rootView.findViewById(R.id.answer_btn_d)

        aAnswerButton.setOnClickListener(this)
        bAnswerButton.setOnClickListener(this)
        cAnswerButton.setOnClickListener(this)
        dAnswerButton.setOnClickListener(this)

        nextQuestion = rootView.findViewById(R.id.nextQuestionBtn)
        rejectBadAnswer = rootView.findViewById(R.id.loadingAds)

        rejectBadAnswer.setOnClickListener {

            alertDialog.show()
            loadRewardedVideoAd()

        }

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this.context)
        rewardedVideoAd.rewardedVideoAdListener = object : RewardedVideoAdListener {

            override fun onRewarded(p0: RewardItem?) { rejectAnswer() }

            override fun onRewardedVideoAdLeftApplication() {

                if(alertDialog.isShowing) {
                    alertDialog.dismiss()
                }

            }

            override fun onRewardedVideoAdClosed() { }

            override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {

                if(alertDialog.isShowing) {
                    alertDialog.dismiss()
                }

            }

            override fun onRewardedVideoAdLoaded() { showAd() }

            override fun onRewardedVideoAdOpened() {

                if(alertDialog.isShowing) {
                    alertDialog.dismiss()
                }

            }

            override fun onRewardedVideoStarted() { }

            override fun onRewardedVideoCompleted() { }


        }

        val dialogBuilder = AlertDialog.Builder(this.context, R.style.Base_Theme_MaterialComponents_Dialog)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTranspery)))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)

        lockButton()

        setText()

        loadImage()

        return rootView
    }

    private fun showAd(){

        if(rewardedVideoAd.isLoaded){

            rewardedVideoAd.show()

        }

    }

    private fun loadRewardedVideoAd() {
        rewardedVideoAd.loadAd(resources.getString(R.string.adMob_reward_ad_id), AdRequest.Builder().build())
    }

    private fun resetButton(){

        nextQuestion.text = "Zgadnij odpowiedź znajomego"
        nextQuestion.background.setColorFilter(
            ContextCompat.getColor(
                this.context!!, R.color.colorBadAnswer
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        stageTitle.text = "Zgadnij Odpowiedzi"

        aAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        bAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        cAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        dAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)

        aAnswerFriendImage.visibility = View.INVISIBLE
        bAnswerFriendImage.visibility = View.INVISIBLE
        cAnswerFriendImage.visibility = View.INVISIBLE
        dAnswerFriendImage.visibility = View.INVISIBLE

        aAnswerUserImage.visibility = View.INVISIBLE
        bAnswerUserImage.visibility = View.INVISIBLE
        cAnswerUserImage.visibility = View.INVISIBLE
        dAnswerUserImage.visibility = View.INVISIBLE

        aAnswerButton.visibility = View.INVISIBLE
        bAnswerButton.visibility = View.INVISIBLE
        cAnswerButton.visibility = View.INVISIBLE
        dAnswerButton.visibility = View.INVISIBLE

        rejectBadAnswer.visibility = View.GONE

    }

    private fun lockButton(){

        aAnswerButton.isEnabled = false
        bAnswerButton.isEnabled = false
        cAnswerButton.isEnabled = false
        dAnswerButton.isEnabled = false

        rejectBadAnswer.isEnabled = false
        nextQuestion.isEnabled = false

    }

    private fun setText(){

        resetButton()

        aAnswerText.text = ""
        bAnswerText.text = ""
        cAnswerText.text = ""
        dAnswerText.text = ""

        if(questionNumber == 1){

            question = questionList.question.question
            a = questionList.question.a
            b = questionList.question.b
            c = questionList.question.c
            d = questionList.question.d

        } else if(questionNumber == 2){

            question = questionList.question1.question
            a = questionList.question1.a
            b = questionList.question1.b
            c = questionList.question1.c
            d = questionList.question1.d

        } else if(questionNumber == 3){

            question = questionList.question2.question
            a = questionList.question2.a
            b = questionList.question2.b
            c = questionList.question2.c
            d = questionList.question2.d

        }

        questionText.text = question

        badAnswer.clear()

        if(b != ""){
            badAnswer.add(b)
        }

        if(c != ""){
            badAnswer.add(c)
        }

        if(d != ""){
            badAnswer.add(d)
        }

        val array = ArrayList<String>()
        array.add(a)
        array.add(b)

        if(c != "" && d != ""){

            array.add(c)
            array.add(d)

        } else if(c != ""){

            array.add(c)

        } else if(d != ""){

            array.add(d)

        }

        if(array.size > 2){

            if(enable) {

                rejectBadAnswer.visibility = View.VISIBLE
                rejectBadAnswer.isEnabled = true

            }

        }


//        if((a.equals("tak", true) && b.equals("nie", true)) || (b.equals("tak", true) && a.equals("nie", true))){
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

        listener.showFragment()

    }

    private fun rejectAnswer(){

        rejectBadAnswer.isEnabled = false
        rejectBadAnswer.visibility = View.GONE

        enable = false

        setText()

        badAnswer.shuffle()

        if(aAnswerText.text.equals(badAnswer[0])){

            aAnswerButton.isEnabled = false

            aAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            return
        }

        if(bAnswerText.text.equals(badAnswer[0])){

            bAnswerButton.isEnabled = false

            bAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            return
        }

        if(cAnswerText.text.equals(badAnswer[0])){

            cAnswerButton.isEnabled = false

            cAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            return
        }

        if(dAnswerText.text.equals(badAnswer[0])){

            dAnswerButton.isEnabled = false

            dAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            return
        }

    }

    private fun nextQuestion() {

        nextQuestion.isEnabled = true
        enable = true

        if (questionNumber == 3) {

            nextQuestion.text = "nastepny etap"
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

                setText()

            } else {

                GameUtil.updateStats(friends.uid, GameActivity.canswer, GameActivity.banswer, userStats.games){

                    GameUtil.sendAnswer(game, UserUtil.user, friends, userAnswer[0], userAnswer[1], userAnswer[2]){

                        listener.next()

                    }

                }

            }
        }

    }

    private fun loadImage(){

        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), aAnswerUserImage) {}
        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), bAnswerUserImage) {}
        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), cAnswerUserImage) {}
        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), dAnswerUserImage) {}

        glide.setImage(friends.fb, friends.image, this.requireContext(), aAnswerFriendImage) {}
        glide.setImage(friends.fb, friends.image, this.requireContext(), bAnswerFriendImage) {}
        glide.setImage(friends.fb, friends.image, this.requireContext(), cAnswerFriendImage) {}
        glide.setImage(friends.fb, friends.image, this.requireContext(), dAnswerFriendImage) {}

    }

    private fun checkAnswer(view: View, s: String){

        if(view == aAnswerButton){

            userAnswer.add(aAnswerText.text.toString())

            if(aAnswerText.text == s) {

                aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                aAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            aAnswerUserImage.visibility = View.VISIBLE

        } else if(view == bAnswerButton){

            userAnswer.add(bAnswerText.text.toString())

            if(bAnswerText.text == s) {

                bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                bAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            bAnswerUserImage.visibility = View.VISIBLE

        } else if(view == cAnswerButton){

            userAnswer.add(cAnswerText.text.toString())

            if(cAnswerText.text == s) {

                cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                cAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            cAnswerUserImage.visibility = View.VISIBLE

        } else if(view == dAnswerButton){

            userAnswer.add(dAnswerText.text.toString())

            if(dAnswerText.text == s) {

                dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                dAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            dAnswerUserImage.visibility = View.VISIBLE

        }

        nextQuestion()

    }

    private fun checkCorrectAnswer(s: String){

        if(aAnswerText.text == s){

            aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            aAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(bAnswerText.text == s){

            bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            bAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(cAnswerText.text == s){

            cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            cAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(dAnswerText.text == s){

            dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            dAnswerFriendImage.visibility = View.VISIBLE

            return

        }

    }

}
