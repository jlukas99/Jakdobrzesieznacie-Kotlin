package pl.idappstudio.howwelldoyouknoweachother.interfaces

import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import pl.idappstudio.howwelldoyouknoweachother.model.UserData

interface ClickListener {

    fun onClickFriendGame(user: UserData, image: CircleImageView, name: TextView, btnChat: ImageButton, btnFavorite: ImageButton, btnGame: ImageButton, bgGame: ImageView)
    fun onClickFriend(user: UserData, image: CircleImageView, name: TextView, btnChat: ImageButton, btnFavorite: ImageButton)

}