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

            if(array.length() == 0){
                onComplete()
            }

            UserUtil.friendsList {

                for (i in 0 until array.length()) {

                    UserUtil.getFacebookFriend(array.getJSONObject(i).getString("id")){it2, b ->

                        if(b) {

                            if (it2.uid != "") {

                                if (!it.contains(it2.uid)) {

                                    UserUtil.addFriend(it2.uid, true) {

                                        val msg: Message = InviteNotificationMessage(
                                            "Znajomy z Facebook'a",
                                            "twój znajomy $name, właśnie zarejestrował się w aplikacji",
                                            UserUtil.user.uid,
                                            it2.uid,
                                            name,
                                            NotificationType.INVITE
                                        )

                                        FirestoreUtil.sendMessage(msg, it2.uid)

                                        onComplete()

                                    }

                                } else {

                                    onComplete()

                                }

                            } else {

                                onComplete()

                            }

                        } else {

                            onComplete()

                        }

                    }

                }

            }

        }

        val parameters = Bundle()
        parameters.putString("fields", "id")
        request.parameters = parameters
        request.executeAsync()

    }

}