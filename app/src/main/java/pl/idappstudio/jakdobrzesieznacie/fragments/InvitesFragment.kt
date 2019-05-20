package pl.idappstudio.jakdobrzesieznacie.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import kotlinx.android.synthetic.main.header_title_item.view.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.adapter.InviteAdapater
import pl.idappstudio.jakdobrzesieznacie.adapter.SearchAdapater
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickInviteListener
import pl.idappstudio.jakdobrzesieznacie.model.FriendItem
import pl.idappstudio.jakdobrzesieznacie.model.UserData
import pl.idappstudio.jakdobrzesieznacie.util.GlideUtil
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

@Suppress("DEPRECATION")
class InvitesFragment : androidx.fragment.app.Fragment(), ClickInviteListener {

    override fun onClickSendInvite(user: UserData, boolean: Boolean) {

        if (boolean) {

            val view = view?.rootView

            if(view != null) {

                SnackBarUtil.setActivitySnack(
                    resources.getString(R.string.sended_invite, user.name),
                    ColorSnackBar.SUCCES,
                    R.drawable.ic_forward,
                    view) { }
            }

        } else {

            val view = view?.rootView

            if(view != null) {

                SnackBarUtil.setActivitySnack(
                    resources.getString(R.string.sended_invite_error),
                    ColorSnackBar.ERROR,
                    R.drawable.ic_error_,
                    view) { }
            }

        }

    }

