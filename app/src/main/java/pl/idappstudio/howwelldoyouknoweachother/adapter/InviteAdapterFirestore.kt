package pl.idappstudio.howwelldoyouknoweachother.adapter

import android.graphics.drawable.Drawable
import android.support.annotation.NonNull
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
import pl.idappstudio.howwelldoyouknoweachother.model.InviteItem
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil


class InviteAdapterFirestore(@NonNull options: FirestoreRecyclerOptions<InviteItem>) : FirestoreRecyclerAdapter<InviteItem, InviteAdapterFirestore.InviteHolder>(options) {

    override fun onBindViewHolder(@NonNull holder: InviteHolder, position: Int, @NonNull model: InviteItem) {

        holder.itemView.invite_name.text = model.name.toString()

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

    fun deleteItem(position: Int){
        snapshots.getSnapshot(position).reference.delete()
    }

    fun addFriend(position: Int){
        FirestoreUtil.addFriend(getItem(position).uid.toString()){
            snapshots.getSnapshot(position).reference.delete()
        }
    }

    inner class InviteHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}