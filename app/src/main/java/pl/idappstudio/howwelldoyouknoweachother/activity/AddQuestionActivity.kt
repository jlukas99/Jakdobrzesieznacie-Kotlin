package pl.idappstudio.howwelldoyouknoweachother.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import kotlinx.android.synthetic.main.activity_add_question.*
import kotlinx.android.synthetic.main.question_number_item.view.*
import pl.idappstudio.howwelldoyouknoweachother.R
import pl.idappstudio.howwelldoyouknoweachother.adapter.QuestionNumberAdapater
import pl.idappstudio.howwelldoyouknoweachother.interfaces.ClickAnswer
import pl.idappstudio.howwelldoyouknoweachother.model.UserQuestionData

class AddQuestionActivity : AppCompatActivity(), TextWatcher {

    override fun afterTextChanged(s: Editable?) {

        checkEditText()

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    private val dbSet = FirebaseFirestore.getInstance().collection("set")

    private var setListener: ListenerRegistration? = null

    private lateinit var setId: String
    private lateinit var dataAnswer: HashMap<String, String>

    private lateinit var questionText: EditText

    private lateinit var aAnswerText: EditText
    private lateinit var bAnswerText: EditText
    private lateinit var cAnswerText: EditText
    private lateinit var dAnswerText: EditText

    private lateinit var questionCheck: ImageView

    private lateinit var aAnswerCheck: ImageView
    private lateinit var bAnswerCheck: ImageView
    private lateinit var cAnswerCheck: ImageView
    private lateinit var dAnswerCheck: ImageView

    private lateinit var snackbar: Snackbar
    private lateinit var saveQuestion: Button

    private var position: Int = -1

    private val questionSection = Section()

    private val questionNumberAdapater = GroupAdapter<ViewHolder>()

    companion object {

        val questionNumberItems = ArrayList<QuestionNumberAdapater>()

    }

    private fun addItem(data: UserQuestionData) :  ArrayList<QuestionNumberAdapater> {
        questionNumberItems.add(QuestionNumberAdapater(data))
        return questionNumberItems
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)

        questionText = findViewById(R.id.questionEditText)

        aAnswerText = findViewById(R.id.aAnswerEditText)
        bAnswerText = findViewById(R.id.bAnswerEditText)
        cAnswerText = findViewById(R.id.cAnswerEditText)
        dAnswerText = findViewById(R.id.dAnswerEditText)

        questionCheck = findViewById(R.id.dTextAnswer6)

        aAnswerCheck = findViewById(R.id.dTextAnswer5)
        bAnswerCheck = findViewById(R.id.dTextAnswer4)
        cAnswerCheck = findViewById(R.id.dTextAnswer3)
        dAnswerCheck = findViewById(R.id.dTextAnswer2)

        saveQuestion = findViewById(R.id.nextQuestionBtn)

        hideSection()

        image_section.setColorFilter(
            ContextCompat.getColor(
                this, R.color.colorLigth
            ), android.graphics.PorterDuff.Mode.SRC_IN)

        questionText.addTextChangedListener(this)

        aAnswerText.addTextChangedListener(this)
        bAnswerText.addTextChangedListener(this)
        cAnswerText.addTextChangedListener(this)
        dAnswerText.addTextChangedListener(this)

        val rvGamesLLM = LinearLayoutManager(this)

        rvGamesLLM.orientation = RecyclerView.HORIZONTAL

        rv_games.layoutManager = rvGamesLLM
        rv_games.itemAnimator = SlideInDownAnimator()

        questionSection.setHideWhenEmpty(true)

        questionNumberAdapater.setOnItemClickListener { item, view ->

            if (item is QuestionNumberAdapater) {

                if(position != -1) {

                    questionSection.notifyItemChanged(position)

                }

                position = item.pos

                view.question_number_background.setImageResource(R.drawable.number_correct_overlay)

                clear()
                setField(item.data)

            }

        }

        fab_add_question.setOnClickListener {

            clear()

            val data = HashMap<String, String>()
            data.put("question", "")
            data.put("a", "")
            data.put("b", "")

            dbSet.document(intent?.extras?.getString("id")!!).collection("questions").add(data).addOnSuccessListener {}

        }

        questionNumberAdapater.add(0, questionSection)

        rv_games.adapter = questionNumberAdapater

        getQuestions()

    }

