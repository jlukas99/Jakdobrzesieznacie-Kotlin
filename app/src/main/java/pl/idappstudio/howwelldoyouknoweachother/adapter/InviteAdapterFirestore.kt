@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.adapter

import android.graphics.drawable.Drawable
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.invite_item.view.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.fragments.InvitesFragment
import pl.idappstudio.howwelldoyouknoweachother.model.InviteItem
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.interfaces.CountInterface


class InviteAdapterFirestore(@NonNull options: FirestoreRecyclerOptions<InviteItem>, private val listener: CountInterface) : FirestoreRecyclerAdapter<InviteItem, InviteAdapterFirestore.InviteHolder>(options) {

    private var rv: RecyclerView? = null

    fun setRV(rv: RecyclerView) {
        this.rv = rv
    }

    override fun onDataChanged() {
        super.onDataChanged()

        if(rv != null) {

            listener.reload()

        }
    }

    override fun onBindViewHolder(@NonNull holder: InviteHolder, position: Int, @NonNull model: InviteItem) {

        holder.itemView.addLoading.visibility = View.INVISIBLE

        holder.itemView.btn_send.isEnabled = true
        holder.itemView.btn_delete.isEnabled = true

        holder.itemView.btn_send.visibility = View.VISIBLE
        holder.itemView.btn_delete.visibility = View.VISIBLE

        holder.itemView.invite_name.text = model.name.toString()

        holder.itemView.btn_send.setOnClickListener {

            holder.itemView.addLoading.visibility = View.VISIBLE

            holder.itemView.btn_send.isEnabled = false
            holder.itemView.btn_delete.isEnabled = false

            holder.itemView.btn_send.visibility = View.GONE
            holder.itemView.btn_delete.visibility = View.GONE

            FirestoreUtil.addFriend(getItem(position).uid.toString(), holder) { b, holder ->

                if (b) {

                    if(holder.itemView.rootView != null) {

                        val snackbar: Snackbar? = Snackbar.make(holder.itemView.rootView, "Zaakceptowano zaproszenie od ${model.name}", 2500)
                        snackbar?.view?.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorAccent))
                        snackbar?.show()

                    }

                    snapshots.getSnapshot(position).reference.delete()

                } else {

                    if(holder.itemView.rootView != null) {

                        holder.itemView.addLoading.visibility = View.INVISIBLE

                        holder.itemView.btn_send.isEnabled = true
                        holder.itemView.btn_delete.isEnabled = true

                        holder.itemView.btn_send.visibility = View.VISIBLE
                        holder.itemView.btn_delete.visibility = View.VISIBLE

                        val snackbar: Snackbar? =
                            Snackbar.make(holder.itemView.rootView, "Nie udało się dodać do znajomych!", 2500)
                        snackbar?.view?.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorYellow))
                        snackbar?.show()

                    }

                }
            }

        }

        holder.itemView.btn_delete.setOnClickListener {

            if(holder.itemView.rootView != null) {

                holder.itemView.addLoading.visibility = View.VISIBLE

                holder.itemView.btn_send.isEnabled = false
                holder.itemView.btn_delete.isEnabled = false

                holder.itemView.btn_send.visibility = View.GONE
                holder.itemView.btn_delete.visibility = View.GONE

                val snackbar: Snackbar? =
                    Snackbar.make(holder.itemView.rootView, "Usunięto zaproszenie od ${model.name}", 2500)
                snackbar?.view?.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorRed))
                snackbar?.show()

            }

            snapshots.getSnapshot(position).reference.delete().addOnFailureListener {

                if(holder.itemView.rootView != null) {

                    holder.itemView.addLoading.visibility = View.INVISIBLE

                    holder.itemView.btn_send.isEnabled = true
                    holder.itemView.btn_delete.isEnabled = true

                    holder.itemView.btn_send.visibility = View.VISIBLE
                    holder.itemView.btn_delete.visibility = View.VISIBLE

                    val snackbar2: Snackbar? =
                        Snackbar.make(holder.itemView.rootView, "Nie udało się usunąć zaproszenie", 2500)
                    snackbar2?.view?.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorRed))
                    snackbar2?.show()

                }

            }
        }

        if(model.image!!.contains("logo")){

            Glide.with(holder.itemView.context).load(R.mipmap.logo).listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    holder.itemView.profileLoading.visibility = View.GONE
                    return false
                }

            }).into(holder.itemView.invite_profile)

        } else {

            if (model.fb!!) {

                GlideApp.with(holder.itemView.context).load("http://graph.facebook.com/${model.image}/picture?type=large")
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

                    }).into(holder.itemView.invite_profile)

            } else {

                val storageReference =
                    FirebaseStorage.getInstance().reference.child("profile_image").child(model.image + "-image")
                        .downloadUrl
                storageReference.addOnSuccessListener { Uri ->

                    GlideApp.with(holder.itemView.context).load(Uri.toString()).listener(object : RequestListener<Drawable> {

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

                    }).into(holder.itemView.invite_profile)

                }.addOnFailureListener {

                    GlideApp.with(holder.itemView.context).load(R.mipmap.logo).listener(object : RequestListener<Drawable> {

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

                    }).into(holder.itemView.invite_profile)

                }
            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): InviteHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.invite_item, parent, false)
        return InviteHolder(v)
    }

    inner class InviteHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}