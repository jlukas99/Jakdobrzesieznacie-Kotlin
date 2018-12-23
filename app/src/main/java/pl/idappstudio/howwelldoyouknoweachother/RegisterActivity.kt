@file:Suppress("DEPRECATION")

package pl.idappstudio.howwelldoyouknoweachother

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern
import com.google.firebase.auth.FirebaseAuth
import android.support.design.widget.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.concurrent.schedule


class RegisterActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("PrivateResource", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val adapter = ArrayAdapter.createFromResource(this, R.array.gender_list, R.layout.gender_list_layout)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        val dialogBuilder = AlertDialog.Builder(this, R.style.Base_Theme_MaterialComponents_Dialog)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading_create_account, null)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTranspery)))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)

        emailInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val content = emailInput?.text.toString().trim()

                if (isEmailValid(content)) {

                    emailInput.setBackgroundResource(R.drawable.input_overlay)
                    emailImage.setBackgroundResource(R.drawable.input_overlay_icon)

                } else {

                    emailInput?.error = "Niepoprawny email"
                    emailInput.setBackgroundResource(R.drawable.input_overlay_error)
                    emailImage.setBackgroundResource(R.drawable.input_overlay_icon_error)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        passwordInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val content = passwordInput?.text.toString().trim()

                if (isPasswordValid(content)) {

                    passwordInput.setBackgroundResource(R.drawable.input_overlay)
                    passwordImage.setBackgroundResource(R.drawable.input_overlay_icon)

                } else {

                    passwordInput?.error = "Hasło musi składać się przynajmniej z 6 liter lub cyfr"
                    passwordInput.setBackgroundResource(R.drawable.input_overlay_error)
                    passwordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        repasswordInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val content = repasswordInput?.text.toString().trim()
                val content2 = passwordInput?.text.toString().trim()

                if (isRePasswordValid(content, content2)) {

                    repasswordInput.setBackgroundResource(R.drawable.input_overlay)
                    repasswordImage.setBackgroundResource(R.drawable.input_overlay_icon)

                } else {

                    repasswordInput?.error = "Hasła się nie zgadzają"
                    repasswordInput.setBackgroundResource(R.drawable.input_overlay_error)
                    repasswordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        btn_send.setOnClickListener {

            var error = 0

            if(!isEmailValid(emailInput?.text.toString().trim())) {

                error++

                emailInput?.error = "Niepoprawny email"
                emailInput.setBackgroundResource(R.drawable.input_overlay_error)
                emailImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

            }

            if(!isPasswordValid(passwordInput?.text.toString().trim())) {

                error++

                passwordInput?.error = "Hasło musi składać się przynajmniej z 6 liter lub cyfr"
                passwordInput.setBackgroundResource(R.drawable.input_overlay_error)
                passwordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

            }

            if(!isRePasswordValid(repasswordInput?.text.toString().trim(), passwordInput?.text.toString().trim())) {

                error++

                repasswordInput?.error = "Hasła się nie zgadzają"
                repasswordInput.setBackgroundResource(R.drawable.input_overlay_error)
                repasswordImage.setBackgroundResource(R.drawable.input_overlay_icon_error)

            }

            if(error > 0) { return@setOnClickListener }

            alertDialog.show()

            auth.createUserWithEmailAndPassword(emailInput?.text.toString().trim(), passwordInput?.text.toString().trim())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val uid = auth.currentUser?.uid.toString()
                        val genderInt: Int = genderSpinner.selectedItemPosition
                        val gender: String

                        gender = if(genderInt == 0) "male" else "famle"

                        var email = emailInput.text.trim()
                        val emailNum = email.indexOf("@")
                        email = email.substring(0, emailNum)

                        val user = HashMap<String, Any>()
                        user["name"] = email
                        user["gender"] = gender
                        user["type"] = "free"
                        user["uid"] = uid

                        db.collection("users").document(uid).get().addOnSuccessListener { document ->

                            if(document.exists()){

                                alertDialog.dismiss()

                                val snackbar: Snackbar? = Snackbar.make(view, "Konto już istnieje", 2500)
                                snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorRed))
                                snackbar?.show()

                                val intent = Intent(this, LoginActivity::class.java)

                                Timer("StartIntent", false).schedule(700) {

                                    startActivity(intent)
                                    finish()

                                }

                            } else {

                                db.collection("users").document(uid).set(user).addOnCompleteListener {

                                    alertDialog.dismiss()

                                    val snackbar: Snackbar? = Snackbar.make(view, "Utworzono konto", 2500)
                                    snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorAccent))
                                    snackbar?.show()

                                    val intent = Intent(this, MenuActivity::class.java)

                                    Timer("StartIntent", false).schedule(700) {

                                        startActivity(intent)
                                        finish()

                                    }

                                }

                            }
                        }

                    } else {

                        alertDialog.dismiss()

                        val snackbar: Snackbar? = Snackbar.make(view, "Nie udało się utworzyć konta!", 2500)
                        snackbar?.view?.setBackgroundColor(resources.getColor(R.color.colorRed))
                        snackbar?.show()

                    }
                }

        }

        btn_zaloguj.setOnClickListener {

            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return Pattern.compile(
            "^[a-zA-Z0-9]{6,}$"
        ).matcher(password).matches()
    }

    fun isRePasswordValid(repassword: String, password: String): Boolean {
        return (repassword == password)
    }
}
