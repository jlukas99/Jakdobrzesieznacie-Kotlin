@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.adapter

import android.graphics.drawable.Drawable
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.friends_item.view.*
import kotlinx.android.synthetic.main.set_item.view.*
import org.jetbrains.anko.startActivity
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.FriendsProfileActivity
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.interfaces.CountInterface
import pl.idappstudio.howwelldoyouknoweachother.model.FriendsItem
import pl.idappstudio.howwelldoyouknoweachother.model.SetItem
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil


class SetAdapterFirestore(@NonNull options: FirestoreRecyclerOptions<SetItem>, private val listener: CountInterface) : FirestoreRecyclerAdapter<SetItem, SetAdapterFirestore.InviteHolder>(options) {

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

    override fun onBindViewHolder(@NonNull holder: InviteHolder, position: Int, @NonNull model: SetItem) {

        holder.itemView.set_btn.text = model.name

        if(model.image == 700034) {

            holder.itemView.set_btn.setIconResource(R.drawable.ic_question_icon)

        } else if(model.premium!!) {

            holder.itemView.set_btn.setIconResource(R.drawable.ic_corn)
            holder.itemView.set_btn.background.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context, R.color.colorYellow
                ), android.graphics.PorterDuff.Mode.SRC_IN)

        } else {

            holder.itemView.set_btn.setIconResource(R.drawable.ic_pack_icon)

        }

        holder.itemView.set_btn.setOnClickListener {

            listener.click(model.id!!, model.premium!!, model.name!!, model.image!!)

        }

    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): InviteHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.set_item, parent, false)
        return InviteHolder(v)
    }

    inner class InviteHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}