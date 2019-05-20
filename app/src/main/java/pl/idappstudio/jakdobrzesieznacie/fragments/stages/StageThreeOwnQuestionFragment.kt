package pl.idappstudio.jakdobrzesieznacie.fragments.stages

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_stage_three_own_question.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.friends
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.friendsStats
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.game
import pl.idappstudio.jakdobrzesieznacie.activity.GameActivity.Companion.userStats
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.interfaces.NextFragment
import pl.idappstudio.jakdobrzesieznacie.model.InviteNotificationMessage
import pl.idappstudio.jakdobrzesieznacie.model.Message
import pl.idappstudio.jakdobrzesieznacie.model.NotificationType
import pl.idappstudio.jakdobrzesieznacie.model.UserQuestionData
import pl.idappstudio.jakdobrzesieznacie.util.FirestoreUtil
import pl.idappstudio.jakdobrzesieznacie.util.GameUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

class StageThreeOwnQuestionFragment(private val listener: NextFragment) : androidx.fragment.app.Fragment(),
    TextWatcher {

    override fun afterTextChanged(s: Editable?) {

        checkEditText()

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    private lateinit var stageTitle: TextView

    private lateinit var questionText: EditText

    private lateinit var aAnswerText: EditText
    private lateinit var bAnswerText: EditText
    private lateinit var cAnswerText: EditText
    private lateinit var dAnswerText: EditText

    private lateinit var questionCheck: ImageView

    private lateinit var aAnswerCheck: ImageView
    private lateinit var bAnswerCheck: ImageView
    private lateinit var cAnswerCheck: ImageView
    private lateinit var dAnswerCheck: ImageView

    private lateinit var nextQuestion: Button

    private var userAnswer: ArrayList<String> = arrayListOf("", "", "")

    private var questionList: ArrayList<UserQuestionData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_stage_three_own_question, container, false)

        stageTitle = rootView.findViewById(R.id.gameStageTitle)

        questionText = rootView.findViewById(R.id.questionEditText)

        aAnswerText = rootView.findViewById(R.id.aAnswerEditText)
        bAnswerText = rootView.findViewById(R.id.bAnswerEditText)
        cAnswerText = rootView.findViewById(R.id.cAnswerEditText)
        dAnswerText = rootView.findViewById(R.id.dAnswerEditText)

        questionCheck = rootView.findViewById(R.id.dTextAnswer6)

        aAnswerCheck = rootView.findViewById(R.id.dTextAnswer5)
        bAnswerCheck = rootView.findViewById(R.id.dTextAnswer4)
        cAnswerCheck = rootView.findViewById(R.id.dTextAnswer3)
        dAnswerCheck = rootView.findViewById(R.id.dTextAnswer2)

        nextQuestion = rootView.findViewById(R.id.nextQuestionBtn)

        nextQuestion.setOnClickListener {

            checkAnswers()

        }

        questionText.addTextChangedListener(this)

        aAnswerText.addTextChangedListener(this)
        bAnswerText.addTextChangedListener(this)
        cAnswerText.addTextChangedListener(this)
        dAnswerText.addTextChangedListener(this)

        stageTitle.text = resources.getString(R.string.create_question)

        listener.showFragment()

        return rootView
    }

    private fun checkAnswers() {

        if(!questionText.text.isNullOrBlank()) {

            if(aAnswerText.text.isNullOrBlank()){

                SnackBarUtil.setActivitySnack(
                    resources.getString(R.string.enter_answer_a),
                    ColorSnackBar.ERROR,
                    R.drawable.ic_edit_text,
                    gameStageTitle
                ) {}

                return
            }

            if(bAnswerText.text.isNullOrBlank()){

                SnackBarUtil.setActivitySnack(
                    resources.getString(R.string.enter_answer_b),
                    ColorSnackBar.ERROR,
                    R.drawable.ic_edit_text,
                    gameStageTitle
                ) {}

                return
            }

            if(!(aAnswerCheck.tag == "canswer" || bAnswerCheck.tag == "canswer" || cAnswerCheck.tag == "canswer" || dAnswerCheck.tag == "canswer")){

                SnackBarUtil.setActivitySnack(
                    resources.getString(R.string.select_correct_answer),
                    ColorSnackBar.ERROR,
                    R.drawable.ic_error_,
                    gameStageTitle
                ) {}

                return
            }

            val q = questionText.text.toString()
            var ca = ""
            var ba1 = ""
            var ba2 = ""
            var ba3 = ""

            if (aAnswerCheck.tag == "canswer") {

                ca = aAnswerText.text.toString()
                ba1 = bAnswerText.text.toString()

                ba2 = if (!cAnswerText.text.isNullOrBlank()) {

                    cAnswerText.text.toString()

                } else {

                    ""

                }

                ba3 = if (!dAnswerText.text.isNullOrBlank()) {

                    dAnswerText.text.toString()

                } else {

                    ""

                }

            }

            if (bAnswerCheck.tag == "canswer") {

                ca = bAnswerText.text.toString()
                ba1 = aAnswerText.text.toString()

                ba2 = if (!cAnswerText.text.isNullOrBlank()) {

                    cAnswerText.text.toString()

                } else {

                    ""

                }

                ba3 = if (!dAnswerText.text.isNullOrBlank()) {

                    dAnswerText.text.toString()

                } else {

                    ""

                }

            }

            if (cAnswerCheck.tag == "canswer") {

                ca = cAnswerText.text.toString()
                ba1 = aAnswerText.text.toString()
                ba2 = bAnswerText.text.toString()

                ba3 = if (!dAnswerText.text.isNullOrBlank()) {

                    dAnswerText.text.toString()

                } else {

                    ""

                }

            }

            if (dAnswerCheck.tag == "canswer") {

                ca = dAnswerText.text.toString()
                ba1 = aAnswerText.text.toString()
                ba2 = bAnswerText.text.toString()
                ba3 = cAnswerText.text.toString()

            }

            if(ca.equals(ba1, true) || ca.equals(ba2, true) || ca.equals(ba3, true) ||
                ba1.equals(ca, true) || ba1.equals(ba2, true) || ba1.equals(ba3, true)) {

                SnackBarUtil.setActivitySnack(
                    resources.getString(R.string.answers_not_repeat),
                    ColorSnackBar.ERROR,
                    R.drawable.ic_error_,
                    gameStageTitle
                ) {}

                return

            }

            if((ba2 != "") && (ba2.equals(ca, true) || ba2.equals(ba1, true) || ba2.equals(ba3, true))){

                SnackBarUtil.setActivitySnack(
                    resources.getString(R.string.answers_not_repeat),
                    ColorSnackBar.ERROR,
                    R.drawable.ic_error_,
                    gameStageTitle
                ) {}

                return

            }

            if((ba3 != "") && (ba3.equals(ca, true) || ba3.equals(ba2, true) || ba3.equals(ba1, true))){

                SnackBarUtil.setActivitySnack(
                    resources.getString(R.string.answers_not_repeat),
                    ColorSnackBar.ERROR,
                    R.drawable.ic_error_,
                    gameStageTitle
                ) {}

                return
            }


            lockEditText()
            questionText.isEnabled = false

            val questionData = UserQuestionData(q, ca, ba1, ba2, ba3, UserUtil.user.uid)
            questionList.add(questionData)

            nextQuestion()

        } else {

            SnackBarUtil.setActivitySnack(
                resources.getString(R.string.enter_question_text),
                ColorSnackBar.ERROR,
                R.drawable.ic_edit_text,
                gameStageTitle
            ) {}

        }

    }

    @SuppressLint("PrivateResource")
    fun onClick(){

        aAnswerCheck.setOnClickListener {

            aAnswerCheck.tag = "canswer"
            bAnswerCheck.tag = "banswer"
            cAnswerCheck.tag = "banswer"
            dAnswerCheck.tag = "banswer"

            aAnswerCheck.setImageResource(R.drawable.ic_mtrl_chip_checked_circle)
            bAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            cAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            dAnswerCheck.setImageResource(R.drawable.ic_check_icon)

            when {
                questionList.size == 0 -> userAnswer[0] = aAnswerText.text.toString()
                questionList.size == 1 -> userAnswer[1] = aAnswerText.text.toString()
                questionList.size == 2 -> userAnswer[2] = aAnswerText.text.toString()
            }

        }

        bAnswerCheck.setOnClickListener {

            aAnswerCheck.tag = "banswer"
            bAnswerCheck.tag = "canswer"
            cAnswerCheck.tag = "banswer"
            dAnswerCheck.tag = "banswer"

            aAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            bAnswerCheck.setImageResource(R.drawable.ic_mtrl_chip_checked_circle)
            cAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            dAnswerCheck.setImageResource(R.drawable.ic_check_icon)

            when {
                questionList.size == 0 -> userAnswer[0] = bAnswerText.text.toString()
                questionList.size == 1 -> userAnswer[1] = bAnswerText.text.toString()
                questionList.size == 2 -> userAnswer[2] = bAnswerText.text.toString()
            }

        }

        cAnswerCheck.setOnClickListener {

            aAnswerCheck.tag = "banswer"
            bAnswerCheck.tag = "banswer"
            cAnswerCheck.tag = "canswer"
            dAnswerCheck.tag = "banswer"

            aAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            bAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            cAnswerCheck.setImageResource(R.drawable.ic_mtrl_chip_checked_circle)
            dAnswerCheck.setImageResource(R.drawable.ic_check_icon)

            when {
                questionList.size == 0 -> userAnswer[0] = cAnswerText.text.toString()
                questionList.size == 1 -> userAnswer[1] = cAnswerText.text.toString()
                questionList.size == 2 -> userAnswer[2] = cAnswerText.text.toString()
            }

        }

        dAnswerCheck.setOnClickListener {

            aAnswerCheck.tag = "banswer"
            bAnswerCheck.tag = "banswer"
            cAnswerCheck.tag = "banswer"
            dAnswerCheck.tag = "canswer"

            aAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            bAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            cAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            dAnswerCheck.setImageResource(R.drawable.ic_mtrl_chip_checked_circle)

            when {
                questionList.size == 0 -> userAnswer[0] = dAnswerText.text.toString()
                questionList.size == 1 -> userAnswer[1] = dAnswerText.text.toString()
                questionList.size == 2 -> userAnswer[2] = dAnswerText.text.toString()
            }

        }

    }

    private fun clear() {

        questionText.text.clear()

        aAnswerText.text.clear()
        bAnswerText.text.clear()
        cAnswerText.text.clear()
        dAnswerText.text.clear()

        questionCheck.visibility = View.INVISIBLE

        aAnswerCheck.visibility = View.INVISIBLE
        bAnswerCheck.visibility = View.INVISIBLE
        cAnswerCheck.visibility = View.INVISIBLE
        dAnswerCheck.visibility = View.INVISIBLE

        aAnswerCheck.tag = ""
        bAnswerCheck.tag = ""
        cAnswerCheck.tag = ""
        dAnswerCheck.tag = ""

        aAnswerCheck.setImageResource(R.drawable.ic_check_icon)
        bAnswerCheck.setImageResource(R.drawable.ic_check_icon)
        cAnswerCheck.setImageResource(R.drawable.ic_check_icon)
        dAnswerCheck.setImageResource(R.drawable.ic_check_icon)

        if(questionList.size == 2){

            nextQuestion.text = resources.getString(R.string.finish_your_turn)

        }

        unlockEditText()

    }

    private fun nextQuestion() {

        when {
            questionList.size == 1 -> {

                listener.updateNumber(1, true)
                clear()

            }
            questionList.size == 2 -> {

                listener.updateNumber(2, true)
                clear()

            }
            questionList.size == 3 -> {

                listener.updateNumber(3, true)

                GameUtil.sendOwnQuestion(
                    questionList,
                    game,
                    UserUtil.user,
                    friends,
                    userAnswer[0],
                    userAnswer[1],
                    userAnswer[2]
                ) {

                    if (userStats.games == 0 && friendsStats.games == 1) {

                        GameUtil.notNewGame(game)

                    }

                    GameUtil.updateGame(userStats.games, UserUtil.user.uid, friends.uid)
                    val msg: Message = InviteNotificationMessage(
                        resources.getString(R.string.notification_finish_turn_title),
                        resources.getString(R.string.notification_finish_turn, UserUtil.user.name),
                        UserUtil.user.uid,
                        friends.uid,
                        UserUtil.user.name,
                        NotificationType.GAME
                    )
                    FirestoreUtil.sendMessage(msg, friends.uid)

                    activity?.onBackPressed()

                }

            }
        }

    }

    private fun checkEditText() {

        if(questionText.text.isNullOrBlank()){

            questionCheck.visibility = View.INVISIBLE
            return

        } else {

            questionCheck.visibility = View.VISIBLE
            aAnswerText.isEnabled = true

        }

        if(aAnswerText.text.isNullOrBlank()){

            aAnswerCheck.visibility = View.INVISIBLE
            return

        } else {

            aAnswerCheck.visibility = View.VISIBLE
            bAnswerText.isEnabled = true
            onClick()

        }

        if(bAnswerText.text.isNullOrBlank()){

            bAnswerCheck.visibility = View.INVISIBLE
            return

        } else {

            bAnswerCheck.visibility = View.VISIBLE
            cAnswerText.isEnabled = true

        }

        if(cAnswerText.text.isNullOrBlank()){

            cAnswerCheck.visibility = View.INVISIBLE
            return

        } else {

            cAnswerCheck.visibility = View.VISIBLE
            dAnswerText.isEnabled = true

        }

        if(dAnswerText.text.isNullOrBlank()){

            dAnswerCheck.visibility = View.INVISIBLE
            return

        } else {

            dAnswerCheck.visibility = View.VISIBLE

        }

    }

    private fun lockEditText() {

        aAnswerText.isEnabled = false
        bAnswerText.isEnabled = false
        cAnswerText.isEnabled = false
        dAnswerText.isEnabled = false

    }

    private fun unlockEditText() {

        questionText.isEnabled = true
        aAnswerText.isEnabled = false
        bAnswerText.isEnabled = false
        cAnswerText.isEnabled = false
        dAnswerText.isEnabled = false

    }

}
