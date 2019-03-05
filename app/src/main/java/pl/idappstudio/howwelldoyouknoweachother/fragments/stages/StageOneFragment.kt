package pl.idappstudio.howwelldoyouknoweachother.fragments.stages

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

import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.friends
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.game
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.questionList
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.userStats
import pl.idappstudio.howwelldoyouknoweachother.interfaces.nextFragment
import pl.idappstudio.howwelldoyouknoweachother.util.GameUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GlideUtil
import pl.idappstudio.howwelldoyouknoweachother.util.UserUtil

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
    private lateinit var bText: TextView
    private lateinit var aText: TextView
    private lateinit var dAnswerText: TextView

    private lateinit var aAnswerUserImage: ImageView
    private lateinit var bUserImage: ImageView
    private lateinit var aUserImage: ImageView
    private lateinit var dAnswerUserImage: ImageView

    private lateinit var aAnswerFriendImage: ImageView
    private lateinit var bFriendImage: ImageView
    private lateinit var aFriendImage: ImageView
    private lateinit var dAnswerFriendImage: ImageView

    private lateinit var aAnswerButton: ConstraintLayout
    private lateinit var bButton: ConstraintLayout
    private lateinit var aButton: ConstraintLayout
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_stage_one, container, false)

        MobileAds.initialize(this.context, resources.getString(R.string.adMob_id))

        stageTitle = rootView.findViewById(R.id.gameStageTitle)

        questionText = rootView.findViewById(R.id.questionEditText)

        aAnswerText = rootView.findViewById(R.id.aAnswerEditText)
        bText = rootView.findViewById(R.id.bAnswerEditText)
        aText = rootView.findViewById(R.id.aAnswerEditText)
        dAnswerText = rootView.findViewById(R.id.dAnswerEditText)

        aAnswerUserImage = rootView.findViewById(R.id.aAnswerUserProfile)
        bUserImage = rootView.findViewById(R.id.bAnswerUserProfile)
        aUserImage = rootView.findViewById(R.id.aAnswerUserProfile)
        dAnswerUserImage = rootView.findViewById(R.id.dAnswerUserProfile)

        aAnswerFriendImage = rootView.findViewById(R.id.aAnswerFriendProfile)
        bFriendImage = rootView.findViewById(R.id.bAnswerFriendProfile)
        aFriendImage = rootView.findViewById(R.id.aAnswerFriendProfile)
        dAnswerFriendImage = rootView.findViewById(R.id.dAnswerFriendProfile)

        aAnswerButton = rootView.findViewById(R.id.answer_btn_a)
        bButton = rootView.findViewById(R.id.answer_btn_b)
        aButton = rootView.findViewById(R.id.answer_btn_c)
        dAnswerButton = rootView.findViewById(R.id.answer_btn_d)

        aAnswerButton.setOnClickListener(this)
        bButton.setOnClickListener(this)
        aButton.setOnClickListener(this)
        dAnswerButton.setOnClickListener(this)

        nextQuestion = rootView.findViewById(R.id.nextQuestionBtn)
        rejectBadAnswer = rootView.findViewById(R.id.loadingAds)

        rejectBadAnswer.setOnClickListener { loadRewardedVideoAd() }

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this.context)
        rewardedVideoAd.rewardedVideoAdListener = object : RewardedVideoAdListener {

            override fun onRewarded(p0: RewardItem?) { rejectAnswer() }

            override fun onRewardedVideoAdLeftApplication() {}

            override fun onRewardedVideoAdClosed() {}

            override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {}

            override fun onRewardedVideoAdLoaded() { showAd() }

            override fun onRewardedVideoAdOpened() {}

            override fun onRewardedVideoStarted() {}

            override fun onRewardedVideoCompleted() {}


        }

        lockButton()

        setText()

        return rootView
    }

    private fun showAd(){

        if(rewardedVideoAd.isLoaded){

            rewardedVideoAd.show()

        }

    }

    private fun loadRewardedVideoAd() {
        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build())
    }

    private fun resetButton(){

        nextQuestion.text = "Zgadnij odpowiedź znajomego"
        nextQuestion.background.setColorFilter(
            ContextCompat.getColor(
                this.context!!, R.color.colorBadAnswer
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        stageTitle.text = "Zgadnij Odpowiedzi"

        aAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        bButton.background = resources.getDrawable(R.drawable.card_background_dark)
        aButton.background = resources.getDrawable(R.drawable.card_background_dark)
        dAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)

        aAnswerFriendImage.visibility = View.INVISIBLE
        bFriendImage.visibility = View.INVISIBLE
        aFriendImage.visibility = View.INVISIBLE
        dAnswerFriendImage.visibility = View.INVISIBLE

        aAnswerUserImage.visibility = View.INVISIBLE
        bUserImage.visibility = View.INVISIBLE
        aUserImage.visibility = View.INVISIBLE
        dAnswerUserImage.visibility = View.INVISIBLE

        aAnswerButton.visibility = View.INVISIBLE
        bButton.visibility = View.INVISIBLE
        aButton.visibility = View.INVISIBLE
        dAnswerButton.visibility = View.INVISIBLE

        rejectBadAnswer.visibility = View.GONE

        rejectBadAnswer.isEnabled = true

    }

    private fun lockButton(){

        aAnswerButton.isEnabled = false
        bButton.isEnabled = false
        aButton.isEnabled = false
        dAnswerButton.isEnabled = false

        rejectBadAnswer.isEnabled = false
        nextQuestion.isEnabled = false

    }

    private fun setText(){

        resetButton()

        aAnswerText.text = ""
        bText.text = ""
        aText.text = ""
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

            rejectBadAnswer.visibility = View.VISIBLE

        }


        if(a.equals("tak", true) || a.equals("nie", true)){

            aAnswerText.text = "Tak"

            aAnswerButton.isEnabled = true
            aAnswerButton.visibility = View.VISIBLE

            bText.text = "Nie"

            bButton.isEnabled = true
            bButton.visibility = View.VISIBLE

            array.removeAt(0)
            array.removeAt(0)

        }

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

        if (bText.text == "") {

            for (a in array) {

                if (a != "") {

                    bText.text = a
                    array.remove(a)

                    bButton.isEnabled = true
                    bButton.visibility = View.VISIBLE

                    break

                }

            }

        }

        if (aText.text == "") {

            for (a in array) {

                if (a != "") {

                    aText.text = a
                    array.remove(a)

                    aButton.isEnabled = true
                    aButton.visibility = View.VISIBLE

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

        loadImage()

    }

    private fun rejectAnswer(){

        setText()

        badAnswer.shuffle()

        if(aAnswerText.text.equals(badAnswer[0])){

            aAnswerButton.isEnabled = false

            aAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            return
        }

        if(bText.text.equals(badAnswer[0])){

            bButton.isEnabled = false

            bButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            return
        }

        if(aText.text.equals(badAnswer[0])){

            aButton.isEnabled = false

            aButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

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
        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), bUserImage) {}
        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), aUserImage) {}
        glide.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), dAnswerUserImage) {}

        glide.setImage(friends.fb, friends.image, this.requireContext(), aAnswerFriendImage) {}
        glide.setImage(friends.fb, friends.image, this.requireContext(), bFriendImage) {}
        glide.setImage(friends.fb, friends.image, this.requireContext(), aFriendImage) {}
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

        } else if(view == bButton){

            userAnswer.add(bText.text.toString())

            if(bText.text == s) {

                bButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                bButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            bUserImage.visibility = View.VISIBLE

        } else if(view == aButton){

            userAnswer.add(aText.text.toString())

            if(aText.text == s) {

                aButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                aButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            aUserImage.visibility = View.VISIBLE

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

        if(bText.text == s){

            bButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            bFriendImage.visibility = View.VISIBLE

            return

        }

        if(aText.text == s){

            aButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            aFriendImage.visibility = View.VISIBLE

            return

        }

        if(dAnswerText.text == s){

            dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            dAnswerFriendImage.visibility = View.VISIBLE

            return

        }

    }

}
