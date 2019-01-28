package pl.idappstudio.howwelldoyouknoweachother.activity

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_friends_profile.*
import kotlinx.android.synthetic.main.dialog_choose_set.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.adapter.SetAdapterFirestore
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.interfaces.CountInterface
import pl.idappstudio.howwelldoyouknoweachother.model.FriendsData
import pl.idappstudio.howwelldoyouknoweachother.model.GameData
import pl.idappstudio.howwelldoyouknoweachother.model.SetItem
import pl.idappstudio.howwelldoyouknoweachother.model.StatsData
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil
import pl.idappstudio.howwelldoyouknoweachother.util.GameUtil

class FriendsProfileActivity : AppCompatActivity(), CountInterface {

    override fun reload() {}

    override fun count() {}

    override fun click(s: String, b: Boolean, name: String) {

        if(setDialog.isShowing){

            if(b && friends.type == "premium"){

                FirestoreUtil.updateGameSettings(game.yourTurn, game.friendTurn, game.yourStage, game.friendStage, s, game.friendSet.id, game.gamemode, game.gameID, FirebaseAuth.getInstance().currentUser?.uid.toString(), friends.uid)

                friends_profile_set_btn.text = GameUtil().getSetName(name)

                getFriendInformation()

                closeDialog()

            } else if (!b) {

                FirestoreUtil.updateGameSettings(game.yourTurn, game.friendTurn, game.yourStage, game.friendStage, s, game.friendSet.id, game.gamemode, game.gameID, FirebaseAuth.getInstance().currentUser?.uid.toString(), friends.uid)

                friends_profile_set_btn.text = GameUtil().getSetName(name)

                getFriendInformation()

                closeDialog()

            } else {

                snackbar = Snackbar.make(crownImage.rootView, "Aby wybrać ten zestaw musisz posiadać konto premium", 2500)
                snackbar.view.setBackgroundColor(this.resources.getColor(R.color.colorYellow))
                snackbar.show()

            }

        }

    }

    private lateinit var friends: FriendsData
    private lateinit var game: GameData
    private lateinit var stats: StatsData

    private lateinit var snackbar: Snackbar

    private lateinit var setDialog: Dialog

    private val db = FirebaseFirestore.getInstance().collection("set")

    private lateinit var adapterSetDefault: SetAdapterFirestore

    private lateinit var rvSetDefault: RecyclerView

    private lateinit var crownImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)

        friends_profile_loading_image.visibility = View.VISIBLE

        setDialog()

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

    fun closeDialog(){

        adapterSetDefault.stopListening()

        setDialog.dismiss()

    }

    fun showDialog(){

        setDialog.show()

        if(setDialog.isShowing) {

            val query: Query = db.whereEqualTo("type", "default")

            val options: FirestoreRecyclerOptions<SetItem> = FirestoreRecyclerOptions.Builder<SetItem>().setQuery(query, SetItem::class.java).setLifecycleOwner(this).build()

            adapterSetDefault = SetAdapterFirestore(options, this)
            adapterSetDefault.setRV(rvSetDefault)

            rvSetDefault.setHasFixedSize(true)
            rvSetDefault.layoutManager = LinearLayoutManager(this)
            rvSetDefault.adapter = adapterSetDefault

            crownImage.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.colorLigth
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

            adapterSetDefault.startListening()

        }

    }

    fun setDialog(){

        setDialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        setDialog.setCancelable(true)
        setDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setDialog.setContentView(R.layout.dialog_choose_set)
        setDialog.window.setBackgroundDrawableResource(R.color.colorDarkDialog)

        rvSetDefault = setDialog.findViewById(R.id.rvSetDefault)

        crownImage = setDialog.findViewById(R.id.crown_image)

    }

    fun updateInofrmation() {

        FirebaseFirestore.getInstance().collection("games").document(game.gameID).addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                return@EventListener
            }

            if (snapshot != null && snapshot.exists()) {

                if(snapshot.getBoolean(FirestoreUtil.currentUser.uid + "-turn")!!){

                    friends_profile_startgame_btn.text = "GRAJ"
                    friends_profile_startgame_btn.background.setColorFilter(
                        ContextCompat.getColor(
                            this, R.color.colorPrimary
                        ), android.graphics.PorterDuff.Mode.SRC_IN)

                } else {

                    friends_profile_startgame_btn.text = "kolej znajomego"
                    friends_profile_startgame_btn.background.setColorFilter(
                        ContextCompat.getColor(
                            this, R.color.colorRed
                        ), android.graphics.PorterDuff.Mode.SRC_IN)

                }

                friends_profile_gamemode_btn.text = GameUtil().getGamemodeName(snapshot.getString("gamemode")!!)

            }
        })

    }

    fun setInformation(){

        friends_profile_name.text = friends.name
        friends_profile_set_btn.text = GameUtil().getSetName(game.youtSet.name)

        friends_profile_set_btn.setOnClickListener {

            showDialog()

        }

        updateInofrmation()

        if(friends.favorite!!){

            friends_profile_favorite.setIconResource(R.drawable.ic_heart_solid)

            friends_profile_favorite.setOnClickListener {

                friends_profile_favorite.setIconResource(R.drawable.ic_heart_over)

                FirestoreUtil.setFavorite(friends.uid, false)
                getFriendInformation()

            }

        } else {

            friends_profile_favorite.setIconResource(R.drawable.ic_heart_over)

            friends_profile_favorite.setOnClickListener {

                friends_profile_favorite.setIconResource(R.drawable.ic_heart_solid)

                FirestoreUtil.setFavorite(friends.uid, true)
                getFriendInformation()

            }
        }

        val a: Float = stats.canswer.toFloat()
        val b: Float = stats.banswer.toFloat()
        val c: Int = stats.games

        val sum: Float = a/(a+b)
        val suma: Int = if(sum.toInt().toString().length == 1) {
            (sum*100).toInt()
        } else if(sum.toInt().toString().length == 2) {
            (sum*10).toInt()
        } else if(sum.toInt().toString().length == 3) {
            sum.toInt()
        } else {
            sum.toString().substring(0, 3).toInt()
        }

        friends_profile_stats_canswer.text = a.toInt().toString()
        friends_profile_stats_banswer.text = b.toInt().toString()
        friends_profile_stats_games.text = c.toString()
        friends_profile_stats_precent.text = "$suma%"

        setImage()

    }

    private fun getFriendInformation(){
        FirestoreUtil.getFriendsUser(intent?.extras?.get("id").toString()) { e ->

            friends = e
            game = e.game
            stats = e.stats

            setInformation()

        }
    }

    override fun onResume() {
        super.onResume()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        getFriendInformation()

    }
}
