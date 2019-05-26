package pl.idappstudio.jakdobrzesieznacie.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import pl.idappstudio.jakdobrzesieznacie.R

object GlideUtil {

    private val options = RequestOptions()
        .error(R.mipmap.placeholder_profile)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .priority(Priority.HIGH)

    private var placeholder: Drawable? = null

    fun setActivityImage(b: Boolean, image: String, ctx: Context, target: ImageView, onComplete: () -> Unit) {

        placeholder = ContextCompat.getDrawable(ctx, R.mipmap.placeholder_profile)

        if (b) {

            if (target.isAttachedToWindow) {

                Glide.with(ctx).asBitmap().load("http://graph.facebook.com/$image/picture?type=large")
                    .apply(options)
                    .placeholder(placeholder)
                    .into(target)

                onComplete()

            }

        } else {

            if (target.isAttachedToWindow) {

                when (image) {
                    "male" -> {

                        target.setImageResource(R.mipmap.male)

                        onComplete()

                    }
                    "female" -> {

                        target.setImageResource(R.mipmap.female)

                        onComplete()

                    }
                    else -> {

                        val storageReference =
                            FirebaseStorage.getInstance().reference.child("profile_image").child("$image-image")
                                .downloadUrl

                        storageReference.addOnSuccessListener { Uri ->

                            Glide.with(ctx).asBitmap().load(Uri.toString())
                                .apply(options)
                                .placeholder(placeholder)
                                .into(target)

                            onComplete()

                        }.addOnFailureListener {

                            target.setImageResource(R.mipmap.placeholder_profile)

                            onComplete()

                        }

                    }

                }

            }

        }
    }

    fun setImage(b: Boolean, image: String, ctx: Context, target: ImageView, onComplete: () -> Unit) {
            placeholder = ContextCompat.getDrawable(ctx, R.mipmap.placeholder_profile)

            if (b) {

                Glide.with(ctx).asBitmap().load("http://graph.facebook.com/$image/picture?type=large")
                    .apply(options)
                    .placeholder(placeholder)
                    .into(target)

                onComplete()

            } else {

                when (image) {
                    "male" -> {

                        target.setImageResource(R.mipmap.male)

                        onComplete()

                    }
                    "female" -> {

                        target.setImageResource(R.mipmap.female)

                        onComplete()

                    }
                    else -> {

                        val storageReference =
                            FirebaseStorage.getInstance().reference.child("profile_image").child("$image-image")
                                .downloadUrl

                        storageReference.addOnSuccessListener { Uri ->

                            Glide.with(ctx).asBitmap().load(Uri.toString())
                                .apply(options)
                                .placeholder(placeholder)
                                .into(target)

                            onComplete()

                        }.addOnFailureListener {

                            target.setImageResource(R.mipmap.placeholder_profile)

                            onComplete()

                        }

                    }
                }

            }
    }

}