package pl.idappstudio.howwelldoyouknoweachother.model

class SetItem {

    var name: String? = null
    var image: Int? = null
    var premium: Boolean? = null
    var category: String? = null
    var type: String? = null
    var id: String? = null

    constructor()

    constructor(name: String, image: Int, premium: Boolean, category: String, type: String, id: String){

        this.name = name
        this.image = image
        this.premium = premium
        this.category = category
        this.type = type
        this.id = id

    }

}