package pl.idappstudio.howwelldoyouknoweachother.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import pl.idappstudio.howwelldoyouknoweachother.R
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.Priority
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp


class GlideUtil {

    fun setImage(b: Boolean, image: String, ctx: Context, target: ImageView, onComplete: () -> Unit) {

        val options = RequestOptions()
            .centerCrop()
            .error(R.mipmap.logo_colored)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)

        if(b){

            if(target.isAttachedToWindow) {

                GlideApp.with(ctx).load("http://graph.facebook.com/${image}/picture?type=large")
                    .apply(options)
                    .into(target)

                onComplete()

            }

        } else {

            if(target.isAttachedToWindow) {

                if (image == "logo") {

                    target.setImageResource(R.mipmap.logo_colored)

                    onComplete()
                    return
                }

                val storageReference =
                    FirebaseStorage.getInstance().reference.child("profile_image").child(image + "-image").downloadUrl

                storageReference.addOnSuccessListener { Uri ->

                    GlideApp.with(ctx).load(Uri.toString())
                        .apply(options)
                        .into(target)

                    onComplete()

                }.addOnFailureListener {

                    target.setImageResource(R.mipmap.logo_colored)

                    onComplete()

                }

            }

        }

    }

}