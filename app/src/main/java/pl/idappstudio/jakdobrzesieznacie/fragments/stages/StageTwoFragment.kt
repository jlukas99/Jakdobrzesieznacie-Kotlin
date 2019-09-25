package pl.idappstudio.jakdobrzesieznacie.fragments.stages

import android.app.Dialog
import android.content.pm.PackageManager
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
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_stage_two.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.answerList
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.friends
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.game
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.questionList
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.yourAnswerList
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.interfaces.NextFragment
import pl.idappstudio.jakdobrzesieznacie.model.UserQuestionData
import pl.idappstudio.jakdobrzesieznacie.util.FirestoreUtil
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

@Suppress("DEPRECATION")
class StageTwoFragment(private val listener: NextFragment) : androidx.fragment.app.Fragment() {

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
    private lateinit var skipStage: Button

    private lateinit var setDialog: Dialog
    private lateinit var dialogReason: EditText
    private lateinit var dialogSend: MaterialButton
    private lateinit var dialogCancel: MaterialButton
    private lateinit var dialogIdQuestion: TextView

    private lateinit var reportQuestion: ImageView

    private val glide = GlideUtil

    private var questionNumber = 1

    private var question = ""
    private var canswer = ""
    private var banswer = ""
    private var banswer2 = ""
    private var banswer3 = ""

