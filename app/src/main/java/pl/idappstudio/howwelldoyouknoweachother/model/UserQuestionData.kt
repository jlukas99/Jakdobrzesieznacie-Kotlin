package pl.idappstudio.howwelldoyouknoweachother.model

data class UserQuestionData(val question: String, val a: String, val b: String, val c: String, val d: String, val questionId: String) {

    constructor(question: String, a: String, b: String, c: String, questionId: String) : this(question, a, b, c, "", questionId)

    constructor(question: String, a: String, b: String, questionId: String) : this(question, a, b, "", questionId)

    constructor() : this("", "", "", "", "", "")

}