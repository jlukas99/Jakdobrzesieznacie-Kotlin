package pl.idappstudio.howwelldoyouknoweachother.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import pl.idappstudio.howwelldoyouknoweachother.R
import com.bumptech.glide.request.RequestOptions
object GlideUtil {

    private val options = RequestOptions()
        .error(R.mipmap.logo_colored)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .priority(Priority.HIGH)

    fun setActivityImage(b: Boolean, image: String, ctx: Context, target: ImageView, onComplete: () -> Unit) {

            if(b){

                if(target.isAttachedToWindow) {

                    Glide.with(ctx).load("http://graph.facebook.com/${image}/picture?type=large")
                        .apply(options)
                        .into(target)

                    onComplete()

                }

            } else {

                if(target.isAttachedToWindow) {

                    if (image == "logo") {

                        target.setImageResource(R.mipmap.logo_colored)

                        onComplete()
                    } else {

                        val storageReference =
                            FirebaseStorage.getInstance().reference.child("profile_image").child(image + "-image")
                                .downloadUrl

                        storageReference.addOnSuccessListener { Uri ->

                            Glide.with(ctx).load(Uri.toString())
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

    fun setImage(b: Boolean, image: String, ctx: Context, target: ImageView, onComplete: () -> Unit) {

        if(b){

            Glide.with(ctx).load("http://graph.facebook.com/${image}/picture?type=large")
                .apply(options)
                .into(target)

            onComplete()

            } else {

            if (image == "logo") {

                target.setImageResource(R.mipmap.logo_colored)

                onComplete()

            } else {

                val storageReference = FirebaseStorage.getInstance().reference.child("profile_image").child(image + "-image").downloadUrl

                storageReference.addOnSuccessListener { Uri ->

                    Glide.with(ctx).load(Uri.toString())
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