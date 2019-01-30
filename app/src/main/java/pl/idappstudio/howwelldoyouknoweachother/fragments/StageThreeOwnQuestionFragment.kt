package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_stage_three_own_question.*

import pl.idappstudio.howwelldoyouknoweachother.R

class StageThreeOwnQuestionFragment : Fragment(), TextWatcher {

    override fun afterTextChanged(s: Editable?) { }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { if(s?.length!! > 2) {checkEditText()} }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_stage_three_own_question, container, false)

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
        dAnswerCheck= rootView.findViewById(R.id.dTextAnswer2)

        questionText.addTextChangedListener(this)

        aAnswerText.addTextChangedListener(this)
        bAnswerText.addTextChangedListener(this)
        cAnswerText.addTextChangedListener(this)
        dAnswerText.addTextChangedListener(this)

        stageTitle.text = "Stwórz Pytania"

        lockEditText()

        return rootView
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

    companion object {
        fun newInstance(): StatesFragment = StatesFragment()
    }

}
