package pl.idappstudio.howwelldoyouknoweachother.activity

import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_game.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.fragments.ClearFragment
import pl.idappstudio.howwelldoyouknoweachother.fragments.StageOneFragment
import pl.idappstudio.howwelldoyouknoweachother.fragments.StageThreeOwnQuestionFragment
import pl.idappstudio.howwelldoyouknoweachother.fragments.StageTwoFragment
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.interfaces.nextFragment
import pl.idappstudio.howwelldoyouknoweachother.model.*
import pl.idappstudio.howwelldoyouknoweachother.saveInstance.FragmentStateHelper
import pl.idappstudio.howwelldoyouknoweachother.util.GameUtil

class GameActivity : AppCompatActivity(), nextFragment {

    override fun next() {

        supportFragmentManager.beginTransaction().replace(R.id.fragment, ClearFragment()).commit()
        showLoading()

        getFriendInformation()

    }

    override fun updateNumber(i: Int, boolean: Boolean){

        if(i == 1){
            if(boolean){
                questionOne.setBackgroundResource(R.drawable.number_correct_overlay)
                return
            } else {
                questionOne.setBackgroundResource(R.drawable.number_bad_overlay)
                return
            }
        }

        if(i == 2){
            if(boolean){
                questionTwo.setBackgroundResource(R.drawable.number_correct_overlay)
                return
            } else {
                questionTwo.setBackgroundResource(R.drawable.number_bad_overlay)
                return
            }
        }

        if(i == 3){
            if(boolean){
                questionThree.setBackgroundResource(R.drawable.number_correct_overlay)
                return
            } else {
                questionThree.setBackgroundResource(R.drawable.number_bad_overlay)
                return
            }
        }

    }

    companion object {

        lateinit var friends: UserData
        lateinit var stats: StatsData

        lateinit var information: FriendInfoData
        lateinit var game: GameData

        lateinit var user: UserData
        lateinit var userStats: StatsData

        lateinit var questionList: QuestionData
        lateinit var answerList: AnswerData

        var canswer: Int = 0
        var banswer: Int = 0

        fun updateStats(onComplete: () -> Unit){

            GameUtil.updateStats(user.uid, friends.uid, GameActivity.userStats.canswer + canswer, GameActivity.userStats.banswer + banswer, GameActivity.userStats.games){

                onComplete()

            }

        }

    }

    lateinit var questionOne: TextView
    lateinit var questionTwo: TextView
    lateinit var questionThree: TextView

    lateinit var mContent: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        questionOne = findViewById(R.id.questionOneText)
        questionTwo = findViewById(R.id.questionTwoText)
        questionThree = findViewById(R.id.questionThreeText)

        userLoadingImage.visibility = View.VISIBLE
        friendLoadingImage.visibility = View.VISIBLE

        showLoading()

    }

    private fun showLoading(){

        gameIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorLigth), android.graphics.PorterDuff.Mode.SRC_IN)

        gameIcon.visibility = View.VISIBLE
        gameText.visibility = View.VISIBLE
        gameLoading.visibility = View.VISIBLE

    }

    private fun hideLoading(){

        gameIcon.visibility = View.GONE
        gameText.visibility = View.GONE
        gameLoading.visibility = View.GONE

        questionOne.background

    }

    private fun setFragment(){

        questionOne.setBackgroundResource(R.drawable.game_number_overlay)
        questionTwo.setBackgroundResource(R.drawable.game_number_overlay)
        questionThree.setBackgroundResource(R.drawable.game_number_overlay)

        if(game.uStage == 3 || game.uStage == 0){

            supportFragmentManager.beginTransaction().replace(R.id.fragment, StageThreeOwnQuestionFragment(this)).commit()
            mContent = supportFragmentManager.fragments.get(0)
            hideLoading()

        } else if(game.uStage == 2){

            GameUtil.getQuestionDataStageTwo("games/${game.gameID}/${friends.uid}/1", "games/${game.gameID}/${user.uid}/3") { a, q ->

                questionList = q
                answerList = a

                supportFragmentManager.beginTransaction().replace(R.id.fragment, StageTwoFragment(this)).commit()
                mContent = supportFragmentManager.fragments.get(0)
                hideLoading()

            }

        } else if(game.uStage == 1){

            GameUtil.getQuestionData("games/${game.gameID}/${friends.uid}/3") {

                questionList = it

                supportFragmentManager.beginTransaction().replace(R.id.fragment, StageOneFragment(this)).commit()
                mContent = supportFragmentManager.fragments.get(0)
                hideLoading()

            }

        } else {

            finish()

        }

    }

    private fun setInterface(onComplete: () -> Unit){

        setUserImage { onComplete() }
        setFriendImage()

        userNameText.text = user.name
        friendNameText.text = friends.name

        if(game.uStage == 0){

            etapText.text = "3 z 3"

        } else {

            etapText.text = "${game.uStage} z 3"

        }

    }

    private fun setUserImage(onComplete: () -> Unit){

        if(user.image.contains("logo")){

            Glide.with(this).load(R.mipmap.logo).listener(object :
                RequestListener<Drawable> {

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    onComplete()
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    userLoadingImage.visibility = View.GONE
                    onComplete()
                    return false
                }

            }).into(userProfileImage)

        } else {

            if (user.fb) {

                GlideApp.with(applicationContext).load("http://graph.facebook.com/${user.image}/picture?type=large").diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            onComplete()
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
                            onComplete()
                            return false
                        }

                    }).into(userProfileImage)

            } else {

                val storageReference =
                    FirebaseStorage.getInstance().reference.child("profile_image").child(user.image + "-image")
                        .downloadUrl

                storageReference.addOnSuccessListener { Uri ->

                    GlideApp.with(applicationContext).load(Uri.toString()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).listener(object :
                        RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            onComplete()
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
                            onComplete()
                            return false
                        }

                    }).into(userProfileImage)

                }.addOnFailureListener {

                    GlideApp.with(applicationContext).load(R.mipmap.logo).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).listener(object :
                        RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            onComplete()
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
                            onComplete()
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

                GlideApp.with(applicationContext).load("http://graph.facebook.com/${friends.image}/picture?type=large").diskCacheStrategy(
                    DiskCacheStrategy.AUTOMATIC)
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

                    GlideApp.with(applicationContext).load(Uri.toString()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).listener(object :
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

                    GlideApp.with(applicationContext).load(R.mipmap.logo).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).listener(object :
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

            setInterface {

                setFragment()

            }

        }
    }


    override fun onResume() {
        super.onResume()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        getFriendInformation()

    }

}
