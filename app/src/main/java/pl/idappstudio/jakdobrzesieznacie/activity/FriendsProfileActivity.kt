package pl.idappstudio.jakdobrzesieznacie.activity

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import android.view.Window
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import kotlinx.android.synthetic.main.activity_friends_profile.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_BTN_CHAT_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_GAME_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_ITEM
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_NAME_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.adapter.SetAdapater
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.enums.StatusMessage
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickSetListener
import pl.idappstudio.jakdobrzesieznacie.items.HeaderItem
import pl.idappstudio.jakdobrzesieznacie.model.*
import pl.idappstudio.jakdobrzesieznacie.util.GameUtil
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule


class FriendsProfileActivity : AppCompatActivity(), ClickSetListener {

    override fun click(setItem: SetItem) {

        if (UserUtil.user.type == "premium" && setItem.premium) {

            dbGames.document(games.gameId).update("${UserUtil.user.uid}-set", setItem.id)
            closeDialog()

        } else if (!setItem.premium){

            dbGames.document(games.gameId).update("${UserUtil.user.uid}-set", setItem.id)
            closeDialog()

        } else {

            SnackBarUtil.setActivitySnack(
                resources.getString(R.string.have_premium),
                ColorSnackBar.WARING,
                R.drawable.ic_corn,
                setDialog.findViewById(R.id.head_title_text)
            ) {

            }

        }

    }

    companion object {

        const val EXTRA_USER_IMAGE_TRANSITION = "image-user"
        const val EXTRA_FRIEND_IMAGE_TRANSITION = "image-friend"


    }

    private lateinit var setDialog: Dialog

    private lateinit var friend: UserData
    private lateinit var games: GamesItem

    private lateinit var userSet: UserSetData
    private lateinit var friendSet: UserSetData

    private val db = FirebaseFirestore.getInstance().collection("users")
    private val dbSet = FirebaseFirestore.getInstance().collection("set")
    private val dbGames = FirebaseFirestore.getInstance().collection("games")

    private var setListener: ListenerRegistration? = null
    private var userStatsListener: ListenerRegistration? = null
    private var userSetListener: ListenerRegistration? = null
    private var friendSetListener: ListenerRegistration? = null
    private var friendStatsListener: ListenerRegistration? = null
    private var friendDataListener: ListenerRegistration? = null
    private var gamesListener: ListenerRegistration? = null

    private lateinit var rvSetDefault: RecyclerView

    private val defaultPack = Section()
    private val yourPack = Section()
    private val socialPack = Section()

    private val defaultItems = HashMap<String, SetAdapater>()
    private val yourItems = HashMap<String, SetAdapater>()
    private val socialItems = HashMap<String, SetAdapater>()

    private fun addDefaultItem(id: String, set: SetItem): HashMap<String, SetAdapater> {
        defaultItems[id] = SetAdapater(set, this)
        return defaultItems
    }

    private fun addYourItem(id: String, set: SetItem): HashMap<String, SetAdapater> {
        yourItems[id] = SetAdapater(set, this)
        return yourItems
    }

    private fun addSocialItem(id: String, set: SetItem): HashMap<String, SetAdapater> {
        socialItems[id] = SetAdapater(set, this)
        return socialItems
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)
        supportPostponeEnterTransition()

        val extras = intent.extras

        friend = extras?.getParcelable(EXTRA_USER_ITEM)!!
        if(extras.getString(EXTRA_USER_IMAGE_GAME_TRANSITION_NAME) != null) {
            val imageGame = extras.getString(EXTRA_USER_IMAGE_GAME_TRANSITION_NAME)
            friends_profile_startgame_btn.transitionName = imageGame
        }

//        if(extras.getString(EXTRA_USER_STATUS_GAME_TRANSITION_NAME) != null) {
//            val imageStatus = extras.getString(EXTRA_USER_STATUS_GAME_TRANSITION_NAME)
//        }

        val imageTransitionName = extras.getString(EXTRA_USER_IMAGE_TRANSITION_NAME)
        val nameTransitionName = extras.getString(EXTRA_USER_NAME_TRANSITION_NAME)
        val btnChatTransitionName = extras.getString(EXTRA_USER_BTN_CHAT_TRANSITION_NAME)
        val btnFavoriteTransitionName = extras.getString(EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME)

        val backBtn = linearLayout4.findViewById<ImageButton>(R.id.back_btn)
        backBtn.setOnClickListener {

            onBackPressed()

        }

        statsFriend()
        statsUser()

        setInformation()

