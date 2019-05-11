package pl.idappstudio.jakdobrzesieznacie.util

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import pl.idappstudio.jakdobrzesieznacie.model.InviteNotificationMessage
import pl.idappstudio.jakdobrzesieznacie.model.Message
import pl.idappstudio.jakdobrzesieznacie.model.NotificationType

object FacebookUtil {

    fun getFacebookFriends(token: AccessToken, name: String, onComplete: () -> Unit) {
        val request = GraphRequest.newMyFriendsRequest(token) { array, _ ->

            if(array.length() == 0){
                onComplete()
            }

            UserUtil.friendsList {

                for (i in 0 until array.length()) {

                    UserUtil.getFacebookFriend(array.getJSONObject(i).getString("id")){it2, b ->

                        if(b) {

                            if (it2.uid != "") {

                                if (!it.contains(it2.uid)) {

                                    if (it2.public) {

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

                                    }

                                }

                            }

                        }

                    }

                    if (i == array.length()) {
                        onComplete()
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