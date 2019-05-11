package pl.idappstudio.jakdobrzesieznacie.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.game_item.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_BTN_CHAT_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_GAME_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_NAME_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_STATUS_GAME_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickListener
import pl.idappstudio.jakdobrzesieznacie.model.FriendItem
import pl.idappstudio.jakdobrzesieznacie.model.UserData
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil

class GamesAdapater(private val user: FriendItem, private val context: Context, private val listener: ClickListener) : Item() {

    private val db = FirebaseFirestore.getInstance().collection("users")

    override fun bind(holder: ViewHolder, position: Int) {

        ViewCompat.setTransitionName(holder.image_passa, EXTRA_USER_IMAGE_GAME_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.imageView5, EXTRA_USER_STATUS_GAME_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.friends_profile,EXTRA_USER_IMAGE_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.profile_name, EXTRA_USER_NAME_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.btn_chat, EXTRA_USER_BTN_CHAT_TRANSITION_NAME)
        ViewCompat.setTransitionName(holder.btn_favorite, EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME)

        if(user.favorite){

            holder.imageView2.setImageResource(R.drawable.input_overlay)
            holder.friends_profile.borderColor = ContextCompat.getColor(context, R.color.colorCorrectAnswer)
            holder.imageView5.setImageResource(R.drawable.input_overlay_icon)

        } else {

            holder.imageView2.setImageResource(R.drawable.input_overlay_error)
            holder.friends_profile.borderColor = ContextCompat.getColor(context, R.color.colorRed)
            holder.imageView5.setImageResource(R.drawable.input_overlay_icon_error)

        }

        db.document(user.uid).addSnapshotListener(EventListener<DocumentSnapshot> { doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc != null){

                if(doc.exists()){

                    holder.profile_name.text = doc.getString("name")

                    GlideUtil.setImage(doc.getBoolean("fb")!!, doc.getString("image").toString(), context, holder.friends_profile) { }

                    val user = doc.toObject(UserData::class.java)

                    holder.image_passa.setOnClickListener {

                        holder.image_passa.isEnabled = false

                        if (user != null) {

                            listener.onClickFriendGame(
                                user,
                                holder.friends_profile,
                                holder.profile_name,
                                holder.btn_chat,
                                holder.btn_favorite,
                                holder.image_passa,
                                holder.imageView5

                            )

                            holder.image_passa.isEnabled = true

                        } else {

                            holder.image_passa.isEnabled = true

                        }

                    }

                    holder.friendLayout.setOnClickListener {

                        holder.friendLayout.isEnabled = false

                        if (user != null) {

                            listener.onClickFriendGame(
                                user,
                                holder.friends_profile,
                                holder.profile_name,
                                holder.btn_chat,
                                holder.btn_favorite,
                                holder.image_passa,
                                holder.imageView5
                            )

                            holder.friendLayout.isEnabled = true

                        } else {

                            holder.friendLayout.isEnabled = true

                        }

                    }

                }

            }

        })

    }

    override fun getLayout(): Int = R.layout.game_item

}