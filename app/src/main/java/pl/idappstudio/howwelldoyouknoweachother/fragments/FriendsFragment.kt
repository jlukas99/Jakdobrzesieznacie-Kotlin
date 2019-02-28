package pl.idappstudio.howwelldoyouknoweachother.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.*
import de.hdodenhof.circleimageview.CircleImageView
import pl.idappstudio.howwelldoyouknoweachother.activity.FriendsProfileActivity
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_BTN_CHAT_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_BTN_FAVORITE_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_ITEM
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_NAME_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.interfaces.ClickListener
import pl.idappstudio.howwelldoyouknoweachother.model.FriendItem
import pl.idappstudio.howwelldoyouknoweachother.model.UserData
import pl.idappstudio.howwelldoyouknoweachother.util.UserUtil
import pl.idappstudio.howwelldoyouknoweachother.R
import android.util.Pair
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import kotlinx.android.synthetic.main.header_title_item.view.*
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_IMAGE_GAME_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.activity.MenuActivity.Companion.EXTRA_USER_STATUS_GAME_TRANSITION_NAME
import pl.idappstudio.howwelldoyouknoweachother.adapter.*

class FriendsFragment : androidx.fragment.app.Fragment(), ClickListener {
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

    private lateinit var friendsListener: ListenerRegistration
    private lateinit var gamesListener: ListenerRegistration

    private lateinit var image_friends: ImageView
    private lateinit var text_none_friends: TextView

    private lateinit var gamesHeader: ConstraintLayout
    private lateinit var friendsHeader: ConstraintLayout

    private lateinit var rvFriends: RecyclerView
    private lateinit var rvGames: RecyclerView

    private val excitingSection = Section()
    private val favoriteSection = Section()

    private val gamesSection = Section()
    private val noGamesSection = Section()

    private val dbFriends = FirebaseFirestore.getInstance().collection("users").document(UserUtil.user.uid).collection("friends")
    private val dbGames = FirebaseFirestore.getInstance().collection("games")

    private val groupAdapter = GroupAdapter<ViewHolder>()
    private val gamesAdapter = GroupAdapter<ViewHolder>()

    private val updatableItems = HashMap<String, FriendsAdapater>()
    private val favoriteItems = HashMap<String, FriendsAdapater>()

    private val gamesItems = HashMap<String, GamesAdapater>()
    private val noGamesItems = HashMap<String, GamesAdapater>()

    private fun addItem(uid: String, b: Boolean) : HashMap<String, FriendsAdapater> {
        updatableItems[uid] = FriendsAdapater(FriendItem(uid, b), this.context!!, this)
        return updatableItems
    }

    private fun addFavoriteItem(uid: String, b: Boolean) : HashMap<String, FriendsAdapater> {
        favoriteItems[uid] = FriendsAdapater(FriendItem(uid, b), this.context!!, this)
        return favoriteItems
    }

    private fun addGamesItem(uid: String, b: Boolean) : HashMap<String, GamesAdapater> {
        gamesItems[uid] = GamesAdapater(FriendItem(uid, b), this.context!!, this)
        return gamesItems
    }

    private fun addNoGamesItem(uid: String, b: Boolean) : HashMap<String, GamesAdapater> {
        noGamesItems[uid] = GamesAdapater(FriendItem(uid, b), this.context!!, this)
        return noGamesItems
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gamesSection.setHideWhenEmpty(true)
        noGamesSection.setHideWhenEmpty(true)

        favoriteSection.setHideWhenEmpty(true)
        excitingSection.setHideWhenEmpty(true)

        gamesAdapter.add(0, gamesSection)
        gamesAdapter.add(1, noGamesSection)

        groupAdapter.add(0, favoriteSection)
        groupAdapter.add(1, excitingSection)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_friends, container, false)

        image_friends = rootView.findViewById(R.id.image_none_friends)
        text_none_friends = rootView.findViewById(R.id.text_none_friends)

        gamesHeader = rootView.findViewById(R.id.include)
        friendsHeader = rootView.findViewById(R.id.include3)

        rvFriends = rootView.findViewById(R.id.rv_friends)
        rvGames = rootView.findViewById(R.id.rv_games)

        image_friends.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        val rvFriendsLLM = LinearLayoutManager(this.context)
        val rvGamesLLM = LinearLayoutManager(this.context)

        rvFriendsLLM.orientation = RecyclerView.VERTICAL
        rvGamesLLM.orientation = RecyclerView.HORIZONTAL

        rvFriends.layoutManager = rvFriendsLLM
        rvGames.layoutManager = rvGamesLLM

        rvFriends.itemAnimator = SlideInDownAnimator()
        rvGames.itemAnimator = SlideInDownAnimator()

        rvFriends.adapter = groupAdapter
        rvGames.adapter = gamesAdapter

        gamesHeader.head_title_text.text = "AKTYWNE GRY"
        friendsHeader.head_title_text.text = "ZNAJOMI"

        checkGamesList {
            getGames()
        }

        checkFriendsList {
            getFriends()
        }