        friends_profile_chat.setOnClickListener {
            resources.getString(R.string.chat_in_progress)
            SnackBarUtil.setActivitySnack(
                resources.getString(R.string.chat_in_progress),
                ColorSnackBar.WARING,
                R.drawable.ic_warning,
                it
            ) { }

        }

        friends_profile_image.transitionName = imageTransitionName
        friends_profile_name.transitionName = nameTransitionName
        friends_profile_chat.transitionName = btnChatTransitionName
        friends_profile_favorite.transitionName = btnFavoriteTransitionName

        friends_profile_name.text = friend.name

        GlideUtil.setImage(UserUtil.user.fb, UserUtil.user.image, this, user_profile_image_stats) {}

        GlideUtil.setImage(friend.fb, friend.image, this, friends_profile_image_stats) {}

        GlideUtil.setImage(friend.fb, friend.image, this, friends_profile_image) {

            supportStartPostponedEnterTransition()

        }

        blockFunction()

        ViewCompat.setTransitionName(friends_profile_image_stats, EXTRA_FRIEND_IMAGE_TRANSITION)
        ViewCompat.setTransitionName(user_profile_image_stats, EXTRA_USER_IMAGE_TRANSITION)

        setDialog()

        val groupAdapter = GroupAdapter<ViewHolder>()

        val llm = LinearLayoutManager(this)

        rvSetDefault.layoutManager = llm
        rvSetDefault.itemAnimator = SlideInDownAnimator()

        rvSetDefault.adapter = groupAdapter

        defaultPack.setHeader(HeaderItem(resources.getString(R.string.head_pack_default)))
        yourPack.setHeader(HeaderItem(resources.getString(R.string.header_pack_yours)))
        socialPack.setHeader(HeaderItem(resources.getString(R.string.premium)))

        defaultPack.setHideWhenEmpty(true)
        yourPack.setHideWhenEmpty(true)
        socialPack.setHideWhenEmpty(true)

        groupAdapter.add(0, defaultPack)
        groupAdapter.add(1, yourPack)
        groupAdapter.add(2, socialPack)

        friends_profile_set_btn.setOnClickListener {

            showDialog()

        }

