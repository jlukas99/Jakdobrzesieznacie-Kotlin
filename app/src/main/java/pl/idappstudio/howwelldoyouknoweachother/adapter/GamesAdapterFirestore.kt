@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.adapter

import android.graphics.drawable.Drawable
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import org.jetbrains.anko.startActivity
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.game_item.view.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.FriendsProfileActivity
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.interfaces.CountInterface
import pl.idappstudio.howwelldoyouknoweachother.model.FriendsItem

class GamesAdapterFirestore(@NonNull options: FirestoreRecyclerOptions<FriendsItem>, private val listener: CountInterface) : FirestoreRecyclerAdapter<FriendsItem, GamesAdapterFirestore.InviteHolder>(options) {

    private var rv: RecyclerView? = null
    private val db = FirebaseFirestore.getInstance().collection("users")
    private val dbGames = FirebaseFirestore.getInstance().collection("games")

    fun setRV(rv: RecyclerView) {
        this.rv = rv
    }

    override fun onDataChanged() {
        super.onDataChanged()

        if(rv != null) {

            listener.count()

        }
    }

    fun check(uid: String, onComplete: (Boolean) -> Unit){

        Log.d("TAG", "DEBUG 2")

        dbGames.whereEqualTo(FirebaseAuth.getInstance().currentUser?.uid + "-turn", true)
            .whereEqualTo(uid + "-turn", false).addSnapshotListener(EventListener<QuerySnapshot> { it, e ->

                if(e != null){
                    return@EventListener
                }

                if(it != null){

                    if(it.size() > 0){

                        onComplete(true)

                    } else {

                        onComplete(false)

                    }

                } else {

                    onComplete(false)

                }

            })
    }


    override fun onBindViewHolder(@NonNull holder: InviteHolder, position: Int, @NonNull model: FriendsItem) {

        Log.d("TAG", "DEBUG 1")

        check(model.uid.toString()) {

            Log.d("TAG", "DEBUG 3")

            if(it) {

                Log.d("TAG", "DEBUG 4")

                db.document(model.uid!!).get().addOnCompleteListener {

                    if (it.isSuccessful && it.result != null) {

                        val doc = it.result

                        if (doc != null) {

                            val image: String = doc.getString("image")!!
                            val fb: Boolean = doc.getBoolean("fb")!!
                            val gender: String = doc.getString("gender")!!
                            val name = doc.get("name").toString()
                            val type = doc.get("type").toString()
                            val uid = doc.get("uid").toString()

                            holder.itemView.profile_name.text = name
                            holder.itemView.passa_count.text = "Znajomy czeka aż skończysz ture"

                            holder.itemView.btn_game.setOnClickListener {

                                it.context.startActivity<FriendsProfileActivity>("id" to uid)

                            }

                            holder.itemView.btn_favorite.visibility = View.GONE
                            holder.itemView.btn_chat.visibility = View.GONE

                            if (image.contains("logo")) {

                                Glide.with(holder.itemView.context).load(R.mipmap.logo).listener(object :
                                    RequestListener<Drawable> {

                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        return true
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        holder.itemView.profileLoading.visibility = View.GONE
                                        return false
                                    }

                                }).into(holder.itemView.friends_profile)

                            } else {

                                if (fb) {

                                    GlideApp.with(holder.itemView.context)
                                        .load("http://graph.facebook.com/${image}/picture?type=large")
                                        .diskCacheStrategy(
                                            DiskCacheStrategy.AUTOMATIC
                                        )
                                        .listener(object : RequestListener<Drawable> {

                                            override fun onLoadFailed(
                                                e: GlideException?,
                                                model: Any?,
                                                target: Target<Drawable>?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                return true
                                            }

                                            override fun onResourceReady(
                                                resource: Drawable?,
                                                model: Any?,
                                                target: Target<Drawable>?,
                                                dataSource: DataSource?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                holder.itemView.profileLoading.visibility = View.GONE
                                                return false
                                            }

                                        }).into(holder.itemView.friends_profile)

                                } else {

                                    val storageReference =
                                        FirebaseStorage.getInstance().reference.child("profile_image")
                                            .child(image + "-image")
                                            .downloadUrl
                                    storageReference.addOnSuccessListener { Uri ->

                                        GlideApp.with(holder.itemView.context).load(Uri.toString())
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .listener(object : RequestListener<Drawable> {

                                                override fun onLoadFailed(
                                                    e: GlideException?,
                                                    model: Any?,
                                                    target: Target<Drawable>?,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    return true
                                                }

                                                override fun onResourceReady(
                                                    resource: Drawable?,
                                                    model: Any?,
                                                    target: Target<Drawable>?,
                                                    dataSource: DataSource?,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    holder.itemView.profileLoading.visibility = View.GONE
                                                    return false
                                                }

                                            }).into(holder.itemView.friends_profile)

                                    }.addOnFailureListener {

                                        GlideApp.with(holder.itemView.context).load(R.mipmap.logo)
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .listener(object : RequestListener<Drawable> {

                                                override fun onLoadFailed(
                                                    e: GlideException?,
                                                    model: Any?,
                                                    target: Target<Drawable>?,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    return true
                                                }

                                                override fun onResourceReady(
                                                    resource: Drawable?,
                                                    model: Any?,
                                                    target: Target<Drawable>?,
                                                    dataSource: DataSource?,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    holder.itemView.profileLoading.visibility = View.GONE
                                                    return false
                                                }

                                            }).into(holder.itemView.friends_profile)

                                    }
                                }
                            }

                        }

                    }

                }
            } else {

                holder.itemView.friendLayout.visibility = View.GONE

            }

        }
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): InviteHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return InviteHolder(v)
    }

    inner class InviteHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}