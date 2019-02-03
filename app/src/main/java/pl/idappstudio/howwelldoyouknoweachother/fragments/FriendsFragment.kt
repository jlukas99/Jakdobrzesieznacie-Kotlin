package pl.idappstudio.howwelldoyouknoweachother.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
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
import pl.idappstudio.howwelldoyouknoweachother.model.GamesItem
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
    private lateinit var adapterFriends: FriendsAdapterFirestore

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
        getGame()

        return rootView
    }

    private fun getFriends() {

        loadingFriends.visibility = View.VISIBLE

        val query: Query = db.document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("friends").orderBy("favorite", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<FriendsItem> = FirestoreRecyclerOptions.Builder<FriendsItem>().setQuery(query, FriendsItem::class.java).setLifecycleOwner(this).build()

        adapterFriends = FriendsAdapterFirestore(options, this)
        adapterFriends.setRV(rvFriends)

        rvFriends.setHasFixedSize(true)
        rvFriends.layoutManager = LinearLayoutManager(context)
        rvFriends.adapter = adapterFriends

    }

    private fun getGame() {

        loadingRound.visibility = View.VISIBLE

        val query: Query = db.document(FirebaseAuth.getInstance().currentUser?.uid.toString()).collection("friends")

        val options: FirestoreRecyclerOptions<FriendsItem> = FirestoreRecyclerOptions.Builder<FriendsItem>().setQuery(query, FriendsItem::class.java).setLifecycleOwner(this).build()

        adapterRound = GamesAdapterFirestore(options, this)
        adapterRound.setRV(rvRound)

        rvRound.setHasFixedSize(true)
        rvRound.layoutManager = LinearLayoutManager(context)
        rvRound.adapter = adapterRound

    }

//    private fun getKeys(onComplete: (ArrayList<String>) -> Unit){
//
//        val list2 = ArrayList<String>()
//
//        dbGames.whereEqualTo("${FirebaseAuth.getInstance().currentUser?.uid.toString()}-turn", true).limit(1).get().addOnSuccessListener {
//
//            if (it != null && !it.isEmpty) {
//
//                for (doc in it.documents){
//
//                    doc.data?.entries?.forEach { it2 ->
//
//                        list2.add(it2.key.toString())
//
//                        if(list2.size == 10){
//
//                            onComplete(list2)
//
//                        }
//
//                    }
//
//                }
//
//            }
//
//        }
//
//    }

//    private fun getGames(onComplete: (Boolean) -> Unit) {
//
//        image_round.visibility = View.GONE
//        text_none_round.visibility = View.GONE
//        loadingRound.visibility = View.VISIBLE
//
//        getKeys {it2 ->
//
//            dbGames.whereEqualTo("${FirebaseAuth.getInstance().currentUser?.uid.toString()}-turn", true)
//                .whereEqualTo("newGame", false).addSnapshotListener(EventListener<QuerySnapshot> {it, e ->
//
//                if (e != null) {
//                    return@EventListener
//                }
//
//                if (it != null && !it.isEmpty) {
//
//                    gamesList.clear()
//
//                    for (doc in it.documents){
//
//                        val id: String = doc.getString(it2[7])!!
//                        val set: String = doc.getString(it2[3])!!
//                        val stage: Int = doc.getLong(it2[5])!!.toInt()
//                        val turn: Boolean = doc.getBoolean(it2[0])!!
//                        val id2: String = doc.getString(it2[9])!!
//                        val set2: String = doc.getString(it2[2])!!
//                        val stage2: Int = doc.getLong(it2[1])!!.toInt()
//                        val turn2: Boolean = doc.getBoolean(it2[6])!!
//                        val gamemode: String = doc.getString(it2[8])!!
//                        val newGame: Boolean = doc.getBoolean(it2[4])!!
//
//                        val list = GamesItem(id, set, stage, turn, id2, set2, stage2, turn2, gamemode, newGame)
//                        gamesList.add(list)
//
//                        if(gamesList.size == it.size()){
//                            onComplete(true)
//                        }
//
//                    }
//
//                } else {
//
//                    onComplete(false)
//
//                }
//
//            })
//
//        }
//
//    }
//
//    private fun setAdapterGame(){
//
//        getGames {
//
//            if(it) {
//
//                adapterRound = GamesAdapterFirestore(this, gamesList)
//                adapterRound.setRV(rvRound)
//
//                rvRound.setHasFixedSize(true)
//                rvRound.layoutManager = LinearLayoutManager(context)
//                rvRound.adapter = adapterRound
//
//                loadingRound.visibility = View.GONE
//                image_round.visibility = View.VISIBLE
//                text_none_round.visibility = View.VISIBLE
//                rvRound.visibility = View.VISIBLE
//
//                rvRound.requestLayout()
//
//            } else {
//
//                loadingRound.visibility = View.GONE
//                image_round.visibility = View.VISIBLE
//                text_none_round.visibility = View.VISIBLE
//
//                rvRound.requestLayout()
//
//            }
//
//        }
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
        adapterFriends.startListening()

    }

    override fun onStop() {
        super.onStop()
        adapterFriends.stopListening()
    }

    override fun onResume() {
        super.onResume()
        FirestoreUtil.initialize()
    }

    companion object {
        fun newInstance(): FriendsFragment = FriendsFragment()
    }


}
