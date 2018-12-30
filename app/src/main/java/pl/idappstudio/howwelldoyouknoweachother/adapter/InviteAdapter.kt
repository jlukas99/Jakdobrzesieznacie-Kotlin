@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.adapter

import android.graphics.drawable.Drawable
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.invite_item.view.*
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import pl.idappstudio.howwelldoyouknoweachother.model.InviteItem
import pl.idappstudio.howwelldoyouknoweachother.R


class InviteAdapter (private val partItemList: List<InviteItem>, private val listener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PartViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.invite_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PartViewHolder).bind(partItemList[position], position, listener)
    }

    override fun getItemCount() = partItemList.size

    class PartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val db = FirebaseFirestore.getInstance().collection("users")
        private val auth = FirebaseAuth.getInstance().currentUser

        fun bind(part: InviteItem, pos: Int, listener: (Int) -> Unit) = with(itemView) {

            itemView.profileLoading.visibility = View.VISIBLE
            itemView.invite_name.text = part.name

            if(part.image!!.contains("logo")){

                Glide.with(itemView.context).load(R.mipmap.logo).listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        itemView.profileLoading.visibility = View.GONE
                        return false
                    }

                }).into(itemView.invite_profile)

            } else {

                if (part.fb!!) {

                    Glide.with(itemView.context).load("http://graph.facebook.com/${part.image}/picture?type=large")
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
                                itemView.profileLoading.visibility = View.GONE
                                return false
                            }

                        }).into(itemView.invite_profile)

                } else {

                    val storageReference =
                        FirebaseStorage.getInstance().reference.child("profile_image").child(part.image + "-image")
                            .downloadUrl
                    storageReference.addOnSuccessListener { Uri ->

                        Glide.with(itemView.context).load(Uri.toString()).listener(object : RequestListener<Drawable> {

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
                                itemView.profileLoading.visibility = View.GONE
                                return false
                            }

                        }).into(itemView.invite_profile)

                    }.addOnFailureListener {

                        Glide.with(itemView.context).load(R.mipmap.logo).listener(object : RequestListener<Drawable> {

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
                                itemView.profileLoading.visibility = View.GONE
                                return false
                            }

                        }).into(itemView.invite_profile)

                    }
                }
            }

            itemView.btn_send.setOnClickListener {

                itemView.addLoading.visibility = View.VISIBLE

                itemView.btn_send.isEnabled = false
                itemView.btn_delete.isEnabled = false

                itemView.btn_send.visibility = View.GONE
                itemView.btn_delete.visibility = View.GONE

                db.document(auth?.uid!!).get().addOnSuccessListener { doc ->

                    val friend = HashMap<String, Any?>()
                    friend["name"] = part.name
                    friend["image"] = part.image
                    friend["uid"] = part.uid
                    friend["fb"] = part.fb

                    db.document(auth.uid).collection("friends").document(part.uid!!).set(friend).addOnSuccessListener {

                        val user = HashMap<String, Any?>()
                        user["name"] = doc.getString("name")
                        user["image"] = doc.getString("image")
                        user["uid"] = doc.getString("uid")
                        user["fb"] = doc.getBoolean("fb")

                        db.document(part.uid!!).collection("friends").document(doc.getString("uid").toString()).set(user).addOnSuccessListener {

                            val snackbar: Snackbar? = Snackbar.make(itemView.btn_delete, "Dodano do znajomych ${part.name}", 2500)
                            snackbar?.view?.setBackgroundColor(itemView.resources.getColor(R.color.colorAccent))
                            snackbar?.show()

                            db.document(auth.uid).collection("invites").document(part.uid!!).delete()

                            listener(pos)

                        }.addOnFailureListener {

                            val snackbar: Snackbar? = Snackbar.make(itemView.btn_delete, "Nie udało się dodać znajomego!", 2500)
                            snackbar?.view?.setBackgroundColor(itemView.resources.getColor(R.color.colorRed))
                            snackbar?.show()

                            itemView.addLoading.visibility = View.INVISIBLE
                            itemView.btn_send.isEnabled = true
                            itemView.btn_delete.isEnabled = true

                            itemView.btn_send.visibility = View.VISIBLE
                            itemView.btn_delete.visibility = View.VISIBLE

                        }
                    }

                }

            }

            itemView.btn_delete.setOnClickListener {

                itemView.addLoading.visibility = View.VISIBLE

                itemView.btn_send.isEnabled = false
                itemView.btn_delete.isEnabled = false

                itemView.btn_send.visibility = View.GONE
                itemView.btn_delete.visibility = View.GONE

                db.document(auth?.uid!!).collection("invites").document(part.uid!!).delete().addOnSuccessListener {

                    val snackbar: Snackbar? = Snackbar.make(itemView.btn_delete, "Usunięto zaproszenie od ${part.name}", 2500)
                    snackbar?.view?.setBackgroundColor(itemView.resources.getColor(R.color.colorYellow))
                    snackbar?.show()

                    listener(pos)

                }.addOnFailureListener {

                    val snackbar: Snackbar? = Snackbar.make(itemView.btn_delete, "Nie udało się usunąć znajomego!", 2500)
                    snackbar?.view?.setBackgroundColor(itemView.resources.getColor(R.color.colorRed))
                    snackbar?.show()

                    itemView.addLoading.visibility = View.INVISIBLE
                    itemView.btn_send.isEnabled = true
                    itemView.btn_delete.isEnabled = true

                    itemView.btn_send.visibility = View.VISIBLE
                    itemView.btn_delete.visibility = View.VISIBLE

                }

            }
        }
    }
}