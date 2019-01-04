@file:Suppress("UNREACHABLE_CODE")

package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.google.firebase.firestore.FirebaseFirestore
import android.view.animation.LinearInterpolator
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.auth.FirebaseAuth
import pl.idappstudio.howwelldoyouknoweachother.adapter.InviteAdapterFirestore
import pl.idappstudio.howwelldoyouknoweachother.model.InviteItem
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.adapter.SearchAdapterFirestore

class InvitesFragment : Fragment() {

    private lateinit var btnHide: ImageButton
    private lateinit var btnHide2: ImageButton

    private lateinit var loading: SpinKitView
    private lateinit var loading2: SpinKitView

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerSearch: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var countSearch: TextView

    private lateinit var adapter: InviteAdapterFirestore
    private lateinit var adapter2: SearchAdapterFirestore

    private var isHide = false
    private var isHide2 = false

    private var query: Query? = null
    private var options:FirestoreRecyclerOptions<InviteItem>? = null

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_invites, container, false)

        loading = rootView.findViewById(R.id.loading)
        loading2 = rootView.findViewById(R.id.loading2)

        btnHide = rootView.findViewById(R.id.btn_hide_przychodzace)
        btnHide2 = rootView.findViewById(R.id.btn_hide_przychodzace2)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerSearch = rootView.findViewById(R.id.recyclerSearch)

        searchInput = rootView.findViewById(R.id.searchInput)
        countSearch = rootView.findViewById(R.id.searchCount)

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

        getInvites()
        getSearch()

        return rootView
    }

    private fun getInvites() {

        loading.visibility = View.VISIBLE

        val query: Query = db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("invites").orderBy("name", Query.Direction.ASCENDING)

        val options:FirestoreRecyclerOptions<InviteItem> = FirestoreRecyclerOptions.Builder<InviteItem>().setQuery(query, InviteItem::class.java).setLifecycleOwner(this).build()

        adapter = InviteAdapterFirestore(options)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        recyclerView.requestLayout()
        loading.visibility = View.GONE

    }

    private fun getSearch() {

        query = if(searchInput.text.isNullOrBlank()){

            db.collection("users").orderBy("name", Query.Direction.ASCENDING)

        } else {

            db.collection("users").orderBy("name", Query.Direction.ASCENDING).whereGreaterThanOrEqualTo("name", searchInput.text.toString())

        }

        options = FirestoreRecyclerOptions.Builder<InviteItem>().setQuery(query!!, InviteItem::class.java).setLifecycleOwner(this).build()

        adapter2 = SearchAdapterFirestore(options as FirestoreRecyclerOptions<InviteItem>)

        recyclerSearch.setHasFixedSize(true)
        recyclerSearch.layoutManager = LinearLayoutManager(context)
        recyclerSearch.adapter = adapter2

        recyclerSearch.requestLayout()
        loading2.visibility = View.GONE

        countSearch.text = adapter2.itemCount.toString()

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
        adapter2.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        adapter2.stopListening()
    }

    companion object {
        fun newInstance(): InvitesFragment = InvitesFragment()
    }

}
