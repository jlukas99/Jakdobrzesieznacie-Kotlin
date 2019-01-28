package pl.idappstudio.howwelldoyouknoweachother.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

    fun getFriendsUser(id: String, onComplete: (FriendsData) -> Unit) {
        db.document(id).get().addOnSuccessListener {

            val image: String = it.getString("image")!!
            val fb: Boolean = it.getBoolean("fb")!!
            val gender: String = it.getString("gender")!!
            val name = it.get("name").toString()
            val type = it.get("type").toString()
            val uid = it.get("uid").toString()

            currentUserDocRef.collection("friends").document(id).get().addOnSuccessListener {

                val days: Int = it.getLong("days")!!.toInt()
                val favorite: Boolean? = it.getBoolean("favorite")
                val canswer: Int = it.getLong("canswer")!!.toInt()
                val banswer: Int = it.getLong("banswer")!!.toInt()
                val games: Int = it.getLong("games")!!.toInt()
                val gameID: String = it.getString("gameId")!!

                val stats = StatsData(canswer, banswer, games)

                dbGame.document(gameID).get().addOnSuccessListener {

                    val gamemode: String = it.getString("gamemode")!!
                    val friendStage: Int = it.getLong("$id-stage")!!.toInt()
                    val yourStage: Int = it.getLong("${FirebaseAuth.getInstance().currentUser?.uid.toString()}-stage")!!.toInt()
                    val friendTurn: Boolean = it.getBoolean("$uid-turn")!!
                    val yourTurn: Boolean = it.getBoolean("${FirebaseAuth.getInstance().currentUser?.uid.toString()}-turn")!!
                    val friendSet: String = it.getString("$uid-set")!!
                    val yourSet: String = it.getString("${FirebaseAuth.getInstance().currentUser?.uid.toString()}-set")!!

                    dbSet.document(yourSet).get().addOnSuccessListener {

                        val ySet = it.toObject(YourSetData::class.java)!!

                        dbSet.document(friendSet).get().addOnSuccessListener {

                            val fSet = it.toObject(FriendSetData::class.java)!!

                            val game = GameData(yourTurn, friendTurn, yourStage, friendStage, ySet, fSet, gamemode, gameID)

                            val friendsData = FriendsData(uid, name, image, fb, gender, type, days, favorite, stats, game)

                            onComplete(friendsData)

                        }

                    }

                }
            }
        }
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
                user3["set"] = "default"
                user3["gamemode"] = "classic"
                user3["$uid-stage"] = 0
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-stage"] = 0
                user3["$uid-turn"] = true
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-turn"] = true
                user3["$uid-set"] = "tK29qYKKfGtBBzl6PBiC"
                user3["${FirebaseAuth.getInstance().currentUser?.uid.toString()}-set"] = "tK29qYKKfGtBBzl6PBiC"

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

    fun removeListener(registration: ListenerRegistration) = registration.remove()

//    fun getOrCreateChatChannel(otherUserId: String,
//                               onComplete: (channelId: String) -> Unit) {
//        currentUserDocRef.collection("engagedChatChannels")
//            .document(otherUserId).get().addOnSuccessListener {
//                if (it.exists()) {
//                    onComplete(it["channelId"] as String)
//                    return@addOnSuccessListener
//                }
//
//                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
//
//                val newChannel = chatChannelsCollectionRef.document()
//                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))
//
//                currentUserDocRef
//                    .collection("engagedChatChannels")
//                    .document(otherUserId)
//                    .set(mapOf("channelId" to newChannel.id))
//
//                firestoreInstance.collection("users").document(otherUserId)
//                    .collection("engagedChatChannels")
//                    .document(currentUserId)
//                    .set(mapOf("channelId" to newChannel.id))
//
//                onComplete(newChannel.id)
//            }
//    }

//    fun addChatMessagesListener(channelId: String, context: Context,
//                                onListen: (List<Item>) -> Unit): ListenerRegistration {
//        return chatChannelsCollectionRef.document(channelId).collection("messages")
//            .orderBy("time")
//            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                if (firebaseFirestoreException != null) {
//                    Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
//                    return@addSnapshotListener
//                }
//
//                val items = mutableListOf<Item>()
//                querySnapshot!!.documents.forEach {
//                    if (it["type"] == MessageType.TEXT)
//                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
//                    else
//                        items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!, context))
//                    return@forEach
//                }
//                onListen(items)
//            }
//    }

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