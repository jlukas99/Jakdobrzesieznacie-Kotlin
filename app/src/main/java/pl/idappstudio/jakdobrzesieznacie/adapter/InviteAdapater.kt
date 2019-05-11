package pl.idappstudio.jakdobrzesieznacie.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.game_item.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickInviteListener
import pl.idappstudio.jakdobrzesieznacie.model.FriendItem
import pl.idappstudio.jakdobrzesieznacie.model.UserData
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil

class InviteAdapater(private val user: FriendItem, private val context: Context, private val listener: ClickInviteListener) : Item() {

    private val db = FirebaseFirestore.getInstance().collection("users")

    override fun bind(holder: ViewHolder, position: Int) {

        holder.imageView2.setImageResource(R.drawable.input_overlay)
        holder.friends_profile.borderColor = ContextCompat.getColor(context, R.color.colorCorrectAnswer)
        holder.image_passa.setImageResource(R.drawable.ic_add_friends_icon)
        holder.imageView5.setImageResource(R.drawable.input_overlay_icon)

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

                            listener.onClickInvite(
                                user
                            )

                            holder.friendLayout.isEnabled = true

                        } else {

                            holder.image_passa.isEnabled = true

                        }

                    }

                    holder.friendLayout.setOnClickListener {

                        holder.friendLayout.isEnabled = false

                        if (user != null) {

                            listener.onClickInvite(
                                user
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