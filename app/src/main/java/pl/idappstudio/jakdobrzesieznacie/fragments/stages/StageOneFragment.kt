package pl.idappstudio.jakdobrzesieznacie.fragments.stages

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_stage_one.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.friends
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.game
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.questionList
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.userStats
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.interfaces.NextFragment
import pl.idappstudio.jakdobrzesieznacie.model.UserQuestionData
import pl.idappstudio.jakdobrzesieznacie.util.GameUtil
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

@Suppress("DEPRECATION")
class StageOneFragment(private val listener: NextFragment) : androidx.fragment.app.Fragment(), View.OnClickListener {

    override fun onClick(v: View?) {

        if(v != null){

            rocketAnimation.start()

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

    private lateinit var setDialog: Dialog
    private lateinit var dialogReason: EditText
    private lateinit var dialogSend: MaterialButton
    private lateinit var dialogCancel: MaterialButton
    private lateinit var dialogIdQuestion: TextView

    private lateinit var nextQuestion: Button
    private lateinit var reportQuestion: ImageView
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

    private lateinit var rocketAnimation: AnimationDrawable

    private lateinit var alertDialog: AlertDialog

    @SuppressLint("PrivateResource", "InflateParams")
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

        reportQuestion = rootView.findViewById(R.id.btnReport)

        setDialog()

        reportQuestion.setOnClickListener {
            showDialog()
        }

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

                SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.load_ad_failed),
                        ColorSnackBar.ERROR,
                        R.drawable.ic_error_,
                        setDialog.window?.decorView!!
                ) {}

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
        val dialogView = this.layoutInflater.inflate(R.layout.dialog_loading, null)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(
                ColorDrawable(
                        ContextCompat.getColor(
                                this.context!!,
                                R.color.colorTranspery
                        )
                )
        )
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

        nextQuestion.text = resources.getString(R.string.guess_answer)
        nextQuestion.background.setColorFilter(
                ContextCompat.getColor(
                        this.context!!, R.color.colorBadAnswer
                ), android.graphics.PorterDuff.Mode.SRC_IN)

        stageTitle.text = resources.getString(R.string.guess_answer_title)

        aAnswerButton.apply {
            background = ContextCompat.getDrawable(this.context!!, R.drawable.background_animation)
            rocketAnimation = background as AnimationDrawable
        }

        bAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.card_background_dark)
        cAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.card_background_dark)
        dAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.card_background_dark)
        aAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.card_background_dark)

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

        resetDialog()
        resetButton()

        aAnswerText.text = ""
        bAnswerText.text = ""
        cAnswerText.text = ""
        dAnswerText.text = ""

        when (questionNumber) {
            1 -> {

                question = questionList.question.question
                a = questionList.question.a
                b = questionList.question.b
                c = questionList.question.c
                d = questionList.question.d

            }
            2 -> {

                question = questionList.question1.question
                a = questionList.question1.a
                b = questionList.question1.b
                c = questionList.question1.c
                d = questionList.question1.d

            }
            3 -> {

                question = questionList.question2.question
                a = questionList.question2.a
                b = questionList.question2.b
                c = questionList.question2.c
                d = questionList.question2.d

            }

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

        if (aAnswerText.text == badAnswer[0]) {

            aAnswerButton.isEnabled = false

            aAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

            return
        }

        if (bAnswerText.text == badAnswer[0]) {

            bAnswerButton.isEnabled = false

            bAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

            return
        }

        if (cAnswerText.text == badAnswer[0]) {

            cAnswerButton.isEnabled = false

            cAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

            return
        }

        if (dAnswerText.text == badAnswer[0]) {

            dAnswerButton.isEnabled = false

            dAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

            return
        }

    }

    private fun nextQuestion() {

        nextQuestion.isEnabled = true
        enable = true

        if (questionNumber == 3) {

            nextQuestion.text = resources.getString(R.string.next_stage)
            nextQuestion.background.setColorFilter(
                    ContextCompat.getColor(
                            this.context!!, R.color.colorCorrectAnswer
                    ), android.graphics.PorterDuff.Mode.SRC_IN
            )

        } else {

            nextQuestion.text = resources.getString(R.string.next_question)
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

    private fun setDialog() {

        setDialog = Dialog(context!!)
        setDialog.setCancelable(true)
        setDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setDialog.setContentView(R.layout.dialog_report_question)
        setDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_invite_overlay)

        dialogSend = setDialog.findViewById(R.id.addFriends)
        dialogCancel = setDialog.findViewById(R.id.deleteFriends)
        dialogReason = setDialog.findViewById(R.id.profile_name2)
        dialogIdQuestion = setDialog.findViewById(R.id.profile_name4)

        dialogCancel.setOnClickListener {
            closeDialog()
        }

        dialogSend.setOnClickListener {

            dialogReason.isEnabled = false
            dialogSend.isEnabled = false
            dialogCancel.isEnabled = false

            if(dialogReason.text.trim().length < 5){

                SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.reason_is_to_short),
                        ColorSnackBar.WARING,
                        R.drawable.ic_edit_text,
                        setDialog.window?.decorView!!
                ) {

                    dialogReason.isEnabled = true
                    dialogSend.isEnabled = true
                    dialogCancel.isEnabled = true

                }

                return@setOnClickListener
            }

            val data = HashMap<String, String>()
            data["questionId"] = getQuestionData()?.questionId.toString()
            data["reason"] = dialogReason.text.toString()
            data["appVersion"] = getVersion()

            FirebaseFirestore.getInstance().collection("reports").add(data).addOnSuccessListener { it2 ->

                closeDialog()
                SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.send_report_successful, it2.id),
                        ColorSnackBar.SUCCES,
                        R.drawable.ic_check_icon,
                        gameStageTitle
                ) {

                    resetDialog()

                }

            }.addOnFailureListener {

                SnackBarUtil.setActivitySnack(
                        resources.getString(R.string.send_report_error),
                        ColorSnackBar.ERROR,
                        R.drawable.ic_error_,
                        setDialog.window?.decorView!!
                ) {

                    dialogReason.isEnabled = true
                    dialogSend.isEnabled = true
                    dialogCancel.isEnabled = true

                }

            }

        }

    }

    private fun resetDialog() {

        dialogIdQuestion.text = resources.getString(R.string.question_id, getQuestionData()?.questionId)
        dialogReason.text.clear()

        dialogReason.isEnabled = true
        dialogSend.isEnabled = true
        dialogCancel.isEnabled = true

    }

    private fun getVersion() : String{

        return try {
            val pInfo = context?.packageManager?.getPackageInfo(context?.packageName!!, 0)
            pInfo?.versionName!!
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "error"
        }

    }

    private fun getQuestionData() : UserQuestionData?{

        return when(questionNumber) {

            1 -> questionList.question
            2 -> questionList.question1
            3 -> questionList.question2
            else -> null

        }

    }

    private fun closeDialog() {

        if (setDialog.isShowing) {

            setDialog.dismiss()

        }

    }

    private fun showDialog() {

        if (!setDialog.isShowing) {

            setDialog.show()

        }

    }

    private fun checkAnswer(view: View, s: String){

        when (view) {
            aAnswerButton -> {

                userAnswer.add(aAnswerText.text.toString())

                if (aAnswerText.text == s) {

                    aAnswerButton.background =
                            ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)

                    listener.updateNumber(questionNumber, true)

                    GameActivity.canswer++

                } else {

                    aAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

                    listener.updateNumber(questionNumber, false)

                    GameActivity.banswer++

                }

                checkCorrectAnswer(s)
                aAnswerUserImage.visibility = View.VISIBLE

            }
            bAnswerButton -> {

                userAnswer.add(bAnswerText.text.toString())

                if (bAnswerText.text == s) {

                    bAnswerButton.background =
                            ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)

                    listener.updateNumber(questionNumber, true)

                    GameActivity.canswer++

                } else {

                    bAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

                    listener.updateNumber(questionNumber, false)

                    GameActivity.banswer++

                }

                checkCorrectAnswer(s)
                bAnswerUserImage.visibility = View.VISIBLE

            }
            cAnswerButton -> {

                userAnswer.add(cAnswerText.text.toString())

                if (cAnswerText.text == s) {

                    cAnswerButton.background =
                            ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)

                    listener.updateNumber(questionNumber, true)

                    GameActivity.canswer++

                } else {

                    cAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

                    listener.updateNumber(questionNumber, false)

                    GameActivity.banswer++

                }

                checkCorrectAnswer(s)
                cAnswerUserImage.visibility = View.VISIBLE

            }
            dAnswerButton -> {

                userAnswer.add(dAnswerText.text.toString())

                if (dAnswerText.text == s) {

                    dAnswerButton.background =
                            ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)

                    listener.updateNumber(questionNumber, true)

                    GameActivity.canswer++

                } else {

                    dAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

                    listener.updateNumber(questionNumber, false)

                    GameActivity.banswer++

                }

                checkCorrectAnswer(s)
                dAnswerUserImage.visibility = View.VISIBLE

            }
        }

        nextQuestion()

    }

    private fun checkCorrectAnswer(s: String){

        if(aAnswerText.text == s){

            aAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)
            aAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(bAnswerText.text == s){

            bAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)
            bAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(cAnswerText.text == s){

            cAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)
            cAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(dAnswerText.text == s){

            dAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)
            dAnswerFriendImage.visibility = View.VISIBLE

            return

        }

    }

}