    private var answer = ""
    private var yanswer = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_stage_two, container, false)

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

        nextQuestion = rootView.findViewById(R.id.nextQuestionBtn)
        skipStage = rootView.findViewById(R.id.skipStage)

        reportQuestion = rootView.findViewById(R.id.btnReport)

        setDialog()

        reportQuestion.setOnClickListener {
            showDialog()
        }

        skipStage.setOnClickListener {

            FirestoreUtil.updateGameSettings(
                    yourTurn = true,
                    friendTurn = false,
                    yourStage = 3,
                    friendStage = 1,
                    yourSet = game.uSet.id,
                    friendSet = game.fSet.id,
                    gamemode = game.gamemode,
                    gameID = game.gameID,
                    yourID = UserUtil.user.uid,
                    friendID = friends.uid
            )

            listener.next()

        }

        loadImage()

        lockButton()

        setText()

        return rootView
    }

    private fun resetButton(){

        nextQuestion.text = resources.getString(R.string.next_answer)
        nextQuestion.background.setColorFilter(
                ContextCompat.getColor(
                        this.context!!, R.color.colorPrimary
                ), android.graphics.PorterDuff.Mode.SRC_IN)

        stageTitle.text = resources.getString(R.string.friend_answer)

        aAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.card_background_dark)
        bAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.card_background_dark)
        cAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.card_background_dark)
        dAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.card_background_dark)

        aAnswerButton.visibility = View.INVISIBLE
        bAnswerButton.visibility = View.INVISIBLE
        cAnswerButton.visibility = View.INVISIBLE
        dAnswerButton.visibility = View.INVISIBLE

        aAnswerUserImage.visibility = View.INVISIBLE
        bAnswerUserImage.visibility = View.INVISIBLE
        cAnswerUserImage.visibility = View.INVISIBLE
        dAnswerUserImage.visibility = View.INVISIBLE

        aAnswerFriendImage.visibility = View.INVISIBLE
        bAnswerFriendImage.visibility = View.INVISIBLE
        cAnswerFriendImage.visibility = View.INVISIBLE
        dAnswerFriendImage.visibility = View.INVISIBLE

    }

    private fun lockButton(){

        aAnswerButton.isEnabled = false
        bAnswerButton.isEnabled = false
        cAnswerButton.isEnabled = false
        dAnswerButton.isEnabled = false

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
                canswer = questionList.question.a
                banswer = questionList.question.b
                banswer2 = questionList.question.c
                banswer3 = questionList.question.d

                answer = answerList.answer1
                yanswer = yourAnswerList.answer1


            }
            2 -> {

                question = questionList.question1.question
                canswer = questionList.question1.a
                banswer = questionList.question1.b
                banswer2 = questionList.question1.c
                banswer3 = questionList.question1.d

                answer = answerList.answer2
                yanswer = yourAnswerList.answer2

            }
            3 -> {

                question = questionList.question2.question
                canswer = questionList.question2.a
                banswer = questionList.question2.b
                banswer2 = questionList.question2.c
                banswer3 = questionList.question2.d

                answer = answerList.answer3
                yanswer = yourAnswerList.answer3

            }
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

        array.shuffle()

        if(aAnswerText.text == ""){

            for (a in array) {

                if (a != "") {

                    aAnswerText.text = a
                    array.remove(a)

                    aAnswerButton.visibility = View.VISIBLE

                    break

                }

            }

        }

        if(bAnswerText.text == ""){

            for (a in array) {

                if (a != "") {

                    bAnswerText.text = a
                    array.remove(a)

                    bAnswerButton.visibility = View.VISIBLE

                    break

                }

            }

        }

        if(cAnswerText.text == ""){

            for (a in array) {

                if (a != "") {

                    cAnswerText.text = a
                    array.remove(a)

                    cAnswerButton.visibility = View.VISIBLE

                    break

                }

            }

        }

        if(dAnswerText.text == ""){

            for (a in array) {

                if (a != "") {

                    dAnswerText.text = a
                    array.remove(a)

                    dAnswerButton.visibility = View.VISIBLE

                    break

                }

            }

        }

        checkAnswer()

    }

    private fun nextQuestion() {

        nextQuestion.isEnabled = true

        if (questionNumber == 3) {

            nextQuestion.text = resources.getString(R.string.next_stage)
            nextQuestion.background.setColorFilter(
                    ContextCompat.getColor(
                            this.context!!, R.color.colorCorrectAnswer
                    ), android.graphics.PorterDuff.Mode.SRC_IN
            )

            skipStage.visibility = View.GONE

        }

        nextQuestion.setOnClickListener {

            lockButton()

            questionNumber++

            if (questionNumber != 4) {

                setText()

            } else {

                FirestoreUtil.updateGameSettings(
                        yourTurn = true,
                        friendTurn = false,
                        yourStage = 3,
                        friendStage = 1,
                        yourSet = game.uSet.id,
                        friendSet = game.fSet.id,
                        gamemode = game.gamemode,
                        gameID = game.gameID,
                        yourID = UserUtil.user.uid,
                        friendID = friends.uid
                )

                listener.next()

            }
        }

        if(questionText.text != "...") {

            listener.showFragment()

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

    private fun checkAnswer(){

        if(answer.trimEnd().trimStart() == yanswer.trimEnd().trimStart()){

            if(aAnswerText.text.toString().trimEnd().trimStart() == answer.trimEnd().trimStart()){

                aAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                aAnswerFriendImage.visibility = View.VISIBLE

            }

            if(bAnswerText.text.toString().trimEnd().trimStart() == answer.trimEnd().trimStart()){

                bAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                bAnswerFriendImage.visibility = View.VISIBLE

            }

            if(cAnswerText.text.toString().trimEnd().trimStart() == answer.trimEnd().trimStart()){

                cAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                cAnswerFriendImage.visibility = View.VISIBLE

            }

            if(dAnswerText.text.toString().trimEnd().trimStart() == answer.trimEnd().trimStart()){

                dAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                dAnswerFriendImage.visibility = View.VISIBLE

            }

            checkCorrectAnswer()

        } else {

            if(aAnswerText.text.toString().trimEnd().trimStart() == answer.trimEnd().trimStart()){

                aAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                aAnswerFriendImage.visibility = View.VISIBLE

            }

            if(bAnswerText.text.toString().trimEnd().trimStart() == answer.trimEnd().trimStart()){

                bAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                bAnswerFriendImage.visibility = View.VISIBLE

            }

            if(cAnswerText.text.toString().trimEnd().trimStart() == answer.trimEnd().trimStart()){

                cAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                cAnswerFriendImage.visibility = View.VISIBLE

            }

            if(dAnswerText.text.toString().trimEnd().trimStart() == answer.trimEnd().trimStart()){

                dAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                dAnswerFriendImage.visibility = View.VISIBLE

            }

            checkCorrectAnswer()

        }

    }

    private fun checkCorrectAnswer(){

        if(aAnswerText.text.trimEnd().trimStart() == yanswer.trimEnd().trimStart()){

            aAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)
            aAnswerUserImage.visibility = View.VISIBLE

            nextQuestion()
            return

        }

        if(bAnswerText.text.trimEnd().trimStart() == yanswer.trimEnd().trimStart()){

            bAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)
            bAnswerUserImage.visibility = View.VISIBLE

            nextQuestion()
            return

        }

        if(cAnswerText.text.trimEnd().trimStart() == yanswer.trimEnd().trimStart()){

            cAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)
            cAnswerUserImage.visibility = View.VISIBLE

            nextQuestion()
            return

        }

        if(dAnswerText.text.trimEnd().trimStart() == yanswer.trimEnd().trimStart()){

            dAnswerButton.background = ContextCompat.getDrawable(this.context!!, R.drawable.number_correct_overlay)
            dAnswerUserImage.visibility = View.VISIBLE

            nextQuestion()
            return

        }

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

}
