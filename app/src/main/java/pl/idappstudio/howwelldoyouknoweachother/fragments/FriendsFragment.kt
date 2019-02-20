package pl.idappstudio.howwelldoyouknoweachother.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.adapter.FriendsAdapterFirestore
import pl.idappstudio.howwelldoyouknoweachother.adapter.GamesAdapterFirestore
import pl.idappstudio.howwelldoyouknoweachother.interfaces.CountInterface
import pl.idappstudio.howwelldoyouknoweachother.model.FriendsItem
import pl.idappstudio.howwelldoyouknoweachother.util.FirestoreUtil


class FriendsFragment : Fragment(), CountInterface {

    override fun click(s: String, b: Boolean, name: String, image: Int) {}

    private lateinit var image_round: ImageView
    private lateinit var image_friends: ImageView

    private lateinit var text_none_friends: TextView
    private lateinit var text_none_round: TextView

    private lateinit var btn_invite: Button

    private lateinit var rvRound: RecyclerView
    private lateinit var rvFriends: RecyclerView

    private lateinit var loadingFriends: SpinKitView
    private lateinit var loadingRound: SpinKitView

    private lateinit var adapterRound: GamesAdapterFirestore
    private var adapterFriends: FriendsAdapterFirestore? = null

    private val db = FirebaseFirestore.getInstance().collection("users")
    private val dbGames = FirebaseFirestore.getInstance().collection("games")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView:View = inflater.inflate(R.layout.fragment_friends, container, false)

        image_round = rootView.findViewById(R.id.image_none_round)
        image_friends = rootView.findViewById(R.id.image_none_friends)

        text_none_friends = rootView.findViewById(R.id.text_none_friends)
        text_none_round = rootView.findViewById(R.id.text_none_round)

        rvRound = rootView.findViewById(R.id.rv_round)
        rvFriends = rootView.findViewById(R.id.rv_friends)

        loadingRound = rootView.findViewById(R.id.loading_round)
        loadingFriends = rootView.findViewById(R.id.loading_friends)

        image_round.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        image_friends.setColorFilter(
            ContextCompat.getColor(
                rootView.context, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        getFriends()
//        getGame()

        return rootView
    }

    private fun getFriends() {

        loadingFriends.visibility = View.VISIBLE

        val query: Query = db.document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("friends").orderBy("favorite", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<FriendsItem> = FirestoreRecyclerOptions.Builder<FriendsItem>().setQuery(query, FriendsItem::class.java).setLifecycleOwner(this).build()

        adapterFriends = FriendsAdapterFirestore(options, this, activity)
        adapterFriends?.setRV(rvFriends)

        rvFriends.setHasFixedSize(true)
        rvFriends.layoutManager = LinearLayoutManager(context)
        rvFriends.adapter = adapterFriends

    }

//    private fun getGame() {
//
//        loadingRound.visibility = View.VISIBLE
//
//        val query: Query = db.document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("friends")
//
//        val options: FirestoreRecyclerOptions<FriendsItem> = FirestoreRecyclerOptions.Builder<FriendsItem>().setQuery(query, FriendsItem::class.java).setLifecycleOwner(this).build()
//
//        adapterRound = GamesAdapterFirestore(options, this)
//        adapterRound.setRV(rvRound)
//
//        rvRound.setHasFixedSize(true)
//        rvRound.layoutManager = LinearLayoutManager(context)
//        rvRound.adapter = adapterRound
//
//    }

    override fun count() {

        dbGames.whereEqualTo("${FirebaseAuth.getInstance().currentUser?.uid.toString()}-turn", true)
            .get().addOnCompleteListener { task ->

                if (!task.result?.isEmpty!!) {

                    rvRound.layoutParams.height = 0
                    rvRound.requestLayout()

                    loadingRound.visibility = View.GONE

                    image_round.visibility = View.GONE
                    text_none_round.visibility = View.GONE
                    rvRound.visibility = View.VISIBLE

                } else {

                    rvRound.layoutParams.height = 240
                    rvRound.requestLayout()

                    loadingRound.visibility = View.GONE

                    rvRound.visibility = View.INVISIBLE
                    text_none_round.visibility = View.VISIBLE
                    image_round.visibility = View.VISIBLE

                }

            }

    }

    override fun reload() {

        db.document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("friends")
            .get().addOnCompleteListener { task ->

            if (!task.result?.isEmpty!!) {

                rvFriends.layoutParams.height = 0
                rvFriends.requestLayout()

                loadingFriends.visibility = View.GONE

                image_friends.visibility = View.GONE
                text_none_friends.visibility = View.GONE
                rvFriends.visibility = View.VISIBLE

            } else {

                rvFriends.layoutParams.height = 240
                rvFriends.requestLayout()

                loadingFriends.visibility = View.GONE

                rvFriends.visibility = View.INVISIBLE
                text_none_friends.visibility = View.VISIBLE
                image_friends.visibility = View.VISIBLE

            }

        }

    }

    override fun onStart() {
        super.onStart()
        if(adapterFriends != null) {
            adapterFriends?.startListening()
        }

    }

    override fun onStop() {
        super.onStop()

        if(adapterFriends != null) {
            adapterFriends?.stopListening()
        }
    }

    override fun onResume() {
        super.onResume()
        FirestoreUtil.initialize()
    }

    companion object {
        fun newInstance(): FriendsFragment = FriendsFragment()
    }


}
