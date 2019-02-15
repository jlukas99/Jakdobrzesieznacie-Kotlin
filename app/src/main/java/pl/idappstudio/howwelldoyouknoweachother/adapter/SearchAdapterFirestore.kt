@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother.adapter

import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.search_item.view.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.fragments.InvitesFragment
import pl.idappstudio.howwelldoyouknoweachother.interfaces.CountInterface
import pl.idappstudio.howwelldoyouknoweachother.model.InviteItem
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GlideUtil

class SearchAdapterFirestore(@NonNull options: FirestoreRecyclerOptions<InviteItem>, private val count: CountInterface, friendsList: ArrayList<String>) : FirestoreRecyclerAdapter<InviteItem, SearchAdapterFirestore.InviteHolder>(options) {

    private var rv: RecyclerView? = null
    private val glide = GlideUtil()
    private val list = friendsList

    fun setRV(rv: RecyclerView) {
        this.rv = rv
    }

    override fun onDataChanged() {
        super.onDataChanged()

        if(rv != null) {

            count.count()

        }
    }

    override fun onBindViewHolder(@NonNull holder: InviteHolder, position: Int, @NonNull model: InviteItem) {

        if(list.contains(model.uid)){

            holder.itemView.btn_send.isEnabled = false

            holder.itemView.btn_send.setColorFilter(getColor(holder.itemView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        }

        holder.itemView.invite_name.text = model.name.toString()

        holder.itemView.btn_send.setOnClickListener {

            holder.itemView.addLoading.visibility = View.VISIBLE

            holder.itemView.btn_send.isEnabled = false

            holder.itemView.btn_send.visibility = View.GONE

            FirestoreUtil.sendInvite(getItem(position).uid.toString(), holder){ b, holder ->

                holder.itemView.addLoading.visibility = View.INVISIBLE
                holder.itemView.btn_send.visibility = View.VISIBLE

                if(b){

                    holder.itemView.btn_send.setColorFilter(getColor(holder.itemView.context, R.color.colorLigth
                    ), android.graphics.PorterDuff.Mode.SRC_IN)

                    if(holder.itemView.rootView != null) {

                        val snackbar: Snackbar? = Snackbar.make(holder.itemView.rootView, "Wysłano zaproszenia do ${model.name}", 2500)
                        snackbar?.view?.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorAccent))
                        snackbar?.show()

                    }

                } else {

                    if(holder.itemView.rootView != null) {

                        holder.itemView.btn_send.isEnabled = true

                        val snackbar2: Snackbar? = Snackbar.make(holder.itemView.rootView, "Nie udało się wysłać zaproszenia!", 2500)
                        snackbar2?.view?.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorRed))
                        snackbar2?.show()

                    }

                }
            }

        }

        glide.setImage(model.fb!!, model.image!!, holder.itemView.context, holder.itemView.invite_profile) {

            holder.itemView.profileLoading.visibility = View.GONE

        }
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): InviteHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return InviteHolder(v)
    }

    inner class InviteHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}