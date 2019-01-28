package pl.idappstudio.howwelldoyouknoweachother.model

data class FriendSetData(val name: String, val image: Int, val premium: Boolean, val category: String, val type: String, val id: String){

    constructor() : this("", 0, false, "default", "default", "")

}