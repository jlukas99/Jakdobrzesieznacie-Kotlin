package pl.idappstudio.jakdobrzesieznacie.adapter

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.pack_item.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.CreateSetActivity
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickSetListener
import pl.idappstudio.jakdobrzesieznacie.model.SetItem

class PackAdapater(private val id: String, private val context: Context, private val listener: ClickSetListener) : Item() {

    private val db = FirebaseFirestore.getInstance().collection("set").document(id)

    override fun bind(holder: ViewHolder, position: Int) {

//        ViewCompat.setTransitionName(holder.friends_profile,EXTRA_USER_IMAGE_TRANSITION_NAME)
//        ViewCompat.setTransitionName(holder.profile_name, EXTRA_USER_NAME_TRANSITION_NAME)
//        ViewCompat.setTransitionName(holder.btn_chat, EXTRA_USER_BTN_CHAT_TRANSITION_NAME)
//        ViewCompat.setTransitionName(holder.btn_favorite, EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME)

        db.addSnapshotListener(EventListener<DocumentSnapshot> { doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc != null){

                if(doc.exists()){

                    holder.pack_name.text = doc.getString("name")

                    val packItem = doc.toObject(SetItem::class.java)

                    holder.btn_edit_pack.setOnClickListener {

                        holder.btn_edit_pack.isEnabled = false

                        if (packItem != null) {

                            val intent = Intent(context, CreateSetActivity::class.java)
                            intent.putExtra("id", doc.id)

                            startActivity(context, intent, null)

                            holder.btn_edit_pack.isEnabled = true

                        } else {

                            holder.btn_edit_pack.isEnabled = true

                        }

                    }

                    holder.packConstraintLayout.setOnClickListener {

                        holder.packConstraintLayout.isEnabled = false

                        if (packItem != null) {

                            val intent = Intent(context, CreateSetActivity::class.java)
                            intent.putExtra("id", doc.id)

                            startActivity(context, intent, null)

                            holder.packConstraintLayout.isEnabled = true

                        } else {

                            holder.packConstraintLayout.isEnabled = true

                        }

                    }

                    holder.btn_delete_pack.setOnClickListener {

                        db.delete()

                    }

                }

            }

        })

    }

    override fun getLayout(): Int = R.layout.pack_item

}