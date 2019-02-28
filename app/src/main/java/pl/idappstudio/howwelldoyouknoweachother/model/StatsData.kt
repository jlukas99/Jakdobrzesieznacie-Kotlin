package pl.idappstudio.howwelldoyouknoweachother.model

data class StatsData(val canswer: Int, val banswer: Int, val games: Int){

    constructor() : this(-1, -1, -1)

}