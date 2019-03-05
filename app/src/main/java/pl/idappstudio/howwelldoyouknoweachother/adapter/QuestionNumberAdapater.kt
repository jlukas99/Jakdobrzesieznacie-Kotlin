package pl.idappstudio.howwelldoyouknoweachother.adapter

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.question_number_item.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.model.UserQuestionData

class QuestionNumberAdapater(val data: UserQuestionData) : Item() {

    var pos: Int = -1

    override fun bind(holder: ViewHolder, position: Int) {

        pos = position

        holder.question_number.text = (position + 1).toString()

        holder.question_number_background.setImageResource(R.drawable.input_overlay_white)

    }

    override fun getLayout(): Int = R.layout.question_number_item

}