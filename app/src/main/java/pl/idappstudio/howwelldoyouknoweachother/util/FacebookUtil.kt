package pl.idappstudio.howwelldoyouknoweachother.util

import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.firebase.auth.FirebaseAuth
import pl.idappstudio.howwelldoyouknoweachother.model.InviteNotificationMessage
import pl.idappstudio.howwelldoyouknoweachother.model.Message
import pl.idappstudio.howwelldoyouknoweachother.model.NotificationType

object FacebookUtil {

    fun getFacebookFriends(token: AccessToken, name: String, onComplete: () -> Unit) {
        val request = GraphRequest.newMyFriendsRequest(token) { array, response ->

//            UserUtil.initializeList {
//
//                for (i in 0 until array.length()) {
//
//                    FirestoreUtil.getUser(array.getJSONObject(i).getString("id")){it2 ->
//
//                        if(it2.uid != ""){
//
//                            FirestoreUtil.addFacebookFriend(it2.uid, it){
//
//                                val msg: Message = InviteNotificationMessage("Znajomy z Facebook'a", "twój znajomyy $name, właśnie zarejestrował się w aplikacji", UserUtil.user.uid, it2.uid, name, NotificationType.INVITE)
//                                FirestoreUtil.sendMessage(msg, it2.uid)
//
//                                if(i == array.length()){
//
//                                    onComplete()
//
//                                }
//
//                            }
//
//
//                        }
//
//                    }
//
//                }
//
//            }

            onComplete()

        }

        val parameters = Bundle()
        parameters.putString("fields", "id")
        request.parameters = parameters
        request.executeAsync()

    }

}