        return rootView
    }

    private fun getFriends() {

        friendsListener = dbFriends.addSnapshotListener(EventListener<QuerySnapshot> { doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc != null) {

                for (it in doc.documentChanges){

                    if(it.type == DocumentChange.Type.REMOVED){

                        rvFriends.itemAnimator = LandingAnimator()

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

                        rvFriends.itemAnimator = LandingAnimator()

                        if(it.document.getBoolean("favorite") == true){

                            if(updatableItems[it.document.id] != null){

                                updatableItems.remove(it.document.id)
                                addFavoriteItem(it.document.id, it.document.getBoolean("favorite")!!)

                                excitingSection.update(updatableItems.values)
                                favoriteSection.update(favoriteItems.values)

                            }

                        } else {

                            if(favoriteItems[it.document.id] != null){

                                favoriteItems.remove(it.document.id)
                                addItem(it.document.id, it.document.getBoolean("favorite")!!)

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

    private fun getGames() {

        gamesListener = dbGames.whereEqualTo("${UserUtil.user.uid}-id", UserUtil.user.uid).addSnapshotListener(EventListener<QuerySnapshot> { doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc != null) {

                for (it in doc.documentChanges){

                    if(it.type == DocumentChange.Type.REMOVED){

                        rvGames.itemAnimator = LandingAnimator()

                        if(noGamesItems[friendId(UserUtil.user.uid, it.document.id)] != null){

                            noGamesItems.remove(friendId(UserUtil.user.uid, it.document.id))
                            noGamesSection.update(noGamesItems.values)

                        } else {

                            gamesItems.remove(friendId(UserUtil.user.uid, it.document.id))
                            gamesSection.update(gamesItems.values)

                        }

                        checkGamesList { }

                    }

                    if(it.type == DocumentChange.Type.MODIFIED){

                        rvGames.itemAnimator = LandingAnimator()

                        if(it.document.getBoolean("${UserUtil.user.uid}-turn") == true && it.document.getBoolean("${friendId(UserUtil.user.uid, it.document.id)}-turn") == true) {

                            if (noGamesItems[friendId(UserUtil.user.uid, it.document.id)] != null) {

                                noGamesItems.remove(friendId(UserUtil.user.uid, it.document.id))
                                noGamesSection.update(noGamesItems.values)

                            } else {

                                gamesItems.remove(friendId(UserUtil.user.uid, it.document.id))
                                gamesSection.update(gamesItems.values)

                            }

                            checkGamesList { }
                            return@EventListener
                        }


                        if(it.document.getBoolean("${UserUtil.user.uid}-turn") == true){

                            if(noGamesItems[friendId(UserUtil.user.uid, it.document.id)] != null){

                                noGamesItems.remove(friendId(UserUtil.user.uid, it.document.id))
                                addGamesItem(friendId(UserUtil.user.uid, it.document.id), it.document.getBoolean("${UserUtil.user.uid}-turn")!!)

                                checkGamesList {
                                    noGamesSection.update(noGamesItems.values)
                                    gamesSection.update(gamesItems.values)
                                }

                            } else {

                                addGamesItem(friendId(UserUtil.user.uid, it.document.id), it.document.getBoolean("${UserUtil.user.uid}-turn")!!)
                                checkGamesList {
                                    gamesSection.update(gamesItems.values)
                                }

                            }

                        } else {

                            if(gamesItems[friendId(UserUtil.user.uid, it.document.id)] != null){

                                gamesItems.remove(friendId(UserUtil.user.uid, it.document.id))
                                addNoGamesItem(friendId(UserUtil.user.uid, it.document.id), it.document.getBoolean("${UserUtil.user.uid}-turn")!!)

                                checkGamesList {
                                    gamesSection.update(gamesItems.values)
                                    noGamesSection.update(noGamesItems.values)
                                }

                            } else {

                                addNoGamesItem(friendId(UserUtil.user.uid, it.document.id), it.document.getBoolean("${UserUtil.user.uid}-turn")!!)
                                checkGamesList {
                                    noGamesSection.update(noGamesItems.values)
                                }

                            }

                        }

                    }

                    if(it.type == DocumentChange.Type.ADDED){

                        if(it.document.getBoolean("${UserUtil.user.uid}-turn") == true && it.document.getBoolean("${friendId(UserUtil.user.uid, it.document.id)}-turn") == true){
                            return@EventListener
                        }

                        if(it.document.getBoolean("${UserUtil.user.uid}-turn") == true) {

                            addGamesItem(friendId(UserUtil.user.uid, it.document.id), it.document.getBoolean("${UserUtil.user.uid}-turn")!!)
                            checkGamesList {
                                gamesSection.update(gamesItems.values)
                            }

                        } else {

                            addNoGamesItem(friendId(UserUtil.user.uid, it.document.id), it.document.getBoolean("${UserUtil.user.uid}-turn")!!)
                            checkGamesList {
                                noGamesSection.update(noGamesItems.values)
                            }

                        }

                    }

                }


            }

        })
    }

    fun friendId(yourID: String, gameID: String): String {

        val bufferId = yourID.toCharArray()

        val idSize = bufferId.size

        val bufferFriend = gameID.substring(idSize)

        if(bufferFriend == yourID){

            return gameID.substring(0, idSize)

        } else {

            return bufferFriend

        }

    }

    fun checkFriendsList(onComplete: () -> Unit){

        if(!updatableItems.isEmpty() || !favoriteItems.isEmpty()){

            image_friends.visibility = View.GONE
            text_none_friends.visibility = View.GONE

            onComplete()

        } else {

            text_none_friends.visibility = View.VISIBLE
            image_friends.visibility = View.VISIBLE

            onComplete()

        }

    }

    fun checkGamesList(onComplete: () -> Unit){

        if(!gamesItems.isEmpty() || !noGamesItems.isEmpty()){

            gamesHeader.visibility = View.VISIBLE

            onComplete()

        } else {

            gamesHeader.visibility = View.GONE

            onComplete()

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        friendsListener.remove()
        gamesListener.remove()
    }

}