        friends_profile_startgame_btn.setOnClickListener {

            blockFunction()

            UserUtil.getIdGame(friend.uid) {

                dbGames.document(it).update("${friend.uid}-turn", false)

                intent = Intent(this, GameActivity::class.java)
                intent.putExtra("uid", friend.uid)
                intent.putExtra("uSet", userSet.id)
                intent.putExtra("fSet", friendSet.id)
                intent.putExtra("gameId", it)
                intent.putExtra(EXTRA_USER_IMAGE_TRANSITION, ViewCompat.getTransitionName(user_profile_image_stats))
                intent.putExtra(EXTRA_FRIEND_IMAGE_TRANSITION, ViewCompat.getTransitionName(friends_profile_image_stats))

                val pairs = arrayOfNulls<Pair<View, String>>(2)
                pairs[0] = Pair(friends_profile_image_stats, EXTRA_FRIEND_IMAGE_TRANSITION)
                pairs[1] = Pair(user_profile_image_stats, EXTRA_USER_IMAGE_TRANSITION)

                val options = ActivityOptions.makeSceneTransitionAnimation(this, pairs[0], pairs[1])

                startActivity(intent, options.toBundle())

                val r = Runnable {
                    unlockAll()
                }

                Handler().postDelayed(r, 1200)

            }

        }

    }

    private fun blockFunction() {

        friends_profile_favorite.isEnabled = false
        friends_profile_set_btn.isEnabled = false
        friends_profile_startgame_btn.isEnabled = false
        friends_profile_gamemode_btn.isEnabled = false

    }

    private fun unlockFunction() {

        friends_profile_favorite.isEnabled = true
        friends_profile_set_btn.isEnabled = true
        friends_profile_gamemode_btn.isEnabled = true

    }

    private fun unlockAll() {

        friends_profile_favorite.isEnabled = true
        friends_profile_set_btn.isEnabled = true
        friends_profile_gamemode_btn.isEnabled = true
        friends_profile_startgame_btn.isEnabled = true

    }

    private fun closeDialog() {

        if (setDialog.isShowing) {

            setDialog.dismiss()

            setListener?.remove()

        }

    }

    private fun showDialog() {

        if (!setDialog.isShowing) {

            setDialog.show()

        }

    }

    private fun loadSetRecycler() {

        setListener = dbSet.addSnapshotListener(EventListener<QuerySnapshot> { doc, e ->

            if (e != null) {
                return@EventListener
            }

            if (doc != null) {

                for (it in doc.documentChanges) {

                    if (it.type == DocumentChange.Type.REMOVED) {

                        rvSetDefault.itemAnimator = LandingAnimator()

                        when {
                            it.document.getBoolean("premium") == true -> {

                                socialItems.remove(it.document.id)
                                socialPack.update(socialItems.values)

                            }
                            it.document.getString("category") == UserUtil.user.uid -> {

                                yourItems.remove(it.document.id)
                                yourPack.update(yourItems.values)

                            }
                            else -> {

                                defaultItems.remove(it.document.id)
                                defaultPack.update(defaultItems.values)

                            }
                        }

                    }

                    if (it.type == DocumentChange.Type.MODIFIED) {

                        rvSetDefault.itemAnimator = LandingAnimator()

                        when {
                            it.document.getBoolean("premium") == true -> {

                                addSocialItem(it.document.id, it.document.toObject(SetItem::class.java))
                                defaultPack.update(defaultItems.values)

                            }
                            it.document.getString("category") == UserUtil.user.uid -> {

                                addYourItem(it.document.id, it.document.toObject(SetItem::class.java))
                                yourPack.update(yourItems.values)

                            }
                            else -> {

                                addDefaultItem(it.document.id, it.document.toObject(SetItem::class.java))
                                defaultPack.update(defaultItems.values)

                            }
                        }

                    }

                    if (it.type == DocumentChange.Type.ADDED) {

                        if (it.document.getBoolean("premium") == true) {

                            addSocialItem(it.document.id, it.document.toObject(SetItem::class.java))
                            socialPack.update(socialItems.values)

                        } else if(it.document.getString("category") == UserUtil.user.uid){

                            addYourItem(it.document.id, it.document.toObject(SetItem::class.java))
                            yourPack.update(yourItems.values)

                        } else {

                            if(it.document.getString("category") == "default" || it.document.getString("category") == "own_question"){

                                addDefaultItem(it.document.id, it.document.toObject(SetItem::class.java))
                                defaultPack.update(defaultItems.values)

                            }

                        }

                    }

                }


            }

        })

    }

    private fun setDialog() {

        setDialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        setDialog.setCancelable(true)
        setDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setDialog.setContentView(R.layout.dialog_choose_set)
        setDialog.window?.setBackgroundDrawableResource(R.color.colorDarkDialog)

        rvSetDefault = setDialog.findViewById(R.id.rvSetDefault)

        loadSetRecycler()

    }

    private fun statsUser() {

        userStatsListener =
            db.document(UserUtil.user.uid).collection("friends").document(friend.uid).addSnapshotListener(
                EventListener<DocumentSnapshot> { doc, e ->

                    if (e != null) {
                        return@EventListener
                    }

                    if (doc != null) {

                        if (doc.exists()) {

                            if (doc.getBoolean("favorite")!!) {

                                friends_profile_favorite.apply {
                                    setImageResource(R.drawable.ic_heart_solid)
                                }

                                friends_profile_favorite.setOnClickListener {

                                    db.document(UserUtil.user.uid).collection("friends").document(friend.uid)
                                        .update("favorite", false)

                                }

                            } else {

                                friends_profile_favorite.apply {
                                    setImageResource(R.drawable.ic_heart_over)
                                }

                                friends_profile_favorite.setOnClickListener {

                                    db.document(UserUtil.user.uid).collection("friends").document(friend.uid)
                                        .update("favorite", true)

                                }

                            }

                            user_profile_stats_canswer.text = doc.getLong("canswer")!!.toInt().toString()
                            user_profile_stats_banswer.text = doc.getLong("banswer")!!.toInt().toString()
                            user_profile_stats_games.text = doc.getLong("games")!!.toInt().toString()
                            val precent = GameUtil.getPrecent(
                                StatsData(
                                    doc.getLong("canswer")!!.toInt(),
                                    doc.getLong("banswer")!!.toInt(),
                                    doc.getLong("games")!!.toInt()
                                )
                            )

                            user_profile_stats_precent.text = String.format("$precent%s", "%")

                        }

                    }

                })

    }

    private fun statsFriend() {

        friendStatsListener =
            db.document(friend.uid).collection("friends").document(UserUtil.user.uid).addSnapshotListener(
                EventListener<DocumentSnapshot> { doc, e ->

                    if (e != null) {
                        return@EventListener
                    }

                    if (doc != null) {

                        if (doc.exists()) {

                            friends_profile_stats_canswer.text = doc.getLong("canswer")!!.toInt().toString()
                            friends_profile_stats_banswer.text = doc.getLong("banswer")!!.toInt().toString()
                            friends_profile_stats_games.text = doc.getLong("games")!!.toInt().toString()
                            val precent = GameUtil.getPrecent(
                                StatsData(
                                    doc.getLong("canswer")!!.toInt(),
                                    doc.getLong("banswer")!!.toInt(),
                                    doc.getLong("games")!!.toInt()
                                )
                            )
                            friends_profile_stats_precent.text = String.format("$precent%s", "%")

                        }

                    }

                })

    }

    private fun setInformation() {

        UserUtil.getIdGame(friend.uid) {

            gamesListener = dbGames.document(it).addSnapshotListener(EventListener<DocumentSnapshot> { doc, e ->

                if (e != null) {
                    return@EventListener
                }

                if (doc != null) {

                    if (doc.exists()) {

                        val gamemode: String = doc.getString("gamemode")!!
                        val friendStage: Int = doc.getLong("${friend.uid}-stage")!!.toInt()
                        val yourStage: Int = doc.getLong("${UserUtil.user.uid}-stage")!!.toInt()
                        val friendTurn: Boolean = doc.getBoolean("${friend.uid}-turn")!!
                        val yourTurn: Boolean = doc.getBoolean("${UserUtil.user.uid}-turn")!!
                        val friendSet: String = doc.getString("${friend.uid}-set")!!
                        val yourSet: String = doc.getString("${UserUtil.user.uid}-set")!!
                        val newGame: Boolean = doc.getBoolean("newGame")!!
                        val userID: String = doc.getString("${UserUtil.user.uid}-id")!!
                        val friendID: String = doc.getString("${friend.uid}-id")!!

                        games = GamesItem(
                            doc.id,
                            yourSet,
                            yourStage,
                            yourTurn,
                            userID,
                            friendSet,
                            friendStage,
                            friendTurn,
                            friendID,
                            gamemode,
                            newGame
                        )

                        updateInofrmation()

                    }

                }

            })

        }

    }

    private fun userSets() {

        userSetListener = dbSet.document(games.yset).addSnapshotListener(EventListener<DocumentSnapshot> { doc, e ->

            if (e != null) {
                return@EventListener
            }

            if (doc != null) {

                if (doc.exists()) {

                    userSet = doc.toObject(UserSetData::class.java)!!

                    if(userSet.category == UserUtil.user.uid){

                        friends_profile_set_btn.text = GameUtil.getSetName(userSet.name, resources)

                    } else {

                        friends_profile_set_btn.text = GameUtil.getSetName(userSet.category, resources)

                    }

                }

            }

        })

    }

    private fun friendSets() {

        friendSetListener = dbSet.document(games.fset).addSnapshotListener(EventListener<DocumentSnapshot> { doc, e ->

            if (e != null) {
                return@EventListener
            }

            if (doc != null) {

                if (doc.exists()) {

                    friendSet = doc.toObject(UserSetData::class.java)!!

                }

            }

        })

    }

    private fun updateInofrmation() {

        userSets()
        friendSets()

        unlockFunction()

        if (games.yturn && games.fturn) {

            friends_profile_startgame_btn.isEnabled = true

            friends_profile_startgame_btn.text = resources.getString(R.string.play)
            friends_profile_startgame_btn.background.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.colorPrimary
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

        } else if(games.yturn && !games.fturn) {

            friends_profile_startgame_btn.isEnabled = true

            friends_profile_startgame_btn.text = resources.getString(R.string.play)
            friends_profile_startgame_btn.background.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.colorAccent
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

        } else {

            friends_profile_startgame_btn.isEnabled = false

            friends_profile_startgame_btn.text = resources.getString(R.string.friend_turn)
            friends_profile_startgame_btn.background.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.colorRed
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

        }

        friends_profile_gamemode_btn.text = GameUtil.getGamemodeName(games.gamemode, resources)

    }

    override fun onResume() {
        super.onResume()

        Timer("status", false).schedule(700) {
            UserUtil.updateStatus(resources.getString(StatusMessage.online)) {}
        }

        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        UserUtil.updateStatus(resources.getString(StatusMessage.offline)) {}
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        hideSystemUI()
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    override fun onBackPressed() {
        hideSystemUI()
        super.onBackPressed()
    }

    private fun hideSystemUI(){

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    }

    override fun onWindowFocusChanged(hasFocus:Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setListener?.remove()
        gamesListener?.remove()
        friendStatsListener?.remove()
        friendDataListener?.remove()
        friendSetListener?.remove()
        userStatsListener?.remove()
        userSetListener?.remove()
    }

}
