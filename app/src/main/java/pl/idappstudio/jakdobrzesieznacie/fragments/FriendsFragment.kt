package pl.idappstudio.jakdobrzesieznacie.fragments

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.header_title_item.view.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.FriendsProfileActivity
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_BTN_CHAT_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_GAME_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_ITEM
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_NAME_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.EXTRA_USER_STATUS_GAME_TRANSITION_NAME
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.viewPager2
import pl.idappstudio.jakdobrzesieznacie.adapter.FriendsAdapater
import pl.idappstudio.jakdobrzesieznacie.adapter.GamesAdapater
import pl.idappstudio.jakdobrzesieznacie.adapter.InviteAdapater
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickInviteListener
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickListener
import pl.idappstudio.jakdobrzesieznacie.items.HeaderItem
import pl.idappstudio.jakdobrzesieznacie.model.FriendItem
import pl.idappstudio.jakdobrzesieznacie.model.UserData
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

class FriendsFragment : androidx.fragment.app.Fragment(), ClickListener, ClickInviteListener {

    override fun onClickFriendGame(user: UserData, image: CircleImageView, name: TextView, btnChat: ImageButton, btnFavorite: ImageButton, btnGame: ImageButton, bgGame: ImageView) {

        val intent = Intent(activity, FriendsProfileActivity::class.java)
        intent.putExtra(EXTRA_USER_ITEM, user)
        intent.putExtra(EXTRA_USER_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(image))
        intent.putExtra(EXTRA_USER_NAME_TRANSITION_NAME, ViewCompat.getTransitionName(name))
        intent.putExtra(EXTRA_USER_BTN_CHAT_TRANSITION_NAME, ViewCompat.getTransitionName(btnChat))
        intent.putExtra(EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME, ViewCompat.getTransitionName(btnFavorite))
        intent.putExtra(EXTRA_USER_IMAGE_GAME_TRANSITION_NAME, ViewCompat.getTransitionName(bgGame))
        intent.putExtra(EXTRA_USER_STATUS_GAME_TRANSITION_NAME, ViewCompat.getTransitionName(btnGame))

        val pairs = arrayOfNulls<Pair<View, String>>(6)
        pairs[0] = Pair(image, EXTRA_USER_IMAGE_TRANSITION_NAME)
        pairs[1] = Pair(name, EXTRA_USER_NAME_TRANSITION_NAME)
        pairs[2] = Pair(btnChat, EXTRA_USER_BTN_CHAT_TRANSITION_NAME)
        pairs[3] = Pair(btnFavorite, EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME)
        pairs[4] = Pair(bgGame, EXTRA_USER_IMAGE_GAME_TRANSITION_NAME)
        pairs[5] = Pair(btnGame, EXTRA_USER_STATUS_GAME_TRANSITION_NAME)

        val options = ActivityOptions.makeSceneTransitionAnimation(activity, pairs[0], pairs[1], pairs[2], pairs[3], pairs[4], pairs[5])

        startActivity(intent, options.toBundle())

    }

    override fun onClickFriend(user: UserData, image: CircleImageView, name: TextView, btnChat: ImageButton, btnFavorite: ImageButton) {

        val intent = Intent(activity, FriendsProfileActivity::class.java)
        intent.putExtra(EXTRA_USER_ITEM, user)
        intent.putExtra(EXTRA_USER_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(image))
        intent.putExtra(EXTRA_USER_NAME_TRANSITION_NAME, ViewCompat.getTransitionName(name))
        intent.putExtra(EXTRA_USER_BTN_CHAT_TRANSITION_NAME, ViewCompat.getTransitionName(btnChat))
        intent.putExtra(EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME, ViewCompat.getTransitionName(btnFavorite))

        val pairs = arrayOfNulls<Pair<View, String>>(4)
        pairs[0] = Pair(image, EXTRA_USER_IMAGE_TRANSITION_NAME)
        pairs[1] = Pair(name, EXTRA_USER_NAME_TRANSITION_NAME)
        pairs[2] = Pair(btnChat, EXTRA_USER_BTN_CHAT_TRANSITION_NAME)
        pairs[3] = Pair(btnFavorite, EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME)

        val options = ActivityOptions.makeSceneTransitionAnimation(activity, pairs[0], pairs[1], pairs[2], pairs[3])

        startActivity(intent, options.toBundle())
    }

