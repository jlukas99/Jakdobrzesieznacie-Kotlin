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

        getCurrentUser { currentUser = it }

    }

    fun registerCurrentUser(uid: String, name: String, image: String, fb: Boolean, gender: String, type: String, onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = UserData(uid, name, image, fb, gender, type, mutableListOf())
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else
                onComplete()
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
            onComplete(it.toObject(UserData::class.java)!!)
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

    fun updateGameSettings(yourTurn: Boolean, friendTurn: Boolean, yourStage: Int, friendStage: Int, yourSet: String, friendSet: String, gamemode: String, gameID: String, yourID: String, friendID: String) {

        dbGame.document(gameID).update("$yourID-turn", yourTurn)
        dbGame.document(gameID).update("$friendID-turn", friendTurn)
        dbGame.document(gameID).update("$yourID-stage", yourStage)
        dbGame.document(gameID).update("$friendID-stage", friendStage)
        dbGame.document(gameID).update("$yourID-set", yourSet)
        dbGame.document(gameID).update("$friendID-set", friendSet)
        dbGame.document(gameID).update("gamemode", gamemode)

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