package pl.idappstudio.jakdobrzesieznacie.model

data class UserSetData(val name: String, val premium: Boolean, val category: String, val id: String) {

    constructor() : this("", false, "", "")

}