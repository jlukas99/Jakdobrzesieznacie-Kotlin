package pl.idappstudio.howwelldoyouknoweachother.adapter

import android.content.Context
import androidx.core.view.ViewCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.friends_item.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_BTN_CHAT_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_NAME_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.interfaces.ClickListener
import pl.idappstudio.howwelldoyouknoweachother.model.FriendItem
import pl.idappstudio.howwelldoyouknoweachother.model.UserData
import pl.idappstudio.howwelldoyouknoweachother.util.GlideUtil
import pl.idappstudio.howwelldoyouknoweachother.util.UserUtil

class FriendsAdapater(private val user: FriendItem, private val context: Context, private val listener: ClickListener) : Item() {

    private val db = FirebaseFirestore.getInstance().collection("users")
    private val dbFriends = FirebaseFirestore.getInstance().collection("users").document(UserUtil.user.uid).collection("friends")

    override fun bind(holder: ViewHolder, position: Int) {

        ViewCompat.setTransitionName(holder.friends_profile,EXTRA_USER_IMAGE_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.profile_name, EXTRA_USER_NAME_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.btn_chat, EXTRA_USER_BTN_CHAT_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.btn_favorite, EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME)

        if(user.favorite){

            holder.btn_favorite.setImageResource(R.drawable.ic_heart_solid)

            holder.btn_favorite.setOnClickListener {

                dbFriends.document(user.uid).update("favorite", false)

            }

        } else {

            holder.btn_favorite.setImageResource(R.drawable.ic_heart_over)

            holder.btn_favorite.setOnClickListener {

                dbFriends.document(user.uid).update("favorite", true)

            }

        }

        db.document(user.uid).addSnapshotListener(EventListener<DocumentSnapshot> { doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc != null){

                if(doc.exists()){

                    holder.profile_name.text = doc.getString("name")
                    holder.statusText.text = doc.getString("status")

                    GlideUtil.setImage(doc.getBoolean("fb")!!, doc.getString("image").toString(), context, holder.friends_profile) { }

                    val user = doc.toObject(UserData::class.java)

                    holder.btn_game.setOnClickListener {

                        holder.btn_game.isEnabled = false

                        if (user != null) {

                            listener.onClickFriend(
                                user,
                                holder.friends_profile,
                                holder.profile_name,
                                holder.btn_chat,
                                holder.btn_favorite
                            )

                            holder.btn_game.isEnabled = true

                        } else {

                            holder.btn_game.isEnabled = true

                        }

                    }

                    holder.friendConstraintLayout.setOnClickListener {

                        holder.friendConstraintLayout.isEnabled = false

                        if (user != null) {

                            listener.onClickFriend(
                                user,
                                holder.friends_profile,
                                holder.profile_name,
                                holder.btn_chat,
                                holder.btn_favorite
                            )

                            holder.friendConstraintLayout.isEnabled = true

                        } else {

                            holder.friendConstraintLayout.isEnabled = true

                        }

                    }

                }

            }

        })

    }

    override fun getLayout(): Int = R.layout.friends_item

}