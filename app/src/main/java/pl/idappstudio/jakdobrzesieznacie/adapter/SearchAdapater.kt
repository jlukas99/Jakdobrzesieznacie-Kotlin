package pl.idappstudio.jakdobrzesieznacie.adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.search_item.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickInviteListener
import pl.idappstudio.jakdobrzesieznacie.model.FriendItem
import pl.idappstudio.jakdobrzesieznacie.model.UserData
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

class SearchAdapater(private val user: FriendItem, private val context: Context, private val listener: ClickInviteListener) : Item() {

    private val db = FirebaseFirestore.getInstance().collection("users")

    override fun bind(holder: ViewHolder, position: Int) {

        var user2: UserData? = null

        holder.addLoading.visibility = View.GONE

        if(user.favorite){

            holder.btn_send.setImageResource(R.drawable.ic_add_friends_icon)
            holder.btn_send.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context, R.color.colorAccent
                ), android.graphics.PorterDuff.Mode.SRC_IN)

            holder.btn_send.isEnabled = true

        } else {

            holder.btn_send.setImageResource(R.drawable.ic_forward)
            holder.btn_send.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context, R.color.colorPrimary
                ), android.graphics.PorterDuff.Mode.SRC_IN)

            holder.btn_send.isEnabled = false

        }

        holder.btn_send.setOnClickListener {

            if(user2 != null) {

                if(user2?.uid != "") {

                    holder.btn_send.isEnabled = false

                    holder.btn_send.visibility = View.GONE
                    holder.addLoading.visibility = View.VISIBLE

                    UserUtil.sendInvite(user.uid) {

                        if (it) {

                            listener.onClickSendInvite(user2!!, true)

                            holder.addLoading.visibility = View.GONE
                            holder.btn_send.setImageResource(R.drawable.ic_forward)
                            holder.btn_send.setColorFilter(
                                ContextCompat.getColor(
                                    holder.itemView.context, R.color.colorPrimary
                                ), android.graphics.PorterDuff.Mode.SRC_IN
                            )

                            holder.btn_send.visibility = View.VISIBLE

                        } else {

                            listener.onClickSendInvite(user2!!, false)

                            holder.addLoading.visibility = View.GONE
                            holder.btn_send.isEnabled = true
                            holder.btn_send.visibility = View.VISIBLE

                        }

                    }

                }

            }

        }

        db.document(user.uid).addSnapshotListener(EventListener<DocumentSnapshot> { doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc != null){

                if(doc.exists()){

                    holder.invite_name.text = doc.getString("name")

                    GlideUtil.setImage(doc.getBoolean("fb")!!, doc.getString("image").toString(), context, holder.invite_profile) { }

                    user2 = doc.toObject(UserData::class.java)

                }

            }

        })

    }

    override fun getLayout(): Int = R.layout.search_item

}