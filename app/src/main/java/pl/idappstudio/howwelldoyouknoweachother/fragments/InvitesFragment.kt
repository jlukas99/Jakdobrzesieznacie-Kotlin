@file:Suppress("UNREACHABLE_CODE")

package pl.idappstudio.howwelldoyouknoweachother.fragments

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.github.ybq.android.spinkit.SpinKitView
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.adapter.InviteAdapterFirestore
import pl.idappstudio.howwelldoyouknoweachother.adapter.SearchAdapterFirestore
import pl.idappstudio.howwelldoyouknoweachother.interfaces.CountInterface
import pl.idappstudio.howwelldoyouknoweachother.model.InviteItem
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil

class InvitesFragment : Fragment(), CountInterface {

    override fun click(s: String, b: Boolean, name: String, image: Int) {}

    private lateinit var btnHide: ImageButton
    private lateinit var btnHide2: ImageButton

    private lateinit var loading: SpinKitView
    private lateinit var loading2: SpinKitView

    private lateinit var text_invite: TextView
    private lateinit var image_invite: ImageView

    private lateinit var text_search: TextView
    private lateinit var image_search: ImageView

    private lateinit var searchInput: EditText
    private lateinit var countSearch: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerSearch: RecyclerView

    private lateinit var adapter: InviteAdapterFirestore
    private lateinit var adapter2: SearchAdapterFirestore

    private var friendsList = ArrayList<String>()

    private var isHide = false
    private var isHide2 = false

    private val db = FirebaseFirestore.getInstance()

    companion object {

        fun newInstance(): InvitesFragment = InvitesFragment()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_invites, container, false)

        loading = rootView.findViewById(R.id.loading)
        loading2 = rootView.findViewById(R.id.loading2)

        text_invite = rootView.findViewById(R.id.text_none_invite)
        image_invite = rootView.findViewById(R.id.image_none_invite)

        text_search = rootView.findViewById(R.id.text_none_invite2)
        image_search = rootView.findViewById(R.id.image_none_invite2)

        btnHide = rootView.findViewById(R.id.btn_hide_przychodzace)
        btnHide2 = rootView.findViewById(R.id.btn_hide_przychodzace2)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerSearch = rootView.findViewById(R.id.recyclerSearch)

        searchInput = rootView.findViewById(R.id.searchInput)
        countSearch = rootView.findViewById(R.id.searchCount)

        image_invite.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        image_search.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

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

            }

        })

        getFriends {

            getInvites()
            getSearch()

        }

        return rootView
    }

    private fun getFriends(onComplete: () -> Unit){

        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("friends").get().addOnSuccessListener {

            var i = 0

            friendsList.clear()

            friendsList.add(FirebaseAuth.getInstance().currentUser?.uid.toString())

            if(it.isEmpty){
                onComplete()
            }

            for(doc in it.documents){

                friendsList.add(doc.id)

                i++

                if(i == it.size()){

                    onComplete()

                }

            }

        }

    }

    private fun getInvites() {

        loading.visibility = View.VISIBLE

        val query: Query = db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("invites")

        val options: FirestoreRecyclerOptions<InviteItem> = FirestoreRecyclerOptions.Builder<InviteItem>().setQuery(query, InviteItem::class.java).setLifecycleOwner(this).build()

        adapter = InviteAdapterFirestore(options, this)
        adapter.setRV(recyclerView)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        adapter.startListening()

    }

    private fun getSearch() {

        val query: Query = if(searchInput.text.isNullOrBlank()){

            db.collection("users").orderBy("name", Query.Direction.ASCENDING)

        } else {

            db.collection("users").whereEqualTo("name", searchInput.text.toString())

        }

        val options:FirestoreRecyclerOptions<InviteItem> = FirestoreRecyclerOptions.Builder<InviteItem>().setQuery(query, InviteItem::class.java).setLifecycleOwner(this).build()

        adapter2 = SearchAdapterFirestore(options, this, friendsList)
        adapter2.setRV(recyclerSearch)

        recyclerSearch.setHasFixedSize(true)
        recyclerSearch.layoutManager = LinearLayoutManager(context)
        recyclerSearch.adapter = adapter2

        adapter2.startListening()

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

    override fun reload(){

        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("invites").get().addOnCompleteListener { task->

            if(!task.result?.isEmpty!!){

                recyclerView.layoutParams.height = 0
                recyclerView.requestLayout()

                loading.visibility = View.GONE

                text_invite.visibility = View.GONE
                image_invite.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

            } else {

                recyclerView.layoutParams.height = 240
                recyclerView.requestLayout()

                loading.visibility = View.GONE

                recyclerView.visibility = View.INVISIBLE
                text_invite.visibility = View.VISIBLE
                image_invite.visibility = View.VISIBLE

            }

        }

    }

    override fun count() {

        if(searchInput.text.isNullOrBlank()){

            db.collection("users").orderBy("name", Query.Direction.ASCENDING).get().addOnCompleteListener { task ->

                if(!task.result?.isEmpty!!){

                    recyclerSearch.layoutParams.height = 0
                    recyclerSearch.requestLayout()

                    countSearch.text = task.result!!.size().toString()

                    loading2.visibility = View.GONE

                    text_search.visibility = View.GONE
                    image_search.visibility = View.GONE
                    recyclerSearch.visibility = View.VISIBLE

                } else {

                    recyclerSearch.requestLayout()

                    countSearch.text = "0"

                    loading2.visibility = View.GONE

                    recyclerSearch.visibility = View.INVISIBLE
                    text_search.visibility = View.VISIBLE
                    image_search.visibility = View.VISIBLE

                }
            }

        } else {

            db.collection("users").whereEqualTo("name", searchInput.text.toString()).get().addOnCompleteListener { task ->

                if(!task.result?.isEmpty!!){

                    recyclerSearch.layoutParams.height = 0
                    recyclerSearch.requestLayout()

                    countSearch.text = task.result!!.size().toString()

                    loading2.visibility = View.GONE

                    text_search.visibility = View.GONE
                    image_search.visibility = View.GONE
                    recyclerSearch.visibility = View.VISIBLE

                } else {

                    recyclerSearch.requestLayout()

                    countSearch.text = "0"

                    loading2.visibility = View.GONE

                    recyclerSearch.visibility = View.INVISIBLE
                    text_search.visibility = View.VISIBLE
                    image_search.visibility = View.VISIBLE

                }
            }

        }

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        adapter2.stopListening()
    }

    override fun onResume() {
        super.onResume()
        FirestoreUtil.initialize()
    }

}
