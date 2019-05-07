package pl.idappstudio.jakdobrzesieznacie.model

data class UserQuestionData(var question: String, var a: String, var b: String, var c: String, var d: String, var questionId: String) {

    constructor(question: String, a: String, b: String, c: String, questionId: String) : this(question, a, b, c, "", questionId)

    constructor(question: String, a: String, b: String, questionId: String) : this(question, a, b, "", questionId)

    constructor() : this("", "", "", "", "", "")

}