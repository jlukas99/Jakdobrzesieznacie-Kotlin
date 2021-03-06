@file:Suppress("DEPRECATION")

package pl.idappstudio.jakdobrzesieznacie.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.util.FirestoreUtil

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        private lateinit var data: Map<String, String>
        private lateinit var messageTitle: String
        private lateinit var messageBody: String
        private lateinit var messageType: String

        fun addTokenToFirestore(newRegistrationToken: String?) {

            if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

            if(FirebaseAuth.getInstance().currentUser != null) {
                FirestoreUtil.getFCMRegistrationTokens { tokens ->
                    if (tokens.contains(newRegistrationToken))
                        return@getFCMRegistrationTokens

                    tokens.add(newRegistrationToken)
                    FirestoreUtil.setFCMRegistrationTokens(tokens)
                }
            }
        }

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        addTokenToFirestore(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

            data = remoteMessage.data

            messageTitle = data["title"].toString()
            messageBody = data["body"].toString()
            messageType = data["type"].toString()

            sendNotification(messageBody, messageTitle, messageType)

    }

    private fun sendNotification(messageBody: String?, title: String?, group: String?) {

        val notificationId: Int = buildNotificationId(messageBody)

        val channelId = getString(R.string.default_notification_channel_id)

        if (group == "INVITE") {

            if (title == resources.getString(R.string.friend_from_facebook)) {

                val notificationBuilder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_add_friends_icon)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setContentTitle(title)
                    .setColor(resources.getColor(R.color.colorFacebook))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setGroup("pl.idappstudio.jakdobrzesieznacie.INVITE_FB")
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
                    .build()

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val channel = NotificationChannel(channelId, "zaproszenie facebook", NotificationManager.IMPORTANCE_DEFAULT)

                    notificationManager.createNotificationChannel(channel)

                }

                notificationManager.notify(notificationId, notificationBuilder)

            } else {

                val notificationBuilder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_add_friends_icon)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setContentTitle(title)
                    .setColor(resources.getColor(R.color.colorAccent))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setGroup("pl.idappstudio.jakdobrzesieznacie.INVITE")
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
                    .build()

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val channel = NotificationChannel(channelId, "zaproszenie", NotificationManager.IMPORTANCE_DEFAULT)

                    notificationManager.createNotificationChannel(channel)

                }

                notificationManager.notify(notificationId, notificationBuilder)

            }

        } else {

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.games_icon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentTitle(title)
                .setColor(resources.getColor(R.color.colorRed))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setGroup("pl.idappstudio.jakdobrzesieznacie.GAMES")
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
                .build()

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val channel = NotificationChannel(channelId, "rozgrywka", NotificationManager.IMPORTANCE_DEFAULT)

                notificationManager.createNotificationChannel(channel)

            }

                notificationManager.notify(notificationId, notificationBuilder)

            }

    }

    private fun buildNotificationId(id: String?): Int {

        var notificationId = 0

        for (i in 0..8) {

            notificationId += id!![0].toInt()

        }

        return notificationId
    }
}