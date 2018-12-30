@file:Suppress("UNREACHABLE_CODE")

package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList
import android.view.animation.LinearInterpolator
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import android.support.v7.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import pl.idappstudio.howwelldoyouknoweachother.adapter.InviteAdapterFirestore
import pl.idappstudio.howwelldoyouknoweachother.model.InviteItem
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.adapter.SearchAdapter
import pl.idappstudio.howwelldoyouknoweachother.model.UserData
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil

class InvitesFragment : Fragment() {

    private lateinit var currentUser: UserData

    private lateinit var btnHide: ImageButton
    private lateinit var btnHide2: ImageButton

    private lateinit var loading: SpinKitView
    private lateinit var loading2: SpinKitView

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerSearch: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var countSearch: TextView

    private lateinit var adapter: InviteAdapterFirestore

    private val list1 = ArrayList<InviteItem>()
    private val list2 = ArrayList<InviteItem>()
    private val list3 = ArrayList<InviteItem>()

    private var isHide = false
    private var isHide2 = false

    private val db = FirebaseFirestore.getInstance().collection("users")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_invites, container, false)

        FirestoreUtil.getCurrentUser { currentUser = it }

        loading = rootView.findViewById(R.id.loading)
        loading2 = rootView.findViewById(R.id.loading2)

        btnHide = rootView.findViewById(R.id.btn_hide_przychodzace)
        btnHide2 = rootView.findViewById(R.id.btn_hide_przychodzace2)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerSearch = rootView.findViewById(R.id.recyclerSearch)

        searchInput = rootView.findViewById(R.id.searchInput)
        countSearch = rootView.findViewById(R.id.searchCount)

        recyclerSearch.layoutManager = LinearLayoutManager(context)

        recyclerSearch.setHasFixedSize(true)

        recyclerSearch.adapter = null

        btnHide.setOnClickListener {

            animation(true)

        }

        btnHide2.setOnClickListener {

            animation(false)

        }

        searchInput.setOnClickListener {

            if(!isHide){
                animation(true)
            }
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

                getSearch()

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { return }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                loading2.visibility = View.VISIBLE

            }

        })

        getInvites()
        getUsers()

        return rootView
    }

    private fun getInvites() {

        loading.visibility = View.VISIBLE

        val query: Query = db.document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("invites").orderBy("name", Query.Direction.ASCENDING)

        val options:FirestoreRecyclerOptions<InviteItem> = FirestoreRecyclerOptions.Builder<InviteItem>().setQuery(query, InviteItem::class.java).build()

        adapter = InviteAdapterFirestore(options)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        recyclerView.requestLayout()
        loading.visibility = View.GONE

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {

                if(direction == ItemTouchHelper.LEFT){
                    adapter.deleteItem(viewHolder.adapterPosition)
                    return
                }

                if(direction == ItemTouchHelper.RIGHT){
                    adapter.addFriend(viewHolder.adapterPosition)
                    return
                }

                }
            }).attachToRecyclerView(recyclerView)

    }

    private fun getUsers(){

        list2.clear()

        db.get().addOnSuccessListener { task ->
            if(!task.isEmpty){

                for(doc in task){

                        list2.add(
                            InviteItem(
                                doc.getString("uid").toString(),
                                doc.getString("name").toString(),
                                doc.getString("image").toString(),
                                doc.getBoolean("fb")!!
                            )
                        )

                    }
                }

            }

    }

    private fun getSearch() {

        list3.clear()

        if(list2.isEmpty()){
            onComplete2(list3)
            return
        }

        if(!searchInput.text.toString().isBlank()){
            for(i in list2){
                    if(i.name!!.toUpperCase().contains(searchInput.text.toString().toUpperCase()) and !searchInput.text.toString().toUpperCase().contains(currentUser.name.toUpperCase())){
                        list3.add(i)
                    }
            }
        }

        onComplete2(list3)

    }

    private fun onComplete2(invite: ArrayList<InviteItem>)  {

        recyclerSearch.adapter = SearchAdapter(invite)
        recyclerSearch.adapter!!.notifyDataSetChanged()

        loading2.visibility = View.GONE

        countSearch.text = invite.size.toString()

    }

    private fun animation(b: Boolean){
        if(b) {
            if (isHide) {

                val rotate = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 400
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()

                btnHide.startAnimation(rotate)

                isHide = false

            } else {

                val rotate = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 400
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()

                btnHide.startAnimation(rotate)

                isHide = true

            }
        } else {
            if (isHide2) {

                val rotate = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 400
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()

                btnHide2.startAnimation(rotate)

                isHide2 = false

            } else {

                val rotate = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 400
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()

                btnHide2.startAnimation(rotate)

                isHide2 = true

            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        fun newInstance(): InvitesFragment = InvitesFragment()
    }

}
