package pl.idappstudio.jakdobrzesieznacie.model

data class GameData(val uTurn: Boolean, val fTurn: Boolean, val uStage: Int, val fStage: Int, val uSet: UserSetData, val fSet: UserSetData, val gamemode: String, val gameID: String, val newGame: Boolean, val userID: String, val friendID: String)