package pl.idappstudio.howwelldoyouknoweachother.util

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.startActivity
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity
import pl.idappstudio.howwelldoyouknoweachother.model.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object GameUtil {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

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

    fun getUserData(userId: String, friendId: String, onComplete: (UsersData) -> Unit){

        db.collection("users").document(userId).get().addOnSuccessListener {

            val userData: UserData = it.toObject(UserData::class.java)!!

            db.collection("users").document(friendId).get().addOnSuccessListener {

                val friendData: UserData = it.toObject(UserData::class.java)!!

                db.collection("users").document(userId).collection("friends").document(friendId).get()
                    .addOnSuccessListener {

                        val ucanswer: Int = it.getLong("canswer")!!.toInt()
                        val ubanswer: Int = it.getLong("banswer")!!.toInt()
                        val ugames: Int = it.getLong("games")!!.toInt()
                        val ugameID: String = it.getString("gameId")!!

                        val uStats = StatsData(ucanswer, ubanswer, ugames)

                        db.collection("users").document(friendId).collection("friends").document(userId).get()
                            .addOnSuccessListener {

                                val fdays: Int = it.getLong("days")!!.toInt()
                                val ffavorite: Boolean = it.getBoolean("favorite")!!
                                val fcanswer: Int = it.getLong("canswer")!!.toInt()
                                val fbanswer: Int = it.getLong("banswer")!!.toInt()
                                val fgames: Int = it.getLong("games")!!.toInt()

                                val finfo = FriendInfoData(fdays, ffavorite)
                                val fStats = StatsData(fcanswer, fbanswer, fgames)

                                db.collection("games").document(ugameID).get().addOnSuccessListener {

                                    val gamemode: String = it.getString("gamemode")!!
                                    val friendStage: Int = it.getLong("$friendId-stage")!!.toInt()
                                    val yourStage: Int = it.getLong("$userId-stage")!!.toInt()
                                    val friendTurn: Boolean = it.getBoolean("$friendId-turn")!!
                                    val yourTurn: Boolean = it.getBoolean("$userId-turn")!!
                                    val friendSet: String = it.getString("$friendId-set")!!
                                    val yourSet: String = it.getString("$userId-set")!!
                                    val newGame: Boolean = it.getBoolean("newGame")!!
                                    val userID: String = it.getString("$userId-id")!!
                                    val friendID: String = it.getString("$friendId-id")!!

                                    db.collection("set").document(yourSet).get().addOnSuccessListener {

                                        val uSet = it.toObject(UserSetData::class.java)!!

                                        db.collection("set").document(friendSet).get().addOnSuccessListener {

                                            val fSet = it.toObject(UserSetData::class.java)!!

                                            val game = GameData(
                                                yourTurn,
                                                friendTurn,
                                                yourStage,
                                                friendStage,
                                                uSet,
                                                fSet,
                                                gamemode,
                                                ugameID,
                                                newGame,
                                                userID,
                                                friendID
                                            )

                                            val gameData = UsersData(
                                                userData,
                                                friendData,
                                                finfo,
                                                uStats,
                                                fStats,
                                                game
                                            )

                                            onComplete(gameData)

                                        }

                                    }

                                }

                            }

                    }
            }

        }
    }

    fun sendAnswer(game: GameData, userData: UserData, friendData: UserData, a1: String, a2: String, a3: String, onComplete: () -> Unit){

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

    fun getQuestionData(s: String, onComplete: (QuestionData) -> Unit) {

        db.document(s).get().addOnSuccessListener {

            val questionItem = it.toObject(QuestionData::class.java)!!

            onComplete(questionItem)

        }

    }

    fun getQuestionDataStageTwo(s: String, s2: String, onComplete: (AnswerData, QuestionData) -> Unit){

        db.document(s).get().addOnSuccessListener {

            val answerItem = it.toObject(AnswerData::class.java)!!

            db.document(s2).get().addOnSuccessListener {

                val questionItem2 = it.toObject(QuestionData::class.java)!!

                onComplete(answerItem, questionItem2)

            }

        }

    }

    fun startGame(friendsData: UserData, ctx: Context){

        ctx.startActivity<GameActivity>("id" to friendsData.uid)

    }

    fun updateStats(id: String, friendId: String, canswer: Int, banswer: Int, game: Int, onComplete: () -> Unit){

        db.collection("users").document(id).collection("friends").document(friendId).update("canswer", canswer, "banswer", banswer, "games", game).addOnSuccessListener {

            onComplete()

        }

    }

    fun sendOwnQuestion(questionData: ArrayList<UserQuestionData>, game: GameData, userData: UserData, friendData: UserData, onComplete: () -> Unit) {

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

        db.collection("games").document(game.gameID).update("newGame", false)

    }

    fun updateGame(game: Int, user: String, friend: String){

        db.collection("users").document(user).collection("friends").document(friend).update("games", game + 1)

    }

}