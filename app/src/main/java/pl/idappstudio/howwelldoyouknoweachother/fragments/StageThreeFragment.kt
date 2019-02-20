package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.user
import pl.idappstudio.howwelldoyouknoweachother.interfaces.nextFragment
import pl.idappstudio.howwelldoyouknoweachother.model.InviteNotificationMessage
import pl.idappstudio.howwelldoyouknoweachother.model.Message
import pl.idappstudio.howwelldoyouknoweachother.model.NotificationType
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GameUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GlideUtil

class StageThreeFragment(private val listener: nextFragment) : android.support.v4.app.Fragment(), View.OnClickListener {

    override fun onClick(v: View?) {

        if(v != null){

            lockButton()

            checkAnswer(v)

        }

    }

    private val glide = GlideUtil()

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

    private var userAnswer: ArrayList<String> = ArrayList()

    private var questionNumber = 1

    private var question = ""
    private var canswer = ""
    private var banswer = ""
    private var banswer2 = ""
    private var banswer3 = ""

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

        loadImage()

        lockButton()

        setText()

        return rootView
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

                setText()

            } else {

                GameUtil.sendAnswerStageThree(GameActivity.game, GameActivity.user, GameActivity.friends, userAnswer[0], userAnswer[1], userAnswer[2],
                    "set/${GameActivity.game.uSet.id}/${GameActivity.user.gender}/${GameActivity.questionList.question.questionId}",
                    "set/${GameActivity.game.uSet.id}/${GameActivity.user.gender}/${GameActivity.questionList.question1.questionId}",
                    "set/${GameActivity.game.uSet.id}/${GameActivity.user.gender}/${GameActivity.questionList.question2.questionId}"){

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

    }

    private fun checkAnswer(view: View){

        if(view == aAnswerButton){

            userAnswer.add(aAnswerText.text.toString())

            aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

            listener.updateNumber(questionNumber, true)

            aAnswerUserImage.visibility = View.VISIBLE

        } else if(view == bAnswerButton){

            userAnswer.add(bAnswerText.text.toString())

            bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

            listener.updateNumber(questionNumber, true)

            bAnswerUserImage.visibility = View.VISIBLE

        } else if(view == cAnswerButton){

            userAnswer.add(cAnswerText.text.toString())

            cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

            listener.updateNumber(questionNumber, true)

            cAnswerUserImage.visibility = View.VISIBLE

        } else if(view == dAnswerButton){

            userAnswer.add(dAnswerText.text.toString())

            dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

            listener.updateNumber(questionNumber, true)

            dAnswerUserImage.visibility = View.VISIBLE

        }

        nextQuestion()

    }

    private fun lockButton(){

        aAnswerButton.isEnabled = false
        bAnswerButton.isEnabled = false
        cAnswerButton.isEnabled = false
        dAnswerButton.isEnabled = false

        nextQuestion.isEnabled = false

    }

    private fun setText(){

        resetButton()

        aAnswerText.text = ""
        bAnswerText.text = ""
        cAnswerText.text = ""
        dAnswerText.text = ""

        if(questionNumber == 1){

            question = GameActivity.questionList.question.question
            canswer = GameActivity.questionList.question.canswer
            banswer = GameActivity.questionList.question.banswer
            banswer2 = GameActivity.questionList.question.banswer2
            banswer3 = GameActivity.questionList.question.banswer3

        } else if(questionNumber == 2){

            question = GameActivity.questionList.question1.question
            canswer = GameActivity.questionList.question1.canswer
            banswer = GameActivity.questionList.question1.banswer
            banswer2 = GameActivity.questionList.question1.banswer2
            banswer3 = GameActivity.questionList.question1.banswer3

        } else if(questionNumber == 3){

            question = GameActivity.questionList.question2.question
            canswer = GameActivity.questionList.question2.canswer
            banswer = GameActivity.questionList.question2.banswer
            banswer2 = GameActivity.questionList.question2.banswer2
            banswer3 = GameActivity.questionList.question2.banswer3

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

        if(canswer.equals("tak", true) || canswer.equals("nie", true)){

            aAnswerText.text = "Tak"

            aAnswerButton.isEnabled = true
            aAnswerButton.visibility = View.VISIBLE

            bAnswerText.text = "Nie"

            bAnswerButton.isEnabled = true
            bAnswerButton.visibility = View.VISIBLE

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

    }

    private fun loadImage(){

        glide.setImage(user.fb, user.image, this.requireContext(), aAnswerUserImage) {}
        glide.setImage(user.fb, user.image, this.requireContext(), bAnswerUserImage) {}
        glide.setImage(user.fb, user.image, this.requireContext(), cAnswerUserImage) {}
        glide.setImage(user.fb, user.image, this.requireContext(), dAnswerUserImage) {}

    }

}
