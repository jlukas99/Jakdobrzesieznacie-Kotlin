package pl.idappstudio.jakdobrzesieznacie.model

data class QuestionData(val question: UserQuestionData, val question1: UserQuestionData, val question2: UserQuestionData) {

    constructor(): this(UserQuestionData("", "", "" ,"", "", ""),
        UserQuestionData("", "", "" ,"", "", ""),
        UserQuestionData("", "", "" ,"", "", ""))


}