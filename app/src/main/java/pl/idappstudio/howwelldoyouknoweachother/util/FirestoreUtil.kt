package pl.idappstudio.howwelldoyouknoweachother.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import pl.idappstudio.howwelldoyouknoweachother.adapter.InviteAdapterFirestore
import pl.idappstudio.howwelldoyouknoweachother.adapter.SearchAdapterFirestore
import pl.idappstudio.howwelldoyouknoweachother.model.*





object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val db: CollectionReference get() = firestoreInstance.collection("users")
    private val dbSet: CollectionReference get() = firestoreInstance.collection("set")
    private val dbGame: CollectionReference get() = firestoreInstance.collection("games")
    private val currentUserDocRef: DocumentReference get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid}")

    lateinit var currentUser: UserData

    fun initialize(){

        getCurrentUser {

            currentUser = it

        }

    }

    fun registerCurrentUser(uid: String, name: String, image: String, fb: Boolean, gender: String, type: String, onComplete: (Boolean) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = UserData(uid, name, image, fb, gender, type, true, true, mutableListOf())
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete(true)
                }
            }
            else
                onComplete(false)
        }
    }

    fun updateCurrentUser(name: String = "", image: String = "", fb: Boolean? = null, gender: String, type: String) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (image.isNotBlank()) userFieldMap["image"] = image
        if (fb != null) userFieldMap["fb"] = fb
        if (gender.isNotBlank()) userFieldMap["gender"] = gender
        if (type.isNotBlank()) userFieldMap["type"] = type
        currentUserDocRef.update(userFieldMap)

        initialize()
    }

    fun getCurrentUser(onComplete: (UserData) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {

            if(it.exists()) {

                currentUser = it.toObject(UserData::class.java)!!
                onComplete(it.toObject(UserData::class.java)!!)

            } else {

                onComplete(UserData())

            }
        }.addOnFailureListener {
            onComplete(UserData())
        }
    }

    fun getStats(onComplete: (StatsData) -> Unit){

        currentUserDocRef.collection("friends").get().addOnSuccessListener {

            var canswer = 0
            var banswer = 0
            var games = 0

            var i = 0

            if(it.isEmpty){

                onComplete(StatsData(0, 0, 0))

            }

            for (doc in it.documents){

                canswer += doc.getLong("canswer")?.toInt()!!
                banswer += doc.getLong("banswer")?.toInt()!!
                games += doc.getLong("games")?.toInt()!!

                i++

                if(it.size() == i){

                    onComplete(StatsData(canswer, banswer, games))

                }

            }

        }

    }

    fun sendInvite(uid: String, inviteHolder: SearchAdapterFirestore.InviteHolder, onComplete: (Boolean, SearchAdapterFirestore.InviteHolder) -> Unit) {

        val user = HashMap<String, Any?>()
        user["name"] = currentUser.name
        user["image"] = currentUser.image
        user["uid"] = currentUser.uid
        user["fb"] = currentUser.fb

        db.document(uid).collection("invites").document(currentUser.uid).set(user).addOnSuccessListener {

            val msg: Message = InviteNotificationMessage("Zaproszenie do znajomych", "${FirestoreUtil.currentUser.name} chce dodać cię do znajomych", FirebaseAuth.getInstance().currentUser?.uid.toString(), uid, FirestoreUtil.currentUser.name, NotificationType.INVITE)
            FirestoreUtil.sendMessage(msg, uid)

            onComplete(true, inviteHolder)

        }.addOnFailureListener {

            onComplete(false, inviteHolder)

        }
    }

    fun setFavorite(uid: String, b: Boolean) {

        db.document(currentUser.uid).collection("friends").document(uid).update("favorite", b)

    }

    fun setPublic(uid: String, b: Boolean) {

        db.document(currentUser.uid).update("public", b)

    }

    fun setNotification(uid: String, b: Boolean) {

        db.document(currentUser.uid).update("notification", b)

    }

    fun updateGameSettings(yourTurn: Boolean, friendTurn: Boolean, yourStage: Int, friendStage: Int, yourSet: String, friendSet: String, gamemode: String, gameID: String, yourID: String, friendID: String) {

        dbGame.document(gameID).update("$yourID-turn", yourTurn)
        dbGame.document(gameID).update("$friendID-turn", friendTurn)
        dbGame.document(gameID).update("$yourID-stage", yourStage)
        dbGame.document(gameID).update("$friendID-stage", friendStage)
        dbGame.document(gameID).update("$yourID-set", yourSet)
        dbGame.document(gameID).update("$friendID-set", friendSet)
        dbGame.document(gameID).update("gamemode", gamemode)

    }

    fun getUser(uid: String, onComplete: (UserData) -> Unit){

        db.whereEqualTo("image", uid).get().addOnSuccessListener {

            if(it.isEmpty){
                onComplete(UserData())
            }

            for(doc in it) {

                onComplete(doc.toObject(UserData::class.java))

            }

        }.addOnFailureListener {

            onComplete(UserData())

        }

    }

    fun getFriendsList(onComplete: (ArrayList<String>) -> Unit){

        val list = ArrayList<String>()

        list.clear()

        currentUserDocRef.collection("friends").get().addOnSuccessListener {

            if(it.isEmpty){

                onComplete(list)

            }

            for(doc in it){

                list.add(doc.id)

                if(it.size() == list.size){

                    onComplete(list)

                }

            }

        }

    }

    fun addFacebookFriend(uid: String, list: ArrayList<String>, onComplete: () -> Unit){

        if(list.contains(uid)){

            onComplete()

        }

        val user = HashMap<String, Any>()
        user["uid"] = uid
        user["favorite"] = false
        user["days"] = 0
        user["canswer"] = 0
        user["banswer"] = 0
        user["games"] = 0
        user["gameId"] = uid+FirebaseAuth.getInstance().currentUser?.uid.toString()

        db.document(FirebaseAuth.getInstance().currentUser?.uid!!).collection("friends").document(uid).set(user).addOnSuccessListener {it2 ->

            val user2 = HashMap<String, Any>()
            user2["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
            user2["favorite"] = false
            user2["days"] = 0
            user2["canswer"] = 0
            user2["banswer"] = 0
            user2["games"] = 0
            user2["gameId"] = uid+FirebaseAuth.getInstance().currentUser?.uid.toString()

            db.document(uid).collection("friends").document(FirebaseAuth.getInstance().currentUser?.uid!!).set(user2).addOnSuccessListener {

                val user3 = HashMap<String, Any>()
                user3["gamemode"] = "classic"
                user3["$uid-stage"] = 0
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-stage"] = 0
                user3["$uid-turn"] = true
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-turn"] = true
                user3["$uid-set"] = "tK29qYKKfGtBBzl6PBiC"
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-set"] = "tK29qYKKfGtBBzl6PBiC"
                user3["newGame"] = true
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-id"] = uid
                user3["$uid-id"] = FirebaseAuth.getInstance().currentUser?.uid.toString()

                dbGame.document(uid+FirebaseAuth.getInstance().currentUser?.uid.toString()).set(user3).addOnSuccessListener {

                    initialize()

                    onComplete()

                }.addOnFailureListener {

                    onComplete()

                }

            }.addOnFailureListener {

                onComplete()

            }

        }.addOnFailureListener {

            onComplete()

        }

    }

    fun addFriend(uid: String, inviteHolder: InviteAdapterFirestore.InviteHolder, onComplete: (Boolean, InviteAdapterFirestore.InviteHolder) -> Unit){

        val user = HashMap<String, Any>()
        user["uid"] = uid
        user["favorite"] = false
        user["days"] = 0
        user["canswer"] = 0
        user["banswer"] = 0
        user["games"] = 0
        user["gameId"] = uid+FirebaseAuth.getInstance().currentUser?.uid.toString()

        db.document(FirebaseAuth.getInstance().currentUser?.uid!!).collection("friends").document(uid).set(user).addOnSuccessListener {

            val user2 = HashMap<String, Any>()
            user2["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
            user2["favorite"] = false
            user2["days"] = 0
            user2["canswer"] = 0
            user2["banswer"] = 0
            user2["games"] = 0
            user2["gameId"] = uid+FirebaseAuth.getInstance().currentUser?.uid.toString()

            db.document(uid).collection("friends").document(FirebaseAuth.getInstance().currentUser?.uid!!).set(user2).addOnSuccessListener {

                val user3 = HashMap<String, Any>()
                user3["gamemode"] = "classic"
                user3["$uid-stage"] = 0
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-stage"] = 0
                user3["$uid-turn"] = true
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-turn"] = true
                user3["$uid-set"] = "tK29qYKKfGtBBzl6PBiC"
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-set"] = "tK29qYKKfGtBBzl6PBiC"
                user3["newGame"] = true
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-id"] = uid
                user3["$uid-id"] = FirebaseAuth.getInstance().currentUser?.uid.toString()

                dbGame.document(uid+FirebaseAuth.getInstance().currentUser?.uid.toString()).set(user3).addOnSuccessListener {

                    val msg: Message = InviteNotificationMessage("Nowy znajomy", "${FirestoreUtil.currentUser.name} i ty jesteście teraz znajomymi", FirebaseAuth.getInstance().currentUser?.uid.toString(), uid, FirestoreUtil.currentUser.name, NotificationType.INVITE)
                    FirestoreUtil.sendMessage(msg, uid)

                    initialize()

                    onComplete(true, inviteHolder)

                }.addOnFailureListener {

                    onComplete(false, inviteHolder)

                }

            }.addOnFailureListener {

                onComplete(false, inviteHolder)

            }

        }.addOnFailureListener {

            onComplete(false, inviteHolder)

        }

    }

    fun sendMessage(message: Message, recipientID: String) {
        db.document(recipientID)
            .collection("notification")
            .add(message)
    }

    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(UserData::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }



}