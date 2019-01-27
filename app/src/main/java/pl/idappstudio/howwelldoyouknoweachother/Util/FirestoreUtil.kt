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

                Log.d("TAG", it.data?.get("stats").toString())

                val days: Int = it.getLong("days")!!.toInt()
                val favorite: Boolean? = it.getBoolean("favorite")
                val stats: HashMap<String, Int> = it.data?.get("stats") as HashMap<String, Int>
                val set: String = it.getString("set")!!
                val gamemode: String = it.getString("gamemode")!!
                val stage: Int = it.getLong("stage")!!.toInt()

                val friendsData = FriendsData(uid, name, image, fb, gender, type, days, favorite, stats, set, gamemode, stage)

                onComplete(friendsData)

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

    fun addFriend(uid: String, inviteHolder: InviteAdapterFirestore.InviteHolder, onComplete: (Boolean, InviteAdapterFirestore.InviteHolder) -> Unit){

        val al:HashMap<String, Int> = HashMap()
        al["canswer"] = 0
        al["banswer"] = 0
        al["playegames"] = 0

        val user = HashMap<String, Any>()
        user["uid"] = uid
        user["favorite"] = false
        user["days"] = 0
        user["stats"] = al
        user["set"] = "default"
        user["gamemode"] = "classic"
        user["stage"] = 0

        db.document(FirebaseAuth.getInstance().currentUser?.uid!!).collection("friends").document(uid).set(user).addOnSuccessListener {

            val user2 = HashMap<String, Any>()
            user2["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
            user2["favorite"] = false
            user2["days"] = 0
            user2["stats"] = al
            user2["set"] = "default"
            user2["gamemode"] = "classic"
            user2["stage"] = 0

            db.document(uid).collection("friends").document(FirebaseAuth.getInstance().currentUser?.uid!!).set(user2).addOnSuccessListener {

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