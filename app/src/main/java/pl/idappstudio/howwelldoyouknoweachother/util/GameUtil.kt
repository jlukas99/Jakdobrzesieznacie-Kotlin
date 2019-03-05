package pl.idappstudio.howwelldoyouknoweachother.util

import com.google.firebase.firestore.*
import pl.idappstudio.howwelldoyouknoweachother.model.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object GameUtil {

    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private var getUserStatsListener: ListenerRegistration? = null
    private var getFriendStatsListener: ListenerRegistration? = null

    fun getSetName(s: String) : String{

        if(s == "default"){ return "domyślne pytania"
        } else if(s == "own_question") { return "własne pytania"
        } else { return s }

    }

    fun getGamemodeName(s: String) : String{

        if(s == "classic"){ return "klasyczny" }
        return "error"

    }

    fun getPrecent(stats: StatsData) : Int{

        val a: Float = stats.canswer.toFloat()
        val b: Float = stats.banswer.toFloat()

        val sum: Float = a/(a+b)
        val suma: Int = if(sum.toInt().toString().length == 1) {
            (sum*100).toInt()
        } else if(sum.toInt().toString().length == 2) {
            (sum*10).toInt()
        } else if(sum.toInt().toString().length == 3) {
            sum.toInt()
        } else {
            sum.toString().substring(0, 3).toInt()
        }

        return suma

    }

    fun getGameData(id: String, uid: String, onComplete: (GamesItem, Boolean) -> Unit){

        db.collection("games").document(id).get().addOnSuccessListener {

            if (it != null) {

                if (it.exists()) {

                    val gamemode: String = it.getString("gamemode")!!
                    val friendStage: Int = it.getLong("${uid}-stage")!!.toInt()
                    val yourStage: Int = it.getLong("${UserUtil.user.uid}-stage")!!.toInt()
                    val friendTurn: Boolean = it.getBoolean("${uid}-turn")!!
                    val yourTurn: Boolean = it.getBoolean("${UserUtil.user.uid}-turn")!!
                    val friendSet: String = it.getString("${uid}-set")!!
                    val yourSet: String = it.getString("${UserUtil.user.uid}-set")!!
                    val newGame: Boolean = it.getBoolean("newGame")!!
                    val userID: String = it.getString("${UserUtil.user.uid}-id")!!
                    val friendID: String = it.getString("${uid}-id")!!

                    val games = GamesItem(
                        it.id,
                        yourSet,
                        yourStage,
                        yourTurn,
                        userID,
                        friendSet,
                        friendStage,
                        friendTurn,
                        friendID,
                        gamemode,
                        newGame
                    )

                    onComplete(games, true)

                } else {

                    onComplete(GamesItem(), false)

                }

            } else {

                onComplete(GamesItem(), false)

            }

        }.addOnFailureListener {

            onComplete(GamesItem(), false)

        }

    }

    fun sendAnswer(game: GameData, userData: UserData, friendData: UserData, a1: String, a2: String, a3: String, onComplete: () -> Unit){

        db.firestoreSettings = settings

        val user = HashMap<String, String>()
        user.put("answer1", a1)
        user.put("answer2", a2)
        user.put("answer3", a3)

        db.collection("games").document(game.gameID).collection(userData.uid).document("1").set(user).addOnSuccessListener {

            if(game.newGame){

                FirestoreUtil.updateGameSettings(true, false, 3, 1, game.uSet.id, game.fSet.id, game.gamemode, game.gameID, userData.uid, friendData.uid)
                onComplete()

            } else {

                FirestoreUtil.updateGameSettings(true, false, 2, 1, game.uSet.id, game.fSet.id, game.gamemode, game.gameID, userData.uid, friendData.uid)
                onComplete()

            }


        }

    }

    fun sendAnswerStageThree(game: GameData, userData: UserData, friendData: UserData, a1: String, a2: String, a3: String, id1: String, id2: String, id3: String, onComplete: () -> Unit){

        db.firestoreSettings = settings

        val user = HashMap<String, String>()
        user.put("answer1", a1)
        user.put("answer2", a2)
        user.put("answer3", a3)
        user.put("id1", id1)
        user.put("id2", id2)
        user.put("id3", id3)

        db.collection("games").document(game.gameID).collection(userData.uid).document("3").set(user).addOnSuccessListener {

            FirestoreUtil.updateGameSettings(false, true, 3, 1, game.uSet.id, game.fSet.id, game.gamemode, game.gameID, userData.uid, friendData.uid)
            onComplete()


        }

    }

    fun getQuestionDataStageThree(s: String, onComplete: (QuestionData) -> Unit) {

        db.firestoreSettings = settings

        db.collection(s).get().addOnSuccessListener {

            val questionList = ArrayList<UserQuestionData>()

            for(doc in it.documents){

                val question = doc.getString("question")!!

                val a = doc.getString("a")!!
                val b = doc.getString("b")!!

                if(doc.getString("c") != null){

                    val c = doc.getString("c")!!

                    if(doc.getString("d") != null){

                        val d = doc.getString("d")!!
                        questionList.add(UserQuestionData(question, a, b ,c , d, doc.id))

                    } else {

                        questionList.add(UserQuestionData(question, a, b ,c , "", doc.id))

                    }

                } else {

                    questionList.add(UserQuestionData(question, a, b ,"" , "", doc.id))

                }

                if(it.size() == questionList.size){

                    questionList.shuffle()

                    val questionData = ArrayList<UserQuestionData>()

                    for(i in 0..2){

                        questionData.add(questionList[i])

                        if(questionData.size == 3){

                            val questionItem = QuestionData(questionData[0], questionData[1], questionData[2])

                            onComplete(questionItem)

                        }

                    }

                }

            }

        }

    }

    fun getQuestionData2(s: String, onComplete: (AnswerData, AnswerData, QuestionData) -> Unit) {

        db.firestoreSettings = settings

        db.document(s).get().addOnSuccessListener {

                val questionList = ArrayList<UserQuestionData>()
                val answerList = AnswerData(it.getString("answer1").toString(), it.getString("answer2").toString(), it.getString("answer3").toString())
                val idList = AnswerData(it.getString("id1").toString(), it.getString("id2").toString(), it.getString("id3").toString())
                val questionID = ArrayList<String>()

                questionID.add(it.getString("id1").toString())
                questionID.add(it.getString("id2").toString())
                questionID.add(it.getString("id3").toString())

                for(i in questionID){

                    db.document(i).get().addOnSuccessListener {it2 ->

                        val question = it2.getString("question")!!

                        val a = it2.getString("a")!!
                        val b = it2.getString("b")!!

                        if(it2.getString("c") != null){

                            val c = it2.getString("c")!!

                            if(it2.getString("d") != null){

                                val d = it2.getString("d")!!

                                questionList.add(UserQuestionData(question, a, b ,c , d, it2.id))

                            } else {

                                questionList.add(UserQuestionData(question, a, b ,c , "", it2.id))

                            }

                        } else {

                            questionList.add(UserQuestionData(question, a, b ,"" , "", it2.id))

                        }

                        if(questionList.size == 3){

                            val questionItem = QuestionData(questionList[0], questionList[1], questionList[2])

                            onComplete(idList, answerList, questionItem)

                        }

                    }

                }

        }

    }

    fun getQuestionData(s: String, onComplete: (QuestionData) -> Unit) {

        db.firestoreSettings = settings

        db.document(s).get().addOnSuccessListener {

            if(it.getString("id1") != null){

                getQuestionData2(s){ id, a, b ->

                    var question1 = UserQuestionData("", "", "", "", "", "")
                    var question2 = UserQuestionData("", "", "", "", "", "")
                    var question3 = UserQuestionData("", "", "", "", "", "")

                    val id1 = id.answer1.substring(id.answer1.lastIndexOf("/") + 1)
                    val id2 = id.answer2.substring(id.answer2.lastIndexOf("/") + 1)
                    val id3 = id.answer3.substring(id.answer3.lastIndexOf("/") + 1)

                    val array2 = ArrayList<UserQuestionData>()

                    array2.add(b.question)
                    array2.add(b.question1)
                    array2.add(b.question2)

                    for(i in array2){

                        if(i.questionId == id1){

                            if(a.answer1.trim() == i.a.trim()){

                                question1 = UserQuestionData(i.question, i.a, i.b, i.c, i.d, id1)

                            }

                            if(a.answer1.trim() == i.b.trim()){

                                question1 = UserQuestionData(i.question, i.b, i.a, i.c, i.d, id1)

                            }

                            if(a.answer1.trim() == i.c.trim()){

                                question1 = UserQuestionData(i.question, i.c, i.b, i.a, i.d, id1)

                            }

                            if(a.answer1.trim() == i.d.trim()){

                                question1 = UserQuestionData(i.question, i.d, i.b, i.c, i.a, id1)

                            }

                        }

                        if(i.questionId == id2){

                            if(a.answer2.trim() == i.a.trim()){

                                question2 = UserQuestionData(i.question, i.a, i.b, i.c, i.d, id2)

                            }

                            if(a.answer2.trim() == i.b.trim()){

                                question2 = UserQuestionData(i.question, i.b, i.a, i.c, i.d, id2)

                            }

                            if(a.answer2.trim() == i.c.trim()){

                                question2 = UserQuestionData(i.question, i.c, i.b, i.a, i.d, id2)

                            }

                            if(a.answer2.trim() == i.d.trim()){

                                question2 = UserQuestionData(i.question, i.d, i.b, i.c, i.a, id2)

                            }

                        }

                        if(i.questionId == id3){

                            if(a.answer3.trim() == i.a.trim()){

                                question3 = UserQuestionData(i.question, i.a, i.b, i.c, i.d, id3)

                            }

                            if(a.answer3.trim() == i.b.trim()){

                                question3 = UserQuestionData(i.question, i.b, i.a, i.c, i.d, id3)

                            }

                            if(a.answer3.trim() == i.c.trim()){

                                question3 = UserQuestionData(i.question, i.c, i.b, i.a, i.d, id3)

                            }

                            if(a.answer3.trim() == i.d.trim()){

                                question3 = UserQuestionData(i.question, i.d, i.b, i.c, i.a, id3)

                            }

                        }

                        if(question1.questionId != "" && question2.questionId != "" && question3.questionId != ""){

                            onComplete(QuestionData(question1, question2, question3))
                            break

                        }

                    }

                }

            } else {

                val questionItem = it.toObject(QuestionData::class.java)!!

                onComplete(questionItem)

            }

        }.addOnFailureListener {

            onComplete(QuestionData())

        }

    }

    fun getQuestionDataStageTwo(s: String, s2: String, onComplete: (AnswerData, QuestionData) -> Unit){

        db.firestoreSettings = settings

            db.document(s).get().addOnSuccessListener {

                val answerItem = it.toObject(AnswerData::class.java)!!

                db.document(s2).get().addOnSuccessListener {

                    if(it.getString("id1") != null){

                        getQuestionDataStageTwo2(s2){ id, a, b ->

                            var question1 = UserQuestionData("", "", "", "", "", "")
                            var question2 = UserQuestionData("", "", "", "", "", "")
                            var question3 = UserQuestionData("", "", "", "", "", "")

                            val id1 = id.answer1.substring(id.answer1.lastIndexOf("/") + 1)
                            val id2 = id.answer2.substring(id.answer2.lastIndexOf("/") + 1)
                            val id3 = id.answer3.substring(id.answer3.lastIndexOf("/") + 1)

                            val array2 = ArrayList<UserQuestionData>()

                            array2.add(b.question)
                            array2.add(b.question1)
                            array2.add(b.question2)

                            for(i in array2){

                                if(i.questionId == id1){

                                    if(a.answer1.trim() == i.a.trim()){

                                        question1 = UserQuestionData(i.question, i.a, i.b, i.c, i.d, id1)

                                    }

                                    if(a.answer1.trim() == i.b.trim()){

                                        question1 = UserQuestionData(i.question, i.b, i.a, i.c, i.d, id1)

                                    }

                                    if(a.answer1.trim() == i.c.trim()){

                                        question1 = UserQuestionData(i.question, i.c, i.b, i.a, i.d, id1)

                                    }

                                    if(a.answer1.trim() == i.d.trim()){

                                        question1 = UserQuestionData(i.question, i.d, i.b, i.c, i.a, id1)

                                    }

                                }

                                if(i.questionId == id2){

                                    if(a.answer2.trim() == i.a.trim()){

                                        question2 = UserQuestionData(i.question, i.a, i.b, i.c, i.d, id2)

                                    }

                                    if(a.answer2.trim() == i.b.trim()){

                                        question2 = UserQuestionData(i.question, i.b, i.a, i.c, i.d, id2)

                                    }

                                    if(a.answer2.trim() == i.c.trim()){

                                        question2 = UserQuestionData(i.question, i.c, i.b, i.a, i.d, id2)

                                    }

                                    if(a.answer2.trim() == i.d.trim()){

                                        question2 = UserQuestionData(i.question, i.d, i.b, i.c, i.a, id2)

                                    }

                                }

                                if(i.questionId == id3){

                                    if(a.answer3.trim() == i.a.trim()){

                                        question3 = UserQuestionData(i.question, i.a, i.b, i.c, i.d, id3)

                                    }

                                    if(a.answer3.trim() == i.b.trim()){

                                        question3 = UserQuestionData(i.question, i.b, i.a, i.c, i.d, id3)

                                    }

                                    if(a.answer3.trim() == i.c.trim()){

                                        question3 = UserQuestionData(i.question, i.c, i.b, i.a, i.d, id3)

                                    }

                                    if(a.answer3.trim() == i.d.trim()){

                                        question3 = UserQuestionData(i.question, i.d, i.b, i.c, i.a, id3)

                                    }

                                }

                                if(question1.questionId != "" && question2.questionId != "" && question3.questionId != ""){

                                    onComplete(answerItem, QuestionData(question1, question2, question3))
                                    break

                                }

                            }

                        }

                    } else {

                        val questionItem2 = it.toObject(QuestionData::class.java)!!

                        onComplete(answerItem, questionItem2)

                    }

            }

        }

        db.document(s).get().addOnSuccessListener {

            val answerItem = it.toObject(AnswerData::class.java)!!

            db.document(s2).get().addOnSuccessListener {

                val questionItem2 = it.toObject(QuestionData::class.java)!!

                onComplete(answerItem, questionItem2)

            }

        }

    }

    fun getQuestionDataStageTwo2(s: String, onComplete: (AnswerData, AnswerData, QuestionData) -> Unit) {

        db.firestoreSettings = settings

        db.document(s).get().addOnSuccessListener {

            val questionList = ArrayList<UserQuestionData>()
            val answerList = AnswerData(it.getString("answer1").toString(), it.getString("answer2").toString(), it.getString("answer3").toString())
            val idList = AnswerData(it.getString("id1").toString(), it.getString("id2").toString(), it.getString("id3").toString())
            val questionID = ArrayList<String>()

            questionID.add(it.getString("id1").toString())
            questionID.add(it.getString("id2").toString())
            questionID.add(it.getString("id3").toString())

            for(i in questionID){

                db.document(i).get().addOnSuccessListener {it2 ->

                    val question = it2.getString("question")!!

                    val a = it2.getString("a")!!
                    val b = it2.getString("b")!!

                    if(it2.getString("c") != null){

                        val c = it2.getString("c")!!

                        if(it2.getString("d") != null){

                            val d = it2.getString("d")!!

                            questionList.add(UserQuestionData(question, a, b ,c , d, it2.id))

                        } else {

                            questionList.add(UserQuestionData(question, a, b ,c , "", it2.id))

                        }

                    } else {

                        questionList.add(UserQuestionData(question, a, b ,"" , "", it2.id))

                    }

                    if(questionList.size == 3){

                        val questionItem = QuestionData(questionList[0], questionList[1], questionList[2])

                        onComplete(idList, answerList, questionItem)

                    }

                }

            }

        }

    }

    fun updateStats(friendId: String, canswer: Int, banswer: Int, game: Int, onComplete: (Boolean) -> Unit){

        db.firestoreSettings = settings

        db.collection("users").document(UserUtil.user.uid).collection("friends").document(friendId).update("canswer", canswer, "banswer", banswer, "games", game).addOnSuccessListener {

            onComplete(true)

        }.addOnFailureListener {

            onComplete(false)

        }

    }

    fun sendOwnQuestion(questionData: ArrayList<UserQuestionData>, game: GameData, userData: UserData, friendData: UserData, onComplete: () -> Unit) {

        db.firestoreSettings = settings

        val user = HashMap<String, UserQuestionData>()
        user.put("question", questionData[0])
        user.put("question1", questionData[1])
        user.put("question2", questionData[2])

        db.collection("games").document(game.gameID).collection(userData.uid).document("3").set(user).addOnSuccessListener {

            FirestoreUtil.updateGameSettings(false, true, 3, 1, game.uSet.id, game.fSet.id, game.gamemode, game.gameID, userData.uid, friendData.uid)
            onComplete()

        }

    }

    fun notNewGame(game: GameData) {

        db.firestoreSettings = settings

        db.collection("games").document(game.gameID).update("newGame", false)

    }

    fun updateGame(game: Int, user: String, friend: String){

        db.firestoreSettings = settings

        db.collection("users").document(user).collection("friends").document(friend).update("games", game + 1)

    }

    fun getSetData(id: String, onComplete: (UserSetData, Boolean) -> Unit){

        db.collection("set").document(id).get().addOnSuccessListener {

            val uSet = it.toObject(UserSetData::class.java)!!
            onComplete(uSet, true)

        }.addOnFailureListener {

            onComplete(UserSetData(), true)

        }
    }

    fun getUserStats(uid: String, onComplete: (Int, Int, Int) -> Unit){

        if(getUserStatsListener != null){
            getUserStatsListener!!.remove()
        }

        getUserStatsListener = db.collection("users").document(UserUtil.user.uid).collection("friends").document(uid).addSnapshotListener(MetadataChanges.INCLUDE, EventListener<DocumentSnapshot> { it, e ->

                if (e != null) {
                    return@EventListener
                }

                if (it == null) {
                    return@EventListener
                }

                if(it.exists()) {

                    val canswer = it.getLong("canswer")!!.toInt()
                    val banswer = it.getLong("banswer")!!.toInt()
                    val games = it.getLong("games")!!.toInt()

                    onComplete(canswer, banswer, games)

                } else {

                    onComplete(-1, -1, -1)

                }

            })

    }

    fun getFriendStats(uid: String, onComplete: (Int, Int, Int) -> Unit){

        if(getFriendStatsListener != null){
            getFriendStatsListener!!.remove()
        }

        getFriendStatsListener = db.collection("users").document(uid).collection("friends").document(UserUtil.user.uid).addSnapshotListener(MetadataChanges.INCLUDE, EventListener<DocumentSnapshot> { it, e ->

            if (e != null) {
                return@EventListener
            }

            if (it == null) {
                return@EventListener
            }

            if(it.exists()) {

                val canswer = it.getLong("canswer")!!.toInt()
                val banswer = it.getLong("banswer")!!.toInt()
                val games = it.getLong("games")!!.toInt()

                onComplete(canswer, banswer, games)

            } else {

                onComplete(-1, -1, -1)

            }

        })

    }

    fun removeListenerStats(){

        if(getUserStatsListener != null){
            getUserStatsListener!!.remove()
        }

        if(getFriendStatsListener != null){
            getFriendStatsListener!!.remove()
        }

    }

}