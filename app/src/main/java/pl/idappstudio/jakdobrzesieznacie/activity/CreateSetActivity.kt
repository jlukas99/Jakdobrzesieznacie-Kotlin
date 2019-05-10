package pl.idappstudio.jakdobrzesieznacie.activity

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_create_set.*
import pl.idappstudio.jakdobrzesieznacie.R
import pl.idappstudio.jakdobrzesieznacie.model.*
import android.content.Context
import android.view.inputmethod.InputMethodManager
import org.jetbrains.anko.startActivity

class CreateSetActivity : AppCompatActivity() {

    private lateinit var snackbar: Snackbar

    private lateinit var setDialog: Dialog

    private lateinit var set: SetItem

    private val dbSet = FirebaseFirestore.getInstance().collection("set")

    private var setListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_set)

        pack_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

                setButtonQuit(true)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                pack_name_text.text = s

            }

        })

        edit_text_button.setOnClickListener {

            edit_text_button.visibility = View.GONE

            pack_name.requestFocus()
            pack_name.text.clear()
            pack_name.hint = set.name
            pack_name_text.visibility = View.INVISIBLE
            pack_name.visibility = View.VISIBLE

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(pack_name, InputMethodManager.SHOW_IMPLICIT)

        }

        pack_chat.setOnClickListener {

            dbSet.document(intent?.extras?.getString("id")!!).delete().addOnSuccessListener {

                onBackPressed()

            }

        }

        pack_add_question_btn.setOnClickListener {

            startActivity<AddQuestionActivity>("id" to intent?.extras?.getString("id")!!)

        }

        blockFunction()

        getInformation()

        getSetData()

        setButtonQuit(false)

    }

    fun setButtonQuit(b: Boolean){

        if(b) {

            pack_quit_btn.text = "ZAPISZ ZMIANY"
            pack_quit_btn.background.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.colorAccent
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

            pack_quit_btn.setOnClickListener {

                dbSet.document(intent?.extras?.getString("id")!!).update("name", pack_name_text.text.toString())
                    .addOnSuccessListener {

                        setButtonQuit(false)

                        pack_name_text.visibility = View.VISIBLE
                        pack_name.visibility = View.INVISIBLE
                        edit_text_button.visibility = View.VISIBLE

                    }
            }

        } else {

            pack_quit_btn.text = "WRÓC"
            pack_quit_btn.background.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.colorPrimary
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

            pack_quit_btn.setOnClickListener {

                onBackPressed()

            }

        }

    }

    fun getSetData(){

        setListener = dbSet.document(intent.extras.getString("id").toString()).addSnapshotListener(EventListener<DocumentSnapshot> {doc, e ->

            if(e != null){
                return@EventListener
            }

            if(doc == null){
                return@EventListener
            }

            if(doc.exists()){

                set = doc.toObject(SetItem::class.java)!!
                setInfo()

            }

        })

    }

    fun setInfo(){

        pack_name_text.text = set.name
        unlockFunction()

    }

    fun blockFunction() {

        pack_add_question_btn.isEnabled = false
        pack_quit_btn.isEnabled = false
        pack_edit_question_btn.isEnabled = false

    }

    fun unlockFunction() {

        pack_add_question_btn.isEnabled = true
        pack_quit_btn.isEnabled = true
        pack_edit_question_btn.isEnabled = true

    }

    fun getInformation(){

        dbSet.document(intent.extras.getString("id").toString()).collection("questions").get().addOnSuccessListener {

            if(!it.isEmpty){

                user_pack_canswer.text = it.size().toString()

                var i = 0

                for(doc in it){

                    i += doc.data.size - 1

                    user_pack_banswer.text = i.toString()

                }

            }

        }

    }

    override fun onResume() {
        super.onResume()
        unlockFunction()
        getInformation()
        hideSystemUI()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        hideSystemUI()
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    override fun onBackPressed() {
        hideSystemUI()
        super.onBackPressed()
    }

    private fun hideSystemUI(){

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    }

    override fun onWindowFocusChanged(hasFocus:Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

}
