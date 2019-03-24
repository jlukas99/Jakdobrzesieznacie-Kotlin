package pl.idappstudio.howwelldoyouknoweachother.model

data class SetItem(val name: String, val image: Int, val premium: Boolean, val category: String, val lang: String, val id: String){

    constructor() : this("", 0, false, "", "", "")

}