package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.friends
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.user
import pl.idappstudio.howwelldoyouknoweachother.interfaces.nextFragment
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GlideUtil

class StageTwoFragment(private val listener: nextFragment) : Fragment() {

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

    private val glide = GlideUtil()

    private var questionNumber = 1

    private var question = ""
    private var canswer = ""
    private var banswer = ""
    private var banswer2 = ""
    private var banswer3 = ""

    private var answer = ""

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

        skipStage.setOnClickListener {

            FirestoreUtil.updateGameSettings(true, false, 3, 1, GameActivity.game.uSet.id, GameActivity.game.fSet.id, GameActivity.game.gamemode, GameActivity.game.gameID, GameActivity.user.uid, GameActivity.friends.uid)

            listener.next()

        }

        loadImage()

        lockButton()

        setText()

        return rootView
    }

    private fun resetButton(){

        nextQuestion.text = "nastepna odpowiedz"
        nextQuestion.background.setColorFilter(
            ContextCompat.getColor(
                this.context!!, R.color.colorPrimary
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        stageTitle.text = "Odpowiedzi Znajomego"

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

    }

    private fun lockButton(){

        aAnswerButton.isEnabled = false
        bAnswerButton.isEnabled = false
        cAnswerButton.isEnabled = false
        dAnswerButton.isEnabled = false

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

            answer = GameActivity.answerList.answer1

        } else if(questionNumber == 2){

            question = GameActivity.questionList.question1.question
            canswer = GameActivity.questionList.question1.canswer
            banswer = GameActivity.questionList.question1.banswer
            banswer2 = GameActivity.questionList.question1.banswer2
            banswer3 = GameActivity.questionList.question1.banswer3

            answer = GameActivity.answerList.answer2

        } else if(questionNumber == 3){

            question = GameActivity.questionList.question2.question
            canswer = GameActivity.questionList.question2.canswer
            banswer = GameActivity.questionList.question2.banswer
            banswer2 = GameActivity.questionList.question2.banswer2
            banswer3 = GameActivity.questionList.question2.banswer3

            answer = GameActivity.answerList.answer3

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

                for(a in array){

                    if(a != ""){

                        aAnswerText.text = a
                        array.remove(a)

                        aAnswerButton.visibility = View.VISIBLE

                        break

                    }

                }

            }

        if(bAnswerText.text == ""){

                for(a in array){

                    if(a != ""){

                        bAnswerText.text = a
                        array.remove(a)

                        bAnswerButton.visibility = View.VISIBLE

                        break

                    }

                }

            }

        if(cAnswerText.text == ""){

                for(a in array){

                    if(a != ""){

                        cAnswerText.text = a
                        array.remove(a)

                        cAnswerButton.visibility = View.VISIBLE

                        break

                    }

                }

            }

        if(dAnswerText.text == ""){

                for(a in array){

                    if(a != ""){

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

        if (questionNumber == 3) {

            nextQuestion.text = "nastepny etap"
            nextQuestion.background.setColorFilter(
                ContextCompat.getColor(
                    this.context!!, R.color.colorCorrectAnswer
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

            skipStage.visibility = View.GONE

        }

        nextQuestion.setOnClickListener {

            questionNumber++

            if (questionNumber != 4) {

                setText()

            } else {

                FirestoreUtil.updateGameSettings(true, false, 3, 1, GameActivity.game.uSet.id, GameActivity.game.fSet.id, GameActivity.game.gamemode, GameActivity.game.gameID, GameActivity.user.uid, GameActivity.friends.uid)

                listener.next()

            }
        }

    }

    private fun loadImage(){

        loop@for(i in 0..3){

            if(i == 0){

                setFriendImage(aAnswerFriendImage)
                setUserImage(aAnswerUserImage)

                continue@loop

            }

            if(i == 1){

                setFriendImage(bAnswerFriendImage)
                setUserImage(bAnswerUserImage)

                continue@loop

            }

            if(i == 2){

                setFriendImage(cAnswerFriendImage)
                setUserImage(cAnswerUserImage)

                continue@loop

            }

            if(i == 3){

                setFriendImage(dAnswerFriendImage)
                setUserImage(dAnswerUserImage)

                continue@loop

            }

        }

    }

    private fun checkAnswer(){

        Log.d("Stage 2", answer + " | " + canswer)

        if(answer == canswer){

            if(aAnswerText.text.toString() == canswer){

                aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                aAnswerUserImage.visibility = View.VISIBLE

            }

            if(bAnswerText.text.toString() == canswer){

                bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, false)

                bAnswerUserImage.visibility = View.VISIBLE

            }

            if(cAnswerText.text.toString() == canswer){

                cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                cAnswerUserImage.visibility = View.VISIBLE

            }

            if(dAnswerText.text.toString() == canswer){

                dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, false)

                dAnswerUserImage.visibility = View.VISIBLE

            }

        } else {

            if(aAnswerText.text.toString() == answer){

                aAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, true)

                aAnswerUserImage.visibility = View.VISIBLE

            }

            if(bAnswerText.text.toString() == answer){

                bAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                bAnswerUserImage.visibility = View.VISIBLE

            }

            if(cAnswerText.text.toString() == answer){

                cAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, true)

                cAnswerUserImage.visibility = View.VISIBLE

            }

            if(dAnswerText.text.toString() == answer){

                dAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                dAnswerUserImage.visibility = View.VISIBLE

            }

        }

        checkCorrectAnswer()
        nextQuestion()

    }

    private fun checkCorrectAnswer(){

        if(aAnswerText.text == canswer){

            aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            aAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(bAnswerText.text == canswer){

            bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            bAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(cAnswerText.text == canswer){

            cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            cAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(dAnswerText.text == canswer){

            dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            dAnswerFriendImage.visibility = View.VISIBLE

            return

        }

    }

    private fun setUserImage(v: ImageView){

        glide.setImage(user.fb, user.image,this.context!!, v) {

        }

    }

    private fun setFriendImage(v: ImageView){

        glide.setImage(friends.fb, friends.image,this.context!!, v) {

        }

    }

}
