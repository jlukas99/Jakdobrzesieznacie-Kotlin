package pl.idappstudio.howwelldoyouknoweachother.model

class InviteItem {

    var uid: String? = null
    var name: String? = null
    var image: String? = null
    var fb: Boolean? = null

    constructor(){}

    constructor(uid: String, name: String, image: String, fb: Boolean){

        this.uid = uid
        this.name = name
        this.image = image
        this.fb = fb

    }

}