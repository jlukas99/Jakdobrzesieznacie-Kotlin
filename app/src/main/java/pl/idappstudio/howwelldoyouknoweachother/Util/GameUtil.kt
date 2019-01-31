package pl.idappstudio.howwelldoyouknoweachother.util

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.startActivity
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity
import pl.idappstudio.howwelldoyouknoweachother.model.*
import java.util.*
import kotlin.collections.ArrayList

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
                                                ugameID
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

    fun checkStage(game: GameData, friend: UserData, ctx: Context){

//        if(game.friendStage == 0 && game.yourStage == 0){
//
            newGame(ctx, friend.uid)
//            return
//
//        }
//
//        if(game.friendStage == 1 && game.yourStage == 0){
//
//            newGame(ctx, friend.uid)
//            return
//
//        }
//
//        return

    }

    fun newGame(ctx: Context, id: String){

        ctx.startActivity<GameActivity>("id" to id)

    }

    fun startGame(game: GameData, friendsData: UserData, ctx: Context){

        checkStage(game, friendsData, ctx)

    }

    fun sendOwnQuestion(questionData: ArrayList<UserQuestionData>, game: GameData, userData: UserData, friendData: UserData, onComplete: () -> Unit) {

        val user = HashMap<String, ArrayList<UserQuestionData>>()
        user.put("question", questionData)

        db.collection("games").document(game.gameID).collection(userData.uid).document("3").set(user).addOnSuccessListener {

            FirestoreUtil.updateGameSettings(false, true, 3, 1, game.uSet.id, game.fSet.id, game.gamemode, game.gameID, userData.uid, friendData.uid)
            onComplete()

        }

    }

}