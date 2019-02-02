package pl.idappstudio.howwelldoyouknoweachother.model

data class UserQuestionData(val question: String, val canswer: String, val banswer: String, val banswer2: String, val banswer3: String, val questionId: String){

    constructor(): this("", "", "", "", "" ,"")

}