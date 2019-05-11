package pl.idappstudio.jakdobrzesieznacie.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import pl.idappstudio.jakdobrzesieznacie.model.InviteNotificationMessage
import pl.idappstudio.jakdobrzesieznacie.model.Message
import pl.idappstudio.jakdobrzesieznacie.model.NotificationType
import pl.idappstudio.jakdobrzesieznacie.model.UserData

object UserUtil {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()

    private val dbUsers: CollectionReference get() = db.collection("users")
    private val dbGames: CollectionReference get() = db.collection("games")
    private val dbFriendsUser: CollectionReference get() = db.collection("users/${FirebaseAuth.getInstance().currentUser?.uid}/friends")
    private val dbCurrentUser: DocumentReference get() = db.document("users/${FirebaseAuth.getInstance().currentUser?.uid}")

    var user = UserData()

    private var getListFriendsListener: ListenerRegistration? = null
    private var getOneFriendListener: ListenerRegistration? = null
    private var getOneFriendInfoListener: ListenerRegistration? = null
    private var getOneFriendStatsListener: ListenerRegistration? = null
    private var getOneUserStatsListener: ListenerRegistration? = null
    private var getProfileUserStatsListener: ListenerRegistration? = null
    private var getOneGameSettingsListener: ListenerRegistration? = null
    private var getOneFriendSetListener: ListenerRegistration? = null
    private var listenRecyclewViewListener: ListenerRegistration? = null
    private var getUserListener: ListenerRegistration? = null

    fun initialize(onComplete: (String) -> Unit) {

        db.firestoreSettings = settings

        initializeUser {

            if(user.uid != "") {

                onComplete(user.uid)

            } else {

                onComplete("")

            }

        }

    }

    fun initializeUser(onComplete: (UserData) -> Unit) {

        dbCurrentUser.get().addOnSuccessListener {

            if (it == null) {
                return@addOnSuccessListener
            }

            if(it.exists()) {

                user = it.toObject(UserData::class.java)!!
                onComplete(it.toObject(UserData::class.java)!!)

            } else {

                onComplete(UserData())

            }

        }

    }

    fun friendsList(onComplete: (ArrayList<String>) -> Unit){

        dbFriendsUser.get().addOnSuccessListener {

            if(it.isEmpty){
                onComplete(ArrayList())
            }

            val array = ArrayList<String>()

            for(i in it){

                array.add(i.id)

                if(it.size() == array.size){
                    onComplete(array)
                }

            }

        }.addOnFailureListener {

            onComplete(ArrayList())

        }

    }

    fun getUser(onComplete: (UserData) -> Unit) {

       getUserListener = dbCurrentUser.addSnapshotListener(MetadataChanges.INCLUDE, EventListener<DocumentSnapshot> { document, e ->

            if (e != null) {
                return@EventListener
            }

            if(document != null){

                if(document.exists()) {

                    user = document.toObject(UserData::class.java)!!
                    onComplete(document.toObject(UserData::class.java)!!)

                } else {

                    onComplete(UserData())

                }

            } else {

                onComplete(UserData())

            }

        })


    }

    fun getIdGame(id: String, onComplete: (String) -> Unit){ dbFriendsUser.document(id).get().addOnSuccessListener { onComplete(it.getString("gameId").toString()) } }

    fun addFriend(uid: String, fb: Boolean, onComplete: (Boolean) -> Unit){

        val you = HashMap<String, Any>()
        you["uid"] = user.uid
        you["favorite"] = false
        you["canswer"] = 0
        you["banswer"] = 0
        you["games"] = 0
        you["gameId"] = uid + user.uid

        val friend = HashMap<String, Any>()
        friend["uid"] = uid
        friend["favorite"] = false
        friend["canswer"] = 0
        friend["banswer"] = 0
        friend["games"] = 0
        friend["gameId"] = uid + user.uid

        val game = HashMap<String, Any>()
        game["gamemode"] = "classic"
        game["$uid-stage"] = 0
        game["${user.uid}-stage"] = 0
        game["$uid-turn"] = true
        game["${user.uid}-turn"] = true
        game["$uid-set"] = "3IlI2MBd04S6AhdkIgsY"
        game["${user.uid}-set"] = "3IlI2MBd04S6AhdkIgsY"
        game["newGame"] = true
        game["${user.uid}-id"] = user.uid
        game["$uid-id"] = uid

        dbGames.document(uid + user.uid).set(game).addOnFailureListener {

            onComplete(false)

        }.addOnSuccessListener {

            dbCurrentUser.collection("friends").document(uid).set(friend).addOnFailureListener {

                dbGames.document(uid + user.uid).delete()
                onComplete(false)

            }.addOnSuccessListener {

                dbUsers.document(uid).collection("friends").document(user.uid).set(you).addOnSuccessListener {

                    if(!fb) {
                        val msg: Message = InviteNotificationMessage(
                            "Masz nowego znajomego",
                            "${user.name} i ty jesteście teraz znajomymi",
                            user.uid,
                            uid,
                            user.name,
                            NotificationType.INVITE
                        )
                        FirestoreUtil.sendMessage(msg, uid)
                    }
                    onComplete(true)

                }.addOnFailureListener {

                    dbCurrentUser.collection("friends").document(uid).delete()
                    dbGames.document(uid + user.uid).delete()
                    onComplete(false)

                }

            }

        }

    }

