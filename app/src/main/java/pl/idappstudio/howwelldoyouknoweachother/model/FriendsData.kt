package pl.idappstudio.howwelldoyouknoweachother.model

data class FriendsData(val uid: String, val days: Int, val favorite: Boolean?){

    constructor(): this("", 0, null)

}