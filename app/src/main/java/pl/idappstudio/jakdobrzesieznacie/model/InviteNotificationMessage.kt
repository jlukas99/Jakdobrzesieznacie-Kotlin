package pl.idappstudio.jakdobrzesieznacie.model

data class InviteNotificationMessage(

    override val title: String,
    override val text: String,
    override val senderID: String,
    override val recipientID: String,
    override val senderName: String,
    override val type: String) : Message{

    constructor() : this("","", "", "", "", "")

}