    override fun onClickInvite(user: UserData) {

        dialogAdd.isClickable = true
        dialogDelete.isClickable = true

        GlideUtil.setImage(user.fb, user.image, activity!!, dialogProfile) {

            dialogName.text = user.name

            dialogAdd.setOnClickListener {

                dialogAdd.isClickable = false
                dialogDelete.isClickable = false

                UserUtil.addFriend(user.uid, false) {

                    if (it) {

                        UserUtil.delInvite(user.uid) {

                            closeDialog()

                            val view = view?.rootView

                            if (view != null) {
                                resources.getString(R.string.add_friend)
                                SnackBarUtil.setActivitySnack(
                                        resources.getString(R.string.add_friend, user.name),
                                        ColorSnackBar.SUCCES,
                                        R.drawable.ic_add_friends_icon,
                                        view) { }
                            }

                        }

                    } else {

                        val view = view?.rootView

                        if (view != null) {
                            SnackBarUtil.setActivitySnack(
                                    resources.getString(R.string.add_friend_error),
                                    ColorSnackBar.ERROR,
                                    R.drawable.ic_error_,
                                    view) { }
                        }

                    }
                }

            }

            dialogDelete.setOnClickListener {

                dialogAdd.isClickable = false
                dialogDelete.isClickable = false

                UserUtil.delInvite(user.uid) {

                    closeDialog()

                    val view = view?.rootView

                    if (view != null) {
                        SnackBarUtil.setActivitySnack(
                                resources.getString(R.string.reject_invites, user.name),
                                ColorSnackBar.WARING,
                                R.drawable.ic_remove,
                                view) { }
                    }

                }

            }

            showDialog()

        }

    }

    private lateinit var setDialog: Dialog

    private lateinit var profileImageButton: CircleImageView
    private lateinit var addFriends: ImageButton

    private lateinit var friendsListener: ListenerRegistration
    private lateinit var gameListener: ListenerRegistration
    private lateinit var invitesListener: ListenerRegistration

    private lateinit var imageFriends: ImageView
    private lateinit var textNoneFriends: TextView

    private lateinit var inviteHeader: ConstraintLayout

    private lateinit var rvFriends: RecyclerView
    private lateinit var rvGames: RecyclerView

    private lateinit var dialogName: TextView
    private lateinit var dialogProfile: CircleImageView
    private lateinit var dialogAdd: MaterialButton
    private lateinit var dialogDelete: MaterialButton

    private val excitingSection = Section()
    private val favoriteSection = Section()
    private val gameSection = Section()
    private val inviteSection = Section()

