package pl.idappstudio.howwelldoyouknoweachother.model

class InviteItem {

    var uid: String? = null
    var name: String? = null
    var image: String? = null
    var fb: Boolean? = null
    var gender: String? = null
    var type: String? = null
    var registrationTokens: MutableList<String>? = null

    constructor()

    constructor(uid: String, name: String, image: String, fb: Boolean){

        this.uid = uid
        this.name = name
        this.image = image
        this.fb = fb

    }

    constructor(uid: String, name: String, image: String, fb: Boolean, gender: String, type: String, registrationTokens: MutableList<String>){

        this.uid = uid
        this.name = name
        this.image = image
        this.fb = fb
        this.gender = gender
        this.type = type
        this.registrationTokens = registrationTokens

    }

}