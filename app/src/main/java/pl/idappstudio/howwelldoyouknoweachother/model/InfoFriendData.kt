package pl.idappstudio.howwelldoyouknoweachother.model

data class InfoFriendData(val banswer: Int, val canswer: Int, val days: Int, val favorite: Boolean, val gameId: String, val games: Int, val uid: String){

    constructor() : this(0, 0, 0, false, "", 0, "")

}