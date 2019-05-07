package pl.idappstudio.jakdobrzesieznacie.model

data class FriendItem(val uid: String, val favorite: Boolean){

    constructor(): this("", false)

}