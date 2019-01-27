package pl.idappstudio.howwelldoyouknoweachother.model

data class FriendsData(val uid: String, val name: String, val image: String, val fb: Boolean?, val gender: String, val type: String, val days: Int, val favorite: Boolean?, val stats: HashMap<String, Int>, val set: String, val gamemode: String, val stage: Int)