@file:Suppress("DEPRECATION")

package pl.idappstudio.jakdobrzesieznacie.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pl.idappstudio.jakdobrzesieznacie.R

import pl.idappstudio.jakdobrzesieznacie.activity.IntroActivity
import pl.idappstudio.jakdobrzesieznacie.activity.MenuActivity
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

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        addTokenToFirestore(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage != null) {

            data = remoteMessage.data

            messageTitle = data["title"].toString()
            messageBody = data["body"].toString()
            messageType = data["type"].toString()

            sendNotification(messageBody, messageTitle, messageType)

        }
    }

    private fun sendNotification(messageBody: String?, title: String?, group: String?) {

        val notificationId: Int = buildNotificationId(messageBody)

//        val intent = Intent(this, IntroActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val notifyPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val bitmapIcon = BitmapFactory.decodeResource(resources, R.mipmap.logo_colored)

        val channelId = getString(R.string.default_notification_channel_id)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (group == "INVITE") {

            if(title == "Znajomy z Facebook'a"){

                val notificationBuilder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_facebook)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setLargeIcon(bitmapIcon)
                    .setShowWhen(true)
                    .setContentTitle(title)
                    .setColor(resources.getColor(R.color.colorFacebook))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setCategory("INVITE_FB")
                    .setGroupSummary(true)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
                    .build()

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val channel = NotificationChannel(channelId, "zaproszenie facebook", NotificationManager.IMPORTANCE_DEFAULT)

                    notificationManager.createNotificationChannel(channel)

                }

                notificationManager.notify(notificationId, notificationBuilder)

            } else {

                val notificationBuilder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_add_friends_icon)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setLargeIcon(bitmapIcon)
                    .setShowWhen(true)
                    .setContentTitle(title)
                    .setColor(resources.getColor(R.color.colorAccent))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setCategory("INVITE")
                    .setGroupSummary(true)
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

            val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.games_icon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setLargeIcon(bitmapIcon)
                .setShowWhen(true)
                .setContentTitle(title)
                .setColor(resources.getColor(R.color.colorRed))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setCategory("GAMES")
                .setGroupSummary(true)
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