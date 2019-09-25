package pl.idappstudio.jakdobrzesieznacie.adapter

import android.content.Context
import android.view.View
import androidx.core.view.ViewCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.friends_item.*
import kotlinx.android.synthetic.main.game_item.btn_chat
import kotlinx.android.synthetic.main.game_item.btn_favorite
import kotlinx.android.synthetic.main.game_item.friends_profile
import kotlinx.android.synthetic.main.game_item.profile_name
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_BTN_CHAT_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_NAME_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickListener
import pl.idappstudio.jakdobrzesieznacie.model.FriendItem
import pl.idappstudio.jakdobrzesieznacie.model.UserData
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil

class GamesAdapater(private val user: FriendItem, private val context: Context, private val listener: ClickListener) : Item() {

    private val db = FirebaseFirestore.getInstance().collection("users")

    override fun bind(holder: ViewHolder, position: Int) {

        ViewCompat.setTransitionName(holder.friends_profile,EXTRA_USER_IMAGE_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.profile_name, EXTRA_USER_NAME_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.btn_chat, EXTRA_USER_BTN_CHAT_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.btn_favorite, EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME)

        holder.btn_chat.setOnClickListener {
            SnackBarUtil.setActivitySnack(
                    context.resources.getString(R.string.chat_in_build),
                    ColorSnackBar.WARING,
                    R.drawable.ic_warning,
                    it
            ) { }

        }

        holder.btn_chat.visibility = View.GONE
        holder.btn_favorite.visibility = View.GONE

        db.document(user.uid).addSnapshotListener(EventListener<DocumentSnapshot> { doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc != null){

                if(doc.exists()){

                    holder.profile_name.text = doc.getString("name")
                    holder.statusText.text = doc.getString("status")

                    GlideUtil.setActivityImage(
                        doc.getBoolean("fb")!!,
                        doc.getString("image").toString(),
                        context,
                        holder.friends_profile
                    ) { }

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