    private val dbFriends = FirebaseFirestore.getInstance().collection("users").document(UserUtil.user.uid).collection("friends")
    private val dbInvites = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!).collection("invites")
    private val dbGames = FirebaseFirestore.getInstance().collection("games")

    private val groupAdapter = GroupAdapter<ViewHolder>()
    private val inviteAdapter = GroupAdapter<ViewHolder>()

    private val updatableItems = HashMap<String, FriendsAdapater>()
    private val favoriteItems = HashMap<String, FriendsAdapater>()
    private val inviteItems = HashMap<String, InviteAdapater>()
    private val gamesItems = HashMap<String, GamesAdapater>()

    private fun addInviteItem(uid: String, b: Boolean): HashMap<String, InviteAdapater> {
        inviteItems[uid] = InviteAdapater(FriendItem(uid, b), this.context!!, this)
        return inviteItems
    }

    private fun addItem(uid: String, b: Boolean) : HashMap<String, FriendsAdapater> {
        updatableItems[uid] = FriendsAdapater(FriendItem(uid, b), this.context!!, this)
        return updatableItems
    }

    private fun addGamesItem(uid: String, b: Boolean) : HashMap<String, GamesAdapater> {
        gamesItems[uid] = GamesAdapater(FriendItem(uid, b), this.context!!, this)
        return gamesItems
    }

    private fun addFavoriteItem(uid: String, b: Boolean): HashMap<String, FriendsAdapater> {
        favoriteItems[uid] = FriendsAdapater(FriendItem(uid, b), this.context!!, this)
        return favoriteItems
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inviteSection.setHideWhenEmpty(true)
        favoriteSection.setHideWhenEmpty(true)
        excitingSection.setHideWhenEmpty(true)
        gameSection.setHideWhenEmpty(true)

        inviteAdapter.add(0, inviteSection)
        groupAdapter.add(0, gameSection)
        groupAdapter.add(1, favoriteSection)
        groupAdapter.add(2, excitingSection)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_friends, container, false)

        profileImageButton = rootView.findViewById(R.id.profile_image)
        addFriends = rootView.findViewById(R.id.addFriendsButton)

        GlideUtil.setImage(UserUtil.user.fb, UserUtil.user.image, this.requireContext(), profileImageButton) { }

        profileImageButton.setOnClickListener {

            viewPager2.setCurrentItem(2, true)

        }

        addFriends.setOnClickListener {

            viewPager2.setCurrentItem(0, true)

        }

        imageFriends = rootView.findViewById(R.id.image_none_friends)
        textNoneFriends = rootView.findViewById(R.id.text_none_friends)

        rvFriends = rootView.findViewById(R.id.rv_friends)
        rvGames = rootView.findViewById(R.id.rv_games)

        imageFriends.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        val rvFriendsLLM = LinearLayoutManager(this.context)
        val rvGamesLLM = LinearLayoutManager(this.context)

        rvFriendsLLM.orientation = RecyclerView.VERTICAL
        rvGamesLLM.orientation = RecyclerView.HORIZONTAL

        rvFriends.layoutManager = rvFriendsLLM
        rvGames.layoutManager = rvGamesLLM

        rvFriends.adapter = groupAdapter
        rvGames.adapter = inviteAdapter

        inviteHeader = rootView.findViewById(R.id.include)

        inviteHeader.head_title_text.text = resources.getString(R.string.invites)
        gameSection.setHeader(HeaderItem(resources.getString(R.string.your_turn)))
        excitingSection.setHeader(HeaderItem(resources.getString(R.string.all)))
        favoriteSection.setHeader(HeaderItem(resources.getString(R.string.favorite)))

        checkInviteList {
            getInvites()
        }

        checkFriendsList {
            getFriends()
        }

        checkGamesList {
            getGames()
        }

        setDialog()

        return rootView
    }

    private fun getInvites() {

        invitesListener = dbInvites.addSnapshotListener(EventListener<QuerySnapshot> { doc, e ->

            if (e != null) {
                return@EventListener
            }

            if (doc != null) {

                for (it in doc.documentChanges) {

                    if (it.type == DocumentChange.Type.REMOVED) {

                        rvGames.itemAnimator = LandingAnimator()

                        inviteItems.remove(it.document.id)
                        checkInviteList {
                            inviteSection.update(inviteItems.values)
                        }

                    }

                    if (it.type == DocumentChange.Type.ADDED) {

                        addInviteItem(it.document.id, true)
                        checkInviteList {
                            inviteSection.update(inviteItems.values)
                        }

                    }

                }


            }

        })
    }

    private fun checkInviteList(onComplete: () -> Unit) {

        if (inviteItems.isNotEmpty()) {

            rvGames.visibility = View.VISIBLE
            inviteHeader.visibility = View.VISIBLE

            view?.requestLayout()

            onComplete()

        } else {

            rvGames.visibility = View.GONE
            inviteHeader.visibility = View.GONE

            view?.requestLayout()

            onComplete()

        }

    }

    private fun getFriends() {

        friendsListener = dbFriends.addSnapshotListener(EventListener<QuerySnapshot> { doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc != null) {

                for (it in doc.documentChanges){

                    if(it.type == DocumentChange.Type.REMOVED){

                        if(it.document.getBoolean("favorite") == true){

                            favoriteItems.remove(it.document.id)
                            checkFriendsList {
                                favoriteSection.update(favoriteItems.values)
                            }

                        } else {

                            updatableItems.remove(it.document.id)
                            checkFriendsList {
                                excitingSection.update(updatableItems.values)
                            }

                        }

                    }

                    if(it.type == DocumentChange.Type.MODIFIED){

                        

                        if(it.document.getBoolean("favorite") == true){

                            if(updatableItems[it.document.id] != null){

                                updatableItems.remove(it.document.id)
                                addFavoriteItem(it.document.id, it.document.getBoolean("favorite")!!)

                                view?.requestLayout()

                                excitingSection.update(updatableItems.values)
                                favoriteSection.update(favoriteItems.values)

                            }

                        } else {

                            if(favoriteItems[it.document.id] != null){

                                favoriteItems.remove(it.document.id)
                                addItem(it.document.id, it.document.getBoolean("favorite")!!)

                                view?.requestLayout()

                                favoriteSection.update(favoriteItems.values)
                                excitingSection.update(updatableItems.values)

                            }

                        }

                    }

                    if(it.type == DocumentChange.Type.ADDED){

                        if(it.document.getBoolean("favorite") == true){

                            addFavoriteItem(it.document.id, it.document.getBoolean("favorite")!!)
                            checkFriendsList {
                                favoriteSection.update(favoriteItems.values)
                            }

                        } else {

                            addItem(it.document.id, it.document.getBoolean("favorite")!!)

                            checkFriendsList {
                                excitingSection.update(updatableItems.values)
                            }

                        }


                    }

                }


            }

        })
    }

    private fun checkFriendsList(onComplete: () -> Unit) {

        if (updatableItems.isNotEmpty() || favoriteItems.isNotEmpty()) {

            imageFriends.visibility = View.GONE
            textNoneFriends.visibility = View.GONE

            view?.requestLayout()

            onComplete()

        } else {

            textNoneFriends.visibility = View.VISIBLE
            imageFriends.visibility = View.VISIBLE

            view?.requestLayout()

            onComplete()

        }

    }

    private fun getGames() {

        gameListener = dbGames.whereEqualTo("${UserUtil.user.uid}-turn", true).addSnapshotListener(EventListener<QuerySnapshot> { doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc != null) {

                for (it in doc.documentChanges){

                    if(it.type == DocumentChange.Type.REMOVED){

                        gamesItems.remove(friendId(UserUtil.user.uid, it.document.id))

                        checkGamesList {
                            gameSection.update(gamesItems.values)
                        }

                    }

                    if (it.type == DocumentChange.Type.MODIFIED) {

                        if (it.document.getBoolean("${UserUtil.user.uid}-turn") == false) {

                            gamesItems.remove(friendId(UserUtil.user.uid, it.document.id))

                            checkGamesList {
                                gameSection.update(gamesItems.values)
                            }

                        } else if (it.document.getBoolean("${UserUtil.user.uid}-turn") == true) {

                            addGamesItem(friendId(UserUtil.user.uid, it.document.id), true)

                            checkGamesList {
                                gameSection.update(gamesItems.values)
                            }

                        }

                    }

                    if(it.type == DocumentChange.Type.ADDED){

                        if (it.document.getBoolean("${friendId(UserUtil.user.uid, it.document.id)}-turn") == false) {

                            addGamesItem(friendId(UserUtil.user.uid, it.document.id), true)

                            checkGamesList {
                                gameSection.update(gamesItems.values)
                            }

                        }

                    }

                }


            }

        })
    }

    private fun friendId(yourID: String, gameID: String): String {

        val bufferId = yourID.toCharArray()

        val idSize = bufferId.size

        val bufferFriend = gameID.substring(idSize)

        return if (bufferFriend == yourID) {

            gameID.substring(0, idSize)

        } else {

            bufferFriend

        }

    }

    private fun checkGamesList(onComplete: () -> Unit) {

        if (gamesItems.isNotEmpty()) {

            view?.requestLayout()

            onComplete()

        } else {

            view?.requestLayout()

            onComplete()

        }

    }

    private fun closeDialog() {

        if (setDialog.isShowing) {

            setDialog.dismiss()

        }

    }

    private fun showDialog() {

        if (!setDialog.isShowing) {

            setDialog.show()

        }

    }

    private fun setDialog() {

        setDialog = Dialog(context!!)
        setDialog.setCancelable(true)
        setDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setDialog.setContentView(R.layout.dialog_invite)
        setDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_invite_overlay)

        dialogAdd = setDialog.findViewById(R.id.addFriends)
        dialogDelete = setDialog.findViewById(R.id.deleteFriends)
        dialogName = setDialog.findViewById(R.id.profile_name3)
        dialogProfile = setDialog.findViewById(R.id.friends_profile2)

    }

    override fun onDestroy() {
        super.onDestroy()
        friendsListener.remove()
        invitesListener.remove()
        gameListener.remove()
    }

}