    override fun onClickInvite(user: UserData) {

        searchInput.text.clear()
        getSearchResult()

        dialogAdd.isClickable = true
        dialogDelete.isClickable = true

        GlideUtil.setImage(user.fb, user.image, activity!!, dialogProfile) {

            dialogName.text = user.name

            dialogAdd.setOnClickListener {

                searchItems.clear()
                searchSection.update(searchItems.values)

                dialogAdd.isClickable = false
                dialogDelete.isClickable = false

                UserUtil.addFriend(user.uid, false) {

                    if (it) {

                        UserUtil.delInvite(user.uid) {

                            closeDialog()

                            val view = view?.rootView

                            if(view != null) {
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

                        if(view != null) {
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

                    if(view != null) {
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

    private var invitesListener: ListenerRegistration? = null
    private var searchListener: ListenerRegistration? = null

    private lateinit var dialogName: TextView
    private lateinit var dialogProfile: CircleImageView
    private lateinit var dialogAdd: MaterialButton
    private lateinit var dialogDelete: MaterialButton

    private lateinit var textSearch: TextView
    private lateinit var imageSearch: ImageView

    private lateinit var searchInput: EditText
    private lateinit var countSearch: TextView

    private lateinit var inviteHeader: ConstraintLayout
    private lateinit var searchHeader: ConstraintLayout

    private lateinit var recyclerInvite: RecyclerView
    private lateinit var recyclerSearch: RecyclerView

    val mHandler = Handler()
    val searchRunnable = Runnable {
        getSearchResult()
    }

    private val inviteSection = Section()
    private val searchSection = Section()

    private val dbInvites =
        FirebaseFirestore.getInstance().collection("users").document(UserUtil.user.uid).collection("invites")
    private val db = FirebaseFirestore.getInstance().collection("users")

    private val inviteAdapter = GroupAdapter<ViewHolder>()
    private val searchAdapter = GroupAdapter<ViewHolder>()

    private val inviteItems = HashMap<String, InviteAdapater>()
    private val searchItems = HashMap<String, SearchAdapater>()

    private fun addInviteItem(uid: String, b: Boolean): HashMap<String, InviteAdapater> {
        inviteItems[uid] = InviteAdapater(FriendItem(uid, b), this.context!!, this)
        return inviteItems
    }

    private fun addSearchItem(uid: String, b: Boolean): HashMap<String, SearchAdapater> {
        searchItems[uid] = SearchAdapater(FriendItem(uid, b), this.context!!, this)
        return searchItems
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inviteSection.setHideWhenEmpty(true)
        searchSection.setHideWhenEmpty(true)

        inviteAdapter.add(0, inviteSection)
        searchAdapter.add(0, searchSection)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_invites, container, false)

        textSearch = rootView.findViewById(R.id.text_none_invite2)
        imageSearch = rootView.findViewById(R.id.image_none_invite2)

        recyclerInvite = rootView.findViewById(R.id.recyclerView)
        recyclerSearch = rootView.findViewById(R.id.recyclerSearch)

        inviteHeader = rootView.findViewById(R.id.inviteHeader)
        searchHeader = rootView.findViewById(R.id.searchHeader)

        searchInput = rootView.findViewById(R.id.searchInput)
        countSearch = rootView.findViewById(R.id.searchCount)

        imageSearch.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

                mHandler.removeCallbacks(searchRunnable)
                mHandler.postDelayed(searchRunnable, 800)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

        })

        val rvFriendsLLM = LinearLayoutManager(this.context)
        val rvGamesLLM = LinearLayoutManager(this.context)

        rvFriendsLLM.orientation = RecyclerView.VERTICAL
        rvGamesLLM.orientation = RecyclerView.HORIZONTAL

        recyclerInvite.layoutManager = rvGamesLLM
        recyclerSearch.layoutManager = rvFriendsLLM

        recyclerInvite.itemAnimator = SlideInDownAnimator()
        recyclerSearch.itemAnimator = SlideInDownAnimator()

        inviteHeader.head_title_text.text = resources.getString(R.string.requests_for_adding)
        searchHeader.head_title_text.text = resources.getString(R.string.search)

        recyclerInvite.adapter = inviteAdapter
        recyclerSearch.adapter = searchAdapter

        checkInviteList {
            getInvites()
        }

        getSearchResult()

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

                        recyclerInvite.itemAnimator = LandingAnimator()

                        inviteItems.remove(it.document.id)
                        inviteSection.update(inviteItems.values)

                        checkInviteList { }

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

    private fun setCountResult() {

        setNoneResults {
            countSearch.text = searchItems.size.toString()
        }

    }

    private fun setNoneResults(onComplete: () -> Unit) {

        if (searchItems.size < 1) {

            textSearch.text = resources.getString(R.string.no_search_results)
            imageSearch.setImageResource(R.drawable.ic_add_friends_icon)

            textSearch.visibility = View.VISIBLE
            imageSearch.visibility = View.VISIBLE

            onComplete()

        } else {

            textSearch.visibility = View.GONE
            imageSearch.visibility = View.GONE
            onComplete()

        }

    }

    private fun setInfoSearch(onComplete: () -> Unit) {

        setCountResult()

        if (searchInput.text.isNullOrBlank()) {

            textSearch.visibility = View.VISIBLE
            imageSearch.visibility = View.VISIBLE

            textSearch.text = resources.getString(R.string.enter_friends_nick_text)
            imageSearch.setImageResource(R.drawable.ic_forward)

            onComplete()

        } else {

            setNoneResults {
                onComplete()
            }

        }

    }

    private fun checkFriendsInvite(uid: String, onComplete: (Int) -> Unit) {

        db.document(uid).collection("invites").document(UserUtil.user.uid).get().addOnSuccessListener {

            if (it != null) {

                if (it.exists()) {

                    onComplete(3)

                } else {

                    onComplete(2)

                }

            } else {

                onComplete(2)

            }

        }

    }

    private fun getFriendsList(uid: String, onComplete: (Int) -> Unit) {

        db.document(UserUtil.user.uid).collection("friends").document(uid).get().addOnSuccessListener {

            if (it != null) {

                if (it.exists()) {

                    onComplete(0)

                } else {

                    db.document(UserUtil.user.uid).collection("invites").document(uid).get().addOnSuccessListener {it3 ->

                        if (it3 != null) {

                            if (it3.exists()) {

                                onComplete(1)

                            } else {

                                checkFriendsInvite(uid) {it2 ->

                                    onComplete(it2)

                                }

                            }

                        } else {

                            checkFriendsInvite(uid) {it1 ->

                                onComplete(it1)

                            }

                        }

                    }

                }

            } else {

                db.document(UserUtil.user.uid).collection("invites").document(uid).get().addOnSuccessListener {it3 ->

                    if (it3 != null) {

                        if (it3.exists()) {

                            onComplete(1)

                        } else {

                            checkFriendsInvite(uid) {it2 ->

                                onComplete(it2)

                            }

                        }

                    } else {

                        checkFriendsInvite(uid) {it1 ->

                            onComplete(it1)

                        }

                    }

                }

            }

        }

    }

    private fun getSearchResult() {

        if (!searchInput.text.isNullOrBlank()) {

            if (searchListener != null) {
                searchListener?.remove()
            }

            searchItems.clear()
            searchSection.update(searchItems.values)

            setInfoSearch {}

            searchListener = db.orderBy("name").startAt(searchInput.text.toString().trim()).endAt(searchInput.text.toString().trim() + "\uf8ff").whereEqualTo("public", true)
                .addSnapshotListener(EventListener<QuerySnapshot> { doc, e ->

                    if (e != null) {
                        return@EventListener
                    }

                    if (doc != null) {

                        for (it in doc.documentChanges) {

                            if (it.type == DocumentChange.Type.REMOVED) {

                                recyclerSearch.itemAnimator = LandingAnimator()

                                searchItems.remove(it.document.id)
                                searchSection.update(searchItems.values)

                                setCountResult()

                            }

                            if (it.type == DocumentChange.Type.ADDED) {

                                if (it.document.id != UserUtil.user.uid) {

                                    getFriendsList(it.document.id) { it2 ->

                                        if (it2 == 3) {

                                            addSearchItem(it.document.id, false)
                                            searchSection.update(searchItems.values)

                                        } else if (it2 == 2) {

                                            addSearchItem(it.document.id, true)
                                            searchSection.update(searchItems.values)

                                        }

                                        setCountResult()


                                    }

                                }

                            }

                        }


                    }

                })

        } else {

            setInfoSearch {}

        }
    }

    private fun checkInviteList(onComplete: () -> Unit) {

        if (inviteItems.isNotEmpty()) {

            inviteHeader.visibility = View.VISIBLE

            onComplete()

        } else {

            inviteHeader.visibility = View.GONE

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
        searchListener?.remove()
        invitesListener?.remove()
    }

}
