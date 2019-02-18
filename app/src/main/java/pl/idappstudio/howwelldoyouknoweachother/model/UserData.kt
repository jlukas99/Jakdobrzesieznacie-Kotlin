package pl.idappstudio.howwelldoyouknoweachother.model

data class UserData(val uid: String, val name: String, val image: String, val fb: Boolean, val gender: String, val type: String, val public: Boolean, val notification: Boolean, val registrationTokens: MutableList<String>){

    constructor(): this("", "", "", false, "", "", false, true, mutableListOf())

}