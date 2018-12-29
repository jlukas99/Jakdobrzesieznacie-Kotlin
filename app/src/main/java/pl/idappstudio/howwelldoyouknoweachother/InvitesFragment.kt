@file:Suppress("UNREACHABLE_CODE")

package pl.idappstudio.howwelldoyouknoweachother


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.animation.ValueAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList
import android.view.animation.LinearInterpolator
import android.animation.LayoutTransition
import android.view.animation.OvershootInterpolator
import android.animation.PropertyValuesHolder
import android.animation.ObjectAnimator
import android.util.Log
import android.widget.Toast


class InvitesFragment : Fragment() {

    private var btnHide: ImageButton? = null
    private var btnHide2: ImageButton? = null

    private var loading: SpinKitView? = null
    private var loading2: SpinKitView? = null

    private var recyclerView: RecyclerView? = null
    private var recyclerSearch: RecyclerView? = null
    private var searchInput: EditText? = null
    private var countSearch: TextView? = null

    private val list = ArrayList<InviteItem>()
    private val list2 = ArrayList<InviteItem>()
    private val list3 = ArrayList<InviteItem>()

    private var isHide = false
    private var isHide2 = false

    private var mOriginalHeight = 0
    private var mIsViewExpanded = true

    private val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val db = FirebaseFirestore.getInstance().collection("users")

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

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerSearch?.layoutManager = LinearLayoutManager(context)

        recyclerView?.setHasFixedSize(true)
        recyclerSearch?.setHasFixedSize(true)

        recyclerView?.adapter = null
        recyclerSearch?.adapter = null

        btnHide?.setOnClickListener {

            animation(true)

        }

        btnHide2?.setOnClickListener {

            animation(false)

        }

        searchInput?.setOnClickListener {

            if(!isHide){
                animation(true)
            }
        }

        searchInput?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                getSearch()

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { return }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                loading2?.visibility = View.VISIBLE

            }

        })

        getInvites()
        getUsers()

        return rootView
    }

    private fun getInvites() {

        loading?.visibility = View.VISIBLE

        list.clear()

        db.document(firebaseUser).collection("invites").get().addOnSuccessListener { task ->
            if(!task.isEmpty){

                for(doc in task){
                    list.add(
                        InviteItem(
                            doc.getString("uid").toString(),
                            doc.getString("name").toString(),
                            doc.getString("image").toString(),
                            doc.getBoolean("fb")!!))

                }

                onComplete(list)

            }
        }

    }

    private fun getUsers(){

        list2.clear()

        db.get().addOnSuccessListener { task ->
            if(!task.isEmpty){

                for(doc in task){

                        list2.add(InviteItem(doc.getString("uid").toString(), doc.getString("name").toString(), doc.getString("image").toString(), doc.getBoolean("fb")!!))

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

        if(!searchInput?.text?.toString().isNullOrBlank()){
            for(i in list2){
                if(i.name.toUpperCase().contains(searchInput?.text.toString().toUpperCase())){
                    list3.add(i)
                }
            }
        }

        onComplete2(list3)

    }

    private fun onComplete(invite: ArrayList<InviteItem>){

            recyclerView?.adapter = InviteAdapter(invite) {pos ->

                recyclerView?.adapter?.notifyItemRemoved(pos)
                invite.removeAt(pos)
                recyclerView?.requestLayout()

            }

            recyclerView?.adapter?.notifyDataSetChanged()

            loading?.visibility = View.GONE

    }

    private fun onComplete2(invite: ArrayList<InviteItem>){

        recyclerSearch?.adapter = SearchAdapter(invite)
        recyclerSearch?.adapter?.notifyDataSetChanged()

        loading2?.visibility = View.GONE

        countSearch?.text = invite.size.toString()

    }

    private fun animation(b: Boolean){
        if(b) {
            if (isHide) {

                val rotate = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 400
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()

                btnHide?.startAnimation(rotate)

                isHide = false

            } else {

                val rotate = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 400
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()

                btnHide?.startAnimation(rotate)

                isHide = true

            }
        } else {
            if (isHide2) {

                val rotate = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 400
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()

                btnHide2?.startAnimation(rotate)

                isHide2 = false

            } else {

                val rotate = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 400
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()

                btnHide2?.startAnimation(rotate)

                isHide2 = true

            }
        }
    }

    companion object {
        fun newInstance(): InvitesFragment = InvitesFragment()
    }


}
