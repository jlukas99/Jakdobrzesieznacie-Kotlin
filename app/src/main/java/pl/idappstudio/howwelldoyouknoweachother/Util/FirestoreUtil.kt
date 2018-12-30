package pl.idappstudio.howwelldoyouknoweachother.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import pl.idappstudio.howwelldoyouknoweachother.model.Message
import pl.idappstudio.howwelldoyouknoweachother.model.UserData

object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val db = firestoreInstance.collection("users")
    private val currentUserDocRef: DocumentReference get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
        ?: throw NullPointerException("UID is null.")}")

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
    }

    fun getCurrentUser(onComplete: (UserData) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
                onComplete(it.toObject(UserData::class.java)!!)
            }
    }

//    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
//        return firestoreInstance.collection("users")
//            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                if (firebaseFirestoreException != null) {
//                    Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
//                    return@addSnapshotListener
//                }
//
//                val items = mutableListOf<Item>()
//                querySnapshot!!.documents.forEach {
//                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
//                        items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
//                }
//                onListen(items)
//            }
//    }

    fun addFriend(uid: String, onComplete: () -> Unit){

        val user = HashMap<String, Any>()
        user["uid"] = uid
        user["favorite"] = false
        user["days"] = 0

        db.document(FirebaseAuth.getInstance().currentUser?.uid!!).collection("friends").document(uid).set(user).addOnSuccessListener {

            val user2 = HashMap<String, Any>()
            user2["uid"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
            user2["favorite"] = false
            user2["days"] = 0

            db.document(uid).collection("friends").document(FirebaseAuth.getInstance().currentUser?.uid!!).set(user2).addOnSuccessListener {

                onComplete()

            }

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

    //region FCM
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