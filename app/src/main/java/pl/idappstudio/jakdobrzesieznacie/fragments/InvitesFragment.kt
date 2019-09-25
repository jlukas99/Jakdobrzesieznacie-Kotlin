package pl.idappstudio.jakdobrzesieznacie.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity.Companion.viewPager2
import pl.idappstudio.jakdobrzesieznacie.adapter.SearchAdapater
import pl.idappstudio.jakdobrzesieznacie.enums.ColorSnackBar
import pl.idappstudio.jakdobrzesieznacie.interfaces.ClickSendInviteListener
import pl.idappstudio.jakdobrzesieznacie.model.FriendItem
import pl.idappstudio.jakdobrzesieznacie.model.UserData
import pl.idappstudio.jakdobrzesieznacie.util.SnackBarUtil
import pl.idappstudio.jakdobrzesieznacie.util.UserUtil

@Suppress("DEPRECATION")
class InvitesFragment : androidx.fragment.app.Fragment(), ClickSendInviteListener {

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

    private var searchListener: ListenerRegistration? = null

    private lateinit var progressBar: ProgressBar

    private lateinit var textSearch: TextView
    private lateinit var imageSearch: ImageView

    private lateinit var searchInput: EditText
    private lateinit var countSearch: TextView

    private lateinit var back: ImageButton

    private lateinit var recyclerSearch: RecyclerView

    private val searchSection = Section()

    private val db = FirebaseFirestore.getInstance().collection("users")

    private val searchAdapter = GroupAdapter<ViewHolder>()

    private val searchItems = HashMap<String, SearchAdapater>()

    private fun addSearchItem(uid: String, b: Boolean): HashMap<String, SearchAdapater> {
        searchItems[uid] = SearchAdapater(FriendItem(uid, b), this.context!!, this)
        return searchItems
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchAdapter.add(0, searchSection)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_invites, container, false)

        val imm: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        textSearch = rootView.findViewById(R.id.text_none_invite2)
        imageSearch = rootView.findViewById(R.id.image_none_invite2)

        back = rootView.findViewById(R.id.back2)

        progressBar = rootView.findViewById(R.id.progressBar)

        recyclerSearch = rootView.findViewById(R.id.recyclerSearch)

        searchInput = rootView.findViewById(R.id.searchInput)
        countSearch = rootView.findViewById(R.id.searchCount)

        imageSearch.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )

        back.setOnClickListener {

            viewPager2.setCurrentItem(1, true)

        }

        searchInput.setOnEditorActionListener { _, p1, _ ->

            if (p1 == EditorInfo.IME_ACTION_SEARCH) {

                getSearchResult()
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }

            false
        }

        val rvFriendsLLM = LinearLayoutManager(this.context)

        rvFriendsLLM.orientation = RecyclerView.VERTICAL
        recyclerSearch.layoutManager = rvFriendsLLM

        recyclerSearch.itemAnimator = SlideInDownAnimator()

        recyclerSearch.adapter = searchAdapter

        return rootView
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

        imageSearch.visibility = View.GONE

        textSearch.text = resources.getString(R.string.searching)

        progressBar.visibility = View.VISIBLE
        textSearch.visibility = View.VISIBLE

        searchItems.clear()
        searchSection.update(searchItems.values)
        recyclerSearch.smoothScrollToPosition(0)

        countSearch.text = searchItems.size.toString()

        if (!searchInput.text.isNullOrBlank()) {

            if (searchListener != null) {
                searchListener?.remove()
            }

            var i = 0

            searchListener = db.orderBy("name").startAt(searchInput.text.toString().trim()).endAt(searchInput.text.toString().trim() + "\uf8ff").whereEqualTo("public", true)
                .addSnapshotListener(EventListener<QuerySnapshot> { doc, e ->

                    if (e != null) {
                        return@EventListener
                    }

                    if (doc != null) {

                        if (doc.isEmpty) {

                            progressBar.visibility = View.INVISIBLE

                            textSearch.text = resources.getString(R.string.no_search_results)
                            imageSearch.setImageResource(R.drawable.ic_add_friends_icon)

                            textSearch.visibility = View.VISIBLE
                            imageSearch.visibility = View.VISIBLE

                            countSearch.text = "0"

                        }

                        for (it in doc.documentChanges) {

                            if (it.type == DocumentChange.Type.REMOVED) {

                                recyclerSearch.itemAnimator = LandingAnimator()

                                searchItems.remove(it.document.id)
                                searchSection.update(searchItems.values)

                            }

                            if (it.type == DocumentChange.Type.ADDED) {

                                if (it.document.id != UserUtil.user.uid) {

                                    getFriendsList(it.document.id) { it2 ->

                                        when (it2) {
                                            3 -> {

                                                addSearchItem(it.document.id, false)
                                                searchSection.update(searchItems.values)

                                            }
                                            2 -> {

                                                addSearchItem(it.document.id, true)
                                                searchSection.update(searchItems.values)

                                            }
                                            else -> i++
                                        }

                                        countSearch.text = searchItems.size.toString()

                                        if (doc.size() - i == searchItems.size) {

                                            recyclerSearch.smoothScrollToPosition(0)
                                            progressBar.visibility = View.INVISIBLE
                                            textSearch.visibility = View.INVISIBLE

                                        }

                                    }

                                } else {

                                    i++

                                    if (doc.size() - i == searchItems.size && searchItems.size != 0) {

                                        recyclerSearch.smoothScrollToPosition(0)
                                        progressBar.visibility = View.INVISIBLE
                                        textSearch.visibility = View.INVISIBLE

                                    } else if (doc.size() - i == 0) {

                                        progressBar.visibility = View.INVISIBLE

                                        textSearch.text = resources.getString(R.string.no_search_results)
                                        imageSearch.setImageResource(R.drawable.ic_add_friends_icon)

                                        textSearch.visibility = View.VISIBLE
                                        imageSearch.visibility = View.VISIBLE

                                    }

                                }

                            }

                        }


                    } else {

                        progressBar.visibility = View.INVISIBLE

                        textSearch.text = resources.getString(R.string.no_search_results)
                        imageSearch.setImageResource(R.drawable.ic_add_friends_icon)

                        textSearch.visibility = View.VISIBLE
                        imageSearch.visibility = View.VISIBLE

                    }

                })

        } else {

            progressBar.visibility = View.INVISIBLE

            textSearch.visibility = View.VISIBLE
            imageSearch.visibility = View.VISIBLE

            textSearch.text = resources.getString(R.string.enter_friends_nick_text)
            imageSearch.setImageResource(R.drawable.ic_forward)

            countSearch.text = "0"

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        searchListener?.remove()
    }

}