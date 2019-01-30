package pl.idappstudio.howwelldoyouknoweachother.model

data class GameData(val uTurn: Boolean, val fTurn: Boolean, val uStage: Int, val fStage: Int, val uSet: UserSetData, val fSet: UserSetData, val gamemode: String, val gameID: String)