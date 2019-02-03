package pl.idappstudio.howwelldoyouknoweachother.model

data class GamesItem(val id: String,
                     val set: String,
                     val stage: Int,
                     val turn: Boolean,
                     val id2: String,
                     val set2: String,
                     val stage2: Int,
                     val turn2: Boolean,
                     val gamemode: String,
                     val newGame: Boolean){

    constructor() : this("", "", 0, false, "", "" , 0, false, "", true)

}
