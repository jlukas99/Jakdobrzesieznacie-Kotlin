package pl.idappstudio.howwelldoyouknoweachother.model

data class FriendItem(val uid: String, val favorite: Boolean){

    constructor(): this("", false)

}