package pl.idappstudio.jakdobrzesieznacie.adapter

import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.set_item.*
import kotlinx.android.synthetic.main.set_item.view.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickSetListener
import pl.idappstudio.jakdobrzesieznacie.model.SetItem

@Suppress("DEPRECATION")
class SetAdapater(
        private val set: SetItem,
        private val listener: ClickSetListener
) : Item() {

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
                holder.itemView.set_btn.background.setColorFilter(
                        ContextCompat.getColor(
                                holder.itemView.context, R.color.colorAccent
                        ), android.graphics.PorterDuff.Mode.SRC_IN)

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