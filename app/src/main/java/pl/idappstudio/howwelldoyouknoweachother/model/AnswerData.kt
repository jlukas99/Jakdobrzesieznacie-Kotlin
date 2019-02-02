package pl.idappstudio.howwelldoyouknoweachother.model

data class AnswerData(val answer1: String, val answer2: String, val answer3: String){

    constructor(): this("", "", "")

}