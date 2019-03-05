package pl.idappstudio.howwelldoyouknoweachother.items

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.header_title_item.*
import pl.idappstudio.howwelldoyouknoweachother.R


class HeaderItem(private val title: String) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.head_title_text.text = title

    }

    override fun getLayout() = R.layout.header_title_item

}