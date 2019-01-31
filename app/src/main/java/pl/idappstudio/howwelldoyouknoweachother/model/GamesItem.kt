package pl.idappstudio.howwelldoyouknoweachother.model

class GamesItem {

    var uTurn: Boolean? = null
    var fTurn: Boolean? = null
    var uStage: Int? = null
    var fStage: Int? = null
    var uSet: String? = null
    var fSet: String? = null
    var gamemode: String? = null
    var gameID: String? = null

    constructor()

    constructor(uTurn: Boolean, fTurn: Boolean, uStage: Int, fStage: Int, uSet: String, fSet: String, gamemode: String, gameID: String){

        this.uTurn = uTurn
        this.fTurn = fTurn
        this.uStage = uStage
        this.fStage = fStage
        this.uSet = uSet
        this.fSet = fSet
        this.gamemode = gamemode
        this.gameID = gameID

    }

}
