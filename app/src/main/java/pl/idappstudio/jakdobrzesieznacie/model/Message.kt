package pl.idappstudio.jakdobrzesieznacie.model

object NotificationType {

    const val INVITE = "INVITE"
    const val GAME = "GAME"
    const val MESSAGE = "MESSAGE"

}

interface Message {

    val title: String
    val text: String
    val senderID: String
    val recipientID: String
    val senderName: String
    val type: String

}