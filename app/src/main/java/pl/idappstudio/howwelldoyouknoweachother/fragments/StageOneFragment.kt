package pl.idappstudio.howwelldoyouknoweachother.fragments


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.firebase.storage.FirebaseStorage

import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.friends
import pl.idappstudio.howwelldoyouknoweachother.activity.GameActivity.Companion.user
import pl.idappstudio.howwelldoyouknoweachother.glide.GlideApp
import pl.idappstudio.howwelldoyouknoweachother.interfaces.nextFragment
import pl.idappstudio.howwelldoyouknoweachother.util.GameUtil

class StageOneFragment(private val listener: nextFragment) : Fragment(), View.OnClickListener {

    override fun onClick(v: View?) {

        if(v != null){

            lockButton()

            checkAnswer(v, canswer)

        }

    }

    private lateinit var stageTitle: TextView

    private lateinit var questionText: TextView

    private lateinit var aAnswerText: TextView
    private lateinit var bAnswerText: TextView
    private lateinit var cAnswerText: TextView
    private lateinit var dAnswerText: TextView

    private lateinit var aAnswerUserImage: ImageView
    private lateinit var bAnswerUserImage: ImageView
    private lateinit var cAnswerUserImage: ImageView
    private lateinit var dAnswerUserImage: ImageView

    private lateinit var aAnswerFriendImage: ImageView
    private lateinit var bAnswerFriendImage: ImageView
    private lateinit var cAnswerFriendImage: ImageView
    private lateinit var dAnswerFriendImage: ImageView

    private lateinit var aAnswerButton: ConstraintLayout
    private lateinit var bAnswerButton: ConstraintLayout
    private lateinit var cAnswerButton: ConstraintLayout
    private lateinit var dAnswerButton: ConstraintLayout

    private lateinit var nextQuestion: Button
    private lateinit var rejectBadAnswer: Button

    private lateinit var rewardedVideoAd: RewardedVideoAd

    private var badAnswer: ArrayList<String> = ArrayList()
    private var userAnswer: ArrayList<String> = ArrayList()

    private var questionNumber = 1

