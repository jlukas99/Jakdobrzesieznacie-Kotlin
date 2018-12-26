package pl.idappstudio.howwelldoyouknoweachother

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.invite_item.view.*
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import pl.idappstudio.howwelldoyouknoweachother.R.id.imageView
import android.graphics.BitmapFactory







class InviteAdapter (private val partItemList: List<InviteItem>, private val clickListener: (InviteItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.invite_item, parent, false)
        return PartViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PartViewHolder).bind(partItemList[position], clickListener)
    }

    override fun getItemCount() = partItemList.size

    class PartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("CheckResult")
        fun bind(part: InviteItem, clickListener: (InviteItem) -> Unit) {

            itemView.invite_name.text = part.name
            Glide.with(itemView.context).load("http://graph.facebook.com/" + part.image + "/picture?type=large").apply(RequestOptions().error(R.mipmap.logo)).into(itemView.invite_profile)
            itemView.setOnClickListener { clickListener(part)}
        }

        fun setImageSize(s: String): String{

            return s
        }
    }
}