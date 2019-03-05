package pl.idappstudio.howwelldoyouknoweachother.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.set_item.*
import kotlinx.android.synthetic.main.set_item.view.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.interfaces.ClickSetListener
import pl.idappstudio.howwelldoyouknoweachother.model.SetItem

class SetAdapater(private val set: SetItem, private val context: Context, private val listener: ClickSetListener) : Item() {

    override fun bind(holder: ViewHolder, position: Int) {

        holder.set_btn.text = set.name

        if(set.premium) {

            holder.itemView.set_btn.setIconResource(R.drawable.ic_corn)
            holder.itemView.set_btn.background.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context, R.color.colorYellow
                ), android.graphics.PorterDuff.Mode.SRC_IN)

        } else {

            if(set.image == 700034){

                holder.itemView.set_btn.setIconResource(R.drawable.ic_question_icon)

            } else {

                holder.itemView.set_btn.setIconResource(R.drawable.ic_pack_icon)

            }

            holder.itemView.set_btn.background.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context, R.color.colorPrimary
                ), android.graphics.PorterDuff.Mode.SRC_IN)

        }

        holder.set_btn.setOnClickListener {
            listener.click(set)
        }

    }

    override fun getLayout(): Int = R.layout.set_item

}