@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.adapter

import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.friends_item.view.*
import org.jetbrains.anko.startActivity
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.FriendsProfileActivity
import pl.idappstudio.howwelldoyouknoweachother.interfaces.CountInterface
import pl.idappstudio.howwelldoyouknoweachother.model.FriendsItem
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GlideUtil


class FriendsAdapterFirestore(@NonNull options: FirestoreRecyclerOptions<FriendsItem>, private val listener: CountInterface) : FirestoreRecyclerAdapter<FriendsItem, FriendsAdapterFirestore.InviteHolder>(options) {

    private var rv: RecyclerView? = null
    private val db = FirebaseFirestore.getInstance().collection("users")
    private val glide = GlideUtil()

    fun setRV(rv: RecyclerView) {
        this.rv = rv
    }

    override fun onDataChanged() {
        super.onDataChanged()

        if(rv != null) {

            listener.reload()

        }
    }

    override fun onBindViewHolder(@NonNull holder: InviteHolder, position: Int, @NonNull model: FriendsItem) {

            db.document(model.uid.toString()).get().addOnCompleteListener {

                if (it.isSuccessful && it.result != null) {

                    val doc = it.result

                    if (doc != null) {

                        val image: String = doc.getString("image")!!
                        val fb: Boolean = doc.getBoolean("fb")!!
                        val gender: String = doc.getString("gender")!!
                        val name = doc.get("name").toString()
                        val type = doc.get("type").toString()
                        val uid = doc.get("uid").toString()

                        glide.setImage(fb, image, holder.itemView.context, holder.itemView.friends_profile) {

                            holder.itemView.profileLoading.visibility = View.GONE

                        }

                        holder.itemView.profile_name.text = name
                        holder.itemView.passa_count.text = model.days.toString()

                        holder.itemView.btn_game.setOnClickListener {

                            it.context.startActivity<FriendsProfileActivity>("id" to uid)

                        }

                        if (model.favorite!!) {

                            holder.itemView.btn_favorite.setImageResource(R.drawable.ic_heart_solid)

                            holder.itemView.btn_favorite.setOnClickListener {

                                FirestoreUtil.setFavorite(uid, false)

                            }

                        } else {

                            holder.itemView.btn_favorite.setImageResource(R.drawable.ic_heart_over)

                            holder.itemView.btn_favorite.setOnClickListener {

                                FirestoreUtil.setFavorite(uid, true)

                            }

                        }

                    }

                }
            }
    }


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): InviteHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.friends_item, parent, false)
        return InviteHolder(v)
    }

    inner class InviteHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}