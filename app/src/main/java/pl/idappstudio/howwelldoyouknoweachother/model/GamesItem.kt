package pl.idappstudio.howwelldoyouknoweachother.model

data class GamesItem(val gameId: String,
                     val yset: String,
                     val ystage: Int,
                     val yturn: Boolean,
                     val yid: String,
                     val fset: String,
                     val fstage: Int,
                     val fturn: Boolean,
                     val fid: String,
                     val gamemode: String,
                     val newGame: Boolean){

    constructor() : this("", "", 0, false, "", "" , 0, false, "", "", true)

}
