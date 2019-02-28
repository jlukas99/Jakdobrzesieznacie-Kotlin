package pl.idappstudio.howwelldoyouknoweachother.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import pl.idappstudio.howwelldoyouknoweachother.model.*

object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val db: CollectionReference get() = firestoreInstance.collection("users")
    private val dbSet: CollectionReference get() = firestoreInstance.collection("set")
    private val dbGame: CollectionReference get() = firestoreInstance.collection("games")
    private val currentUserDocRef: DocumentReference get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid}")

    fun registerCurrentUser(uid: String, name: String, image: String, fb: Boolean, gender: String, type: String, status: String, onComplete: (Boolean) -> Unit) {

        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->

            if (!documentSnapshot.exists()) {

                val newUser = UserData(uid, name, image, fb, gender, type, status, true, true, mutableListOf())

                currentUserDocRef.set(newUser).addOnSuccessListener {

                    onComplete(true)

                }

            } else {

                onComplete(false)

            }

        }

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

//    fun addFacebookFriend(uid: String, list: ArrayList<String>, onComplete: () -> Unit){
//
//        if(list.contains(uid)){
//
//            onComplete()
//
//        }
//
//        val user = HashMap<String, Any>()
//        user["uid"] = uid
//        user["favorite"] = false
//        user["days"] = 0
//        user["canswer"] = 0
//        user["banswer"] = 0
//        user["games"] = 0
//        user["gameId"] = uid + UserUtil.user.uid
//
//        db.document(UserUtil.user.uid).collection("friends").document(uid).set(user).addOnSuccessListener {it2 ->
//
//            val user2 = HashMap<String, Any>()
//            user2["uid"] = UserUtil.user.uid
//            user2["favorite"] = false
//            user2["days"] = 0
//            user2["canswer"] = 0
//            user2["banswer"] = 0
//            user2["games"] = 0
//            user2["gameId"] = uid + UserUtil.user.uid
//
//            db.document(uid).collection("friends").document(UserUtil.user.uid).set(user2).addOnSuccessListener {
//
//                val user3 = HashMap<String, Any>()
//                user3["gamemode"] = "classic"
//                user3["$uid-stage"] = 0
//                user3["${UserUtil.user.uid}-stage"] = 0
//                user3["$uid-turn"] = true
//                user3["${UserUtil.user.uid}-turn"] = true
//                user3["$uid-set"] = "tK29qYKKfGtBBzl6PBiC"
//                user3["${UserUtil.user.uid}-set"] = "tK29qYKKfGtBBzl6PBiC"
//                user3["newGame"] = true
//                user3["${UserUtil.user.uid}-id"] = uid
//                user3["$uid-id"] = UserUtil.user.uid
//
//                dbGame.document(uid + UserUtil.user.uid).set(user3).addOnSuccessListener {
//
//                    onComplete()
//
//                }.addOnFailureListener {
//
//                    onComplete()
//
//                }
//
//            }.addOnFailureListener {
//
//                onComplete()
//
//            }
//
//        }.addOnFailureListener {
//
//            onComplete()
//
//        }
//
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