    fun setPublic(b: Boolean, onComplete: (Boolean) -> Unit) {

        dbCurrentUser.update("public", b).addOnSuccessListener {

            onComplete(true)

        }.addOnFailureListener {

            onComplete(false)

        }

    }

    fun setNotification(b: Boolean, onComplete: (Boolean) -> Unit) {

        dbCurrentUser.update("notification", b).addOnSuccessListener {

            onComplete(true)

        }.addOnFailureListener {

            onComplete(false)

        }

    }

    fun getProfileUserStats(onComplete: (Int, Int, Int) -> Unit){

        var canswer = 0
        var banswer = 0
        var games = 0

        getProfileUserStatsListener = dbFriendsUser.addSnapshotListener(MetadataChanges.INCLUDE, EventListener<QuerySnapshot> { it, e ->

            if (e != null) {
                return@EventListener
            }

            if (it == null) {
                return@EventListener
            }

            for (doc in it.documentChanges) {

                canswer += doc.document.getLong("canswer")!!.toInt()
                banswer += doc.document.getLong("banswer")!!.toInt()
                games += doc.document.getLong("games")!!.toInt()

                onComplete(canswer, banswer, games)

            }

        })

    }

    fun delInvite(uid: String, onComplete: (Boolean) -> Unit){

        dbCurrentUser.collection("invites").document(uid).delete().addOnSuccessListener {

            onComplete(true)

        }.addOnFailureListener {

            onComplete(false)

        }

    }

    private fun checkInvite(uid: String, onComplete: (Boolean) -> Unit) {

        dbCurrentUser.collection("invites").document(uid).get().addOnSuccessListener {

            if(it.exists()){

                onComplete(false)

            } else {

                onComplete(true)

            }

        }.addOnFailureListener {

            onComplete(false)

        }

    }

    fun sendInvite(uid: String, onComplete: (Boolean) -> Unit) {

        checkInvite(uid) {

            if(it) {

                val u = HashMap<String, Any?>()
                u["name"] = user.name
                u["image"] = user.image
                u["uid"] = user.uid
                u["fb"] = user.fb

                dbUsers.document(uid).collection("invites").document(user.uid).set(u).addOnSuccessListener {

                    val msg: Message = InviteNotificationMessage(
                        "Zaproszenie do znajomych",
                        "${user.name} chce dodać cię do znajomych",
                        user.uid,
                        uid,
                        user.name,
                        NotificationType.INVITE
                    )

                    FirestoreUtil.sendMessage(msg, uid)

                    onComplete(true)

                }.addOnFailureListener {

                    onComplete(false)

                }

            } else {

                onComplete(false)

            }

        }
    }

    fun updateStatus(msg: String, onComplete: () -> Unit) {
        dbCurrentUser.update("status", msg).addOnCompleteListener { onComplete() }
    }

    fun getFacebookFriend(uid: String, onComplete: (UserData, Boolean) -> Unit){

        dbUsers.whereEqualTo("image", uid).get().addOnSuccessListener {

            for(i in it){

                val friend = i.toObject(UserData::class.java)

                if(friend.uid != ""){

                    onComplete(friend, true)

                } else {

                    onComplete(UserData(), false)

                }

            }

        }.addOnFailureListener {

            onComplete(UserData(), false)

        }

    }

    fun getFriendData(uid: String, onComplete: (UserData, Boolean) -> Unit){

        dbUsers.document(uid).get().addOnSuccessListener {

            val friend = it.toObject(UserData::class.java)

            if(friend != null && friend.uid != ""){

                onComplete(friend, true)

            } else {

                onComplete(UserData(), false)

            }

        }.addOnFailureListener {

            onComplete(UserData(), false)

        }

    }

    fun stopListener(){

        if(getListFriendsListener != null) {
            getListFriendsListener?.remove()
        }

        if(getOneFriendListener != null) {
            getOneFriendListener?.remove()
        }

        if(getOneFriendInfoListener != null) {
            getOneFriendInfoListener?.remove()
        }

        if(getOneFriendStatsListener != null) {
            getOneFriendStatsListener?.remove()
        }

        if(getOneUserStatsListener != null) {
            getOneUserStatsListener?.remove()
        }

        if(getOneGameSettingsListener != null) {
            getOneGameSettingsListener?.remove()
        }

        if(getOneFriendSetListener != null) {
            getOneFriendSetListener?.remove()
        }

        if(listenRecyclewViewListener != null) {
            listenRecyclewViewListener?.remove()
        }
        
    }

}