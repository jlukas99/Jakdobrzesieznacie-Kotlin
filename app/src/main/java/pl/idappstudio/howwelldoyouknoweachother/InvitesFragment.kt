@file:Suppress("UNREACHABLE_CODE")

package pl.idappstudio.howwelldoyouknoweachother


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ImageButton
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList


class InvitesFragment : Fragment() {

    var btn_hide: ImageButton? = null
    var recyclerView: RecyclerView? = null
    var isHide = false

    val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val db = FirebaseFirestore.getInstance().collection("users").document(firebaseUser).collection("invites")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_invites, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        btn_hide = rootView.findViewById(R.id.btn_hide_przychodzace)

        recyclerView?.layoutManager = LinearLayoutManager(context)

        recyclerView?.hasFixedSize()
        getInvites()

        recyclerView?.setHasFixedSize(true)

        btn_hide?.setOnClickListener {

            animation()

        }

        return rootView
    }

    private fun partItemClicked(partItem : InviteItem) {
        Toast.makeText(context, "Clicked: ${partItem.name}", Toast.LENGTH_LONG).show()
    }

    private fun getInvites() {

        val list = ArrayList<InviteItem>()
        list.clear()

//        db.get().addOnSuccessListener { task ->
//            if(!task.isEmpty){
//
//                for(doc in task){
//
//                    list.add(InviteItem(doc.getString("id").toString(), doc.getString("name").toString(), doc.getString("image").toString()))
//
//                }
                for(i in 1..100){
                    list.add(InviteItem("1", "siema $i", "828570640671342"))
                }
                onComplete(list)

//            }
//        }

    }

    private fun onComplete(invite: ArrayList<InviteItem>){

        recyclerView?.adapter = InviteAdapter(invite) { partItem : InviteItem -> partItemClicked(partItem) }

    }

    private fun animation(){
        if(isHide){

            val rotate = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotate.duration = 400
            rotate.fillAfter = true
            rotate.interpolator = LinearInterpolator()

            btn_hide?.startAnimation(rotate)

            val va = ValueAnimator.ofInt(0, recyclerView?.measuredHeight!!)
            va.duration = 400
            va.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                recyclerView?.layoutParams?.height = value
                recyclerView?.requestLayout()
            }

            va.start()

            isHide = false

        } else {

            val rotate = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotate.duration = 400
            rotate.fillAfter = true
            rotate.interpolator = LinearInterpolator()

            btn_hide?.startAnimation(rotate)

            val va = ValueAnimator.ofInt(recyclerView?.measuredHeight!!, 0)
            va.duration = 400
            va.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                recyclerView?.layoutParams?.height = value
                recyclerView?.requestLayout()
            }

            va.start()

            isHide = true

        }
    }

    companion object {
        fun newInstance(): InvitesFragment = InvitesFragment()
    }


}
