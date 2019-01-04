package pl.idappstudio.howwelldoyouknoweachother.model

class FriendsItem {

    var uid: String? = null
    var days: Int? = null
    var favorite: Boolean? = null

    constructor()

    constructor(uid: String, days: Int, favorite: Boolean){

        this.uid = uid
        this.days = days
        this.favorite = favorite

    }


}