    fun getQuestions(){

        if(setListener != null){
            setListener?.remove()
        }

        questionNumberItems.clear()

        setListener = dbSet.document(intent?.extras?.getString("id")!!).collection("questions").addSnapshotListener(EventListener<QuerySnapshot> {doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc == null){
                return@EventListener
            }

            if(doc.isEmpty){
                questionSection.update(questionNumberItems)
            }

            for(it in doc.documentChanges){

                if(it.type == DocumentChange.Type.REMOVED){

                    getQuestions()

                }

                if(it.type == DocumentChange.Type.MODIFIED){

                    getQuestions()

                }

                if(it.type == DocumentChange.Type.ADDED){

                    var data: UserQuestionData

                    if(it.document.getString("c") != null){

                        data = UserQuestionData(it.document.getString("question")!!,
                            it.document.getString("a")!!,
                            it.document.getString("b")!!,
                            it.document.getString("c")!!,
                            it.document.id)

                        if(it.document.getString("d") != null) {

                            data = UserQuestionData(it.document.getString("question")!!,
                                it.document.getString("a")!!,
                                it.document.getString("b")!!,
                                it.document.getString("c")!!,
                                it.document.getString("d")!!,
                                it.document.id)

                        }

                    } else {

                        data = UserQuestionData(it.document.getString("question")!!,
                            it.document.getString("a")!!,
                            it.document.getString("b")!!,
                            it.document.id)

                    }

                    setId = data.questionId

                    deleteButton()

                    addItem(data)
                    showSection()
                    nextQuestion()

                }

            }


        })

    }

    fun setField(data: UserQuestionData){

        questionText.setText(data.question)

        aAnswerText.setText(data.a)
        bAnswerText.setText(data.b)
        cAnswerText.setText(data.c)
        dAnswerText.setText(data.d)

        setId = data.questionId

        deleteButton()
        checkEditText()

        showSection()

    }

    fun showSection(){

        if(questionNumberItems.isEmpty()){

            editext_section.visibility = View.INVISIBLE

            text_section.visibility = View.VISIBLE
            image_section.visibility = View.VISIBLE

            text_btn_add.visibility = View.VISIBLE

            return

        }

        if(!questionNumberItems.isEmpty()){

            text_btn_add.visibility = View.GONE

            editext_section.visibility = View.VISIBLE

            text_section.visibility = View.INVISIBLE
            image_section.visibility = View.INVISIBLE

            return

        }

    }

    fun hideSection(){

        if(questionNumberItems.isEmpty()){

            editext_section.visibility = View.INVISIBLE

            text_section.visibility = View.VISIBLE
            image_section.visibility = View.VISIBLE

            text_btn_add.visibility = View.VISIBLE

            return

        }

        if(!questionNumberItems.isEmpty()){

            text_btn_add.visibility = View.GONE

            editext_section.visibility = View.INVISIBLE

            text_section.visibility = View.VISIBLE
            image_section.visibility = View.VISIBLE

            return

        }

    }

    fun checkAnswers(onComplete: (Boolean) -> Unit) {

        if(!questionText.text.isNullOrBlank()) {

            if(aAnswerText.text.isNullOrBlank()){

                snackbar = Snackbar.make(this.view!!, "Uzupełnij odpowiedź A", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()
                onComplete(false)
                return
            }

            if(bAnswerText.text.isNullOrBlank()){

                snackbar = Snackbar.make(this.view!!, "Uzupełnij odpowiedź B", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()
                onComplete(false)
                return
            }

            val q = questionText.text.toString()
            var ca = ""
            var ba1 = ""
            var ba2 = ""
            var ba3 = ""

            val data = HashMap<String, String>()

            ca = aAnswerText.text.toString()
            ba1 = bAnswerText.text.toString()

            data.put("question", q)
            data.put("a", ca)
            data.put("b", ba1)

            if (!cAnswerText.text.isNullOrBlank()) {

                ba2 = cAnswerText.text.toString()
                data.put("c", ba2)

            } else {

                ba2 = ""

            }

            if (!dAnswerText.text.isNullOrBlank()) {

                ba3 = dAnswerText.text.toString()
                data.put("d", ba3)

            } else {

                ba3 = ""

            }

            if(ca.equals(ba1, true) || ca.equals(ba2, true) || ca.equals(ba3, true) ||
                ba1.equals(ca, true) || ba1.equals(ba2, true) || ba1.equals(ba3, true)) {

                snackbar = Snackbar.make(this.view!!, "Odpowiedzi nie mogą się powtarzać", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()
                onComplete(false)
                return

            }

            if((ba2 != "") && (ba2.equals(ca, true) || ba2.equals(ba1, true) || ba2.equals(ba3, true))){

                snackbar = Snackbar.make(this.view!!, "Odpowiedzi nie mogą się powtarzać", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()
                onComplete(false)
                return

            }

            if((ba3 != "") && (ba3.equals(ca, true) || ba3.equals(ba2, true) || ba3.equals(ba1, true))){

                snackbar = Snackbar.make(this.view!!, "Odpowiedzi nie mogą się powtarzać", 2500)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
                snackbar.show()
                onComplete(false)
                return
            }

            lockEditText()
            questionText.isEnabled = false

            dataAnswer = data

            onComplete(true)

        } else {

            snackbar = Snackbar.make(this.view!!, "Wpisz treść pytania", 2500)
            snackbar.view.setBackgroundColor(resources.getColor(R.color.colorRed))
            snackbar.show()

            onComplete(false)

        }

    }

    fun clear(){

        questionText.text.clear()

        aAnswerText.text.clear()
        bAnswerText.text.clear()
        cAnswerText.text.clear()
        dAnswerText.text.clear()

        questionCheck.visibility = View.INVISIBLE

        aAnswerCheck.visibility = View.INVISIBLE
        bAnswerCheck.visibility = View.INVISIBLE
        cAnswerCheck.visibility = View.INVISIBLE
        dAnswerCheck.visibility = View.INVISIBLE

        aAnswerCheck.tag = ""
        bAnswerCheck.tag = ""
        cAnswerCheck.tag = ""
        dAnswerCheck.tag = ""

        aAnswerCheck.setImageResource(R.drawable.ic_check_icon)
        bAnswerCheck.setImageResource(R.drawable.ic_check_icon)
        cAnswerCheck.setImageResource(R.drawable.ic_check_icon)
        dAnswerCheck.setImageResource(R.drawable.ic_check_icon)

        unlockEditText()

        showSection()

    }

    fun nextQuestion(){

        if(questionSection.itemCount > 0) {
            questionSection.getItem(questionSection.itemCount - 1).notifyChanged()
        }

        questionSection.update(questionNumberItems)

    }

    fun saveButton(){

        saveQuestion.text = "ZAPISZ ZMIANY"
        saveQuestion.background.setColorFilter(
            ContextCompat.getColor(
                this, R.color.colorAccent
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )

        saveQuestion.setOnClickListener {

            checkAnswers {

                if (it) {

                    if (!setId.isBlank() && !dataAnswer.isEmpty()) {

                        dbSet.document(intent?.extras?.getString("id")!!).collection("questions").document(setId).set(dataAnswer).addOnSuccessListener {

                                deleteButton()

                            }

                    }

                }

            }

        }

    }

    fun deleteButton(){

        saveQuestion.text = "USUŃ PYTANIE"
        saveQuestion.background.setColorFilter(
            ContextCompat.getColor(
                this, R.color.colorRed
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )

        saveQuestion.setOnClickListener {

            if(!setId.isBlank()) {

                dbSet.document(intent?.extras?.getString("id")!!).collection("questions").document(setId).delete()
                    .addOnSuccessListener {

                        getQuestions()

                    }

            }

        }

    }

    fun checkEditText(){

        if(questionText.text.isNullOrBlank()){

            questionCheck.visibility = View.INVISIBLE
            return

        } else {

            questionCheck.visibility = View.VISIBLE
            aAnswerText.isEnabled = true

        }

        if(aAnswerText.text.isNullOrBlank()){

            aAnswerCheck.visibility = View.INVISIBLE
            return

        } else {

            aAnswerCheck.visibility = View.VISIBLE
            bAnswerText.isEnabled = true

        }

        if(bAnswerText.text.isNullOrBlank()){

            bAnswerCheck.visibility = View.INVISIBLE
            return

        } else {

            bAnswerCheck.visibility = View.VISIBLE
            cAnswerText.isEnabled = true

            saveButton()

        }

        if(cAnswerText.text.isNullOrBlank()){

            cAnswerCheck.visibility = View.INVISIBLE
            return

        } else {

            cAnswerCheck.visibility = View.VISIBLE
            dAnswerText.isEnabled = true

        }

        if(dAnswerText.text.isNullOrBlank()){

            dAnswerCheck.visibility = View.INVISIBLE
            return

        } else {

            dAnswerCheck.visibility = View.VISIBLE

        }

    }

    fun lockEditText(){

        aAnswerText.isEnabled = false
        bAnswerText.isEnabled = false
        cAnswerText.isEnabled = false
        dAnswerText.isEnabled = false

    }

    fun unlockEditText(){

        questionText.isEnabled = true
        aAnswerText.isEnabled = false
        bAnswerText.isEnabled = false
        cAnswerText.isEnabled = false
        dAnswerText.isEnabled = false

    }

    override fun onResume() {
        super.onResume()
        hideNavigationBar()
    }

    private fun hideNavigationBar() {

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.decorView.systemUiVisibility = flags

    }

}
