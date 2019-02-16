package pl.idappstudio.howwelldoyouknoweachother.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.model.UserQuestionData
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity
import pl.idappstudio.howwelldoyouknoweachother.interfaces.nextFragment
import pl.idappstudio.howwelldoyouknoweachother.model.InviteNotificationMessage
import pl.idappstudio.howwelldoyouknoweachother.model.Message
import pl.idappstudio.howwelldoyouknoweachother.model.NotificationType
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GameUtil


class StageThreeOwnQuestionFragment(private val listener: nextFragment) : Fragment(), TextWatcher {

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

    private lateinit var snackbar: Snackbar
    private lateinit var nextQuestion: Button

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

        stageTitle.text = "Stwórz Pytania"

        return rootView
    }

    fun checkAnswers() {

        if(!questionText.text.isNullOrBlank()) {

            if(aAnswerText.text.isNullOrBlank()){

                snackbar = Snackbar.make(this.view!!, "Uzupełnij odpowiedź A", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()

                return
            }

            if(bAnswerText.text.isNullOrBlank()){

                snackbar = Snackbar.make(this.view!!, "Uzupełnij odpowiedź B", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()

                return
            }

            if(!(aAnswerCheck.tag == "canswer" || bAnswerCheck.tag == "canswer" || cAnswerCheck.tag == "canswer" || dAnswerCheck.tag == "canswer")){

                snackbar = Snackbar.make(this.view!!, "Kliknij na ptaszek przy treści odpowiedzi, aby zaznaczyć która odpowiedź jest prawidłowa", 5000)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()

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

                if (!cAnswerText.text.isNullOrBlank()) {

                    ba2 = cAnswerText.text.toString()

                } else {

                    ba2 = ""

                }

                if (!dAnswerText.text.isNullOrBlank()) {

                    ba3 = dAnswerText.text.toString()

                } else {

                    ba3 = ""

                }

            }

            if (bAnswerCheck.tag == "canswer") {

                ca = bAnswerText.text.toString()
                ba1 = aAnswerText.text.toString()

                if (!cAnswerText.text.isNullOrBlank()) {

                    ba2 = cAnswerText.text.toString()

                } else {

                    ba2 = ""

                }

                if (!dAnswerText.text.isNullOrBlank()) {

                    ba3 = dAnswerText.text.toString()

                } else {

                    ba3 = ""

                }

            }

            if (cAnswerCheck.tag == "canswer") {

                ca = cAnswerText.text.toString()
                ba1 = aAnswerText.text.toString()
                ba2 = bAnswerText.text.toString()

                if (!dAnswerText.text.isNullOrBlank()) {

                    ba3 = dAnswerText.text.toString()

                } else {

                    ba3 = ""

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

                snackbar = Snackbar.make(this.view!!, "Odpowiedzi nie mogą się powtarzać", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()

                return

            }

            if((ba2 != "") && (ba2.equals(ca, true) || ba2.equals(ba1, true) || ba2.equals(ba3, true))){

                snackbar = Snackbar.make(this.view!!, "Odpowiedzi nie mogą się powtarzać", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()

                return

            }

            if((ba3 != "") && (ba3.equals(ca, true) || ba3.equals(ba2, true) || ba3.equals(ba1, true))){

                snackbar = Snackbar.make(this.view!!, "Odpowiedzi nie mogą się powtarzać", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()

                return
            }


            lockEditText()
            questionText.isEnabled = false

            val questionData = UserQuestionData(q, ca, ba1, ba2, ba3, GameActivity.user.uid)
            questionList.add(questionData)

            nextQuestion()

        } else {

            snackbar = Snackbar.make(this.view!!, "Wpisz treść pytania", 2500)
            snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
            snackbar.show()

        }

    }

    fun onClick(){

        aAnswerCheck.setOnClickListener {

            aAnswerCheck.tag = "canswer"
            bAnswerCheck.tag = "banswer"
            cAnswerCheck.tag = "banswer"
            dAnswerCheck.tag = "banswer"

            aAnswerCheck.setImageResource(R.drawable.fui_ic_check_circle_black_128dp)
            bAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            cAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            dAnswerCheck.setImageResource(R.drawable.ic_check_icon)

        }

        bAnswerCheck.setOnClickListener {

            aAnswerCheck.tag = "banswer"
            bAnswerCheck.tag = "canswer"
            cAnswerCheck.tag = "banswer"
            dAnswerCheck.tag = "banswer"

            aAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            bAnswerCheck.setImageResource(R.drawable.fui_ic_check_circle_black_128dp)
            cAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            dAnswerCheck.setImageResource(R.drawable.ic_check_icon)

        }

        cAnswerCheck.setOnClickListener {

            aAnswerCheck.tag = "banswer"
            bAnswerCheck.tag = "banswer"
            cAnswerCheck.tag = "canswer"
            dAnswerCheck.tag = "banswer"

            aAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            bAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            cAnswerCheck.setImageResource(R.drawable.fui_ic_check_circle_black_128dp)
            dAnswerCheck.setImageResource(R.drawable.ic_check_icon)

        }

        dAnswerCheck.setOnClickListener {

            aAnswerCheck.tag = "banswer"
            bAnswerCheck.tag = "banswer"
            cAnswerCheck.tag = "banswer"
            dAnswerCheck.tag = "canswer"

            aAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            bAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            cAnswerCheck.setImageResource(R.drawable.ic_check_icon)
            dAnswerCheck.setImageResource(R.drawable.fui_ic_check_circle_black_128dp)

        }

    }

    fun clear(){

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

            nextQuestion.text = "Zakończ swoją turę"

        }

        unlockEditText()

    }

    fun nextQuestion(){

        if(questionList.size == 1){

            listener.updateNumber(1, true)
            clear()

        } else if(questionList.size == 2){

            listener.updateNumber(2, true)
            clear()

        } else if(questionList.size == 3){

            listener.updateNumber(3, true)

            GameUtil.sendOwnQuestion(questionList, GameActivity.game, GameActivity.user, GameActivity.friends){

                if(GameActivity.userStats.games == 0 && GameActivity.stats.games == 1){

                    GameUtil.notNewGame(GameActivity.game)

                }

                GameUtil.updateGame(GameActivity.userStats.games, GameActivity.user.uid, GameActivity.friends.uid)

                val msg: Message = InviteNotificationMessage("Twoja kolej!", "${GameActivity.user.name} skończył swoją turę, czas na ciebie!", GameActivity.user.uid, GameActivity.friends.uid,GameActivity.user.name, NotificationType.GAME)
                FirestoreUtil.sendMessage(msg, GameActivity.friends.uid)

                this.activity?.finish()

            }

        }

    }

    fun checkEditText(){

        if(questionText.text.isNullOrBlank()){

            return

        } else {

            questionCheck.visibility = View.VISIBLE
            aAnswerText.isEnabled = true

        }

        if(aAnswerText.text.isNullOrBlank()){

            return

        } else {

            aAnswerCheck.visibility = View.VISIBLE
            bAnswerText.isEnabled = true
            onClick()

        }

        if(bAnswerText.text.isNullOrBlank()){

            return

        } else {

            bAnswerCheck.visibility = View.VISIBLE
            cAnswerText.isEnabled = true

        }

        if(cAnswerText.text.isNullOrBlank()){

            return

        } else {

            cAnswerCheck.visibility = View.VISIBLE
            dAnswerText.isEnabled = true

        }

        if(dAnswerText.text.isNullOrBlank()){

            return

        } else {

            dAnswerCheck.visibility = View.VISIBLE

        }

    }

    fun lockEditText(){

        aAnswerText.isEnabled = false
        bAnswerText.isEnabled = false
        cAnswerText.isEnabled = false
        dAnswerText.isEnabled = false

    }

    fun unlockEditText(){

        questionText.isEnabled = true
        aAnswerText.isEnabled = false
        bAnswerText.isEnabled = false
        cAnswerText.isEnabled = false
        dAnswerText.isEnabled = false

    }

    companion object {
        fun newInstance(): StatesFragment = StatesFragment()
    }

}
