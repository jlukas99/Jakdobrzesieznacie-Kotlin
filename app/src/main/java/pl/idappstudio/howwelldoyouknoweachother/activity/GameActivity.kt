package pl.idappstudio.howwelldoyouknoweachother.activity

import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_game.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.fragments.StageThreeOwnQuestionFragment
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.model.FriendInfoData
import pl.idappstudio.howwelldoyouknoweachother.model.GameData
import pl.idappstudio.howwelldoyouknoweachother.model.StatsData
import pl.idappstudio.howwelldoyouknoweachother.model.UserData
import pl.idappstudio.howwelldoyouknoweachother.util.GameUtil



class GameActivity : AppCompatActivity() {

    private lateinit var friends: UserData
    private lateinit var stats: StatsData

    private lateinit var information: FriendInfoData
    private lateinit var game: GameData

    private lateinit var user: UserData
    private lateinit var userStats: StatsData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        userLoadingImage.visibility = View.VISIBLE
        friendLoadingImage.visibility = View.VISIBLE

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment, StageThreeOwnQuestionFragment()).commit()

    }

    private fun setInterface(){

        setUserImage()
        setFriendImage()

        userNameText.text = user.name
        friendNameText.text = friends.name

        etapText.text = "${game.uStage} z 3"

    }

    private fun setUserImage(){

        if(user.image.contains("logo")){

            Glide.with(this).load(R.mipmap.logo).listener(object :
                RequestListener<Drawable> {

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    userLoadingImage.visibility = View.GONE
                    return false
                }

            }).into(userProfileImage)

        } else {

            if (user.fb) {

                GlideApp.with(applicationContext).load("http://graph.facebook.com/${user.image}/picture?type=large")
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
                            userLoadingImage.visibility = View.GONE
                            return false
                        }

                    }).into(userProfileImage)

            } else {

                val storageReference =
                    FirebaseStorage.getInstance().reference.child("profile_image").child(user.image + "-image")
                        .downloadUrl

                storageReference.addOnSuccessListener { Uri ->

                    GlideApp.with(applicationContext).load(Uri.toString()).listener(object :
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
                            userLoadingImage.visibility = View.GONE
                            return false
                        }

                    }).into(userProfileImage)

                }.addOnFailureListener {

                    GlideApp.with(applicationContext).load(R.mipmap.logo).listener(object :
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
                            userLoadingImage.visibility = View.GONE
                            return false
                        }

                    }).into(userProfileImage)
                }
            }
        }
    }

    private fun setFriendImage(){

        if(friends.image.contains("logo")){

            Glide.with(this).load(R.mipmap.logo).listener(object :
                RequestListener<Drawable> {

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    friendLoadingImage.visibility = View.GONE
                    return false
                }

            }).into(friendProfileImage)

        } else {

            if (friends.fb) {

                GlideApp.with(applicationContext).load("http://graph.facebook.com/${friends.image}/picture?type=large")
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
                            friendLoadingImage.visibility = View.GONE
                            return false
                        }

                    }).into(friendProfileImage)

            } else {

                val storageReference =
                    FirebaseStorage.getInstance().reference.child("profile_image").child(friends.image + "-image")
                        .downloadUrl

                storageReference.addOnSuccessListener { Uri ->

                    GlideApp.with(applicationContext).load(Uri.toString()).listener(object :
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
                            userLoadingImage.visibility = View.GONE
                            return false
                        }

                    }).into(friendProfileImage)

                }.addOnFailureListener {

                    GlideApp.with(applicationContext).load(R.mipmap.logo).listener(object :
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
                            friendLoadingImage.visibility = View.GONE
                            return false
                        }

                    }).into(friendProfileImage)
                }
            }
        }
    }

    private fun getFriendInformation(){
        GameUtil.getUserData(FirebaseAuth.getInstance().currentUser?.uid.toString(), intent?.extras?.get("id").toString()) { e ->

            friends = e.friendsData
            user = e.userData

            game = e.game
            information = e.finfo

            stats = e.fstats
            userStats = e.ustats

            setInterface()

        }
    }

    override fun onResume() {
        super.onResume()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        getFriendInformation()

    }

}
