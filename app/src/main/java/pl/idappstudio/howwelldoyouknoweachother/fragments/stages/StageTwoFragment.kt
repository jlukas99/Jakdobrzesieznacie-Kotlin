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

import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.answerList
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.friends
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.game
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.questionList
import pl.idappstudio.howwelldoyouknoweachother.interfaces.nextFragment
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GlideUtil
import pl.idappstudio.howwelldoyouknoweachother.util.UserUtil

class StageTwoFragment(private val listener: nextFragment) : androidx.fragment.app.Fragment() {

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

    private val glide = GlideUtil

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

            FirestoreUtil.updateGameSettings(true, false, 3, 1, game.uSet.id, game.fSet.id, game.gamemode, game.gameID, UserUtil.user.uid, friends.uid)

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
            canswer = questionList.question.a
            banswer = questionList.question.b
            banswer2 = questionList.question.c
            banswer3 = questionList.question.d

            answer = answerList.answer1

        } else if(questionNumber == 2){

            question = questionList.question1.question
            canswer = questionList.question1.a
            banswer = questionList.question1.b
            banswer2 = questionList.question1.c
            banswer3 = questionList.question1.d

            answer = answerList.answer2

        } else if(questionNumber == 3){

            question = questionList.question2.question
            canswer = questionList.question2.a
            banswer = questionList.question2.b
            banswer2 = questionList.question2.c
            banswer3 = questionList.question2.d

            answer = answerList.answer3

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

        nextQuestion.isEnabled = true

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

            lockButton()

            questionNumber++

            if (questionNumber != 4) {

                setText()

            } else {

                FirestoreUtil.updateGameSettings(true, false, 3, 1, game.uSet.id, game.fSet.id, game.gamemode, game.gameID, UserUtil.user.uid, friends.uid)

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

        if(answer == canswer){

            if(aAnswerText.text.toString() == canswer){

                aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                aAnswerFriendImage.visibility = View.VISIBLE

            }

            if(bAnswerText.text.toString() == canswer){

                bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                bAnswerFriendImage.visibility = View.VISIBLE

            }

            if(cAnswerText.text.toString() == canswer){

                cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                cAnswerFriendImage.visibility = View.VISIBLE

            }

            if(dAnswerText.text.toString() == canswer){

                dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                dAnswerFriendImage.visibility = View.VISIBLE

            }

        } else {

            if(aAnswerText.text.toString() == answer){

                aAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                aAnswerFriendImage.visibility = View.VISIBLE

            }

            if(bAnswerText.text.toString() == answer){

                bAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                bAnswerFriendImage.visibility = View.VISIBLE

            }

            if(cAnswerText.text.toString() == answer){

                cAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                cAnswerFriendImage.visibility = View.VISIBLE

            }

            if(dAnswerText.text.toString() == answer){

                dAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                dAnswerFriendImage.visibility = View.VISIBLE

            }

        }

        checkCorrectAnswer()

    }

    private fun checkCorrectAnswer(){

        if(aAnswerText.text == canswer){

            aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            aAnswerUserImage.visibility = View.VISIBLE
            nextQuestion()
            return

        }

        if(bAnswerText.text == canswer){

            bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            bAnswerUserImage.visibility = View.VISIBLE
            nextQuestion()
            return

        }

        if(cAnswerText.text == canswer){

            cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            cAnswerUserImage.visibility = View.VISIBLE
            nextQuestion()
            return

        }

        if(dAnswerText.text == canswer){

            dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            dAnswerUserImage.visibility = View.VISIBLE
            nextQuestion()
            return

        }

    }

}