    private var question = ""
    private var canswer = ""
    private var banswer = ""
    private var banswer2 = ""
    private var banswer3 = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_stage_one, container, false)

        MobileAds.initialize(this.context, resources.getString(R.string.adMob_id))

        stageTitle = rootView.findViewById(R.id.gameStageTitle)

        questionText = rootView.findViewById(R.id.questionEditText)

        aAnswerText = rootView.findViewById(R.id.aAnswerEditText)
        bAnswerText = rootView.findViewById(R.id.bAnswerEditText)
        cAnswerText = rootView.findViewById(R.id.cAnswerEditText)
        dAnswerText = rootView.findViewById(R.id.dAnswerEditText)

        aAnswerUserImage = rootView.findViewById(R.id.aAnswerUserProfile)
        bAnswerUserImage = rootView.findViewById(R.id.bAnswerUserProfile)
        cAnswerUserImage = rootView.findViewById(R.id.cAnswerUserProfile)
        dAnswerUserImage = rootView.findViewById(R.id.dAnswerUserProfile)

        aAnswerFriendImage = rootView.findViewById(R.id.aAnswerFriendProfile)
        bAnswerFriendImage = rootView.findViewById(R.id.bAnswerFriendProfile)
        cAnswerFriendImage = rootView.findViewById(R.id.cAnswerFriendProfile)
        dAnswerFriendImage = rootView.findViewById(R.id.dAnswerFriendProfile)

        aAnswerButton = rootView.findViewById(R.id.answer_btn_a)
        bAnswerButton = rootView.findViewById(R.id.answer_btn_b)
        cAnswerButton = rootView.findViewById(R.id.answer_btn_c)
        dAnswerButton = rootView.findViewById(R.id.answer_btn_d)

        aAnswerButton.setOnClickListener(this)
        bAnswerButton.setOnClickListener(this)
        cAnswerButton.setOnClickListener(this)
        dAnswerButton.setOnClickListener(this)

        nextQuestion = rootView.findViewById(R.id.nextQuestionBtn)
        rejectBadAnswer = rootView.findViewById(R.id.loadingAds)

        rejectBadAnswer.setOnClickListener { loadRewardedVideoAd() }

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this.context)
        rewardedVideoAd.rewardedVideoAdListener = object : RewardedVideoAdListener {

            override fun onRewarded(p0: RewardItem?) { rejectAnswer() }

            override fun onRewardedVideoAdLeftApplication() {}

            override fun onRewardedVideoAdClosed() {}

            override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {}

            override fun onRewardedVideoAdLoaded() { showAd() }

            override fun onRewardedVideoAdOpened() {}

            override fun onRewardedVideoStarted() {}

            override fun onRewardedVideoCompleted() {}


        }


        loadImage()

        lockButton()

        setText()

        return rootView
    }

    private fun showAd(){

        if(rewardedVideoAd.isLoaded){

            rewardedVideoAd.show()

        }

    }

    private fun loadRewardedVideoAd() {
        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build())
    }

    private fun resetButton(){

        nextQuestion.text = "Zgadnij odpowiedź znajomego"
        nextQuestion.background.setColorFilter(
            ContextCompat.getColor(
                this.context!!, R.color.colorBadAnswer
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        stageTitle.text = "Zgadnij Odpowiedzi"

        aAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        bAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        cAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)
        dAnswerButton.background = resources.getDrawable(R.drawable.card_background_dark)

        aAnswerFriendImage.visibility = View.INVISIBLE
        bAnswerFriendImage.visibility = View.INVISIBLE
        cAnswerFriendImage.visibility = View.INVISIBLE
        dAnswerFriendImage.visibility = View.INVISIBLE

        aAnswerUserImage.visibility = View.INVISIBLE
        bAnswerUserImage.visibility = View.INVISIBLE
        cAnswerUserImage.visibility = View.INVISIBLE
        dAnswerUserImage.visibility = View.INVISIBLE

        aAnswerButton.visibility = View.INVISIBLE
        bAnswerButton.visibility = View.INVISIBLE
        cAnswerButton.visibility = View.INVISIBLE
        dAnswerButton.visibility = View.INVISIBLE

        rejectBadAnswer.visibility = View.GONE

        rejectBadAnswer.isEnabled = true

    }

    private fun lockButton(){

        aAnswerButton.isEnabled = false
        bAnswerButton.isEnabled = false
        cAnswerButton.isEnabled = false
        dAnswerButton.isEnabled = false

        rejectBadAnswer.isEnabled = false

    }

    private fun setText(){

        resetButton()

        aAnswerText.text = ""
        bAnswerText.text = ""
        cAnswerText.text = ""
        dAnswerText.text = ""

        if(questionNumber == 1){

            question = GameActivity.questionList.question.question
            canswer = GameActivity.questionList.question.canswer
            banswer = GameActivity.questionList.question.banswer
            banswer2 = GameActivity.questionList.question.banswer2
            banswer3 = GameActivity.questionList.question.banswer3

        } else if(questionNumber == 2){

            question = GameActivity.questionList.question1.question
            canswer = GameActivity.questionList.question1.canswer
            banswer = GameActivity.questionList.question1.banswer
            banswer2 = GameActivity.questionList.question1.banswer2
            banswer3 = GameActivity.questionList.question1.banswer3

        } else if(questionNumber == 3){

            question = GameActivity.questionList.question2.question
            canswer = GameActivity.questionList.question2.canswer
            banswer = GameActivity.questionList.question2.banswer
            banswer2 = GameActivity.questionList.question2.banswer2
            banswer3 = GameActivity.questionList.question2.banswer3

        }

        questionText.text = question

        badAnswer.clear()

        if(banswer != ""){
            badAnswer.add(banswer)
        }

        if(banswer2 != ""){
            badAnswer.add(banswer2)
        }

        if(banswer3 != ""){
            badAnswer.add(banswer3)
        }

        val array = ArrayList<String>()
        array.add(canswer)
        array.add(banswer)

        if(banswer2 != "" && banswer3 != ""){

            array.add(banswer2)
            array.add(banswer3)

        } else if(banswer2 != ""){

            array.add(banswer2)

        } else if(banswer3 != ""){

            array.add(banswer3)

        }

        array.shuffle()

        if(array.size > 2){

            rejectBadAnswer.visibility = View.VISIBLE

        }

        if(aAnswerText.text == ""){

                for(a in array){

                    if(a != ""){

                        aAnswerText.text = a
                        array.remove(a)

                        aAnswerButton.isEnabled = true
                        aAnswerButton.visibility = View.VISIBLE

                        break

                    }

                }

            }

        if(bAnswerText.text == ""){

                for(a in array){

                    if(a != ""){

                        bAnswerText.text = a
                        array.remove(a)

                        bAnswerButton.isEnabled = true
                        bAnswerButton.visibility = View.VISIBLE

                        break

                    }

                }

            }

        if(cAnswerText.text == ""){

                for(a in array){

                    if(a != ""){

                        cAnswerText.text = a
                        array.remove(a)

                        cAnswerButton.isEnabled = true
                        cAnswerButton.visibility = View.VISIBLE

                        break

                    }

                }

            }

        if(dAnswerText.text == ""){

                for(a in array){

                    if(a != ""){

                        dAnswerText.text = a
                        array.remove(a)

                        dAnswerButton.isEnabled = true
                        dAnswerButton.visibility = View.VISIBLE

                        break

                    }

                }

            }

    }

    private fun rejectAnswer(){

        setText()

        badAnswer.shuffle()

        Log.d("TAG", badAnswer[0])

        if(aAnswerText.text.equals(badAnswer[0])){

            aAnswerButton.isEnabled = false

            aAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            Log.d("TAG", "A")

            return
        }

        if(bAnswerText.text.equals(badAnswer[0])){

            bAnswerButton.isEnabled = false

            bAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            Log.d("TAG", "B")

            return
        }

        if(cAnswerText.text.equals(badAnswer[0])){

            cAnswerButton.isEnabled = false

            cAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            Log.d("TAG", "C")

            return
        }

        if(dAnswerText.text.equals(badAnswer[0])){

            dAnswerButton.isEnabled = false

            dAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

            Log.d("TAG", "D")

            return
        }

    }

    private fun nextQuestion() {

        if (questionNumber == 3) {

            nextQuestion.text = "nastepny etap"
            nextQuestion.background.setColorFilter(
                ContextCompat.getColor(
                    this.context!!, R.color.colorCorrectAnswer
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

        } else {

            nextQuestion.text = "następne pytanie"
            nextQuestion.background.setColorFilter(
                ContextCompat.getColor(
                    this.context!!, R.color.colorPrimary
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

        }


        nextQuestion.setOnClickListener {

            questionNumber++

            if (questionNumber != 4) {

                lockButton()
                setText()

            } else {

                GameActivity.updateStats { }

                GameUtil.sendAnswer(GameActivity.game, GameActivity.user, GameActivity.friends, userAnswer[0], userAnswer[1], userAnswer[2]){ }

                listener.next()

            }
        }

    }

    private fun loadImage(){

        loop@for(i in 0..3){

            if(i == 0){

                setFriendImage(aAnswerFriendImage)
                setUserImage(aAnswerUserImage)

                continue@loop

            }

            if(i == 1){

                setFriendImage(bAnswerFriendImage)
                setUserImage(bAnswerUserImage)

                continue@loop

            }

            if(i == 2){

                setFriendImage(cAnswerFriendImage)
                setUserImage(cAnswerUserImage)

                continue@loop

            }

            if(i == 3){

                setFriendImage(dAnswerFriendImage)
                setUserImage(dAnswerUserImage)

                continue@loop

            }

        }

    }

    private fun checkAnswer(view: View, s: String){

        if(view == aAnswerButton){

            userAnswer.add(aAnswerText.text.toString())

            if(aAnswerText.text == s) {

                aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                aAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            aAnswerUserImage.visibility = View.VISIBLE

        } else if(view == bAnswerButton){

            userAnswer.add(bAnswerText.text.toString())

            if(bAnswerText.text == s) {

                bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                bAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            bAnswerUserImage.visibility = View.VISIBLE

        } else if(view == cAnswerButton){

            userAnswer.add(cAnswerText.text.toString())

            if(cAnswerText.text == s) {

                cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                cAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            cAnswerUserImage.visibility = View.VISIBLE

        } else if(view == dAnswerButton){

            userAnswer.add(dAnswerText.text.toString())

            if(dAnswerText.text == s) {

                dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)

                listener.updateNumber(questionNumber, true)

                GameActivity.canswer++

            } else {

                dAnswerButton.background = resources.getDrawable(R.drawable.number_bad_overlay)

                listener.updateNumber(questionNumber, false)

                GameActivity.banswer++

            }

            checkCorrectAnswer(s)
            dAnswerUserImage.visibility = View.VISIBLE

        }

        nextQuestion()

    }

    private fun checkCorrectAnswer(s: String){

        if(aAnswerText.text == s){

            aAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            aAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(bAnswerText.text == s){

            bAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            bAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(cAnswerText.text == s){

            cAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            cAnswerFriendImage.visibility = View.VISIBLE

            return

        }

        if(dAnswerText.text == s){

            dAnswerButton.background = resources.getDrawable(R.drawable.number_correct_overlay)
            dAnswerFriendImage.visibility = View.VISIBLE

            return

        }

    }

    private fun setUserImage(v: ImageView){

        if(user.image.contains("logo")){

            Glide.with(this).load(R.mipmap.logo).listener(object :
                RequestListener<Drawable> {

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }

            }).into(v)

        } else {

            if (user.fb) {

                GlideApp.with(this.context!!).load("http://graph.facebook.com/${user.image}/picture?type=large").diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(v)

            } else {

                val storageReference =
                    FirebaseStorage.getInstance().reference.child("profile_image").child(user.image + "-image")
                        .downloadUrl

                storageReference.addOnSuccessListener { Uri ->

                    GlideApp.with(this.context!!).load(Uri.toString()).diskCacheStrategy(DiskCacheStrategy.ALL).listener(object :
                        RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(v)

                }.addOnFailureListener {

                    GlideApp.with(this.context!!).load(R.mipmap.logo).diskCacheStrategy(DiskCacheStrategy.ALL).listener(object :
                        RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(v)
                }
            }
        }
    }

    private fun setFriendImage(v: ImageView){

        if(friends.image.contains("logo")){

            Glide.with(this).load(R.mipmap.logo).listener(object :
                RequestListener<Drawable> {

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }

            }).into(v)

        } else {

            if (friends.fb) {

                GlideApp.with(this.context!!).load("http://graph.facebook.com/${friends.image}/picture?type=large").diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(v)

            } else {

                val storageReference =
                    FirebaseStorage.getInstance().reference.child("profile_image").child(friends.image + "-image")
                        .downloadUrl

                storageReference.addOnSuccessListener { Uri ->

                    GlideApp.with(this.context!!).load(Uri.toString()).diskCacheStrategy(DiskCacheStrategy.ALL).listener(object :
                        RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(v)

                }.addOnFailureListener {

                    GlideApp.with(this.context!!).load(R.mipmap.logo).diskCacheStrategy(DiskCacheStrategy.ALL).listener(object :
                        RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(v)
                }
            }
        }
    }

}
