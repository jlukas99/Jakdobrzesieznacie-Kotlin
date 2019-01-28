package pl.idappstudio.howwelldoyouknoweachother.model

data class GameData(val yourTurn: Boolean, val friendTurn: Boolean, val yourStage: Int, val friendStage: Int, val youtSet: YourSetData, val friendSet: FriendSetData, val gamemode: String, val gameID: String)