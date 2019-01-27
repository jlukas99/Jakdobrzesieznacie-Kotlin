package pl.idappstudio.howwelldoyouknoweachother.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_friends_profile.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.model.FriendsData
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil

class FriendsProfileActivity : AppCompatActivity() {

    private lateinit var friends: FriendsData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)

        friends_profile_loading_image.visibility = View.VISIBLE
    }

    fun setImage(){

        if(friends.image.contains("logo")){

            Glide.with(this).load(R.mipmap.logo).listener(object :
                RequestListener<Drawable> {

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    friends_profile_loading_image.visibility = View.GONE
                    return false
                }

            }).into(friends_profile_image)

        } else {

            if (friends.fb!!) {

                GlideApp.with(this).load("http://graph.facebook.com/${friends.image}/picture?type=large")
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
                            friends_profile_loading_image.visibility = View.GONE
                            return false
                        }

                    }).into(friends_profile_image)

            } else {

                val storageReference =
                    FirebaseStorage.getInstance().reference.child("profile_image").child(friends.image + "-image")
                        .downloadUrl
                storageReference.addOnSuccessListener { Uri ->

                    GlideApp.with(this).load(Uri.toString()).listener(object :
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
                            friends_profile_loading_image.visibility = View.GONE
                            return false
                        }

                    }).into(friends_profile_image)

                }.addOnFailureListener {

                    GlideApp.with(this).load(R.mipmap.logo).listener(object :
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
                            friends_profile_loading_image.visibility = View.GONE
                            return false
                        }

                    }).into(friends_profile_image)

                }
            }
        }
    }

    fun setInformation(){

        friends_profile_name.text = friends.name

        if(friends.favorite!!){

            friends_profile_favorite.setIconResource(R.drawable.ic_heart_solid)

            friends_profile_favorite.setOnClickListener {

                FirestoreUtil.setFavorite(friends.uid, false)
                friends_profile_favorite.setIconResource(R.drawable.ic_heart_over)

            }

        } else {

            friends_profile_favorite.setIconResource(R.drawable.ic_heart_over)

            friends_profile_favorite.setOnClickListener {

                FirestoreUtil.setFavorite(friends.uid, true)
                friends_profile_favorite.setIconResource(R.drawable.ic_heart_solid)

            }
        }

        val a: Int = friends.stats.get("canswer")!!
        val b: Int = friends.stats.get("banswer")!!
        val c: Int = friends.stats.get("playegames")!!

        val sum = if(a != 0) { a/(a + b)*100 } else { 0 }

        friends_profile_stats_canswer.text = a.toString()
        friends_profile_stats_banswer.text = b.toString()
        friends_profile_stats_games.text = c.toString()
        friends_profile_stats_precent.text = "$sum%"

        setImage()

    }

    override fun onResume() {
        super.onResume()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        FirestoreUtil.getFriendsUser(intent?.extras?.get("id").toString()) { e ->

            friends = e
            setInformation()

        }

    }
}
